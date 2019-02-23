package com.example.abstractcontroller.tasks.flight;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 20/10/2017.
 */

public class FlyToAbstract extends RunnableDroneTask<FlightTaskType> implements FlyTo {

    private final AbstractDroneController controller;
    private final Location location;
    private final AltitudeInfo altitudeInfo;
    private final Double az;
    private final Double maxVelocity;
    private final Double radiusReached;

    private Removable telemetryObserver = Removable.STUB;

    public FlyToAbstract(AbstractDroneController controller, Location location, AltitudeInfo altitudeInfo, Double az, Double maxVelocity, Double radiusReached) {
        this.controller = controller;
        this.location = location;
        this.altitudeInfo = altitudeInfo;
        this.az = az;
        this.maxVelocity = maxVelocity;
        this.radiusReached = radiusReached;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public AltitudeInfo altitudeInfo() {
        return altitudeInfo;
    }

    @Override
    public Double az() {
        return az;
    }

    @Override
    public Double maxVelocity() {
        return maxVelocity;
    }

    @Override
    public Double radiusReached() {
        return radiusReached;
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.GOTO_POINT;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        final CountDownLatch taskLatch = new CountDownLatch(1);

        telemetryObserver = controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {

                if(newValue == null || newValue.location() == null){
                    return;
                }

                Location currentLocation = newValue.location();

                if (location.distance(currentLocation) < radiusReached) {

                    double altDistance = 0;
                    if(!altitudeInfo.isNull()){
                        switch (altitudeInfo.getAltitudeType()){

                            case ABOVE_GROUND_LEVEL:
                                Double currentAboveGroundLevel = controller.aboveGroundAltitude().value();
                                if(currentAboveGroundLevel != null) {
                                    altDistance = Math.abs(currentAboveGroundLevel - altitudeInfo.getValueInMeters());
                                }
                                break;
                            case ABOVE_SEA_LEVEL:
                                Double currentAboveSeaLevel = controller.aboveSeaAltitude().value();
                                if(currentAboveSeaLevel != null) {
                                    altDistance = Math.abs(currentAboveSeaLevel - altitudeInfo.getValueInMeters());
                                }
                                break;
                            case FROM_TAKE_OFF_LOCATION:
                                altDistance = Math.abs(currentLocation.getAltitude() - altitudeInfo.getValueInMeters());
                                break;
                        }
                    }

                    if(altDistance < 1) {
                        telemetryObserver.remove();
                        telemetryObserver = Removable.STUB;
                        taskLatch.countDown();
                    }
                }
            }
        }).observeCurrentValue();

        taskLatch.await();
    }



    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        telemetryObserver.remove();
    }
}
