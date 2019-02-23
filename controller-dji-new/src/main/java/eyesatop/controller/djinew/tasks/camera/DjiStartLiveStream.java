package eyesatop.controller.djinew.tasks.camera;

import android.widget.Toast;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;
import eyesatop.controller.beans.StreamState;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StartLiveStream;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.Predicate;
import eyesatop.util.drone.DroneModel;
import eyesatop.util.model.Property;

public class DjiStartLiveStream extends RunnableDroneTask<CameraTaskType> implements StartLiveStream {

    private final ControllerDjiNew controller;
    private final String url;

    public DjiStartLiveStream(ControllerDjiNew controller, String url) {
        this.controller = controller;
        this.url = url;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        final LiveStreamManager liveStreamManager = DJISDKManager.getInstance().getLiveStreamManager();
        if(liveStreamManager == null){
            throw new DroneTaskException("Live Stream Manager is null");
        }

        StreamState currentState = controller.camera().streamState().value();
        if(currentState != null && currentState.isStreaming() && !url.equals(currentState.getStreamURL())){

            liveStreamManager.stopStream();

            controller.camera().streamState().await(new Predicate<StreamState>() {
                @Override
                public boolean test(StreamState subject) {
                    return subject == null || !subject.isStreaming();
                }
            },3, TimeUnit.SECONDS);
        }

        StreamState currentStreamState = controller.camera().streamState().value();
        if(currentStreamState != null && currentStreamState.isStreaming()){
            throw new DroneTaskException("Already Streaming");
        }

        liveStreamManager.setVideoEncodingEnabled(true);
        liveStreamManager.setLiveUrl(url);
        int result = liveStreamManager.startStream();
        liveStreamManager.setStartTime();

        throw new DroneTaskException(result + "");
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.START_LIVE_STREAM;
    }
}
