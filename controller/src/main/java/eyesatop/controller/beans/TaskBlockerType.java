package eyesatop.controller.beans;

import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.flight.FlightTaskType;

/**
 * Created by einav on 09/04/2017.
 */
public enum TaskBlockerType {
    DRONE_NOT_CONNECTED,
    NOT_IN_F_MODE,
    COMPASS_ERROR,
    CAMERA_BUSY,
    FLIGHT_TASKS_BUSY,
    GIMBAL_BUSY,
    HOME_TASKS_BUSY,
    PREHEATING,
    GPS_ERROR;

    private static FlightTaskType[] flightTasksEmptyArray = new FlightTaskType[0];
    private static FlightTaskType[] flightTasksAllValues = FlightTaskType.values();
    private static FlightTaskType[] fModeArray = {
            FlightTaskType.TAKE_OFF,
            FlightTaskType.GOTO_POINT,
            FlightTaskType.LAND_AT_LOCATION,
            FlightTaskType.LAND_IN_LANDING_PAD,
            FlightTaskType.LAND_IN_PLACE};
    private static FlightTaskType[] preHeatingArray = {
            FlightTaskType.TAKE_OFF
    };

    private static CameraTaskType[] cameraTasksEmptyArray = new CameraTaskType[0];
    private static CameraTaskType[] cameraTasksAllValues = CameraTaskType.values();

    public CameraTaskType[] affectedCameraTasks(){
        switch (this){

            case DRONE_NOT_CONNECTED:
                return cameraTasksAllValues;
            case NOT_IN_F_MODE:
                break;
            case COMPASS_ERROR:
                break;
            case CAMERA_BUSY:
                break;
            case FLIGHT_TASKS_BUSY:
                break;
            case GIMBAL_BUSY:
                break;
            case HOME_TASKS_BUSY:
                break;
            case PREHEATING:
                break;
            case GPS_ERROR:
                break;
        }
        return null;
    }

    public FlightTaskType[] affectedFlightTasks(){

        switch (this) {

            case DRONE_NOT_CONNECTED:
                return flightTasksAllValues;
            case NOT_IN_F_MODE:
                return fModeArray;
            case COMPASS_ERROR:
                return flightTasksAllValues;
            case FLIGHT_TASKS_BUSY:
                return flightTasksAllValues;
            case PREHEATING:
                return preHeatingArray;
            case GPS_ERROR:
                return flightTasksAllValues;
            default:
                return flightTasksEmptyArray;
        }
    }

    public boolean isBusyType(){
        switch (this){
            case CAMERA_BUSY:
                return true;
            case FLIGHT_TASKS_BUSY:
                return true;
            case GIMBAL_BUSY:
                return true;
            case HOME_TASKS_BUSY:
                return true;
        }
        return false;
    }
}
