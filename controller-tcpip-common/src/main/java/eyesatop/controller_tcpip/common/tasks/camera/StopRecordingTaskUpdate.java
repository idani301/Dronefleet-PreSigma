package eyesatop.controller_tcpip.common.tasks.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.camera.MockStopRecording;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;

public class StopRecordingTaskUpdate extends CameraTaskUpdate {

    @JsonCreator
    public StopRecordingTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                   @JsonProperty(ERROR) DroneTaskException error,
                                   @JsonProperty(STATUS) TaskStatus status) {
        super(uuid, error, status);
    }

    @Override
    @JsonIgnore
    protected MockDroneTask<CameraTaskType> createNewMockTask() {
        return new MockStopRecording(getUuid());
    }
}
