package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StopShootingPhotos;

public class MockStopShootingPhotos extends MockDroneTask<CameraTaskType> implements StopShootingPhotos {

    public MockStopShootingPhotos(UUID uuid) {
        super(uuid, CameraTaskType.STOP_SHOOTING_PHOTOS);
    }
}
