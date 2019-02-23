package eyesatop.controller_tcpip.common.tasksrequests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import eyesatop.controller.tasks.exceptions.DroneTaskException;

public class TaskResponse {

    private static final String UUID = "taskUUID";
    private static final String ERROR = "error";

    private final UUID taskUUID;
    private final String error;

    @JsonCreator
    public TaskResponse(@JsonProperty(UUID) UUID taskUUID,
                        @JsonProperty(ERROR) String error) {
        this.taskUUID = taskUUID;
        this.error = error;
    }

    @JsonProperty(UUID)
    public java.util.UUID getTaskUUID() {
        return taskUUID;
    }

    @JsonProperty(ERROR)
    public String getError() {
        return error;
    }
}
