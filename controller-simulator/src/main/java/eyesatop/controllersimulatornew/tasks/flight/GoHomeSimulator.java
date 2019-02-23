package eyesatop.controllersimulatornew.tasks.flight;

import java.util.Random;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.GoHome;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.Velocities;
import eyesatop.util.model.Property;

/**
 * Created by einav on 17/05/2017.
 */

public class GoHomeSimulator extends RunnableDroneTask<FlightTaskType> implements GoHome {

    private final ControllerSimulator controller;

    public GoHomeSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Boolean isControllerFlying = controller.flying().value();
        if(isControllerFlying == null){
            throw new DroneTaskException("don't know if drone is flying");
        }

        if(!isControllerFlying){
            throw new DroneTaskException("Drone is not flying, Go Home Fail");
        }

        if(controller.droneHome().homeLocation().isNull()){
            throw new DroneTaskException("Unknown home location");
        }
        Location homeLocation = controller.droneHome().homeLocation().value();

        Thread.sleep(2000);

        controller.flightTasks().getTelemetrySimulator().simulatorsTelemetry().set(new Telemetry(new Location(homeLocation.getLatitude(),homeLocation.getLongitude(),0.5),new Velocities(0,0,0),controller.telemetry().value().heading()));

//        controller.flightTasks().confirmLandRequire().setIfNew(true);
//
//        controller.flightTasks().confirmLandRequire().awaitFalse();
//        Thread.sleep(500);

//        Random rand = new Random();
//        if(controller.getFailEnable().value() && rand.nextBoolean()){
//            throw new DroneTaskException("Random fail");
//        }
        controller.flightTasks().getTelemetrySimulator().simulatorsTelemetry().set(new Telemetry(new Location(homeLocation.getLatitude(),homeLocation.getLongitude(),0),new Velocities(0,0,0),controller.telemetry().value().heading()));
        controller.flying().set(false);
        controller.motorsOn().set(false);
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.GO_HOME;
    }
}
