package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 13/10/2017.
 */

public interface StopShootingPhotos extends DroneTask<CameraTaskType> {

    public class StubStopShootingPhotos extends StubDroneTask<CameraTaskType> implements StopShootingPhotos{
        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.STOP_SHOOTING_PHOTOS;
        }
    }
}
