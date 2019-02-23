package eyesatop.controller_tcpip.common.tasks.gimbal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.GimbalRequest;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.gimbal.MockRotateGimbal;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;

public class RotateGimbalTaskUpdate extends GimbalTaskUpdate {

    private static final String ROTATION_REQUEST = "rotationRequest";
    private static final String TIMEOUT = "timeoutInSeconds";

    private final GimbalRequest rotationRequest;
    private final Integer timeoutInSeconds;

    @JsonCreator
    public RotateGimbalTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                  @JsonProperty(ERROR) DroneTaskException error,
                                  @JsonProperty(STATUS) TaskStatus status,
                                  @JsonProperty(ROTATION_REQUEST) GimbalRequest rotationRequest,
                                  @JsonProperty(TIMEOUT) Integer timeoutInSeconds) {
        super(uuid, error, status);
        this.rotationRequest = rotationRequest;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    @JsonProperty(ROTATION_REQUEST)
    public GimbalRequest getRotationRequest() {
        return rotationRequest;
    }

    @JsonProperty(TIMEOUT)
    public Integer getTimeoutInSeconds() {
        return timeoutInSeconds;
    }

    @Override
    @JsonIgnore
    protected MockDroneTask<GimbalTaskType> createNewMockTask() {
        return new MockRotateGimbal(getUuid(),rotationRequest,timeoutInSeconds);
    }
}
