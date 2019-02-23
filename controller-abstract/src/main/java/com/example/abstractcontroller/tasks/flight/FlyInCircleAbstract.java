package com.example.abstractcontroller.tasks.flight;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.util.Removable;
import logs.LoggerTypes;
//import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 20/10/2017.
 */

public class FlyInCircleAbstract extends RunnableDroneTask<FlightTaskType> implements FlyInCircle {

    private final AbstractDroneController controller;
    private Removable telemetryObserver = Removable.STUB;
    private Removable circleObserver = Removable.STUB;

    private final Location center;
    private final double radius;
    private final RotationType rotationType;
    private final double degreesToCover;
    private final double startingDegree;
    private final AltitudeInfo altitudeInfo;
    private final double velocity;

    private OperationType operationType = OperationType.UNKNOWN;
    private Location startingDegreeLocation;

    public enum OperationType{
        FLY_TO_CIRCLE,
        FLY_IN_CIRCLE,
        UNKNOWN;
    }

    public FlyInCircleAbstract(AbstractDroneController controller, Location center, double radius, RotationType rotationType, double degreesToCover, double startingDegree, AltitudeInfo altitudeInfo, double velocity) {
        this.controller = controller;
        this.center = center;
        this.radius = radius;
        this.rotationType = rotationType;
        this.degreesToCover = degreesToCover;
        this.startingDegree = startingDegree;
        this.altitudeInfo = altitudeInfo;
        this.velocity = velocity;
    }

    @Override
    public Location center() {
        return center;
    }

    @Override
    public double radius() {
        return radius;
    }

    @Override
    public RotationType rotationType() {
        return rotationType;
    }

    @Override
    public double degreesToCover() {
        return degreesToCover;
    }

    @Override
    public double startingDegree() {
        return startingDegree;
    }

    @Override
    public AltitudeInfo altitudeInfo() {
        return altitudeInfo;
    }

    @Override
    public double velocity() {
        return velocity;
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.FLY_IN_CIRCLE;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public Location getStartingDegreeLocation() {
        return startingDegreeLocation;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Location currentLocation = Telemetry.telemetryToLocation(controller.telemetry().value());
        if(currentLocation == null){
            throw new DroneTaskException("Unknown controller position");
        }

        double startingDegree;
        if(this.startingDegree != -1){
            startingDegree = this.startingDegree;
        }
        else{
            startingDegree = center.az(currentLocation);
        }
        startingDegreeLocation = center.getLocationFromAzAndDistance(radius,startingDegree);

//        final CountDownLatch flyToLatch = new CountDownLatch(1);
//
//        operationType = OperationType.FLY_TO_CIRCLE;
//
//        telemetryObserver = controller.telemetry().observe(new Observer<Telemetry>() {
//            @Override
//            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
//                if(newValue == null || newValue.location() == null){
//                    return;
//                }
//
//                Location currentLocation = newValue.location();
//                Double altDistance = null;
//
//                switch (altitudeInfo.getAltitudeType()){
//
//                    case ABOVE_GROUND_LEVEL:
//                        Double aboveGroundAltitude = controller.aboveGroundAltitude().value();
//                        if(aboveGroundAltitude != null){
//                            altDistance = Math.abs(altitudeInfo.getValueInMeters() - aboveGroundAltitude);
//                        }
//                        break;
//                    case ABOVE_SEA_LEVEL:
//                        Double aboveSeaAltitude = controller.aboveSeaAltitude().value();
//                        if(aboveSeaAltitude != null){
//                            altDistance = Math.abs(altitudeInfo.getValueInMeters() - aboveSeaAltitude);
//                        }
//                        break;
//                    case FROM_TAKE_OFF_LOCATION:
//                        altDistance = Math.abs(altitudeInfo.getValueInMeters() - newValue.location().getAltitude());
//                        break;
//                }
//
//                if (startingDegreeLocation.distance(currentLocation) < 3 && altDistance != null && altDistance < 1) {
//                    telemetryObserver.remove();
//                    telemetryObserver = Removable.STUB;
//                    flyToLatch.countDown();
//                }
//            }
//        }).observeCurrentValue();
//
//        flyToLatch.await();

        operationType = OperationType.FLY_IN_CIRCLE;

        final Property<Double> degreesCovered = new Property<Double>(0D);

        final CountDownLatch flyInCircleLatch = new CountDownLatch(1);

        circleObserver = controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {

//                MainLogger.logger.write_message(LoggerTypes.FLY_IN_CIRCLE,"Fly in circle Got Telemetry, so far degrees covered : " + degreesCovered.value());

                if(oldValue == null || oldValue.location() == null || newValue == null || newValue.location() == null){
//                    MainLogger.logger.write_message(LoggerTypes.FLY_IN_CIRCLE,"Fly in circle Got Telemetry, won't calc this step since something null" );
                    return;
                }

                double degreesDifference = 0;
                try {
                    degreesDifference = Location.degreesCovered(center.az(oldValue.location()), center.az(newValue.location()));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
//                    MainLogger.logger.write_message(LoggerTypes.ERROR,"Error inside degrees covered : " +
//                            oldValue.toString() + "," + newValue.toString() +
//                            MainLogger.TAB + "Circle Center : " + center +
//                            MainLogger.TAB + "Center az to old value : " + center.az(oldValue.location()) +
//                            MainLogger.TAB + "Center az to new value : " + center.az(newValue.location()));
                }
                degreesCovered.set(degreesCovered.value() + degreesDifference);

//                MainLogger.logger.write_message(LoggerTypes.FLY_IN_CIRCLE,"Calc Step : " +
//                                MainLogger.TAB + "Degrees covered this step : " + degreesDifference +
//                        MainLogger.TAB + "Degrees covered until now : " + degreesCovered.value());

                if (Math.abs(degreesCovered.value()) - degreesToCover > 0) {

//                    MainLogger.logger.write_message(LoggerTypes.FLY_IN_CIRCLE,"Fly in circle Done." );
                    telemetryObserver.remove();
                    telemetryObserver = Removable.STUB;
                    flyInCircleLatch.countDown();
                    return;
                }
            }
        });
        flyInCircleLatch.await();
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
//        MainLogger.logger.write_message(LoggerTypes.FLY_IN_CIRCLE,"Fly in circle Clean up" );
        telemetryObserver.remove();
        circleObserver.remove();
    }
}
