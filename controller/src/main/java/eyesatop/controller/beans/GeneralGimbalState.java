package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;

/**
 * Created by Idan on 08/05/2018.
 */

public class GeneralGimbalState {

    private final static String STATE = "gimbalState";
    private final static String LOCK_OPTIONS = "gimbalLockOptions";
    private final static String LOCKED_LOCATION = "locationLocked";
    private final static String YAW_SHIFT = "yawDegreeFromLocation";

    private final GimbalState gimbalState;

    private final GimbalLockOptions gimbalLockOptions;
    private final Location locationLocked;
    private final Double yawDegreeFromLocation;

    @JsonCreator
    public GeneralGimbalState(
            @JsonProperty(STATE) GimbalState gimbalState,
            @JsonProperty(LOCK_OPTIONS) GimbalLockOptions gimbalLockOptions,
            @JsonProperty(LOCKED_LOCATION) Location locationLocked,
            @JsonProperty(YAW_SHIFT) Double yawDegreeFromLocation) {
        this.gimbalState = gimbalState;
        this.gimbalLockOptions = gimbalLockOptions;
        this.locationLocked = locationLocked;
        this.yawDegreeFromLocation = yawDegreeFromLocation;
    }

    @JsonProperty(STATE)
    public GimbalState getGimbalState() {
        return gimbalState;
    }

    @JsonProperty(LOCK_OPTIONS)
    public GimbalLockOptions getGimbalLockOptions() {
        return gimbalLockOptions;
    }

    @JsonProperty(LOCKED_LOCATION)
    public Location getLocationLocked() {
        return locationLocked;
    }

    @JsonProperty(YAW_SHIFT)
    public Double getYawDegreeFromLocation() {
        return yawDegreeFromLocation;
    }
}
