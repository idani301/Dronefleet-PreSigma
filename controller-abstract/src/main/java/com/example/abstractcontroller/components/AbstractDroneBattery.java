package com.example.abstractcontroller.components;

import com.example.abstractcontroller.taskmanager.BatteryTaskManager;
import com.example.abstractcontroller.taskmanager.GenericTaskManager;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.battery.BatteryTaskBlockerType;
import eyesatop.controller.tasks.battery.BatteryTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 09/09/2017.
 */

public abstract class AbstractDroneBattery extends GeneralDroneComponent<BatteryTaskType,BatteryTaskBlockerType> {

    private final Property<BatteryState> droneBattery = new Property<>();

    public AbstractDroneBattery() {
        super(new BatteryTaskManager());
    }

    @Override
    public void clearData() {
        droneBattery.set(null);
    }

    public Property<BatteryState> getDroneBattery() {
        return droneBattery;
    }
}
