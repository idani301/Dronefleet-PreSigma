package eyesatop.controller.mock.tasks.flight;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.LandAtLandingPad;
import eyesatop.landingpad.LandingPad;

public class MockLandAtLandingPad extends MockDroneTask<FlightTaskType> implements LandAtLandingPad {

    private final LandingPad landingPad;

    public MockLandAtLandingPad(UUID uuid, LandingPad landingPad) {
        super(uuid, FlightTaskType.LAND_IN_LANDING_PAD);
        this.landingPad = landingPad;
    }

    @Override
    public LandingPad landingPad() {
        return landingPad;
    }
}
