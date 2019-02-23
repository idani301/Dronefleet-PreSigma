package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StartRecording;

public class MockStartRecording extends MockDroneTask<CameraTaskType> implements StartRecording {
    public MockStartRecording(UUID uuid) {
        super(uuid, CameraTaskType.START_RECORD);
    }
}
