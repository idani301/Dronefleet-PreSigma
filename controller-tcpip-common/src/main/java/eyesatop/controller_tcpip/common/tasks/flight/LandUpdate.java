package eyesatop.controller_tcpip.common.tasks.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.flight.MockLandInPlace;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;

public class LandUpdate extends FlightTaskUpdate {

    @JsonCreator
    public LandUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                      @JsonProperty(ERROR) DroneTaskException error,
                      @JsonProperty(STATUS) TaskStatus status) {
        super(uuid, error, status);
    }

    @Override
    protected MockDroneTask<FlightTaskType> createNewMockTask() {
        return new MockLandInPlace(getUuid());
    }
}
