package eyesatop.controller_tcpip.common.tasks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.tasks.EnumWithName;

public class CurrentTaskUpdateMessage<T extends EnumWithName> {

    protected static final String TASK_UPDATE = "taskUpdate";

    private final TaskUpdate<T> taskUpdate;

    @JsonCreator
    public CurrentTaskUpdateMessage(@JsonProperty(TASK_UPDATE) TaskUpdate<T> taskUpdate) {
        this.taskUpdate = taskUpdate;
    }

    @JsonProperty(TASK_UPDATE)
    public TaskUpdate<T> getTaskUpdate() {
        return taskUpdate;
    }
}
