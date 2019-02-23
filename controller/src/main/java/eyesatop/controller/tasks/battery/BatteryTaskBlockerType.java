package eyesatop.controller.tasks.battery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eyesatop.controller.tasks.TaskBlocker;
import eyesatop.controller.tasks.airlink.AirLinkTaskType;

/**
 * Created by Idan on 09/09/2017.
 */

public enum  BatteryTaskBlockerType implements TaskBlocker<BatteryTaskType>{

    NOT_CONNECTED("Camera Not Connected"),
    MISSION_PLANNER("Mission Planner"),
    BUSY("Busy");

    private static List<BatteryTaskType> allTasks = Arrays.asList(BatteryTaskType.values());
    private static List<BatteryTaskType> none = new ArrayList<>();

    private final String name;

    BatteryTaskBlockerType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<BatteryTaskType> affectedTasks() {

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
