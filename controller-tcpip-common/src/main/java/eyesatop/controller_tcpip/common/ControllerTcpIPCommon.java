package eyesatop.controller_tcpip.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControllerTcpIPCommon {

    public static final int ELBIT_START_PORT = 8003;

    public static final int PIPE_BROADCAST_START_PORT = 5000;
    public static final int REMOTE_BROADCAST_START_PORT = 6000;

    private static final int TELEMETRY_TCP_START_PORT = 11000;

    private static final int FLIGHT_TASKS_REQUEST_START_PORT = 12000;
    private static final int GIMBAL_TASKS_REQUEST_START_PORT = 13000;
    private static final int CAMERA_TASKS_REQUEST_START_PORT = 14000;
    private static final int HOME_TASKS_REQUEST_START_PORT = 15000;

    private static final int FLIGHT_CURRENT_TASK_UPDATE_START_PORT = 16000;
    private static final int CAMERA_CURRENT_TASK_UPDATE_START_PORT = 17000;
    private static final int GIMBAL_CURRENT_TASK_UPDATE_START_PORT = 18000;
    private static final int HOME_CURRENT_TASK_UPDATE_START_PORT = 19000;

    private static final int CANCEL_TASKS_START_PORT = 20000;

    private static final int VIRTUAL_STICKS_START_PORT = 21000;

    private static final int VIDEO_START_PORT = 9000;

    public static int getCancelPort(int droneID){
        return CANCEL_TASKS_START_PORT + droneID;
    }

    public static int getFlightCurrentTaskUpdatePort(int droneID){
        return FLIGHT_CURRENT_TASK_UPDATE_START_PORT + droneID;
    }

    public static int getCameraCurrentTaskUpdatePort(int droneID){
        return CAMERA_CURRENT_TASK_UPDATE_START_PORT + droneID;
    }

    public static int getGimbalCurrentTaskUpdatePort(int droneID){
        return GIMBAL_CURRENT_TASK_UPDATE_START_PORT + droneID;
    }

    public static int getHomeCurrentTaskUpdatePort(int droneID){
        return HOME_CURRENT_TASK_UPDATE_START_PORT + droneID;
    }

    public static int getVideoPort(int droneID){
        return VIDEO_START_PORT + droneID;
    }

    public static int getTelemetryPortMap(int droneID){
        return TELEMETRY_TCP_START_PORT + droneID;
    }

    public static int getFlightTaskRequestPort(int droneID){
        return FLIGHT_TASKS_REQUEST_START_PORT + droneID;
    }

    public static int getGimbalTaskRequestPort(int droneID){
        return GIMBAL_TASKS_REQUEST_START_PORT + droneID;
    }

    public static int getCameraTaskRequestPort(int droneID){
        return CAMERA_TASKS_REQUEST_START_PORT + droneID;
    }

    public static int getHomeTaskRequestPort(int droneID){
        return HOME_TASKS_REQUEST_START_PORT + droneID;
    }

    public static int getVirtualSticksStartPort(int droneID){
        return VIRTUAL_STICKS_START_PORT + droneID;
    }

    public static int getPipeBroadcastStartPort(int droneID){
        return PIPE_BROADCAST_START_PORT + droneID;
    }

    public static int getRemoteBroadcastStartPort(int droneID){
        return REMOTE_BROADCAST_START_PORT + droneID;
    }

    public static int getElbitStartPort(int droneID){
        return ELBIT_START_PORT + droneID;
    }
}
