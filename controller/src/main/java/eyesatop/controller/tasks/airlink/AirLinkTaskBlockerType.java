package eyesatop.controller.tasks.airlink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eyesatop.controller.tasks.TaskBlocker;
import eyesatop.controller.tasks.camera.CameraTaskType;

/**
 * Created by Idan on 09/09/2017.
 */

public enum  AirLinkTaskBlockerType implements TaskBlocker<AirLinkTaskType>{
    NOT_CONNECTED("Camera Not Connected"),
    MISSION_PLANNER("Mission Planner"),
    BUSY("Busy");

    private static List<AirLinkTaskType> allTasks = Arrays.asList(AirLinkTaskType.values());
    private static List<AirLinkTaskType> none = new ArrayList<>();

    private final String name;

    AirLinkTaskBlockerType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<AirLinkTaskType> affectedTasks() {
        switch (this){

            case NOT_CONNECTED:
                return allTasks;
            case MISSION_PLANNER:
                return allTasks;
            case BUSY:
                return allTasks;
            default:
                return allTasks;
        }
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
