package eyesatop.controllersimulatornew.tasks.home;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 21/07/2017.
 */

public class SetMaxAltitudeFromHomeLocationSimulator extends RunnableDroneTask<HomeTaskType> implements SetMaxAltitudeFromHomeLocation {

    private final double maxAltitude;
    private final ControllerSimulator controller;

    public SetMaxAltitudeFromHomeLocationSimulator(double maxAltitude, ControllerSimulator controller) {
        this.maxAltitude = maxAltitude;
        this.controller = controller;
    }

    @Override
    public double altitude() {
        return maxAltitude;
    }

    @Override
    public HomeTaskType taskType() {
        return HomeTaskType.SET_MAX_ALT_FROM_TAKE_OFF_ALT;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(1000);
        controller.droneHome().maxAltitudeFromTakeOffLocation().set(maxAltitude);
    }
}
