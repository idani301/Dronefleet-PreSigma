package eyesatop.controller.tasks.home;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eyesatop.controller.tasks.TaskBlocker;

/**
 * Created by einav on 15/05/2017.
 */
public enum HomeTaskBlockerType implements TaskBlocker<HomeTaskType>{
    NOT_CONNECTED("Not Connected"),
    MISSION_PLANNER("Mission Planner"),
    BUSY("Busy");

    private static List<HomeTaskType> allTasks = Arrays.asList(HomeTaskType.values());
    private static List<HomeTaskType> none = new ArrayList<>();

    private final String name;

    HomeTaskBlockerType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<HomeTaskType> affectedTasks(){
        switch (this){

            case NOT_CONNECTED:
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
