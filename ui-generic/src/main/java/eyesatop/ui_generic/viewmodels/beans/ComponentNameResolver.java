package eyesatop.ui_generic.viewmodels.beans;

import java.util.UUID;

import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;

public interface ComponentNameResolver {
    String resolve(UUID uuid);
    ObservableValue<Location> focusRequests();
}
