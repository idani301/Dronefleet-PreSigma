package eyesatop.controller.mission;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by Idan on 02/09/2017.
 */

public enum MissionTaskType implements EnumWithName {

    NORMAL("Normal Mission");

    private final String name;

    MissionTaskType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
