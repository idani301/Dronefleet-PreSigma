package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 30/04/2017.
 */
public interface StopRecording extends DroneTask<CameraTaskType> {
    public class StubStopRecording extends StubDroneTask<CameraTaskType> implements StopRecording{
        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.STOP_RECORD;
        }
    }
}
