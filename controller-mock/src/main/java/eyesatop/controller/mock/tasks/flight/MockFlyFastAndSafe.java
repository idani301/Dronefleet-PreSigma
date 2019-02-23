package eyesatop.controller.mock.tasks.flight;

import java.util.UUID;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.util.geo.Location;

public class MockFlyFastAndSafe extends MockDroneTask<FlightTaskType> implements FlyToSafeAndFast{

    private final Location location;
    private final AltitudeInfo altitudeInfo;

    public MockFlyFastAndSafe(UUID uuid, FlightTaskType taskType, Location location, AltitudeInfo altitudeInfo) {
        super(uuid, taskType);
        this.location = location;
        this.altitudeInfo = altitudeInfo;
    }

    @Override
    public Location targetLocation() {
        return location;
    }

    @Override
    public AltitudeInfo altitudeInfo() {
        return altitudeInfo;
    }
}
