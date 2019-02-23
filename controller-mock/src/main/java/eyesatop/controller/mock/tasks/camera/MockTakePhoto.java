package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.TakePhoto;

public class MockTakePhoto extends MockDroneTask<CameraTaskType> implements TakePhoto {
    public MockTakePhoto(UUID uuid) {
        super(uuid, CameraTaskType.TAKE_PHOTO);
    }
}
