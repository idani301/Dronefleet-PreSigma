package eyesatop.unit.ui.specialfunctions.oblimapper;

/**
 * Created by Idan on 08/10/2017.
 */

public class ObliCircle {
    private final double altitudeFromGround;
    private final double radius;
    private final boolean isLookInside;
    private final double gimbalPosition;

    public ObliCircle(double altitudeFromGround, double radius, boolean isLookInside, double gimbalPosition) {
        this.altitudeFromGround = altitudeFromGround;
        this.radius = radius;
        this.isLookInside = isLookInside;
        this.gimbalPosition = gimbalPosition;
    }

    public double getRadius() {
        return radius;
    }

    public boolean isLookInside() {
        return isLookInside;
    }

    public double getGimbalPosition() {
        return gimbalPosition;
    }

    public double getAltitudeFromGround() {
        return altitudeFromGround;
    }
}
