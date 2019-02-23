package eyesatop.controller.tasks.flight;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by einav on 14/05/2017.
 */
public enum FlightTaskType implements EnumWithName {
    TAKE_OFF("Take off"),
    FLY_IN_CIRCLE("Fly In Circle"),
    GOTO_POINT("Fly to Point"),
    FLY_TO_USING_DTM("Fly to Point With DTM"),
    GO_HOME("Go Home"),
    LAND_IN_LANDING_PAD("Land in Landing Pad"),
    LAND_AT_LOCATION("Land in Location"),
    ROTATE_HEADING("Rotate Heading"),
    FLY_SAFE_TO("Fly Safe To"),
    HOVER("Hover"),
    FOLLOW_NAV_PLAN("Follow Nav Plan"),
    LAND_IN_PLACE("Land in Place");

    private final String name;

    FlightTaskType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
