package eyesatop.controller.functions;

import java.util.UUID;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.util.Function;

public class TaskUuid<T extends EnumWithName> implements Function<DroneTask<T>, UUID> {
    @Override
    public UUID apply(DroneTask<T> task) {
        return task.uuid();
    }
}
