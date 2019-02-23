package eyesatop.controller.mock.tasks.flight;

import java.util.List;
import java.util.UUID;

import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FollowNavPlan;

public class MockFollowNavPlan extends MockDroneTask<FlightTaskType> implements FollowNavPlan{

    private final List<NavPlanPoint> navPlanPoints;

    public MockFollowNavPlan(UUID uuid, List<NavPlanPoint> navPlanPoints) {
        super(uuid, FlightTaskType.FOLLOW_NAV_PLAN);
        this.navPlanPoints = navPlanPoints;
    }

    @Override
    public List<NavPlanPoint> navPlanPoints() {
        return navPlanPoints;
    }
}
