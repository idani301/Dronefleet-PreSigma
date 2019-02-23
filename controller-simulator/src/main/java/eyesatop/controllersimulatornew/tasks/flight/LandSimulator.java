package eyesatop.controllersimulatornew.tasks.flight;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.LandInPlace;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.Velocities;
import eyesatop.util.model.Property;

public class LandSimulator extends RunnableDroneTask<FlightTaskType> implements LandInPlace {

    private final ControllerSimulator controller;

    public LandSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Location droneLocation = Telemetry.telemetryToLocation(controller.telemetry().value());

        if(droneLocation == null) {
            throw new DroneTaskException("Unknown drone location, can't land");
        }

        Thread.sleep(3000);

//        Random rand = new Random();
//        if(controller.getFailEnable().value() && rand.nextBoolean()){
//            throw new DroneTaskException("Random fail");
//        }

        controller.flightTasks().getTelemetrySimulator().simulatorsTelemetry().set(new Telemetry(new Location(droneLocation.getLatitude(),droneLocation.getLongitude(),0),new Velocities(0,0,0),controller.telemetry().value().heading()));
        controller.flying().set(false);
        controller.motorsOn().set(false);
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.LAND_IN_PLACE;
    }
}
