package eyesatop.controller.tasks.camera;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eyesatop.controller.tasks.TaskBlocker;

/**
 * Created by einav on 15/05/2017.
 */
public enum CameraTaskBlockerType implements TaskBlocker<CameraTaskType>{
    NOT_CONNECTED("Camera Not Connected"),
    RECORDING("Camera is Recording"),
    MISSION_PLANNER("Mission Planner"),
    BUSY("Busy");

    private static List<CameraTaskType> allTasks = Arrays.asList(CameraTaskType.values());
    private static List<CameraTaskType> none = new ArrayList<>();

    private final String name;

    CameraTaskBlockerType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<CameraTaskType> affectedTasks(){
        switch (this){

            case NOT_CONNECTED:
                return allTasks;
            case RECORDING:
                return Arrays.asList(CameraTaskType.CHANGE_MODE);
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
