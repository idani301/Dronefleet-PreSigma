package eyesatop.controller.mock.tasks.gimbal;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.util.geo.Location;

public class MockLockGimbalAtLocation extends MockDroneTask<GimbalTaskType> implements LockGimbalAtLocation {

    private final Location location;

    public MockLockGimbalAtLocation(UUID uuid, Location location) {
        super(uuid, GimbalTaskType.LOCK_LOOK_AT_LOCATION);
        this.location = location;
    }

    @Override
    public Location location() {
        return location;
    }
}
