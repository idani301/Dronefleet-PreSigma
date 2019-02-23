package com.example.abstractcontroller.components;

import com.example.abstractcontroller.taskmanager.GenericTaskManager;
import com.example.abstractcontroller.taskmanager.RemoteControllerTaskManager;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.RCFlightModeSwitchPosition;
import eyesatop.controller.beans.SticksPosition;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.remotecontroller.RemoteControllerTaskBlockerType;
import eyesatop.controller.tasks.remotecontroller.RemoteControllerTaskType;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 09/09/2017.
 */

public abstract class AbstractDroneRemoteController extends GeneralDroneComponent<RemoteControllerTaskType,RemoteControllerTaskBlockerType> {

    private final Property<BatteryState> rcBattery = new Property<>();
    private final Property<Location> rcLocation = new Property<>();
    private final Property<RCFlightModeSwitchPosition> rcFlightModeSwitchPosition = new Property<>();
    private final BooleanProperty rcInFunctionMode = new BooleanProperty();
    private final Property<SticksPosition> sticksPosition = new Property<>();
    private final Property<Boolean> goHomeButtonPressed = new Property<>();

    public AbstractDroneRemoteController() {
        super(new RemoteControllerTaskManager());
    }

    public Property<BatteryState> getRcBattery() {
        return rcBattery;
    }

    public Property<Location> getRcLocation() {
        return rcLocation;
    }

    public Property<RCFlightModeSwitchPosition> getRcFlightModeSwitchPosition() {
        return rcFlightModeSwitchPosition;
    }

    public BooleanProperty getRcInFunctionMode() {
        return rcInFunctionMode;
    }

    @Override
    public void clearData() {
        rcBattery.set(null);
        rcLocation.set(null);
        rcFlightModeSwitchPosition.set(null);
        rcInFunctionMode.set(null);
        goHomeButtonPressed.set(null);
    }

    public Property<SticksPosition> getSticksPosition() {
        return sticksPosition;
    }

    public Property<Boolean> getGoHomeButtonPressed() {
        return goHomeButtonPressed;
    }
}
