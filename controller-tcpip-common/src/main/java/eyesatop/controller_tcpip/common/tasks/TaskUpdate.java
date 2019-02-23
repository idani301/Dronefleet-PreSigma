package eyesatop.controller_tcpip.common.tasks;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public abstract class TaskUpdate<T extends EnumWithName> {

    protected static final String UUID = "uuid";
    protected static final String ERROR = "error";
    protected static final String STATUS = "status";

    private final UUID uuid;
    private final DroneTaskException error;
    private final TaskStatus status;

    @JsonCreator
    public TaskUpdate(@JsonProperty(UUID) UUID uuid,
                      @JsonProperty(ERROR) DroneTaskException error,
                      @JsonProperty(STATUS) TaskStatus status) {
        this.uuid = uuid;
        this.error = error;
        this.status = status;
    }

    @JsonProperty(UUID)
    public UUID getUuid() {
        return uuid;
    }

    @JsonProperty(ERROR)
    public DroneTaskException getError() {
        return error;
    }

    @JsonProperty(STATUS)
    public TaskStatus getStatus() {
        return status;
    }

    @JsonIgnore
    protected abstract MockDroneTask<T> createNewMockTask();

    @JsonIgnore
    public void updateMockTask(MockDroneTask<T> mockTask){
        mockTask.error().setIfNew(getError());
        mockTask.status().setIfNew(getStatus());
    }

    @JsonIgnore
    public MockDroneTask<T> createMockTask(){
        MockDroneTask<T> newTask = createNewMockTask();
        newTask.error().set(getError());
        newTask.status().set(getStatus());
        return newTask;
    }
}
