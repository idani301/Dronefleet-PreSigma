package eyesatop.controllersimulatornew.tasks.home;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by einav on 16/05/2017.
 */

public class SimulatorSetHomeLocationTask extends RunnableDroneTask<HomeTaskType> implements SetHomeLocation {

    private final Location location;
    private final ControllerSimulator controller;

    public SimulatorSetHomeLocationTask(ControllerSimulator controller, Location location) {
        this.location = location;
        this.controller = controller;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(1000);
        controller.droneHome().homeLocation().set(location);
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public HomeTaskType taskType() {
        return HomeTaskType.SET_HOME_LOCATION;
    }
}
