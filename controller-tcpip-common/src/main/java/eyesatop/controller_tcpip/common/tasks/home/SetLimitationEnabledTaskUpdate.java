package eyesatop.controller_tcpip.common.tasks.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.home.MockSetLimitationEnabled;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;

public class SetLimitationEnabledTaskUpdate extends HomeTaskUpdate {

    private static final String ENABLED = "enabled";

    private final boolean enabled;

    @JsonCreator
    public SetLimitationEnabledTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                          @JsonProperty(ERROR) DroneTaskException error,
                                          @JsonProperty(STATUS) TaskStatus status,
                                          @JsonProperty(ENABLED) boolean enabled) {
        super(uuid, error, status);
        this.enabled = enabled;
    }

    @JsonProperty(ENABLED)
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    @JsonIgnore
    protected MockDroneTask<HomeTaskType> createNewMockTask() {
        return new MockSetLimitationEnabled(getUuid(),enabled);
    }
}
