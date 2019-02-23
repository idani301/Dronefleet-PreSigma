package eyesatop.controller_tcpip.common.tasks.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FollowNavPlan;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.controller_tcpip.common.tasks.TaskUpdate;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public abstract class FlightTaskUpdate extends TaskUpdate<FlightTaskType> {

    @JsonCreator
    public FlightTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                            @JsonProperty(ERROR) DroneTaskException error,
                            @JsonProperty(STATUS) TaskStatus status) {
        super(uuid, error, status);
    }

    @JsonIgnore
    public static FlightTaskUpdate getFromTask(DroneTask<FlightTaskType> task){
        switch (task.taskType()){

            case FLY_IN_CIRCLE:
                FlyInCircle flyInCircle = (FlyInCircle)task;
                return new FlyInCircleTaskUpdate(flyInCircle.uuid(),flyInCircle.error().value(),flyInCircle.status().value(),flyInCircle.center(),flyInCircle.radius(),flyInCircle.rotationType(),flyInCircle.degreesToCover(),flyInCircle.startingDegree(),flyInCircle.altitudeInfo(),flyInCircle.velocity());
            case FOLLOW_NAV_PLAN:
                FollowNavPlan followNavPlan = (FollowNavPlan) task;
                return new FollowNavPlanTaskUpdate(followNavPlan.uuid(),followNavPlan.error().value(),followNavPlan.status().value(),followNavPlan.navPlanPoints());
            case LAND_IN_PLACE:
                return new LandUpdate(task.uuid(),task.error().value(),task.status().value());
            case TAKE_OFF:
                TakeOff takeOff = (TakeOff) task;
                return new TakeOffUpdate(task.uuid(),task.error().value(),task.status().value(),takeOff.altitude());
            case GOTO_POINT:
                FlyTo flyTo = (FlyTo)task;
                return new FlyToUpdate(task.uuid(),task.error().value(),task.status().value(),
                        flyTo.location(),
                        flyTo.altitudeInfo(),
                        flyTo.az(),
                        flyTo.maxVelocity(),
                        flyTo.radiusReached());
            case GO_HOME:
                return new GoHomeUpdate(task.uuid(),task.error().value(),task.status().value());
                default:
                    return null;
        }
    }
}
