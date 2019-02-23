package eyesatop.controller.tasks.flight;

import java.util.List;

import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

public interface FollowNavPlan extends DroneTask<FlightTaskType> {

    List<NavPlanPoint> navPlanPoints();

    public class FollowNavPlanStub extends StubDroneTask<FlightTaskType> implements FollowNavPlan {

        private final List<NavPlanPoint> navPlanPoints;

        public FollowNavPlanStub(List<NavPlanPoint> navPlanPoints) {
            this.navPlanPoints = navPlanPoints;
        }

        @Override
        public List<NavPlanPoint> navPlanPoints() {
            return navPlanPoints;
        }

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.FOLLOW_NAV_PLAN;
        }
    }
}
