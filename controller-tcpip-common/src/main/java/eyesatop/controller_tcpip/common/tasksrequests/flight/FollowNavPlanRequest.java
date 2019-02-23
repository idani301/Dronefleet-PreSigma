package eyesatop.controller_tcpip.common.tasksrequests.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class FollowNavPlanRequest implements FlightTaskRequest {

    private static final String NAV_PLAN_POINTS = "navPlanPoints";

    private final List<NavPlanPoint> navPlanPoints;

    @JsonCreator
    public FollowNavPlanRequest(@JsonProperty(NAV_PLAN_POINTS)List<NavPlanPoint> navPlanPoints) {
        this.navPlanPoints = navPlanPoints;
    }

    @JsonProperty(NAV_PLAN_POINTS)
    public List<NavPlanPoint> getNavPlanPoints() {
        return navPlanPoints;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {
        try {
            DroneTask newTask = controller.flightTasks().followNavPlan(navPlanPoints);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
