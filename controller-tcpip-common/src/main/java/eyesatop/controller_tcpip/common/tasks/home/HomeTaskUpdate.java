package eyesatop.controller_tcpip.common.tasks.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import eyesatop.controller.tasks.home.SetLimitationEnabled;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;
import eyesatop.controller_tcpip.common.tasks.TaskUpdate;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public abstract class HomeTaskUpdate extends TaskUpdate<HomeTaskType> {

    @JsonCreator
    public HomeTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                          @JsonProperty(ERROR) DroneTaskException error,
                          @JsonProperty(STATUS) TaskStatus status) {
        super(uuid, error, status);
    }

    @JsonIgnore
    public static HomeTaskUpdate getFromTask(DroneTask<HomeTaskType> task){
        switch (task.taskType()){

            case SET_HOME_LOCATION:
                SetHomeLocation setHomeLocation = (SetHomeLocation) task;
                return new SetHomeLocationTaskUpdate(task.uuid(),task.error().value(),task.status().value(),setHomeLocation.location());
            case SET_LIMITATION_ENABLED:
                SetLimitationEnabled setLimitationEnabled = (SetLimitationEnabled) task;
                return new SetLimitationEnabledTaskUpdate(task.uuid(),task.error().value(),task.status().value(),setLimitationEnabled.enabled());
            case SET_MAX_DISTANCE_FROM_HOME:
                SetMaxDistanceFromHome setMaxDistanceFromHome = (SetMaxDistanceFromHome) task;
                return new SetMaxDistanceFromHomeTaskUpdate(task.uuid(),task.error().value(),task.status().value(),setMaxDistanceFromHome.maxDistanceFromHome());
            case SET_MAX_ALT_FROM_TAKE_OFF_ALT:
                SetMaxAltitudeFromHomeLocation setMaxAltitudeFromHomeLocation = (SetMaxAltitudeFromHomeLocation) task;
                return new SetMaxAltitudeTaskUpdate(task.uuid(),task.error().value(),task.status().value(),setMaxAltitudeFromHomeLocation.altitude());
            case SET_RETURN_HOME_ALT:
                SetReturnHomeAltitude setReturnHomeAltitude = (SetReturnHomeAltitude) task;
                return new SetReturnHomeAltitudeTaskUpdate(task.uuid(),task.error().value(),task.status().value(),setReturnHomeAltitude.altitude());
                default:
                    return null;
        }
    }
}
