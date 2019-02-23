package eyesatop.controllersimulatornew.tasks.home;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 22/07/2017.
 */

public class SetReturnHomeAltitudeSimulator extends RunnableDroneTask<HomeTaskType> implements SetReturnHomeAltitude {

    private final ControllerSimulator controller;
    private final double altitude;

    public SetReturnHomeAltitudeSimulator(ControllerSimulator controller, double altitude) {
        this.controller = controller;
        this.altitude = altitude;
    }


    @Override
    public double altitude() {
        return altitude;
    }

    @Override
    public HomeTaskType taskType() {
        return HomeTaskType.SET_RETURN_HOME_ALT;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(1000);
        controller.droneHome().returnHomeAltitude().set(altitude);
    }
}
