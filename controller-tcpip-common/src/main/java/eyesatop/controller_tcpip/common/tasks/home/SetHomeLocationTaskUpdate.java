package eyesatop.controller_tcpip.common.tasks.home;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.home.MockSetHomeLocation;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.util.geo.Location;

public class SetHomeLocationTaskUpdate extends HomeTaskUpdate {

    private static final String LOCATION = "location";

    private final Location location;

    @JsonCreator
    public SetHomeLocationTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                     @JsonProperty(ERROR) DroneTaskException error,
                                     @JsonProperty(STATUS) TaskStatus status,
                                     @JsonProperty(LOCATION) Location location) {
        super(uuid, error, status);
        this.location = location;
    }

    @JsonProperty(LOCATION)
    public Location getLocation() {
        return location;
    }

    @Override
    @JsonIgnore
    protected MockDroneTask<HomeTaskType> createNewMockTask() {
        return new MockSetHomeLocation(getUuid(),location);
    }
}
