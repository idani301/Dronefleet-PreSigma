package com.example.abstractcontroller.tasks.flight;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.AltitudeType;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 10/05/2018.
 */

public class FlyToSafeAndFastAbstract extends RunnableDroneTask<FlightTaskType> implements FlyToSafeAndFast {

    public enum FlySafeOperationType {
        NONE,
        RAISE_AT_START_LOCATION,
        FLY_TO_USING_AZ,
        LOWER_TO_TARGET;
    }

    private final AbstractDroneController controller;
    private final Location targetLocation;
    private final AltitudeInfo altitudeInfo;

    private final Property<FlySafeOperationType> operationType = new Property<>(FlySafeOperationType.NONE);
    private final Property<AltitudeInfo> raiseAtStartLocationAltitudeInfo = new Property<>();
    private final Property<Double> azForFlyToTarget = new Property<>();

    private RemovableCollection telemetryObserver = new RemovableCollection();

    public FlyToSafeAndFastAbstract(AbstractDroneController controller, Location targetLocation, AltitudeInfo altitudeInfo) {
        this.controller = controller;
        this.targetLocation = targetLocation;
        this.altitudeInfo = altitudeInfo;
    }

    @Override
    public Location targetLocation() {
        return targetLocation;
    }

    @Override
    public AltitudeInfo altitudeInfo() {
        return altitudeInfo;
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.FLY_SAFE_TO;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Telemetry startTelemetry = controller.telemetry().value();
        Location startLocation = startTelemetry == null ? null : startTelemetry.location();

        if(startTelemetry == null || startTelemetry.location() == null){
            throw new DroneTaskException("Unknown startLocation");
        }

        double startAz = startTelemetry.location().az(targetLocation);
        double distanceToTarget = startLocation.distance(targetLocation);

        Double startAboveSeaLevel = controller.aboveSeaAltitude().value();
        try {

            Double targetDTM = null;

            if(startAboveSeaLevel == null){
                throw new TerrainNotFoundException();
            }

            targetDTM = controller.getDtmProvider().terrainAltitude(targetLocation);

            double highestDTMToTarget = Math.max(controller.getDtmProvider().terrainAltitude(startLocation),targetDTM);

            for(int i=1; i < distanceToTarget/controller.getDtmProvider().stepDistanceInMeters();i++){
                double tempDTM = controller.getDtmProvider().terrainAltitude(startLocation.getLocationFromAzAndDistance(i*controller.getDtmProvider().stepDistanceInMeters(),startAz));
                highestDTMToTarget = Math.max(tempDTM,highestDTMToTarget);
            }

            Double aboveSeaLevelTarget = null;

            switch (altitudeInfo.getAltitudeType()){

                case ABOVE_GROUND_LEVEL:
                    aboveSeaLevelTarget = targetDTM + altitudeInfo.getValueInMeters();
                    break;
                case ABOVE_SEA_LEVEL:
                    aboveSeaLevelTarget = altitudeInfo.getValueInMeters();
                    break;
                case FROM_TAKE_OFF_LOCATION:
                    Double takeOffDTM = controller.droneHome().takeOffDTM().value();
                    if(takeOffDTM == null){
                        throw new DroneTaskException("Unknown takeoff DTM");
                    }
                    aboveSeaLevelTarget = altitudeInfo.getValueInMeters() + controller.droneHome().takeOffDTM().value();
                    break;
            }

            double minimalAltitudeFromHighestObstalce = Math.min(MIN_AGL,distanceToTarget/4);
            double startAltitudeASL = Math.max(highestDTMToTarget + minimalAltitudeFromHighestObstalce,aboveSeaLevelTarget);
            startAltitudeASL = Math.max(startAltitudeASL,startAboveSeaLevel);
            this.raiseAtStartLocationAltitudeInfo.set(new AltitudeInfo(AltitudeType.ABOVE_SEA_LEVEL,startAltitudeASL));

            final double finalStartAltitudeASL = startAltitudeASL;
            final CountDownLatch raiseLatch = new CountDownLatch(1);
            telemetryObserver.add(controller.aboveSeaAltitude().observe(new Observer<Double>() {
                @Override
                public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                    if(newValue != null && Math.abs(newValue - finalStartAltitudeASL) < 1){
                        observation.remove();
                        raiseLatch.countDown();
                    }
                }
            }).observeCurrentValue());

            operationType.set(FlySafeOperationType.RAISE_AT_START_LOCATION);
            raiseLatch.await();

            final CountDownLatch reachTargetLatch = new CountDownLatch(1);
            telemetryObserver.add(controller.telemetry().observe(new Observer<Telemetry>() {
                @Override
                public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                    Location currentLocation = Telemetry.telemetryToLocation(newValue);
                    if(currentLocation == null){
                        return;
                    }

                    if(currentLocation.distance(targetLocation) < 3){
                        observation.remove();
                        reachTargetLatch.countDown();
                    }

                }
            }).observeCurrentValue());

            azForFlyToTarget.set(startAz);
            operationType.set(FlySafeOperationType.FLY_TO_USING_AZ);
            reachTargetLatch.await();

            final CountDownLatch lowerAltitudeLatch = new CountDownLatch(1);
            switch (altitudeInfo.getAltitudeType()){

                case ABOVE_GROUND_LEVEL:
                    telemetryObserver.add(controller.aboveGroundAltitude().observe(new Observer<Double>() {
                        @Override
                        public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                            if(newValue != null && Math.abs(newValue - altitudeInfo.getValueInMeters()) < 0.5){
                                observation.remove();
                                lowerAltitudeLatch.countDown();
                            }
                        }
                    }).observeCurrentValue());
                    break;
                case ABOVE_SEA_LEVEL:
                    telemetryObserver.add(controller.aboveSeaAltitude().observe(new Observer<Double>() {
                        @Override
                        public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                            if(newValue != null && Math.abs(newValue - altitudeInfo.getValueInMeters()) < 0.5){
                                observation.remove();
                                lowerAltitudeLatch.countDown();
                            }
                        }
                    }).observeCurrentValue());
                    break;
                case FROM_TAKE_OFF_LOCATION:
                    telemetryObserver.add(controller.telemetry().observe(new Observer<Telemetry>() {
                        @Override
                        public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                            Location currentLocation = Telemetry.telemetryToLocation(newValue);
                            if(currentLocation != null && Math.abs(currentLocation.getAltitude() - altitudeInfo.getValueInMeters()) < 0.5){
                                observation.remove();
                                lowerAltitudeLatch.countDown();
                            }
                        }
                    }).observeCurrentValue());
                    break;
            }
            operationType.set(FlySafeOperationType.LOWER_TO_TARGET);
            lowerAltitudeLatch.await();
        } catch (TerrainNotFoundException e) {

            final CountDownLatch raiseLatch = new CountDownLatch(1);
            switch (altitudeInfo.getAltitudeType()){

                case ABOVE_GROUND_LEVEL:
                    throw new DroneTaskException("Unable to perform fly to safe AGL value without DTM");
                case ABOVE_SEA_LEVEL:
                    if(startAboveSeaLevel == null){
                        throw new DroneTaskException("Unable to perform fly to safe ASL value without DTM or DTK");
                    }
                    final double raiseTargetASL = Math.max(altitudeInfo.getValueInMeters(),startAboveSeaLevel);
                    raiseAtStartLocationAltitudeInfo.set(new AltitudeInfo(AltitudeType.ABOVE_SEA_LEVEL,raiseTargetASL));
                    telemetryObserver.add(controller.aboveSeaAltitude().observe(new Observer<Double>() {
                        @Override
                        public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                            if(newValue == null){
                                cancel();
                            }
                            else{
                                if(Math.abs(newValue - raiseTargetASL) < 0.5){
                                    observation.remove();
                                    raiseLatch.countDown();
                                }
                            }
                        }
                    }).observeCurrentValue());
                    break;
                case FROM_TAKE_OFF_LOCATION:
                    final double targetATL = Math.max(altitudeInfo.getValueInMeters(),startLocation.getAltitude());
                    raiseAtStartLocationAltitudeInfo.set(new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION,targetATL));
                    telemetryObserver.add(controller.telemetry().observe(new Observer<Telemetry>() {
                        @Override
                        public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                            Location currentLocation = Telemetry.telemetryToLocation(newValue);
                            if(currentLocation != null && Math.abs(currentLocation.getAltitude() - targetATL) < 0.5){
                                observation.remove();
                                raiseLatch.countDown();
                            }
                        }
                    }).observeCurrentValue());
                    break;
            }
            operationType.set(FlySafeOperationType.RAISE_AT_START_LOCATION);
            raiseLatch.await();

            final CountDownLatch reachTargetLatch = new CountDownLatch(1);
            telemetryObserver.add(controller.telemetry().observe(new Observer<Telemetry>() {
                @Override
                public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                    Location currentLocation = Telemetry.telemetryToLocation(newValue);
                    if(currentLocation == null){
                        return;
                    }

                    if(currentLocation.distance(targetLocation) < 3){
                        observation.remove();
                        reachTargetLatch.countDown();
                    }

                }
            }).observeCurrentValue());
            azForFlyToTarget.set(startAz);
            operationType.set(FlySafeOperationType.FLY_TO_USING_AZ);
            reachTargetLatch.await();

            final CountDownLatch lowerAltitudeLatch = new CountDownLatch(1);
            switch (altitudeInfo.getAltitudeType()){

                case ABOVE_GROUND_LEVEL:
                    throw new DroneTaskException("Unable to perform this action without DTM");
                case ABOVE_SEA_LEVEL:
                    telemetryObserver.add(controller.aboveSeaAltitude().observe(new Observer<Double>() {
                        @Override
                        public void observe(Double oldValue, Double newValue, Observation<Double> observation) {

                            if(newValue == null){
                                cancel();
                            }
                            else {
                                if(Math.abs(newValue - altitudeInfo.getValueInMeters()) < 0.5){
                                    observation.remove();
                                    lowerAltitudeLatch.countDown();
                                }
                            }
                        }
                    }).observeCurrentValue());
                    break;
                case FROM_TAKE_OFF_LOCATION:
                    telemetryObserver.add(controller.telemetry().observe(new Observer<Telemetry>() {
                        @Override
                        public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                            Location currentLocation = Telemetry.telemetryToLocation(newValue);
                            if(currentLocation != null && Math.abs(currentLocation.getAltitude() - altitudeInfo.getValueInMeters()) < 0.5){
                                observation.remove();
                                lowerAltitudeLatch.countDown();
                            }
                        }
                    }).observeCurrentValue());
                    break;
            }
            operationType.set(FlySafeOperationType.LOWER_TO_TARGET);
            lowerAltitudeLatch.await();
        }
    }

    public Property<FlySafeOperationType> getOperationType() {
        return operationType;
    }

    public Property<AltitudeInfo> getRaiseAtStartLocationAltitudeInfo() {
        return raiseAtStartLocationAltitudeInfo;
    }

    public Property<Double> getAzForFlyToTarget() {
        return azForFlyToTarget;
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        super.cleanUp(exitStatus);
    }
}
