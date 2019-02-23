package eyesatop.controller.mock.tasks.home;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetLimitationEnabled;

public class MockSetLimitationEnabled extends MockDroneTask<HomeTaskType> implements SetLimitationEnabled {
    private final boolean enabled;

    public MockSetLimitationEnabled(UUID uuid, boolean enabled) {
        super(uuid, HomeTaskType.SET_LIMITATION_ENABLED);
        this.enabled = enabled;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }
}
