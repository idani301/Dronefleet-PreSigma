package eyesatop.unit.tasks;

import java.util.UUID;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.unit.exceptions.ComponentNotFoundException;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by einav on 13/07/2017.
 */
public interface Swap extends DroneTask<UnitTaskType> {
    UUID controllerToSwap();
    UUID swappingController();
}
