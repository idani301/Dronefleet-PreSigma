package eyesatop.controller.mission.flightplans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.CameraActionType;

/**
 * Created by Idan on 24/12/2017.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CircleFlightPlanInfo.class),
        @JsonSubTypes.Type(value = RadiatorFlightPlanInfo.class)
})
public abstract class FlightPlanInfo {

    protected final static String NAME = "name";
    protected final static String ALTITUDE = "altitudeInfo";
    protected final static String VELOCITY = "velocity";
    protected final static String PITCH_DEGREE = "gimbalPitchDegree";
    protected final static String CAMERA_ACTION = "cameraActionType";
    protected final static String OPTICAL_ZOOM = "opticalZoomLevel";
    protected final static String PHOTO_INTERVAL_NUMBER = "shootPhotoInIntervalNumber";
    protected final static String HEADING = "heading";
    protected final static String HOVER_TIME = "hoverTime";

    private final String name;
    private final AltitudeInfo altitudeInfo;
    private final Double velocity;
    private final Integer gimbalPitchDegree;
    private final CameraActionType cameraActionType;
    private final Integer shootPhotoInIntervalNumber;
    private final Integer heading;
    private final Integer hoverTime;

    private final Double opticalZoom;

    protected FlightPlanInfo(
            @JsonProperty(NAME) String name,
            @JsonProperty(ALTITUDE) AltitudeInfo altitudeInfo,
            @JsonProperty(VELOCITY) Double velocity,
            @JsonProperty(PITCH_DEGREE) Integer gimbalPitchDegree,
            @JsonProperty(CAMERA_ACTION) CameraActionType cameraActionType,
            @JsonProperty(PHOTO_INTERVAL_NUMBER) Integer shootPhotoInIntervalNumber,
            @JsonProperty(HEADING) Integer heading,
            @JsonProperty(HOVER_TIME) Integer hoverTime,
            @JsonProperty(OPTICAL_ZOOM) Double opticalZoom) {
        this.name = name;
        this.altitudeInfo = altitudeInfo;
        this.velocity = velocity;
        this.gimbalPitchDegree = gimbalPitchDegree;
        this.cameraActionType = cameraActionType;
        this.shootPhotoInIntervalNumber = shootPhotoInIntervalNumber;
        this.heading = heading;
        this.hoverTime = hoverTime;
        this.opticalZoom = opticalZoom;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(OPTICAL_ZOOM)
    public Double getOpticalZoom() {
        return opticalZoom;
    }

    @JsonProperty(ALTITUDE)
    public AltitudeInfo getAltitudeInfo() {
        return altitudeInfo;
    }

    @JsonProperty(VELOCITY)
    public Double getVelocity() {
        return velocity;
    }

    @JsonProperty(PITCH_DEGREE)
    public Integer getGimbalPitchDegree() {
        return gimbalPitchDegree;
    }

    @JsonProperty(CAMERA_ACTION)
    public CameraActionType getCameraActionType() {
        return cameraActionType;
    }

    @JsonProperty(PHOTO_INTERVAL_NUMBER)
    public Integer getShootPhotoInIntervalNumber() {
        return shootPhotoInIntervalNumber;
    }

    @JsonProperty(HEADING)
    public Integer getHeading() {
        return heading;
    }

    @JsonProperty(HOVER_TIME)
    public Integer getHoverTime() {
        return hoverTime;
    }

    public abstract FlightPlanComponentType componentType();
}
