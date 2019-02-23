package eyesatop.controller_tcpip.common.tasksrequests.home;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

import eyesatop.controller.DroneController;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public interface HomeTaskRequest {
    TaskResponse perform(DroneController controller);
}
