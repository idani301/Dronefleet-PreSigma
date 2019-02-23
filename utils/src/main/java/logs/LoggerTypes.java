package logs;

/**
 * Created by idany on 19/10/2016.
 */
public enum LoggerTypes {
    NETWORK_CONNECTIVITY("Network Connections Changes"),
    FLY_IN_CIRCLE("Fly in Circle"),
    FLIGHT_MODES("flight modes"),
    FLY_USING_DTM_DEBUG("Fly using dtm debug"),
    YOSSI("Yossi Altitudes"),
    RTK_YOSSI("RTK_Yossi"),
    SDK_INIT("SDK Init"),
    ZOOM("Zoom"),
    RTK_FIXES("RTK_Fixes"),
    RTK_BASIC("RTK_BASIC"),
    RTK_FULL("RTK_FULL"),
    RTK_BUZAGLO("RTK_Buzaglo"),
    BLACKBOX("BlackBox"),
    EINAV("Einav"),
    DEBUG("Debug"),
    SWAP("Swap"),
    RC_GPS("RCGps"),
    ALTITUDE_CALCS("Altitude Calcs"),
    ENABLE_STICKS("Enable Sticks"),
    MICRO_MOVE("Micro Move"),
    PRODUCT_CHANGES("Product Changes"),
    MAVIC("Mavic"),
    MATRICE("Matrice"),
    CALLBACKS("Callbacks"),
    DTM_CALCS("Dtm calcs"),
    CAMERA_TASKS("Camera Tasks"),
    HOME_TASKS("Home Tasks"),
    FLIGHT_TASKS("Flight Tasks"),
    TASKS("Tasks"),
    VIDEO("Video"),
    VIDEO_TCP("Video_TCP"),
    GIMBAL_REQUEST("Gimbal Request"),
    TASKS_BLOCKERS("Tasks Blockers"),
    AIRLINK("AirLink"),
    MAVLINK("Mavlink"),
    MAVLINK_MICRO_MOVE("Mavlink_MicroMove"),
    MAVLINK_BATTERY("Mavlink_battery"),
    MAVLINK_TASKS("Mavlink_Tasks"),
    MAVLINK_STATE("Mavlink_State"),
    MAVLINK_HOME("Mavlink_Home"),
    MAVLINK_FLIGHT_TASKS("Mavlink_FlightTasks"),
    MAVLINK_DRONE_EVENTS("Mavlink_DroneEvent"),
    UNIT_TASKS("Unit Tasks"),
    MISSION("Mission Planner"),
    ANYVISION("AnyVision"),
    ANYVISION_RECORD("AnyVision_Record"),
    MISSION_UI("Mission UI"),
    LOCATION_FIX("Location fix"),
    ERROR("Error");

    private final String name;

    LoggerTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
