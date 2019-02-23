package eyesatop.controller.mock.tasks.gimbal;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtFlightDirection;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.util.geo.Location;

public class MockLockGimbalAtFlightDirection extends MockDroneTask<GimbalTaskType> implements LockGimbalAtFlightDirection {

    public MockLockGimbalAtFlightDirection(UUID uuid) {
        super(uuid, GimbalTaskType.LOCK_TO_FLIGHT_DIRECTION);
    }
}
