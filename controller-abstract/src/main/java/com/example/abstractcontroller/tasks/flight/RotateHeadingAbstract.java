package com.example.abstractcontroller.tasks.flight;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 09/05/2018.
 */

public class RotateHeadingAbstract extends RunnableDroneTask<FlightTaskType> implements RotateHeading {

    private final AbstractDroneController controller;
    private final double angle;
    RemovableCollection bindings = new RemovableCollection();

    public RotateHeadingAbstract(AbstractDroneController controller, double angle) {
        this.controller = controller;
        this.angle = angle;
    }

    @Override
    public double angle() {
        return angle;
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.ROTATE_HEADING;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        final CountDownLatch taskLatch = new CountDownLatch(1);

        Boolean hasFullGimbalSupport = controller.gimbal().fullGimbalSupported().value();
        if(hasFullGimbalSupport != null && !hasFullGimbalSupport){
            bindings.add(controller.gimbal().currentTask().observe(new Observer<DroneTask<GimbalTaskType>>() {
                @Override
                public void observe(DroneTask<GimbalTaskType> oldValue, DroneTask<GimbalTaskType> newValue, Observation<DroneTask<GimbalTaskType>> observation) {
                    if(newValue != null && !newValue.status().value().isTaskDone()){
                        switch (newValue.taskType()){

                            case LOOK_AT_POINT:
                                cancel();
                                break;
                            case LOCK_LOOK_AT_LOCATION:
                                cancel();
                                break;
                            case LOCK_YAW_AT_LOCATION:
                                cancel();
                                break;
                            case LOCK_TO_FLIGHT_DIRECTION:
                                cancel();
                                break;
                            case ROTATE_GIMBAL:
                                if(((RotateGimbal)newValue).rotationRequest().isYawEnable()){
                                    cancel();
                                }
                                break;
                        }
                    }
                }
            }).observeCurrentValue());
        }

        bindings.add(controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                if(newValue != null && Location.angularDistance(newValue.heading(),angle) <= 1){
                    taskLatch.countDown();
                }
            }
        }));

        taskLatch.await();
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        bindings.remove();
    }
}
