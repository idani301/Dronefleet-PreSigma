package eyesatop.controller.tasks.gimbal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eyesatop.controller.tasks.TaskBlocker;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;

/**
 * Created by einav on 15/05/2017.
 */
public enum GimbalTaskBlockerType implements TaskBlocker<GimbalTaskType>{
    NOT_CONNECTED("Not Connected"),
    MISSION_PLANNER("Inside Mission Planner"),
    BUSY("Busy");

    private final String name;

    private static List<GimbalTaskType> allTasks = Arrays.asList(GimbalTaskType.values());
    private static List<GimbalTaskType> none = new ArrayList<>();

    GimbalTaskBlockerType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<GimbalTaskType> affectedTasks(){
        switch (this){


            case NOT_CONNECTED:
                return allTasks;
            case MISSION_PLANNER:
                return allTasks;
            case BUSY:
                return allTasks;
            default :
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
