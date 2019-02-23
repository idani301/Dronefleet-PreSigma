package eyesatop.controller_tcpip.common.tasksrequests.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class SetMaxDistanceFromHomeRequest implements HomeTaskRequest {

    private static final String DISTANCE  = "distance";

    private final double distance;

    @JsonCreator
    public SetMaxDistanceFromHomeRequest(@JsonProperty(DISTANCE) double distance) {
        this.distance = distance;
    }

    @JsonProperty(DISTANCE)
    public double getDistance() {
        return distance;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.droneHome().setMaxDistanceFromHome(distance);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
