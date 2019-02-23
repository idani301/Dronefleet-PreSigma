package eyesatop.controller_tcpip.common.tasksrequests.flight;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.util.geo.Location;

public class FlyInCircleTaskRequest implements FlightTaskRequest {

    private static final String CENTER = "center";
    private static final String RADIUS = "radius";
    private static final String ROTATION_TYPE = "rotationType";
    private static final String DEGREES_TO_COVER = "degreesToCover";
    private static final String STARTING_DEGREE = "startingDegree";
    private static final String ALTITUDE_INFO = "altitudeInfo";
    private static final String VELOCITY = "velocity";

    private final Location center;
    private final double radius;
    private final RotationType rotationType;
    private final double degreesToCover;
    private final double startingDegree;
    private final AltitudeInfo altitudeInfo;
    private final double velocity;

    @JsonCreator
    public FlyInCircleTaskRequest(@JsonProperty(CENTER) Location center,
                                  @JsonProperty(RADIUS) double radius,
                                  @JsonProperty(ROTATION_TYPE) RotationType rotationType,
                                  @JsonProperty(DEGREES_TO_COVER) double degreesToCover,
                                  @JsonProperty(STARTING_DEGREE) double startingDegree,
                                  @JsonProperty(ALTITUDE_INFO) AltitudeInfo altitudeInfo,
                                  @JsonProperty(VELOCITY) double velocity) {
        this.center = center;
        this.radius = radius;
        this.rotationType = rotationType;
        this.degreesToCover = degreesToCover;
        this.startingDegree = startingDegree;
        this.altitudeInfo = altitudeInfo;
        this.velocity = velocity;
    }

    @JsonProperty(CENTER)
    public Location getCenter() {
        return center;
    }

    @JsonProperty(RADIUS)
    public double getRadius() {
        return radius;
    }

    @JsonProperty(ROTATION_TYPE)
    public RotationType getRotationType() {
        return rotationType;
    }

    @JsonProperty(DEGREES_TO_COVER)
    public double getDegreesToCover() {
        return degreesToCover;
    }

    @JsonProperty(STARTING_DEGREE)
    public double getStartingDegree() {
        return startingDegree;
    }

    @JsonProperty(ALTITUDE_INFO)
    public AltitudeInfo getAltitudeInfo() {
        return altitudeInfo;
    }

    @JsonProperty(VELOCITY)
    public double getVelocity() {
        return velocity;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {
        try {
            DroneTask flyInCircle = controller.flightTasks().flyInCircle(
                    getCenter(),
                    getRadius(),
                    getRotationType(),
                    getDegreesToCover(),
                    getStartingDegree(),
                    getAltitudeInfo(),
                    getVelocity());
            return new TaskResponse(flyInCircle.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
