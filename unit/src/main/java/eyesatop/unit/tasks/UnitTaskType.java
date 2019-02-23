package eyesatop.unit.tasks;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by einav on 13/07/2017.
 */

public enum UnitTaskType implements EnumWithName{
    ANYTARGET("Anytarget"),
    SWAP("Swap");

    private final String name;

    UnitTaskType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
