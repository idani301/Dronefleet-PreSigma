package eyesatop.controller_tcpip.common.tasks.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.home.MockSetMaxAltitudeFromHomeLocation;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;

public class SetMaxAltitudeTaskUpdate extends HomeTaskUpdate {

    private static final String ALTITUDE = "altitude";

    private final double altitude;

    @JsonCreator
    public SetMaxAltitudeTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
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
    protected MockDroneTask<HomeTaskType> createNewMockTask() {
        return new MockSetMaxAltitudeFromHomeLocation(getUuid(),altitude);
    }
}
