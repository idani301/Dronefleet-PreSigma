package eyesatop.controller_tcpip.common.tasksrequests.gimbal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.util.geo.Location;

public class LockGimbalAtLocationRequest implements GimbalTaskRequest {

    private static final String LOCATION = "Location";

    private final Location location;

    @JsonCreator
    public LockGimbalAtLocationRequest(@JsonProperty(LOCATION) Location location) {
        this.location = location;
    }

    @JsonProperty(LOCATION)
    public Location getLocation() {
        return location;
    }

    @Override
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.gimbal().lockGimbalAtLocation(location);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
