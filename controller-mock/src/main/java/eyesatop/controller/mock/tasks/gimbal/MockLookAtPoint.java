package eyesatop.controller.mock.tasks.gimbal;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.util.geo.Location;

public class MockLookAtPoint extends MockDroneTask<GimbalTaskType> implements LookAtPoint {

    private final Location location;

    public MockLookAtPoint(UUID uuid, Location location) {
        super(uuid, GimbalTaskType.LOOK_AT_POINT);
        this.location = location;
    }

    @Override
    public Location location() {
        return location;
    }
}
