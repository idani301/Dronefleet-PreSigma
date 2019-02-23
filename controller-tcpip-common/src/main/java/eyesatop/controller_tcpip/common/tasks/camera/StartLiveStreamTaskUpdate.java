package eyesatop.controller_tcpip.common.tasks.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.camera.MockStartLiveStream;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;

public class StartLiveStreamTaskUpdate extends CameraTaskUpdate {

    private static final String URL = "url";

    private final String url;

    @JsonCreator
    public StartLiveStreamTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                     @JsonProperty(ERROR) DroneTaskException error,
                                     @JsonProperty(STATUS) TaskStatus status,
                                     @JsonProperty(URL) String url) {
        super(uuid, error, status);
        this.url = url;
    }

    @JsonProperty(URL)
    public String getUrl() {
        return url;
    }

    @Override
    protected MockDroneTask<CameraTaskType> createNewMockTask() {
        return new MockStartLiveStream(getUuid(),getUrl());
    }
}
