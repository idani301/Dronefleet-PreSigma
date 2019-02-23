package eyesatop.controller.mock.tasks.flight;

import java.util.UUID;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.util.geo.Location;

public class MockFlyInCircle extends MockDroneTask<FlightTaskType> implements FlyInCircle {


    private final Location center;
    private final double radius;
    private final RotationType rotationType;
    private final double degreesToCover;
    private final double startingDegree;
    private final AltitudeInfo altitudeInfo;
    private final double velocity;

    public MockFlyInCircle(UUID uuid, FlightTaskType taskType, Location center, double radius, RotationType rotationType, double degreesToCover, double startingDegree, AltitudeInfo altitudeInfo, double velocity) {
        super(uuid, taskType);
        this.center = center;
        this.radius = radius;
        this.rotationType = rotationType;
        this.degreesToCover = degreesToCover;
        this.startingDegree = startingDegree;
        this.altitudeInfo = altitudeInfo;
        this.velocity = velocity;
    }

    @Override
    public Location center() {
        return center;
    }

    @Override
    public double radius() {
        return radius;
    }

    @Override
    public RotationType rotationType() {
        return rotationType;
    }

    @Override
    public double degreesToCover() {
        return degreesToCover;
    }

    @Override
    public double startingDegree() {
        return startingDegree;
    }

    @Override
    public AltitudeInfo altitudeInfo() {
        return altitudeInfo;
    }

    @Override
    public double velocity() {
        return velocity;
    }
}
