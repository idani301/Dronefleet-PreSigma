package eyesatop.controllersimulatornew.components;

import com.example.abstractcontroller.components.AbstractDroneFlightTasks;
import com.example.abstractcontroller.components.ComponentConnectivityType;
import com.example.abstractcontroller.tasks.flight.FlyInCircleAbstract;
import com.example.abstractcontroller.tasks.flight.FlyToAbstract;
import com.example.abstractcontroller.tasks.flight.FlyToSafeAndFastAbstract;
import com.example.abstractcontroller.tasks.flight.FlyToUsingDTMAbstract;
import com.example.abstractcontroller.tasks.flight.FollowNavPlanAbstract;
import com.example.abstractcontroller.tasks.flight.HoverAbstract;
import com.example.abstractcontroller.tasks.flight.RotateHeadingAbstract;
import com.example.abstractcontroller.tasks.flight.TakeOffAbstract;

import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.controller.tasks.flight.FollowNavPlan;
import eyesatop.controller.tasks.flight.Hover;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.controllersimulatornew.TelemetrySimulator;
import eyesatop.controllersimulatornew.tasks.flight.GoHomeSimulator;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 29/08/2017.
 */

public class DroneFlightTasksSimulator extends AbstractDroneFlightTasks {

    private final ControllerSimulator controller;
    private final TelemetrySimulator telemetrySimulator;

    public DroneFlightTasksSimulator(ControllerSimulator controller) {
        this.controller = controller;

        getSticksEnabled().set(true);

        telemetrySimulator = new TelemetrySimulator(telemetry());
        try {
            telemetrySimulator.start();
        } catch (DroneTaskException e) {
            e.printStackTrace();
        }
        getConnectivity().observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                switch (newValue){

                    case NULL:
                        telemetrySimulator.simulatorsTelemetry().set(null);
                        break;
                    case NOT_CONNECTED:
                        telemetrySimulator.simulatorsTelemetry().set(null);
                        break;
                    case CONNECTED:
                        break;
                }
            }
        });
    }

    @Override
    protected RunnableDroneTask<FlightTaskType> stubToRunnable(StubDroneTask<FlightTaskType> stubDroneTask) throws DroneTaskException {

        switch (stubDroneTask.taskType()){

            case FLY_SAFE_TO:
                FlyToSafeAndFast flyToSafeAndFast = (FlyToSafeAndFast)stubDroneTask;
                return new FlyToSafeAndFastAbstract(controller,flyToSafeAndFast.targetLocation(),flyToSafeAndFast.altitudeInfo());
            case FLY_IN_CIRCLE:
                FlyInCircle.FlyInCircleStub flyInCircleStub = (FlyInCircle.FlyInCircleStub) stubDroneTask;
                return new FlyInCircleAbstract(controller,
                        flyInCircleStub.center(),
                        flyInCircleStub.radius(),
                        flyInCircleStub.rotationType(),
                        flyInCircleStub.degreesToCover(),
                        flyInCircleStub.startingDegree(),
                        flyInCircleStub.altitudeInfo(),
                        flyInCircleStub.velocity());
            case TAKE_OFF:
                return new TakeOffAbstract(controller,((TakeOff)stubDroneTask).altitude());
            case GOTO_POINT:
                FlyTo flyToTask = (FlyTo)stubDroneTask;
                return new FlyToAbstract(controller,flyToTask.location(),flyToTask.altitudeInfo(), flyToTask.az(), flyToTask.maxVelocity(), flyToTask.radiusReached());
            case GO_HOME:
                return new GoHomeSimulator(controller);
            case FLY_TO_USING_DTM:
                FlyToUsingDTM flyToUsingDTM = (FlyToUsingDTM)stubDroneTask;
                return new FlyToUsingDTMAbstract(controller,
                        flyToUsingDTM.location(),
                        flyToUsingDTM.az(),
                        flyToUsingDTM.agl(),
                        flyToUsingDTM.underGapInMeter(),
                        flyToUsingDTM.upperGapInMeter(),
                        flyToUsingDTM.maxVelocity(),
                        flyToUsingDTM.radiusReached());
            case HOVER:
                Hover hoverTask = (Hover)stubDroneTask;
                return new HoverAbstract(hoverTask.hoverTime());
            case FOLLOW_NAV_PLAN:
                FollowNavPlan followNavPlan = (FollowNavPlan)stubDroneTask;
                return new FollowNavPlanAbstract(controller,followNavPlan.navPlanPoints());
            case ROTATE_HEADING:
                RotateHeading rotateHeading = (RotateHeading)stubDroneTask;
                return new RotateHeadingAbstract(controller,rotateHeading.angle());
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

    @Override
    public void sendSpeedsCommand(double pitch, double roll, double yaw, double vertical) {
        telemetrySimulator.sendSpeedCommand(pitch,roll,yaw,vertical);
    }

    public TelemetrySimulator getTelemetrySimulator() {
        return telemetrySimulator;
    }

    @Override
    public void internalTakeOff() throws DroneTaskException {

        Location currentDroneLocation = Telemetry.telemetryToLocation(controller.telemetry().value());
        if(currentDroneLocation == null){
            throw new DroneTaskException("We don't know where the drone is");
        }
        controller.droneHome().homeLocation().set(currentDroneLocation);
        controller.flightMode().set(FlightMode.AUTO_TAKE_OFF);
        controller.flying().set(true);
        controller.motorsOn().set(true);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        controller.flightMode().set(FlightMode.APP_CONTROL);
    }

    @Override
    public void confirmLand() throws DroneTaskException {
        confirmLandRequire().setIfNew(false);
    }
}
