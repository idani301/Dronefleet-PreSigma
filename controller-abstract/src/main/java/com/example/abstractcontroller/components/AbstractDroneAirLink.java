package com.example.abstractcontroller.components;

import com.example.abstractcontroller.taskmanager.AirLinkTaskManager;
import com.example.abstractcontroller.taskmanager.GenericTaskManager;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.airlink.AirLinkTaskBlockerType;
import eyesatop.controller.tasks.airlink.AirLinkTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 09/09/2017.
 */

public abstract class AbstractDroneAirLink extends GeneralDroneComponent<AirLinkTaskType,AirLinkTaskBlockerType>{

    private final Property<Integer> rcSignalStrengthPercent = new Property<>();

    public AbstractDroneAirLink() {
        super(new AirLinkTaskManager());
    }

    public Property<Integer> rcSignalStrengthPercent() {
        return rcSignalStrengthPercent;
    }

    @Override
    public void clearData() {
        rcSignalStrengthPercent.set(null);
    }
}
