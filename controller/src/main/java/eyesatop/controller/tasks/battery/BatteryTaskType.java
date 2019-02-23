package eyesatop.controller.tasks.battery;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by Idan on 09/09/2017.
 */

public enum  BatteryTaskType implements EnumWithName{
    ;

    private final String name;

    BatteryTaskType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
