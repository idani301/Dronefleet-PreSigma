package com.example.abstractcontroller.tasks.gimbal;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 22/10/2017.
 */

public class LockGimbalAtLocationAbstract extends RunnableDroneTask<GimbalTaskType> implements LockGimbalAtLocation {

    private final Location location;
    private final AbstractDroneController controller;

    public LockGimbalAtLocationAbstract(Location location, AbstractDroneController controller) {

        this.controller = controller;

        this.location = DtmProvider.DtmTools.getGroundLocationRelativeToRefPoint(
                location,
                this.controller.droneHome().takeOffDTM().value(),
                this.controller.getDtmProvider());
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOCK_LOOK_AT_LOCATION;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        final CountDownLatch stamLatch = new CountDownLatch(1);
        stamLatch.await();
    }
}
