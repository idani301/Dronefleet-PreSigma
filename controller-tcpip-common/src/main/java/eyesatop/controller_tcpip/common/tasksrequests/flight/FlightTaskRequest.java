package eyesatop.controller_tcpip.common.tasksrequests.flight;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface FlightTaskRequest {
    TaskResponse perform(DroneController controller);
}
