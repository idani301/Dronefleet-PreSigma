package com.example.abstractcontroller.tasks.gimbal;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.Explore;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.util.model.Property;

public class ExploreAbstract extends RunnableDroneTask<GimbalTaskType> implements Explore {

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        final CountDownLatch stamLatch = new CountDownLatch(1);
        stamLatch.await();
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.EXPLORE;
    }
}
