package eyesatop.controller.djinew.tasks.flight;

import java.util.concurrent.CountDownLatch;

import dji.sdk.flightcontroller.FlightController;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.functions.TelemetryLocation;
import eyesatop.controller.math.Geodetic;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.LandAtLandingPad;
import eyesatop.landingpad.Landing;
import eyesatop.landingpad.LandingPad;
import eyesatop.util.Predicate;
import eyesatop.util.Removable;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.ControlAxes;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

public class DjiLandAtLandingPad extends RunnableDroneTask<FlightTaskType> implements LandAtLandingPad {

    private final ControllerDjiNew droneController;
    private final LandingPad landingPad;

    private Removable landingObserver;
    private Removable gotoLandingPadObserver;
    private Landing landing;
    private Geodetic math = new Geodetic();

    public DjiLandAtLandingPad(
            ControllerDjiNew droneController,
            LandingPad landingPad) {
        this.droneController = droneController;
        this.landingPad = landingPad;
    }

    public FlightController getDjiFlightController(){
        return droneController.getHardwareManager().getDjiFlightController();
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException,InterruptedException {

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "Starting Landing task.");

        if (getDjiFlightController() == null || getDjiFlightController().isConnected()) {
            MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "LandAtLandingPad: aborting since drone is not connected");
        }

        landing = landingPad.requestLanding();
        landing.telemetry().bind(droneController.telemetry());

        final Location landingPadLocation = landingPad.location().value();

        final CountDownLatch goToLandingPadLocationLatch = new CountDownLatch(1);
        gotoLandingPadObserver = droneController.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {

                double distanceFromLandingPadLocation = math.GeoDistance(newValue.location(), landingPadLocation);
                if (distanceFromLandingPadLocation > 5) {
//                    droneController.getMovementManager().microMovementToTarget(landingPadLocation,0);
                } else {
                    observation.remove();
                    goToLandingPadLocationLatch.countDown();
                }
            }
        });

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "Gooing to landing pad location");
        goToLandingPadLocationLatch.await();

        landingObserver = landing.controlAxes().observe(new Observer<ControlAxes>() {
            @Override
            public void observe(ControlAxes oldValue, ControlAxes newValue, Observation<ControlAxes> observation) {
                if (newValue != null) {
                    MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "Got new control axes: " + newValue.toString());
                    droneController.flightTasks().sendSpeedsCommand(newValue.getPitch(), newValue.getRoll(), newValue.getYaw(), newValue.getZ());
                }
            }
        });

        droneController.telemetry().transform(new TelemetryLocation()).await(new Predicate<Location>() {
            @Override
            public boolean test(Location subject) {
                return subject.getAltitude() < 0.3;
            }
        });

        landing.close();
        landingObserver.remove();

        while (droneController.motorsOn().value()) {
            droneController.flightTasks().sendSpeedsCommand(0, 0, 0, -0.7);
            Thread.sleep(100);
        }
    }

    protected void cleanUp(TaskStatus exitStatus) throws Exception {

        if(landingObserver != null) {
            landingObserver.remove();
        }
        if(landing != null) {
            landing.close();
            landing = null;
        }

        if(gotoLandingPadObserver != null){
            gotoLandingPadObserver.remove();
        }
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.LAND_IN_LANDING_PAD;
    }

    @Override
    public LandingPad landingPad() {
        return landingPad;
    }
}
