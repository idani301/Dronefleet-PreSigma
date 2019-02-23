package eyesatop.controller.tasks.remotecontroller;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by Idan on 09/09/2017.
 */

public enum RemoteControllerTaskType implements EnumWithName {
    ;

    private final String name;

    RemoteControllerTaskType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
