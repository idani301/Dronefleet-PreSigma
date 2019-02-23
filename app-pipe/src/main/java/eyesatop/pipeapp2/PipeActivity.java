package eyesatop.pipeapp2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.WindowManager;

import com.example.abstractcontroller.AbstractDroneController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import eyesatop.controller.beans.VideoCodecType;
import eyesatop.controller.beans.VideoPacket;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.djinew.DjiControllerFactory;
import eyesatop.controller.djinew.livestream.DjiRTMPLiveStreamer;
import eyesatop.controller_tcpip.local.TCPControllerLocal;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.ui_generic.viewmodels.EyesatopAppsActivity;
import eyesatop.ui_generic.viewmodels.ViewModel;
import eyesatop.util.Function;
import eyesatop.util.android.HandlerExecutor;
import eyesatop.util.android.dtm.TerrainAltitude;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.SimpleObstacleProvider;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import logs.LoggerTypes;

public class PipeActivity extends EyesatopAppsActivity {

    private Integer droneID = null;

    @Override
    protected List<LoggerTypes> logsRequired() {

        ArrayList<LoggerTypes> loggers = new ArrayList<>();

        loggers.add(LoggerTypes.ERROR);
        loggers.add(LoggerTypes.SDK_INIT);
        loggers.add(LoggerTypes.PRODUCT_CHANGES);

        return loggers;
    }

    @Override
    protected void postChecks(final Activity thisActivity) {

        SharedPreferences prefs = getSharedPreferences("pipe", MODE_PRIVATE);
        String savedDroneIDs = prefs.getString("droneID",null);

        if(savedDroneIDs != null){
            try {
                Integer droneID = Integer.parseInt(savedDroneIDs);
                getDroneIDsEditText().text().set(droneID + "");
            }
            catch (Exception e){}
        }

        DtmProvider stamProvider = new TerrainAltitude(this);
        SimpleObstacleProvider provider = new SimpleObstacleProvider(stamProvider);

        ControllerDjiNew controllerDJI = null;
        getLoadingText().text().set("Dji SDK init");
        try {
            controllerDJI = (ControllerDjiNew) new DjiControllerFactory(this, provider).newController().await();
        } catch (Exception e) {
            getLoadingText().text().set("Unable to Init DJI SDK. Maybe check internet connection");
            return;
        }

        getLoadingText().text().set("Choose App Type and ID, And click Done");
        getAppTypeView().visibility().set(ViewModel.Visibility.VISIBLE);

        final CountDownLatch appLatch = new CountDownLatch(1);

        getDoneText().singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                getDoneText().singleTap().set(new Function<MotionEvent, Boolean>() {
                    @Override
                    public Boolean apply(MotionEvent input) {
                        return false;
                    }
                });
                try {
                    appLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        try {
            appLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getLoadingText().text().set("Loading the UI");

        getAppTypeView().visibility().set(ViewModel.Visibility.GONE);

        ApplicationType selectedType = (ApplicationType) getSpinner().selectedItem().value();
        String droneIDString = getDroneIDsEditText().getViewText();

        try {
            droneID = Integer.parseInt(droneIDString);
        }
        catch (Exception e){
            getLoadingText().text().set("Illegal Drone ID : " + droneIDString);
            return;
        }

        SharedPreferences.Editor editor = getSharedPreferences("pipe", MODE_PRIVATE).edit();
        editor.putString("droneID", droneIDString);
        editor.apply();

        final AbstractDroneController finalController;
        final Activity activity = this;
        final Property<DJICodecManager> codecInstance = new Property<>();

        if (selectedType == ApplicationType.SIMULATOR) {
            Random random = new Random();
            Location startLocation = new Location(31.788530, 35.180395);

            final ControllerSimulator controllerA = new ControllerSimulator(provider);

            finalController = controllerA;
            controllerA.togglePower(startLocation.getLocationFromAzAndDistance(
                    ControllerSimulator.randomInt(random, 0, 50),
                    ControllerSimulator.randomInt(random, 0, 360)));
            try {
                InputStream inputStream = thisActivity.getAssets().open("simulatorVideo.txt");
                controllerA.camera().startVideoSimulator(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            controllerA.camera().getLiveStreamer().set(new DjiRTMPLiveStreamer());
            controllerA.camera().videoBuffer().observe(new Observer<VideoPacket>() {
                @Override
                public void observe(VideoPacket oldValue, VideoPacket newValue, Observation<VideoPacket> observation) {
                    DJICodecManager codecManager = codecInstance.value();
                    if (codecManager != null) {
                        codecManager.sendDataToDecoder(newValue.getData(), newValue.getSize());
                    }
                }
            });
        } else {
            finalController = controllerDJI;
            final VideoFeeder.VideoDataListener videoListener = new VideoFeeder.VideoDataListener() {
                @Override
                public void onReceive(byte[] bytes, int i) {
                    DJICodecManager currentCodec = codecInstance.value();
                    if (currentCodec != null) {
                        currentCodec.sendDataToDecoder(bytes, i);
                    }

                    byte[] subArray = new byte[i];
                    System.arraycopy(bytes, 0, subArray, 0, i);

                    finalController.camera().videoBuffer().set(new VideoPacket(VideoCodecType.H264, subArray, i));
                }
            };

            VideoFeeder.getInstance().addPhysicalSourceListener(new VideoFeeder.PhysicalSourceListener() {
                @Override
                public void onChange(VideoFeeder.VideoFeed videoFeed, VideoFeeder.PhysicalSource physicalSource) {
                    if (!videoFeed.getListeners().contains(videoListener)) {
                        videoFeed.addVideoDataListener(videoListener);
                    }
                }
            });
        }

        TCPControllerLocal controllerLocal = null;

        try {
            controllerLocal = new TCPControllerLocal(finalController, droneID);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        final TCPControllerLocal finalControllerLocal = controllerLocal;
        HandlerExecutor.MAIN_LOOPER_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                activity.setContentView(R.layout.pipe_main_layout);
                final TextureView videoSurface = (TextureView) activity.findViewById(R.id.cameraSimpleVideo);

                videoSurface.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {

                        DJICodecManager codecManager = codecInstance.value();
                        if (codecManager != null) {
                            return;
                        }

                        codecInstance.set(new DJICodecManager(activity, surfaceTexture, i, i1));
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                        DJICodecManager currentCodec = codecInstance.value();
                        if (currentCodec != null) {
                            currentCodec.cleanSurface();
                        }
                        return false;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                    }
                });

                PipeStatusModel pipeStatusModel = new PipeStatusModel(thisActivity,finalController,finalControllerLocal.remoteIP(),droneID);
            }
        });
    }

    @Override
    public List<String> permissionsRequired() {
        ArrayList<String> permissionsRequired = new ArrayList<>();

        permissionsRequired.add("android.permission.BLUETOOTH");
        permissionsRequired.add("android.permission.BLUETOOTH_ADMIN");
        permissionsRequired.add("android.permission.VIBRATE");
        permissionsRequired.add("android.permission.INTERNET");
        permissionsRequired.add("android.permission.ACCESS_WIFI_STATE");
        permissionsRequired.add("android.permission.WAKE_LOCK");
        permissionsRequired.add("android.permission.ACCESS_COARSE_LOCATION");
        permissionsRequired.add("android.permission.ACCESS_NETWORK_STATE");
        permissionsRequired.add("android.permission.ACCESS_FINE_LOCATION");
        permissionsRequired.add("android.permission.CHANGE_WIFI_STATE");
        permissionsRequired.add("android.permission.WRITE_EXTERNAL_STORAGE");
        permissionsRequired.add("android.permission.READ_EXTERNAL_STORAGE");
        permissionsRequired.add("android.permission.READ_PHONE_STATE");
        permissionsRequired.add("android.permission.RECEIVE_BOOT_COMPLETED");

        return permissionsRequired;
    }
}