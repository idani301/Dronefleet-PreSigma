package eyesatop.controller_tcpip.common.tasksrequests.camera;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class StopShootingPhotoRequest implements CameraTaskRequest {

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {
        try {
            DroneTask newTask = controller.camera().stopShootingPhotos();
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
