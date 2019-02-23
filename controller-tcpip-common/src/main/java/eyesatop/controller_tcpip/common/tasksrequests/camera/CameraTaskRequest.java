package eyesatop.controller_tcpip.common.tasksrequests.camera;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface CameraTaskRequest {
    TaskResponse perform(DroneController controller);
}
