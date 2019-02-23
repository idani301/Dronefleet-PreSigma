package eyesatop.controller.mock.tasks.flight;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.LandAtLocation;
import eyesatop.util.geo.Location;

public class MockLandAtLocation extends MockDroneTask<FlightTaskType> implements LandAtLocation {

    private final Location location;

    public MockLandAtLocation(UUID uuid, Location location) {
        super(uuid, FlightTaskType.LAND_AT_LOCATION);
        this.location = location;
    }

    @Override
    public Location location() {
        return location;
    }
}
