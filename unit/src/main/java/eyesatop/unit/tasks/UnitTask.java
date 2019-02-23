package eyesatop.unit.tasks;

import java.util.List;
import java.util.UUID;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.unit.UnitFlightTasks;

/**
 * Created by einav on 13/07/2017.
 */

public interface UnitTask extends DroneTask<UnitTaskType>{
    List<UUID> controllersResources();
}
