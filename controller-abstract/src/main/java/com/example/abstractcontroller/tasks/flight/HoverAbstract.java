package com.example.abstractcontroller.tasks.flight;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.Hover;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 22/05/2018.
 */

public class HoverAbstract extends RunnableDroneTask<FlightTaskType> implements Hover {

    private final int hoverTime;

    public HoverAbstract(int hoverTime) {
        this.hoverTime = hoverTime;
    }

    @Override
    public int hoverTime() {
        return hoverTime;
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.HOVER;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(hoverTime * 1000);
    }
}
