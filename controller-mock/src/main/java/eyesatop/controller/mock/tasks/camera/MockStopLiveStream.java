package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StopLiveStream;

public class MockStopLiveStream extends MockDroneTask<CameraTaskType> implements StopLiveStream {

    public MockStopLiveStream(UUID uuid) {
        super(uuid, CameraTaskType.STOP_LIVE_STREAM);
    }
}
