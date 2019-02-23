package com.example.abstractcontroller.tasks.flight;

import com.example.abstractcontroller.AbstractDroneController;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.LandInPlace;
import eyesatop.util.model.Property;

public class LandInPlaceAbstract extends RunnableDroneTask<FlightTaskType> implements LandInPlace {

    private final AbstractDroneController controller;

    public LandInPlaceAbstract(AbstractDroneController controller) {
        this.controller = controller;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Boolean motorsOn = controller.motorsOn().value();

        if(motorsOn == null){
            throw new DroneTaskException("Unknown motors state");
        }

        if(motorsOn == false){
            throw new DroneTaskException("Motors are off, can't land");
        }
        controller.motorsOn().awaitFalse();
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.LAND_IN_PLACE;
    }
}
