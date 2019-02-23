package eyesatop.unit;

import java.util.UUID;

import eyesatop.landingpad.LandingPad;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;

/**
 * Created by einav on 19/04/2017.
 */

public class LandingPadComponent implements DroneControllerComponent<LandingPad> {

    private final LandingPad landingPad;

    public LandingPadComponent(LandingPad landingPad) {
        this.landingPad = landingPad;
    }

    @Override
    public ObservableValue<Location> location() {
        return landingPad.location();
    }

    @Override
    public UUID uuid() {
        return landingPad.uuid();
    }

    @Override
    public ComponentType type() {
        return ComponentType.LANDING_PAD;
    }

    @Override
    public LandingPad component() {
        return landingPad;
    }
}
