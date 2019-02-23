package com.example.abstractcontroller.tasks.gimbal;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

/**
 * Created by Idan on 22/10/2017.
 */

public class LookAtPointAbstract extends RunnableDroneTask<GimbalTaskType> implements LookAtPoint {

    private final AbstractDroneController controller;
    private final Location location;
    private double reachedTargetTime = -1;

    private Removable telemetryObserver = Removable.STUB;

    public LookAtPointAbstract(AbstractDroneController controller, Location location) {
        this.controller = controller;

        this.location = DtmProvider.DtmTools.getGroundLocationRelativeToRefPoint(
                location,
                controller.droneHome().takeOffDTM().value(),
                controller.getDtmProvider());

    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOOK_AT_POINT;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        final CountDownLatch lookAtPointLatch = new CountDownLatch(1);

        MainLoggerJava.writeMessage(JavaLoggerType.LOOK_AT_POINT,"Starting to look at : " + location.toString());
        telemetryObserver = controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {

                boolean reachedPitch;
                boolean reachedYaw;

                if(newValue == null){
                    return;
                }

                double distanceFromLocationToLookAt = location.distance(newValue.location());
                double altitudeDistance = Math.max(newValue.location().getAltitude() - location.getAltitude(),0);
                double gimbalPitchRotation = Math.toDegrees(Math.atan(altitudeDistance/distanceFromLocationToLookAt));

                double gimbalPitchDistance = Math.abs(gimbalPitchRotation) - Math.abs(controller.gimbal().gimbalState().value().getPitch());

                reachedPitch = Math.abs(gimbalPitchDistance) < 3;

                double azFromLookAtLocation = newValue.location().az(location);

                double currentGimbalYaw = Location.degreeBetween0To360(controller.gimbal().gimbalState().value().getYaw());

                double targetHeading = azFromLookAtLocation;

                double angularDistance = Location.angularDistance(targetHeading, currentGimbalYaw);

                reachedYaw = Math.abs(angularDistance) < 3;

                MainLoggerJava.writeMessage(JavaLoggerType.LOOK_AT_POINT,
                        "\nReached Pitch : " + reachedPitch +
                                "\nReached yaw   :" + reachedYaw);

                if(reachedPitch && reachedYaw){

                    if(reachedTargetTime == -1){
                        reachedTargetTime = System.currentTimeMillis();
                    }

                    long currentTime  = System.currentTimeMillis();

                    MainLoggerJava.writeMessage(JavaLoggerType.LOOK_AT_POINT,"Reached for " + (currentTime - reachedTargetTime));

                    if(currentTime - reachedTargetTime > 1000) {
                        MainLoggerJava.writeMessage(JavaLoggerType.LOOK_AT_POINT,"Done");
                        telemetryObserver.remove();
                        telemetryObserver = Removable.STUB;
                        lookAtPointLatch.countDown();
                    }
                }
                else{
                    reachedTargetTime = -1;
                }
            }
        });

        lookAtPointLatch.await();

    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        telemetryObserver.remove();
    }
}
