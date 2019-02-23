package eyesatop.controller.mission.flightplans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.CameraActionType;
import eyesatop.controller.beans.CircleRotationInfo;
import eyesatop.util.geo.Location;

/**
 * Created by Idan on 24/12/2017.
 */
public class CircleFlightPlanInfo extends FlightPlanInfo {

    private final static String CENTER = "centerLocation";
    private final static String RADIUS = "radiusReached";
    private final static String IS_LOOK_TO_MID = "isLookToMid";
    private final static String PHOTO_NUMBER = "photoNumber";
    private final static String CIRCLE_INFO = "circleRotationInfo";

    private final Integer photoNumber;
    private final Location centerLocation;
    private final Integer radius;
    private final CircleRotationInfo circleRotationInfo;

    private final Boolean isLookToMid;

    public CircleFlightPlanInfo(@JsonProperty(NAME) String name,
                                @JsonProperty(ALTITUDE) AltitudeInfo altitudeInfo,
                                @JsonProperty(VELOCITY) Double velocity,
                                @JsonProperty(PITCH_DEGREE) Integer gimbalPitchDegree,
                                @JsonProperty(CAMERA_ACTION) CameraActionType cameraActionType,
                                @JsonProperty(PHOTO_INTERVAL_NUMBER) Integer shootPhotoInIntervalNumber,
                                @JsonProperty(HEADING) Integer heading,
                                @JsonProperty(HOVER_TIME) Integer hoverTime,
                                @JsonProperty(CENTER) Location centerLocation,
                                @JsonProperty(RADIUS) Integer radius,
                                @JsonProperty(CIRCLE_INFO) CircleRotationInfo circleRotationInfo,
                                @JsonProperty(IS_LOOK_TO_MID) Boolean isLookToMid,
                                @JsonProperty(OPTICAL_ZOOM) Double opticalZoom,
                                @JsonProperty(PHOTO_NUMBER) Integer photoNumber) {
        super(name,altitudeInfo,velocity,gimbalPitchDegree,cameraActionType,shootPhotoInIntervalNumber,heading,hoverTime, opticalZoom);
        this.centerLocation = centerLocation;
        this.radius = radius;
        this.circleRotationInfo = circleRotationInfo;
        this.isLookToMid = isLookToMid;
        this.photoNumber = photoNumber;
    }

    @JsonProperty(PHOTO_NUMBER)
    public Integer getPhotoNumber() {
        return photoNumber;
    }

    @JsonProperty(CENTER)
    public Location getCenterLocation() {
        return centerLocation;
    }

    @JsonProperty(RADIUS)
    public Integer getRadius() {
        return radius;
    }

    @JsonProperty(IS_LOOK_TO_MID)
    public Boolean getLookToMid() {
        return isLookToMid;
    }

    @JsonProperty(CIRCLE_INFO)
    public CircleRotationInfo getCircleRotationInfo() {
        return circleRotationInfo;
    }

    @Override
    @JsonIgnore
    public FlightPlanComponentType componentType() {
        return FlightPlanComponentType.CIRCLE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleFlightPlanInfo info = (CircleFlightPlanInfo) o;

        if (getName() != null ? !getName().equals(info.getName()) : info.getName()!= null) return false;
        if (centerLocation != null ? !centerLocation.equals(info.centerLocation) : info.centerLocation != null)
            return false;
        if (radius != null ? !radius.equals(info.radius) : info.radius != null) return false;
        if (getAltitudeInfo() != null ? !getAltitudeInfo().equals(info.getAltitudeInfo()) : info.getAltitudeInfo() != null)
            return false;
        if (getVelocity() != null ? !getVelocity().equals(info.getVelocity()) : info.getVelocity() != null)
            return false;
        if (circleRotationInfo != null ? !circleRotationInfo.equals(info.circleRotationInfo) : info.circleRotationInfo != null)
            return false;
        if (getGimbalPitchDegree() != null ? !getGimbalPitchDegree().equals(info.getGimbalPitchDegree()) : info.getGimbalPitchDegree() != null)
            return false;
        if(getPhotoNumber() != null ? !getPhotoNumber().equals(info.getPhotoNumber()) : info.getPhotoNumber() != null){
            return false;
        }
        return isLookToMid != null ? isLookToMid.equals(info.isLookToMid) : info.isLookToMid == null;
    }
}
