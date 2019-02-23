package eyesatop.util.geo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ControlAxes /* Nope -- Not a typo: (Many axis) == Axes */ {

    private static final String PITCH = "pitch";
    private static final String ROLL = "roll";
    private static final String YAW = "yaw";
    private static final String Z = "z";

    private final double pitch;
    private final double roll;
    private final double yaw;
    private final double z;

    @JsonCreator
    public ControlAxes(
            @JsonProperty(PITCH)
            double pitch,

            @JsonProperty(ROLL)
            double roll,

            @JsonProperty(YAW)
            double yaw,

            @JsonProperty(Z)
            double z) {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
        this.z = z;
    }

    @JsonProperty(PITCH)
    public double getPitch() {
        return pitch;
    }

    @JsonProperty(ROLL)
    public double getRoll() {
        return roll;
    }

    @JsonProperty(YAW)
    public double getYaw() {
        return yaw;
    }

    @JsonProperty(Z)
    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ControlAxes that = (ControlAxes) o;

        if (Double.compare(that.pitch, pitch) != 0) return false;
        if (Double.compare(that.roll, roll) != 0) return false;
        if (Double.compare(that.yaw, yaw) != 0) return false;
        return Double.compare(that.z, z) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(pitch);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(roll);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(yaw);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ControlAxes{" +
                "pitch=" + pitch +
                ", roll=" + roll +
                ", yaw=" + yaw +
                ", z=" + z +
                '}';
    }
}
