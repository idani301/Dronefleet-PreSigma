package eyesatop.controller_tcpip.common.tasksrequests.gimbal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.GimbalRequest;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class RotateGimbalTaskRequest implements GimbalTaskRequest {

    private static final String ROTATION_REQUEST = "rotationRequest";
    private static final String TIMEOUT = "timeoutInSeconds";

    private final GimbalRequest rotationRequest;
    private final Integer timeoutInSeconds;

    @JsonCreator
    public RotateGimbalTaskRequest(@JsonProperty(ROTATION_REQUEST) GimbalRequest rotationRequest,
                                   @JsonProperty(TIMEOUT) Integer timeoutInSeconds) {
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
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.gimbal().rotateGimbal(rotationRequest,timeoutInSeconds);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
