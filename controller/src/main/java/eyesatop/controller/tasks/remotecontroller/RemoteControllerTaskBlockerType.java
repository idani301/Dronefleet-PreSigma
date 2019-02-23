package eyesatop.controller.tasks.remotecontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eyesatop.controller.tasks.TaskBlocker;
import eyesatop.controller.tasks.battery.BatteryTaskType;

/**
 * Created by Idan on 09/09/2017.
 */

public enum  RemoteControllerTaskBlockerType implements TaskBlocker<RemoteControllerTaskType>{
    NOT_CONNECTED("Camera Not Connected"),
    MISSION_PLANNER("Mission Planner"),
    BUSY("Busy");

    private static List<RemoteControllerTaskType> allTasks = Arrays.asList(RemoteControllerTaskType.values());
    private static List<RemoteControllerTaskType> none = new ArrayList<>();

    private final String name;

    RemoteControllerTaskBlockerType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<RemoteControllerTaskType> affectedTasks() {
        return allTasks;
    }

    @Override
    public boolean isBusy() {
        return this == BUSY;
    }

    @Override
    public boolean isNotConnected() {
        return this == NOT_CONNECTED;
    }

    @Override
    public boolean isMissionPlanner() {
        return this == MISSION_PLANNER;
    }
}
