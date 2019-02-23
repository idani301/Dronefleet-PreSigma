package eyesatop.controller_tcpip.common.tasksrequests.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.util.geo.Location;

public class FlyToRequest implements FlightTaskRequest {

    private static final String LOCATION = "location";
    private static final String ALTITUDE_INFO = "AltitudeInfo";
    private static final String AZ = "az";
    private static final String MAX_VELOCITY = "maxVelocity";
    private static final String RADIUS_REACHED = "radiusReached";

    private final Location location;
    private final AltitudeInfo altitudeInfo;
    private final Double az;
    private final Double maxVelocity;
    private final Double radiusReached;

    @JsonCreator
    public FlyToRequest(@JsonProperty(LOCATION) Location location,
                        @JsonProperty(ALTITUDE_INFO) AltitudeInfo altitudeInfo,
                        @JsonProperty(AZ) Double az,
                        @JsonProperty(MAX_VELOCITY) Double maxVelocity,
                        @JsonProperty(RADIUS_REACHED) Double radiusReached) {
        this.location = location;
        this.altitudeInfo = altitudeInfo;
        this.az = az;
        this.maxVelocity = maxVelocity;
        this.radiusReached = radiusReached;
    }

    @JsonProperty(LOCATION)
    public Location getLocation() {
        return location;
    }

    @JsonProperty(ALTITUDE_INFO)
    public AltitudeInfo getAltitudeInfo() {
        return altitudeInfo;
    }

    @JsonProperty(AZ)
    public Double getAz() {
        return az;
    }

    @JsonProperty(MAX_VELOCITY)
    public Double getMaxVelocity() {
        return maxVelocity;
    }

    @JsonProperty(RADIUS_REACHED)
    public Double getRadiusReached() {
        return radiusReached;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.flightTasks().flyTo(location,altitudeInfo,az,maxVelocity,radiusReached);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
