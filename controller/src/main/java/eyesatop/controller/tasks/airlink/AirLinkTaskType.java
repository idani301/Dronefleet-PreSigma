package eyesatop.controller.tasks.airlink;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by Idan on 09/09/2017.
 */

public enum  AirLinkTaskType implements EnumWithName{
    ;

    private final String name;

    AirLinkTaskType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return null;
    }
}
