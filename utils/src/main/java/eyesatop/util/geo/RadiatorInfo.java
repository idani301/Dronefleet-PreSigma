package eyesatop.util.geo;

import eyesatop.math.Geometry.Angle;

/**
 * Created by Einav on 07/11/2017.
 */

public class RadiatorInfo {

    private final LocationGroup radiatorWayPoints;
    private final double gap;
    private final double velocity;
    private final double altitude;
    private final Angle rotation;

    public RadiatorInfo(LocationGroup radiatorWayPoints, double gap, double velocity, double altitude, Angle rotation) {
        this.radiatorWayPoints = radiatorWayPoints;
        this.gap = gap;
        this.velocity = velocity;
        this.altitude = altitude;
        this.rotation = rotation;
    }

    public LocationGroup getRadiatorWayPoints() {
        return radiatorWayPoints;
    }

    public double getGap() {
        return gap;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getAltitude() {
        return altitude;
    }

    public Angle getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return "RadiatorInfo{" +
                "radiatorWayPoints=" + radiatorWayPoints +
                '}';
    }
}
