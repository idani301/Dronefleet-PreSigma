package eyesatop.controller.djinew.livestream;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;
import eyesatop.controller.beans.StreamState;
import eyesatop.controller.livestream.RTMPLiveStreamer;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

public class DjiRTMPLiveStreamer implements RTMPLiveStreamer {

    private final Property<StreamState> streamState = new Property<>();
    private final LiveStreamManager liveStreamManager;
    private final ExecutorService streamStateExecutor = Executors.newSingleThreadExecutor();

    public DjiRTMPLiveStreamer() {
        this.liveStreamManager = DJISDKManager.getInstance().getLiveStreamManager();
        liveStreamManager.setVideoEncodingEnabled(true);
        liveStreamManager.setAudioMuted(true);

        liveStreamManager.registerListener(new LiveStreamManager.OnLiveChangeListener() {
            @Override
            public void onStatusChanged(int i) {
                updateStreamState();
            }
        });
        streamStateExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    updateStreamState();
                }
            }
        });
    }

    private void updateStreamState(){
        streamState.set(new StreamState(liveStreamManager.isStreaming(),liveStreamManager.getLiveUrl(),liveStreamManager.getLiveVideoBitRate()));
    }

    @Override
    public ObservableValue<StreamState> streamState() {
        return streamState;
    }

    @Override
    public void startStream(final String url) throws DroneTaskException {

        final Property<Integer> result = new Property<>();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                liveStreamManager.setVideoEncodingEnabled(true);
                liveStreamManager.setLiveUrl(url);
                result.set(liveStreamManager.startStream());
                liveStreamManager.setStartTime();
            }
        }).start();
        throw new DroneTaskException(result.value() + "");

    }

    @Override
    public void stopStream() throws DroneTaskException {
        liveStreamManager.stopStream();
    }
}
