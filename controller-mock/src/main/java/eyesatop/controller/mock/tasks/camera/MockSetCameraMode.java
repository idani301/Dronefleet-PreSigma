package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetCameraMode;

public class MockSetCameraMode extends MockDroneTask<CameraTaskType> implements SetCameraMode {

    private final CameraMode mode;

    public MockSetCameraMode(UUID uuid, CameraMode mode) {
        super(uuid, CameraTaskType.CHANGE_MODE);
        this.mode = mode;
    }

    @Override
    public CameraMode mode() {
        return mode;
    }
}
