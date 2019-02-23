package eyesatop.imageprocess.android;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.imageprocess.DetectionData;
import eyesatop.imageprocess.Imageprocess;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;
import logs.LoggerTypes;

/**
 * 0586972005
 * Created by Idan on 20/05/2018.
 */

public class ImageprocessAnyvisionAndroid implements Imageprocess {

    private static ImageprocessAnyvisionAndroid instance = null;

    public static ImageprocessAnyvisionAndroid getInstance(){
        return instance;
    }

    public static void initInstance(final String ip){
        instance = new ImageprocessAnyvisionAndroid(ip);
//        try {
//            instance.connect();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            instance = null;
//        }
    }

    public interface DetectionListener {
        void onDetection();
    }

    private final String ip;
    private DetectionListener listener = null;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Property<DetectionData> detectionData = new Property<>();
    private final BooleanProperty isAlive = new BooleanProperty(false);

    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public ImageprocessAnyvisionAndroid(final String ip) {

        this.ip = ip;
    }

    public void setListener(DetectionListener listener){
        this.listener = listener;
    }

    public void connect() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                IO.Options options = new IO.Options();
                options.transports = new String[1];
                options.transports[0] = "websocket";

                try {
                    Socket socket = IO.socket(ip,options);

                    socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            MainLogger.logger.write_message(LoggerTypes.ANYVISION,"Connected");
                            isAlive.set(true);
                            latch.countDown();
                        }
                    });

                    socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            isAlive.set(false);
                        }
                    });

                    socket.on("NEW_DETECTION", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            // What to do with the detction,
                            // including getting lat lon from the detection AGL.
                            System.out.println("Got new Detection");

                            detectionData.set(null);

                            try {
                                if (listener != null) {
                                    listener.onDetection();
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                            for(Object arg : args){
                                try {
                                    if(arg != null) {
                                        JSONObject jsonObject = (JSONObject) arg;

                                        MainLogger.logger.write_message(LoggerTypes.ANYVISION_RECORD,arg.toString());

                                        JSONArray positions = (JSONArray) jsonObject.get("object_positions");
                                        for(int i=positions.length()-1; i >= 0; i--){
                                            JSONObject tempObject = positions.getJSONObject(i);
                                            if(tempObject != null){
                                                JSONArray pixel = (JSONArray) tempObject.get("rect");
//                                                Integer pts = (Integer) tempObject.get("pts");
                                                JSONArray topLeftPixel = (JSONArray) pixel.get(0);
                                                JSONArray botRightPixel = (JSONArray) pixel.get(1);

                                                if(topLeftPixel.length() == 2 && botRightPixel.length() == 2) {
                                                    System.out.println("Got Pixel : " + pixel.toString());
                                                    System.out.println("i : " + i);
                                                    System.out.println("size : " + positions.length());

                                                    Integer topLeftX = (Integer) topLeftPixel.get(0);
                                                    Integer topLeftY = (Integer) topLeftPixel.get(1);
                                                    Integer botRightX = (Integer) botRightPixel.get(0);
                                                    Integer botRightY = (Integer) botRightPixel.get(1);

                                                    Integer averageX = (topLeftX + botRightX) / 2;
                                                    Integer averageY = (topLeftY + botRightY) / 2;
                                                    String frameDate = (String) jsonObject.get("frame_date");
                                                    Date inputDate = inputFormat.parse(frameDate);
//                                                    detectionData.set(new DetectionData(inputDate.getTime(),averageX,averageY, DetectionData.DetectionType.PERSON));
                                                    break;
                                                }
                                            }
                                        }
//                                        JSONObject tempPosition = positions.getJSONArray(0);

//                                        JSONObject object = (JSONObject) arg;
//                                        AnyvisionDetectionData data = Serialization.JSON.deserialize((String) arg.toString(), AnyvisionDetectionData.class);
//                                        System.out.println("Got Data");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
//                                System.out.println(arg == null ? "N/A" : arg.toString());
                            }
                        }
                    });
                    socket.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        });

        latch.await();
    }

    public void destroy(){
        executor.shutdownNow();
    }

    public BooleanProperty getIsAlive() {
        return isAlive;
    }

    @Override
    public ObservableValue<DetectionData> detection() {
        return detectionData;
    }
}
