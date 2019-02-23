package eyesatop.controllersimulatornew.components;

import com.example.abstractcontroller.components.AbstractDroneHome;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import eyesatop.controller.tasks.home.SetLimitationEnabled;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.controllersimulatornew.tasks.home.SetLimitationEnabledSimulator;
import eyesatop.controllersimulatornew.tasks.home.SetMaxAltitudeFromHomeLocationSimulator;
import eyesatop.controllersimulatornew.tasks.home.SetMaxDistanceFromHomeSimulator;
import eyesatop.controllersimulatornew.tasks.home.SetReturnHomeAltitudeSimulator;
import eyesatop.controllersimulatornew.tasks.home.SimulatorSetHomeLocationTask;

/**
 * Created by Idan on 29/08/2017.
 */

public class DroneHomeSimulator extends AbstractDroneHome {

    private final ControllerSimulator controller;

    public DroneHomeSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    protected RunnableDroneTask<HomeTaskType> stubToRunnable(StubDroneTask<HomeTaskType> stubDroneTask) throws DroneTaskException {

        switch (stubDroneTask.taskType()){

            case SET_HOME_LOCATION:
                return new SimulatorSetHomeLocationTask(controller,((SetHomeLocation)stubDroneTask).location());
            case SET_LIMITATION_ENABLED:
                return new SetLimitationEnabledSimulator(((SetLimitationEnabled)stubDroneTask).enabled(),controller);
            case SET_MAX_DISTANCE_FROM_HOME:
                return new SetMaxDistanceFromHomeSimulator(((SetMaxDistanceFromHome)stubDroneTask).maxDistanceFromHome(),controller);
            case SET_MAX_ALT_FROM_TAKE_OFF_ALT:
                return new SetMaxAltitudeFromHomeLocationSimulator(((SetMaxAltitudeFromHomeLocation)stubDroneTask).altitude(),controller);
            case SET_RETURN_HOME_ALT:
                return new SetReturnHomeAltitudeSimulator(controller,((SetReturnHomeAltitude)stubDroneTask).altitude());
            default:
                throw new DroneTaskException("Didn't implement stubToRunnable for : " + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {

    }

    @Override
    public void onComponentConnected() {

    }
}
