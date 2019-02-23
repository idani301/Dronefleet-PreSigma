package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.ZoomOut;

public class MockZoomOut extends MockDroneTask<CameraTaskType> implements ZoomOut {

    public MockZoomOut(UUID uuid) {
        super(uuid, CameraTaskType.ZOOM_OUT);
    }
}
