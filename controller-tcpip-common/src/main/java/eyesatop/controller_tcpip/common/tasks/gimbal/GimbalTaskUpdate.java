package eyesatop.controller_tcpip.common.tasks.gimbal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.controller_tcpip.common.tasks.TaskUpdate;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public abstract class GimbalTaskUpdate extends TaskUpdate<GimbalTaskType> {

    @JsonCreator
    public GimbalTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                            @JsonProperty(ERROR) DroneTaskException error,
                            @JsonProperty(STATUS) TaskStatus status) {
        super(uuid, error, status);
    }

    @JsonIgnore
    public static GimbalTaskUpdate getFromTask(DroneTask<GimbalTaskType> gimbalTask){
        switch (gimbalTask.taskType()){

            case ROTATE_GIMBAL:
                RotateGimbal rotateGimbal = (RotateGimbal) gimbalTask;
                return new RotateGimbalTaskUpdate(rotateGimbal.uuid(),rotateGimbal.error().value(),rotateGimbal.status().value(),rotateGimbal.rotationRequest(),rotateGimbal.timeoutInSeconds());
            case LOOK_AT_POINT:
                LookAtPoint lookAtPoint = (LookAtPoint) gimbalTask;
                return new LookAtPointTaskUpdate(gimbalTask.uuid(),gimbalTask.error().value(),gimbalTask.status().value(),lookAtPoint.location());
            case LOCK_LOOK_AT_LOCATION:
                LockGimbalAtLocation lockGimbalAtLocation = (LockGimbalAtLocation) gimbalTask;
                return new LockGimbalAtLocationTaskUpdate(gimbalTask.uuid(),gimbalTask.error().value(),gimbalTask.status().value(),lockGimbalAtLocation.location());
                default:
                    return null;
        }
    }
}
