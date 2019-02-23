package eyesatop.controller.tasks.flight;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.TaskBlocker;

/**
 * Created by einav on 15/05/2017.
 */
public enum FlightTaskBlockerType implements TaskBlocker<FlightTaskType> {
    BUSY("Busy"),
    NOT_CONNECTED("Not connected"),
    NOT_IN_F_MODE("Not in F Mode"),
    NOT_IN_APP_CONTROL("Not in App Control"),
    COMPASS_ERROR("Compass Error"),
    PREHEATING("Preheating"),
    MISSION_PLANNER("Inside Mission Planner"),
    GPS_ERROR("GPS error");

    private final String name;

    private static List<FlightTaskType> allTasks = Arrays.asList(FlightTaskType.values());
    private static List<FlightTaskType> none = new ArrayList<>();

    private static List<FlightTaskType> notInAppControl =
            Arrays.asList(
                    FlightTaskType.FLY_IN_CIRCLE,
                    FlightTaskType.GOTO_POINT,
                    FlightTaskType.FLY_TO_USING_DTM,
                    FlightTaskType.LAND_IN_LANDING_PAD,
                    FlightTaskType.ROTATE_HEADING,
                    FlightTaskType.FLY_SAFE_TO,
                    FlightTaskType.HOVER
    );
    private static List<FlightTaskType> preheating = Arrays.asList(
            FlightTaskType.TAKE_OFF
    );

    FlightTaskBlockerType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<FlightTaskType> affectedTasks(){

        switch (this){

            case BUSY:
                return allTasks;
            case NOT_CONNECTED:
                return allTasks;
            case NOT_IN_F_MODE:
                return allTasks;
            case NOT_IN_APP_CONTROL:
                return notInAppControl;
            case COMPASS_ERROR:
                return allTasks;
            case PREHEATING:
                return preheating;
            case MISSION_PLANNER:
                return allTasks;
            case GPS_ERROR:
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

    public static boolean containTaskBlocker(DroneController controller,FlightTaskType taskType){
        if(controller == null){
            return true;
        }

        for(FlightTaskBlockerType taskBlocker : controller.flightTasks().tasksBlockers()){
            if(taskBlocker.affectedTasks().contains(taskType)){
                return true;
            }
        }

        return false;
    }
}
