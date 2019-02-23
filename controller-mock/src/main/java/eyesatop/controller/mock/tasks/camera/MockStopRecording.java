package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StopRecording;

public class MockStopRecording extends MockDroneTask<CameraTaskType> implements StopRecording {
    public MockStopRecording(UUID uuid) {
        super(uuid, CameraTaskType.STOP_RECORD);
    }
}
