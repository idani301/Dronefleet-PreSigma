package eyesatop.controllersimulatornew.tasks.home;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 21/07/2017.
 */

public class SetMaxDistanceFromHomeSimulator extends RunnableDroneTask<HomeTaskType> implements SetMaxDistanceFromHome {

    private final double maxDistance;
    private final ControllerSimulator controller;

    public SetMaxDistanceFromHomeSimulator(double maxDistance, ControllerSimulator controllerSimulator) {
        this.maxDistance = maxDistance;
        this.controller = controllerSimulator;
    }

    @Override
    public double maxDistanceFromHome() {
        return maxDistance;
    }

    @Override
    public HomeTaskType taskType() {
        return HomeTaskType.SET_MAX_DISTANCE_FROM_HOME;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(1000);
        controller.droneHome().maxDistanceFromHome().set(maxDistance);
    }
}
