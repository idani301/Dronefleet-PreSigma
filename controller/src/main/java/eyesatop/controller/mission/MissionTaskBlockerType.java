package eyesatop.controller.mission;

import java.util.Arrays;
import java.util.List;

import eyesatop.controller.tasks.TaskBlocker;

/**
 * Created by Idan on 03/09/2017.
 */

public enum MissionTaskBlockerType implements TaskBlocker<MissionTaskType>{

    NOT_CONNECTED("Not Connected"),
    BUSY("Busy");

    private final String name;

    private static List<MissionTaskType> allTasks = Arrays.asList(MissionTaskType.values());

    MissionTaskBlockerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public List<MissionTaskType> affectedTasks() {

        switch (this){

            case NOT_CONNECTED:
                return allTasks;
            case BUSY:
                return allTasks ;
        }

        return null;
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
        return false;
    }
}
