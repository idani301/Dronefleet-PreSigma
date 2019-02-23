package eyesatop.util.geo.functions;

import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.util.geo.Shape3D;
import eyesatop.util.geo.Telemetry;

/**
 * Created by Einav on 01/11/2017.
 */

public class LinearGoToPointShape3D implements Shape3D {

    private final double slope;

    public LinearGoToPointShape3D(double slope) {
        this.slope = slope;
    }

    @Override
    public Point3D influence(Telemetry myCenter, Telemetry location) {

        double forceSize = slope*myCenter.location().distance(location.location());
        double friction = myCenter.velocities().toPoint3D().get2dRadius()*6;
        if (friction < 1 || forceSize > 40)
            friction = 1;
        Angle azimuth = Angle.angleDegree(myCenter.location().az(location.location()));
        Point2D force = Point2D.GeographicPointAzimuthAndRadius(forceSize/friction,azimuth.radian());


        return Point3D.cartesianPoint(force,0);
    }
}
