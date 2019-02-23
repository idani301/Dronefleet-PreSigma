package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

public interface StopLiveStream extends DroneTask<CameraTaskType>{

    public class StopLiveStreamStub extends StubDroneTask<CameraTaskType> implements StopLiveStream {

        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.STOP_LIVE_STREAM;
        }
    }
}
