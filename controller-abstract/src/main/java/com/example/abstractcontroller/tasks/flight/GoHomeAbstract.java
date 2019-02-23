package com.example.abstractcontroller.tasks.flight;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.GoHome;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 22/10/2017.
 */

public class GoHomeAbstract extends RunnableDroneTask<FlightTaskType> implements GoHome {
    @Override
    public FlightTaskType taskType() {
        return null;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

    }
}
