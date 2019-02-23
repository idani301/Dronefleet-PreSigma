package eyesatop.controller.djinew.tasks.camera;

import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.sdkmanager.LiveStreamManager;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StopLiveStream;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.Property;

public class DjiStopLiveStream extends RunnableDroneTask<CameraTaskType> implements StopLiveStream {

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        LiveStreamManager liveStreamManager = DJISDKManager.getInstance().getLiveStreamManager();

        if(liveStreamManager == null){
            throw new DroneTaskException("Live Stream Manager is null");
        }

        liveStreamManager.stopStream();
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.STOP_LIVE_STREAM;
    }
}
