package com.example.abstractcontroller.tasks.flight;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.util.Removable;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 20/10/2017.
 */

public class TakeOffAbstract extends RunnableDroneTask<FlightTaskType> implements TakeOff {

    private Removable telemetryObserver = Removable.STUB;
    private final AbstractDroneController controller;
    private final double altitude;

    public TakeOffAbstract(AbstractDroneController controller, double altitude) {
        this.controller = controller;
        this.altitude = altitude;
    }

    @Override
    public double altitude() {
        return altitude;
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.TAKE_OFF;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Boolean isFlying = controller.flying().value();

        if(isFlying != null && isFlying){
            throw new DroneTaskException("Drone Already Flying");
        }

        final CountDownLatch taskLatch = new CountDownLatch(1);
        telemetryObserver = controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                if(newValue == null || newValue.location() == null || newValue.velocities() == null){
                    return;
                }

                if(Math.abs(newValue.location().getAltitude() - altitude) < 1 && Math.abs(newValue.velocities().getZ()) < 0.7){
                    telemetryObserver.remove();
                    telemetryObserver = Removable.STUB;
                    taskLatch.countDown();
                }
            }
        }).observeCurrentValue();

        if(isFlying == null || !isFlying) {
            controller.flightTasks().internalTakeOff();
        }

        taskLatch.await();
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        telemetryObserver.remove();
    }
}
