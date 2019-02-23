package eyesatop.controllersimulatornew.tasks.flight;

import java.util.Random;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 17/05/2017.
 */

public class TakeOffSimulator extends RunnableDroneTask<FlightTaskType> implements TakeOff {

    private final ControllerSimulator controller;
    private final double altitude;

    public TakeOffSimulator(ControllerSimulator controller, double altitude) {
        this.controller = controller;
        this.altitude = altitude;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

//        if(!controller.flying().isNull() && controller.flying().value()){
//            throw new DroneTaskException("Drone already flying");
//        }

        if(controller.telemetry().isNull()){
            throw new DroneTaskException("Unknown location for drone");
        }

        Thread.sleep(500);

        Random rand = new Random();
        if(controller.getFailEnable().value() && rand.nextBoolean()){
            throw new DroneTaskException("Random fail");
        }

        controller.motorsOn().set(true);

        Thread.sleep(1500);

        controller.flying().set(true);
        controller.telemetry().set(controller.telemetry().value().location(controller.telemetry().value().location().altitude(altitude)));
        controller.droneHome().setHomeLocation(controller.telemetry().value().location());
    }

    @Override
    public double altitude() {
        return altitude;
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.TAKE_OFF;
    }
}
