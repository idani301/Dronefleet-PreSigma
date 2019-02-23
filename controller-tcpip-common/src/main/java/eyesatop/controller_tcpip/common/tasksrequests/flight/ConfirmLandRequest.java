package eyesatop.controller_tcpip.common.tasksrequests.flight;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class ConfirmLandRequest implements FlightTaskRequest {

    @Override
    public TaskResponse perform(DroneController controller) {
        try {
            controller.flightTasks().confirmLand();
        } catch (DroneTaskException e) {
            e.printStackTrace();
            return new TaskResponse(null,e.getErrorString());
        }
        return new TaskResponse(null,"Confirm Land does not give back UUID");
    }
}
