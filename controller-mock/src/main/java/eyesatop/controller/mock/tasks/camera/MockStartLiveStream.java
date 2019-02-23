package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StartLiveStream;

public class MockStartLiveStream extends MockDroneTask<CameraTaskType> implements StartLiveStream {

    private final String url;

    public MockStartLiveStream(UUID uuid, String url) {
        super(uuid, CameraTaskType.START_LIVE_STREAM);
        this.url = url;
    }

    @Override
    public String url() {
        return url;
    }
}
