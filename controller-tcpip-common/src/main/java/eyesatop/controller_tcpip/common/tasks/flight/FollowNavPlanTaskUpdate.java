package eyesatop.controller_tcpip.common.tasks.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.flight.MockFollowNavPlan;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;

public class FollowNavPlanTaskUpdate extends FlightTaskUpdate {

    private static final String NAV_PLAN_POINTS = "navPlanPoints";

    private final List<NavPlanPoint> navPlanPoints;

    @JsonCreator
    public FollowNavPlanTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                   @JsonProperty(ERROR) DroneTaskException error,
                                   @JsonProperty(STATUS) TaskStatus status,
                                   @JsonProperty(NAV_PLAN_POINTS) List<NavPlanPoint> navPlanPoints) {
        super(uuid, error, status);
        this.navPlanPoints = navPlanPoints;
    }

    @JsonProperty(NAV_PLAN_POINTS)
    public List<NavPlanPoint> getNavPlanPoints() {
        return navPlanPoints;
    }

    @Override
    @JsonIgnore
    protected MockDroneTask<FlightTaskType> createNewMockTask() {
        return new MockFollowNavPlan(getUuid(),navPlanPoints);
    }
}
