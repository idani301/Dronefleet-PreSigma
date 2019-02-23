package eyesatop.controller.mock.tasks.gimbal;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.util.geo.Location;

public class MockLockYawAtLocation extends MockDroneTask<GimbalTaskType> implements LockYawAtLocation {

    private final Location location;
    private final double shiftDegrees;

    public MockLockYawAtLocation(UUID uuid, Location location, double shiftDegrees) {
        super(uuid, GimbalTaskType.LOCK_YAW_AT_LOCATION);
        this.location = location;
        this.shiftDegrees = shiftDegrees;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public double degreeShiftFromLocation() {
        return shiftDegrees;
    }
}
