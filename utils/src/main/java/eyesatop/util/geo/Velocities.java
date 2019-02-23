package eyesatop.util.geo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by einav on 24/01/2017.
 */
public class Velocities {
    private static final String X = "x";
    private static final String Y = "y";
    private static final String Z = "z";

    private final double x;
    private final double y;
    private final double z;

    @JsonIgnore
    public Point3D toPoint3D(){
        return Point3D.cartesianPoint(x,y,z);
    }

    @JsonCreator
    public Velocities(
            @JsonProperty(X)
            double x,

            @JsonProperty(Y)
            double y,

            @JsonProperty(Z)
            double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @JsonProperty(X)
    public double getX() {
        return x;
    }

    @JsonProperty(Y)
    public double getY() {
        return y;
    }

    @JsonProperty(Z)
    public double getZ() {
        return z;
    }

    @JsonIgnore
    public double getVelocity(){
        return Math.pow(x*x + y*y,0.5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Velocities that = (Velocities) o;

        if (Double.compare(that.x, x) != 0) return false;
        if (Double.compare(that.y, y) != 0) return false;
        return Double.compare(that.z, z) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Velocities{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
