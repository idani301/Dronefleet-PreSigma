package eyesatop.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.util.geo.GimbalState;

/**
 * Created by einav on 28/03/2017.
 */
public class GimbalRequest {

    private static final String STATE = "gimbalState";
    private static final String PITCH_ENABLE = "pitchEnable";
    private static final String ROLL_ENABLE = "rollEnable";
    private static final String YAW_ENABLE = "yawEnable";

    private final GimbalState gimbalState;
    private final boolean pitchEnable;
    private final boolean rollEnable;
    private final boolean yawEnable;

    @JsonCreator
    public GimbalRequest(
            @JsonProperty(STATE)
            GimbalState gimbalState,

            @JsonProperty(PITCH_ENABLE)
            boolean pitchEnable,

            @JsonProperty(ROLL_ENABLE)
            boolean rollEnable,

            @JsonProperty(YAW_ENABLE)
            boolean yawEnable) {
        this.gimbalState = gimbalState;
        this.pitchEnable = pitchEnable;
        this.rollEnable = rollEnable;
        this.yawEnable = yawEnable;
    }

    public GimbalRequest addNewerRequest(GimbalRequest gimbalRequest){

        double pitch = gimbalState.getPitch();
        double roll = gimbalState.getRoll();
        double yaw = gimbalState.getYaw();
        boolean pitchEnable = isPitchEnable();
        boolean rollEnable = isRollEnable();
        boolean yawEnable = isYawEnable();

        if(gimbalRequest.pitchEnable){
            pitchEnable = true;
            pitch = gimbalRequest.getGimbalState().getPitch();
        }
        if(gimbalRequest.rollEnable){
            rollEnable = true;
            roll = gimbalRequest.getGimbalState().getRoll();
        }
        if(gimbalRequest.yawEnable){
            yawEnable = true;
            yaw = gimbalRequest.getGimbalState().getYaw();
        }

        return new GimbalRequest(new GimbalState(roll,pitch,yaw),pitchEnable,rollEnable,yawEnable);
    }

    @JsonProperty(STATE)
    public GimbalState getGimbalState() {
        return gimbalState;
    }

    @JsonProperty(PITCH_ENABLE)
    public boolean isPitchEnable() {
        return pitchEnable;
    }

    @JsonProperty(ROLL_ENABLE)
    public boolean isRollEnable() {
        return rollEnable;
    }

    @JsonProperty(YAW_ENABLE)
    public boolean isYawEnable() {
        return yawEnable;
    }

    @Override
    public String toString() {
        return "GimbalRequest{" +
                "gimbalState=" + (gimbalState == null ? "N/A" : gimbalState.toString()) +
                ", pitchEnable=" + pitchEnable +
                ", rollEnable=" + rollEnable +
                ", yawEnable=" + yawEnable +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GimbalRequest that = (GimbalRequest) o;

        if (pitchEnable != that.pitchEnable) return false;
        if (rollEnable != that.rollEnable) return false;
        if (yawEnable != that.yawEnable) return false;
        return gimbalState != null ? gimbalState.equals(that.gimbalState) : that.gimbalState == null;

    }

    @Override
    public int hashCode() {
        int result = gimbalState != null ? gimbalState.hashCode() : 0;
        result = 31 * result + (pitchEnable ? 1 : 0);
        result = 31 * result + (rollEnable ? 1 : 0);
        result = 31 * result + (yawEnable ? 1 : 0);
        return result;
    }
}
