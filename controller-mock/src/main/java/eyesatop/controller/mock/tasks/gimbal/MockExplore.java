package eyesatop.controller.mock.tasks.gimbal;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.gimbal.Explore;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;

public class MockExplore extends MockDroneTask<GimbalTaskType> implements Explore {

    public MockExplore(UUID uuid) {
        super(uuid, GimbalTaskType.EXPLORE);
    }
}
