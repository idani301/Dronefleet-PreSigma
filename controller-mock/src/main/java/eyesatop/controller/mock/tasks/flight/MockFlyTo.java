package eyesatop.controller.mock.tasks.flight;

import java.util.UUID;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.util.geo.Location;

public class MockFlyTo extends MockDroneTask<FlightTaskType> implements FlyTo {

    private final Location location;
    private final AltitudeInfo altitudeInfo;
    private final Double az;
    private final Double maxVelocity;
    private final Double radiusReached;

    public MockFlyTo(UUID uuid, Location location, AltitudeInfo altitudeInfo, Double az, Double maxVelocity, Double radiusReached) {
        super(uuid, FlightTaskType.GOTO_POINT);
        this.location = location;
        this.altitudeInfo = altitudeInfo;
        this.az = az;
        this.maxVelocity = maxVelocity;
        this.radiusReached = radiusReached;
    }

    @Override
    public Double radiusReached() {
        return radiusReached;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public AltitudeInfo altitudeInfo() {
        return altitudeInfo;
    }

    @Override
    public Double az() {
        return az;
    }

    @Override
    public Double maxVelocity() {
        return maxVelocity;
    }
}
