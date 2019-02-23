package eyesatop.util.geo;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 23/10/2017.
 */

public class WorldSpeeds {
    private final double roll;
    private final double pitch;
    private final double vertical;

    public WorldSpeeds(double roll, double pitch, double vertical) {
        this.roll = roll;
        this.pitch = pitch;
        this.vertical = vertical;
    }

    public double getRoll() {
        return roll;
    }

    public double getPitch() {
        return pitch;
    }

    public double getVertical() {
        return vertical;
    }

    public Point3D getVelocities(){
        return Point3D.cartesianPoint(getRoll(),getPitch(),getVertical());
    }

    @Override
    public String toString() {
        return "WorldSpeeds{" +
                "roll=" + roll +
                ", pitch=" + pitch +
                ", vertical=" + vertical +
                '}';
    }
}
