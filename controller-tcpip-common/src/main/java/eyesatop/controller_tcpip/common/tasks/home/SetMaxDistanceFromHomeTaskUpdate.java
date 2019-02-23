package eyesatop.controller_tcpip.common.tasks.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.home.MockSetMaxDistanceFromHome;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;

public class SetMaxDistanceFromHomeTaskUpdate extends HomeTaskUpdate {
    private static final String DISTANCE = "maxDistance";

    private final double maxDistance;

    @JsonCreator
    public SetMaxDistanceFromHomeTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                            @JsonProperty(ERROR) DroneTaskException error,
                                            @JsonProperty(STATUS) TaskStatus status,
                                            @JsonProperty(DISTANCE) double maxDistance) {
        super(uuid, error, status);
        this.maxDistance = maxDistance;
    }

    @JsonProperty(DISTANCE)
    public double getMaxDistance() {
        return maxDistance;
    }

    @Override
    protected MockDroneTask<HomeTaskType> createNewMockTask() {
        return new MockSetMaxDistanceFromHome(getUuid(),maxDistance);
    }
}
