package eyesatop.util.geo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by einav on 22/01/2017.
 */
public class GimbalState {

    private static final String ROLL = "roll";
    private static final String PITCH = "pitch";
    private static final String YAW = "yaw";

    private final double roll;
    private final double pitch;
    private final double yaw;

    @JsonCreator
    public GimbalState(
            @JsonProperty(ROLL)
            double roll,

            @JsonProperty(PITCH)
            double pitch,

            @JsonProperty(YAW)
            double yaw) {
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @JsonProperty(ROLL)
    public double getRoll() {
        return roll;
    }

    @JsonProperty(PITCH)
    public double getPitch() {
        return pitch;
    }

    @JsonProperty(YAW)
    public double getYaw() {
        return yaw;
    }

    public GimbalState roll(double roll){
        return new GimbalState(roll,pitch,yaw);
    }

    public GimbalState pitch(double pitch){
        return new GimbalState(roll,pitch,yaw);
    }

    public GimbalState yaw(double yaw){
        return new GimbalState(roll,pitch,yaw);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GimbalState that = (GimbalState) o;

        if (Double.compare(that.roll, roll) != 0) return false;
        if (Double.compare(that.pitch, pitch) != 0) return false;
        return Double.compare(that.yaw, yaw) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(roll);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(pitch);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yaw);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "GimbalState{" +
                "roll=" + roll +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                '}';
    }
}
