package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 30/04/2017.
 */
public interface StartRecording extends DroneTask<CameraTaskType> {

    public class StubStartRecording extends StubDroneTask<CameraTaskType> implements StartRecording{
        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.START_RECORD;
        }
    }
}
