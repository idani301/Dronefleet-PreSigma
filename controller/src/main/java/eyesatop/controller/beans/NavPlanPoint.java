package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.util.geo.Location;

public class NavPlanPoint {

    private static final String LOCATION = "location";
    private static final String ALTITUDE_INFO = "altitudeInfo";
    private static final String MAX_VELOCITY = "maxVelocity";
    private static final String RADIUS_REACHED = "radiusReached";

    private final Location location;
    private final AltitudeInfo altitudeInfo;
    private final double maxVelocity;
    private final double radiusReached;

    @JsonCreator
    public NavPlanPoint(@JsonProperty(LOCATION) Location location,
                        @JsonProperty(ALTITUDE_INFO) AltitudeInfo altitudeInfo,
                        @JsonProperty(MAX_VELOCITY) double maxVelocity,
                        @JsonProperty(RADIUS_REACHED) double radiusReached) {
        this.location = location;
        this.altitudeInfo = altitudeInfo;
        this.maxVelocity = maxVelocity == 0 ? 5D : maxVelocity;
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

    @JsonProperty(MAX_VELOCITY)
    public double getMaxVelocity() {
        return maxVelocity;
    }

    @JsonProperty(RADIUS_REACHED)
    public double getRadiusReached() {
        return radiusReached;
    }
}
