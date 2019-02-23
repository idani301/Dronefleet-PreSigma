package eyesatop.controllersimulatornew.tasks.home;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetLimitationEnabled;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 21/07/2017.
 */

public class SetLimitationEnabledSimulator extends RunnableDroneTask<HomeTaskType> implements SetLimitationEnabled {

    private final boolean enabled;
    private final ControllerSimulator controller;

    public SetLimitationEnabledSimulator(boolean enabled, ControllerSimulator controller) {
        this.enabled = enabled;
        this.controller = controller;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public HomeTaskType taskType() {
        return HomeTaskType.SET_LIMITATION_ENABLED;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(1000);
        controller.droneHome().limitationActive().set(enabled);
    }
}
