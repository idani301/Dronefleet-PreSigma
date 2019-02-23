package eyesatop.controller.mission.flightplans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.CameraActionType;
import eyesatop.util.geo.Location;

/**
 * Created by Idan on 01/05/2018.
 */

public class RadiatorFlightPlanInfo extends FlightPlanInfo {

    private final static String CENTER = "centerLocation";
    private final static String GAP = "gap";
    private final static String LENGTH = "length";
    private final static String WIDTH = "width";
    private final static String ROTATION = "degree";

    private final Location centerLocation;
    private final Integer gap;
    private final Integer length;
    private final Integer width;
    private final Integer degree;

    @JsonCreator
    public RadiatorFlightPlanInfo(
            @JsonProperty(NAME) String name,
            @JsonProperty(CENTER) Location centerLocation,
            @JsonProperty(ALTITUDE) AltitudeInfo altitudeInfo,
            @JsonProperty(PITCH_DEGREE) Integer gimbalPitch,
            @JsonProperty(VELOCITY) Double velocity,
            @JsonProperty(GAP) Integer gap,
            @JsonProperty(LENGTH) Integer length,
            @JsonProperty(WIDTH) Integer width,
            @JsonProperty(ROTATION) Integer degree,
            @JsonProperty(CAMERA_ACTION) CameraActionType cameraActionType,
            @JsonProperty(PHOTO_INTERVAL_NUMBER) Integer shootPhotoInIntervalNumber,
            @JsonProperty(HEADING) Integer heading,
            @JsonProperty(HOVER_TIME) Integer hoverTime,
            @JsonProperty(OPTICAL_ZOOM) Double opticalZoom) {
        super(name,altitudeInfo,velocity,gimbalPitch,cameraActionType,shootPhotoInIntervalNumber,heading,hoverTime, opticalZoom);
        this.centerLocation = centerLocation;
        this.gap = gap;
        this.length = length;
        this.width = width;
        this.degree = degree;
    }

    @JsonProperty(CENTER)
    public Location getCenterLocation() {
        return centerLocation;
    }

    @JsonProperty(GAP)
    public Integer getGap() {
        return gap;
    }

    @JsonProperty(LENGTH)
    public Integer getLength() {
        return length;
    }

    @JsonProperty(WIDTH)
    public Integer getWidth() {
        return width;
    }

    @JsonProperty(ROTATION)
    public Integer getDegree() {
        return degree;
    }

    @Override
    @JsonIgnore
    public FlightPlanComponentType componentType() {
        return FlightPlanComponentType.RADIATOR;
    }

    @Override
    @JsonIgnore
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RadiatorFlightPlanInfo that = (RadiatorFlightPlanInfo) o;

        if (getName() != null ? !getName().equals(that.getName()) : that.getName()!= null) return false;
        if (centerLocation != null ? !centerLocation.equals(that.centerLocation) : that.centerLocation != null)
            return false;
        if (getAltitudeInfo() != null ? !getAltitudeInfo().equals(that.getAltitudeInfo()) : that.getAltitudeInfo() != null)
            return false;
        if (getVelocity() != null ? !getVelocity().equals(that.getVelocity()) : that.getVelocity() != null)
            return false;
        if (getGimbalPitchDegree() != null ? !getGimbalPitchDegree().equals(that.getGimbalPitchDegree()) : that.getGimbalPitchDegree() != null)
            return false;
        if (gap != null ? !gap.equals(that.gap) : that.gap != null) return false;
        if (length != null ? !length.equals(that.length) : that.length != null) return false;
        if (width != null ? !width.equals(that.width) : that.width != null) return false;
        return degree != null ? degree.equals(that.degree) : that.degree == null;
    }

}
