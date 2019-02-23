package eyesatop.controller_tcpip.common.tasksrequests.camera;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class StopLiveStreamRequest implements CameraTaskRequest {

    @Override
    public TaskResponse perform(DroneController controller) {
            try {
            return new TaskResponse(controller.camera().stopLiveStream().uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
