package com.example.abstractcontroller.tasks.flight;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Einav on 21/11/2017.
 */

public class FlyToUsingDTMAbstract extends RunnableDroneTask<FlightTaskType> implements FlyToUsingDTM {

    private final AbstractDroneController controller;
    private final Location location;
    private final Double az;
    private final double agl;
    private final double underGrapInMeter;
    private final double upperGapInMeter;
    private final Double maxVelocity;
    private final Double radiusReached;

    private Removable telemetryObserver = Removable.STUB;
    private Removable aglObserver = Removable.STUB;

    public FlyToUsingDTMAbstract(AbstractDroneController controller, Location location, Double az, double agl, double underGrapInMeter, double upperGapInMeter, Double maxVelocity, Double radiusReached) {
        this.controller = controller;
        this.location = location;
        this.az = az;
        this.agl = agl;
        this.underGrapInMeter = underGrapInMeter;
        this.upperGapInMeter = upperGapInMeter;
        this.maxVelocity = maxVelocity;
        this.radiusReached = radiusReached;
    }

    @Override
    public Double az() {
        return az;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public double agl() {
        return agl;
    }

    @Override
    public double underGapInMeter() {
        return underGrapInMeter;
    }

    @Override
    public double upperGapInMeter() {
        return upperGapInMeter;
    }

    @Override
    public Double maxVelocity() {
        return maxVelocity;
    }
    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.FLY_TO_USING_DTM;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        final CountDownLatch taskLatch = new CountDownLatch(1);

        aglObserver = controller.aboveGroundAltitude().observe(new Observer<Double>() {
            @Override
            public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                if(newValue == null){
                    cancel();
                }
            }
        }).observeCurrentValue();

        telemetryObserver = controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                if(newValue == null || newValue.location() == null){
                    return;
                }

                Location currentLocation = newValue.location();

                if (location.distance(currentLocation) < radiusReached) {
                    telemetryObserver.remove();
                    telemetryObserver = Removable.STUB;
                    taskLatch.countDown();
                }
            }
        }).observeCurrentValue();

        taskLatch.await();
    }

    @Override
    public Double radiusReached() {
        return radiusReached;
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        telemetryObserver.remove();
        aglObserver.remove();
    }
}
