package eyesatop.util.geo.obstacles.shapes;

import eyesatop.math.Geometry.Point3D;
import eyesatop.util.geo.Shape3D;
import eyesatop.util.geo.Telemetry;

/**
 * Created by Idan on 17/10/2017.
 */

public class SphereShape implements Shape3D {

    private final double radius;

    public SphereShape(double radius) {
        this.radius = radius;
    }


    @Override
    public Point3D influence(Telemetry myCenter, Telemetry location) {
        Point3D distance = location.location().distance3DPoint(myCenter.location());
//        if (distance.getSphereRadius() > 50){
//            return Point3D.zero();
//        }
        double constNumber = Math.pow(radius,2);
        double force = constNumber/Math.pow(distance.getSphereRadius(),2);
        Point3D point3D = Point3D.ElevationAzimuthPointOfView(distance.getElevation(),distance.getAzimuth()).multipal(force);

        return point3D;
    }

    @Override
    public String toString() {
        return "SphereShape{" +
                "radius=" + radius +
                ", influence= 0.5*radius^2/d^2" +
                '}';
    }
}
