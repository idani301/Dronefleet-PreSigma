package com.example.abstractcontroller.tasks.gimbal;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 22/10/2017.
 */

public class LockYawAtLocationAbstract extends RunnableDroneTask<GimbalTaskType> implements LockYawAtLocation {

    private final Location location;
    private final double degreeShiftFromLocation;

    public LockYawAtLocationAbstract(Location location, double degreeShiftFromLocation) {
        this.location = location;
        this.degreeShiftFromLocation = degreeShiftFromLocation;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public double degreeShiftFromLocation() {
        return degreeShiftFromLocation;
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOCK_YAW_AT_LOCATION;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        final CountDownLatch stamLatch = new CountDownLatch(1);
        stamLatch.await();
    }
}
