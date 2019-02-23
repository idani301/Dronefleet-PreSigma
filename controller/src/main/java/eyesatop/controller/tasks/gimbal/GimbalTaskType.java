package eyesatop.controller.tasks.gimbal;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by einav on 14/05/2017.
 */
public enum GimbalTaskType implements EnumWithName {
    LOOK_AT_POINT("Look at point"),
    LOCK_LOOK_AT_LOCATION("Lock Gimbal at location"),
    LOCK_YAW_AT_LOCATION("Lock Yaw at location"),
    LOCK_TO_FLIGHT_DIRECTION("Look gimbal at flight direction"),
    EXPLORE("Explore"),
    ROTATE_GIMBAL("Rotate Gimbal");

    private final String name;

    GimbalTaskType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
