package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.util.geo.Location;

/**
 * Created by Idan on 08/05/2018.
 */

public class GeneralDroneState {

    private final static String LOCATION = "location";
    private final static String ASL = "aboveSeaLevel";
    private final static String CAMERA_STATE = "cameraState";
    private final static String GIMBAL_STATE = "gimbalState";
    private final static String HEADING = "heading";

    private final Location location;
    private final Double aboveSeaLevel;
    private final GeneralCameraState cameraState;
    private final GeneralGimbalState gimbalState;
    private final Double heading;

    @JsonCreator
    public GeneralDroneState(
            @JsonProperty(LOCATION) Location droneLocation,
            @JsonProperty(ASL) Double aboveSeaLevel,
            @JsonProperty(CAMERA_STATE) GeneralCameraState cameraState,
            @JsonProperty(GIMBAL_STATE) GeneralGimbalState gimbalState,
            @JsonProperty(HEADING) Double heading) {
        this.location = droneLocation;
        this.aboveSeaLevel = aboveSeaLevel;
        this.cameraState = cameraState;
        this.gimbalState = gimbalState;
        this.heading = heading;
    }

    @JsonProperty(LOCATION)
    public Location getLocation() {
        return location;
    }

    @JsonProperty(ASL)
    public Double getAboveSeaLevel() {
        return aboveSeaLevel;
    }

    @JsonProperty(CAMERA_STATE)
    public GeneralCameraState getCameraState() {
        return cameraState;
    }

    @JsonProperty(GIMBAL_STATE)
    public GeneralGimbalState getGimbalState() {
        return gimbalState;
    }

    public Double getHeading() {
        return heading;
    }
}
