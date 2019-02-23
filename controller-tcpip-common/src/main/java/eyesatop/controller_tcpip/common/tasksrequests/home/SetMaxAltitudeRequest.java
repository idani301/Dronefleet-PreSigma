package eyesatop.controller_tcpip.common.tasksrequests.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class SetMaxAltitudeRequest implements HomeTaskRequest {

    private static final String ALTITUDE = "altitude";

    private final double altitude;

    @JsonCreator
    public SetMaxAltitudeRequest(@JsonProperty(ALTITUDE) double altitude) {
        this.altitude = altitude;
    }

    @JsonProperty(ALTITUDE)
    public double getAltitude() {
        return altitude;
    }

    @Override
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.droneHome().setMaxAltitudeFromTakeOffLocation(altitude);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
