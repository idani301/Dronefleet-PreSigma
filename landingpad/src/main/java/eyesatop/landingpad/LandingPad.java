package eyesatop.landingpad;

import java.util.UUID;

import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;

public interface LandingPad {
    UUID uuid();
    ObservableValue<Location> location();
    ObservableValue<Double> angle();
    Landing requestLanding();
}
