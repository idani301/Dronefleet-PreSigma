package com.example.abstractcontroller.tasks.gimbal;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.GimbalRequest;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.util.Removable;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

/**
 * Created by Idan on 23/10/2017.
 */

public class RotateGimbalAbstract extends RunnableDroneTask<GimbalTaskType> implements RotateGimbal {

    private final AbstractDroneController controller;
    private final GimbalRequest gimbalRequest;
    private final Integer timeoutInSeconds;
    private Removable telemetryObserver = Removable.STUB;

    public RotateGimbalAbstract(AbstractDroneController controller, GimbalRequest gimbalRequest, Integer timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;

        GimbalRequest newGimbalRequest = null;

        if (gimbalRequest.isPitchEnable()) {
            if (gimbalRequest.getGimbalState().getPitch() > 0) {
                newGimbalRequest = new GimbalRequest(gimbalRequest.getGimbalState().pitch(0), true, gimbalRequest.isYawEnable(), gimbalRequest.isRollEnable());
            } else if (gimbalRequest.getGimbalState().getPitch() < -90) {
                newGimbalRequest = new GimbalRequest(gimbalRequest.getGimbalState().pitch(-90), true, gimbalRequest.isYawEnable(), gimbalRequest.isRollEnable());
            }
        }

        if (newGimbalRequest == null) {
            newGimbalRequest = gimbalRequest;
        }

        this.controller = controller;
        this.gimbalRequest = newGimbalRequest;
    }

    @Override
    public GimbalRequest rotationRequest() {
        return gimbalRequest;
    }

    @Override
    public Integer timeoutInSeconds() {
        return timeoutInSeconds;
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.ROTATE_GIMBAL;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final BooleanProperty isSuccess = new BooleanProperty(false);

        GimbalState currentGimbalState = controller.gimbal().gimbalState().value();

        final double pitchReachedAngularDistance;
        final double yawReachedAngularDistance;
        if(currentGimbalState == null){
            pitchReachedAngularDistance = 3;
            yawReachedAngularDistance = 3;
        }
        else{
            double pitchInitialDistance = Location.angularDistance(currentGimbalState.getPitch(),gimbalRequest.getGimbalState().getPitch());
            double yawInitialDistance = Location.angularDistance(currentGimbalState.getYaw(),gimbalRequest.getGimbalState().getYaw());

            pitchReachedAngularDistance = Math.min(pitchInitialDistance /10,1);
            yawReachedAngularDistance = Math.min(yawInitialDistance/10,1);
        }

        telemetryObserver = controller.gimbal().gimbalState().observe(new Observer<GimbalState>() {
            @Override
            public void observe(GimbalState oldValue, GimbalState newValue, Observation<GimbalState> observation) {

                boolean isPitchReached = false;
                if(gimbalRequest.isPitchEnable()){
                    if(Location.angularDistance(newValue.getPitch(),gimbalRequest.getGimbalState().getPitch()) < pitchReachedAngularDistance){
                        isPitchReached = true;
                    }
                }
                else{
                    isPitchReached = true;
                }

                boolean isYawReached = false;
                if(gimbalRequest.isYawEnable()){
                    if(Location.angularDistance(newValue.getYaw(),gimbalRequest.getGimbalState().getYaw()) < yawReachedAngularDistance){
                        isYawReached = true;
                    }
                }
                else{
                    isYawReached = true;
                }

                if(isPitchReached && isYawReached){
                    try {
                        isSuccess.set(true);
                        taskLatch.countDown();
                        observation.remove();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).observeCurrentValue();

        if(timeoutInSeconds != null){
            taskLatch.await(timeoutInSeconds, TimeUnit.SECONDS);
        }
        else {
            taskLatch.await();
        }

        if(!isSuccess.value()){
            throw new DroneTaskException("Unable to Rotate gimbal,Check why gimbal won't response");
        }
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        telemetryObserver.remove();
    }
}
