package eyesatop.controller_tcpip.common.tasksrequests.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class TakeOffRequest implements FlightTaskRequest {

    private static final String ALTITUDE = "altitude";

    private final double altitude;

    @JsonCreator
    public TakeOffRequest(@JsonProperty(ALTITUDE) double altitude) {
        this.altitude = altitude;
    }

    @JsonProperty(ALTITUDE)
    public double getAltitude() {
        return altitude;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.flightTasks().takeOff(altitude);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
