package eyesatop.controller.tasks;

import java.util.List;

/**
 * Created by einav on 15/05/2017.
 */

public interface TaskBlocker<T> {
    String getName();
    List<T> affectedTasks();

    boolean isBusy();
    boolean isNotConnected();
    boolean isMissionPlanner();
}
