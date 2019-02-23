package eyesatop.controller_tcpip.common.tasksrequests.gimbal;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface GimbalTaskRequest {
    TaskResponse perform(DroneController controller);
}
