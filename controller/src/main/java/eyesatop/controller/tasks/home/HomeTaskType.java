package eyesatop.controller.tasks.home;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by einav on 14/05/2017.
 */
public enum HomeTaskType implements EnumWithName{
    SET_HOME_LOCATION("Set Home Location"),
    SET_LIMITATION_ENABLED("Set Limitation enabled"),
    SET_MAX_DISTANCE_FROM_HOME("Set max distance from home"),
    SET_MAX_ALT_FROM_TAKE_OFF_ALT("Set max altitude from take off location"),
    SET_RETURN_HOME_ALT("Set return home altitude");

    private final String name;

    HomeTaskType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
