package eyesatop.controller_tcpip.common.tasksrequests.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.util.geo.Location;

public class SetHomeLocationRequest implements HomeTaskRequest {

    private static final String LOCATION = "location";

    private final Location location;

    @JsonCreator
    public SetHomeLocationRequest(@JsonProperty(LOCATION) Location location) {
        this.location = location;
    }

    @JsonProperty(LOCATION)
    public Location getLocation() {
        return location;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.droneHome().setHomeLocation(location);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
