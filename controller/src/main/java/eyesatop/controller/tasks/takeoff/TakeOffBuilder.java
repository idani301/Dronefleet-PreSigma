package eyesatop.controller.tasks.takeoff;

import eyesatop.controller.tasks.TaskBuilder;

public interface TakeOffBuilder extends TaskBuilder<TakeOff> {
    double DEFAULT_ALTITUDE = 30;
    double DEFAULT_SPEED = 1;

    TakeOffBuilder altitude(double altitude);
    TakeOffBuilder speed(double speed);
}
