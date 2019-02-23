package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 30/04/2017.
 */
public interface TakePhoto extends DroneTask<CameraTaskType> {
    public class StubTakePhoto extends StubDroneTask<CameraTaskType> implements TakePhoto{
        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.TAKE_PHOTO;
        }
    }
}
