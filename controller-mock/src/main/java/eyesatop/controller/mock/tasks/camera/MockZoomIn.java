package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.ZoomIn;

public class MockZoomIn extends MockDroneTask<CameraTaskType> implements ZoomIn {

    public MockZoomIn(UUID uuid) {
        super(uuid, CameraTaskType.ZOOM_IN);
    }
}
