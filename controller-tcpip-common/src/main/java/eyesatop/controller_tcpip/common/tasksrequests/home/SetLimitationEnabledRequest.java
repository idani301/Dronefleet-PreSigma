package eyesatop.controller_tcpip.common.tasksrequests.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class SetLimitationEnabledRequest implements HomeTaskRequest {

    private static final String ENABLED = "enabled";

    private final boolean enabled;

    @JsonCreator
    public SetLimitationEnabledRequest(@JsonProperty(ENABLED) boolean enabled) {
        this.enabled = enabled;
    }

    @JsonProperty(ENABLED)
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.droneHome().setFlightLimitationEnabled(enabled);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
