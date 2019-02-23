package eyesatop.controller_tcpip.common.tasks.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.flight.MockTakeOff;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller_tcpip.common.tasks.flight.FlightTaskUpdate;

public class TakeOffUpdate extends FlightTaskUpdate {

    private static final String ALTITUDE = "altitude";

    private final double altitude;

    @JsonCreator()
    public TakeOffUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                         @JsonProperty(ERROR) DroneTaskException error,
                         @JsonProperty(STATUS) TaskStatus status,
                         @JsonProperty(ALTITUDE) double altitude) {
        super(uuid, error, status);
        this.altitude = altitude;
    }

    @JsonProperty(ALTITUDE)
    public double getAltitude() {
        return altitude;
    }

    @Override
    @JsonIgnore
    public MockDroneTask<FlightTaskType> createNewMockTask() {
        return new MockTakeOff(getUuid(),altitude);
    }
}
