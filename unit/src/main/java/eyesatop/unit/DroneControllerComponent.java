package eyesatop.unit;

import java.util.UUID;

import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;

/**
 * Created by einav on 19/04/2017.
 */

public interface DroneControllerComponent<T> {

    ObservableValue<Location> location();
    UUID uuid();
    ComponentType type();
    T component();
}
