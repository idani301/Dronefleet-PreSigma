package eyesatop.controller.mock.tasks.flight;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.LandInPlace;

public class MockLandInPlace extends MockDroneTask<FlightTaskType> implements LandInPlace {
    public MockLandInPlace(UUID uuid) {
        super(uuid, FlightTaskType.LAND_IN_PLACE);
    }
}
