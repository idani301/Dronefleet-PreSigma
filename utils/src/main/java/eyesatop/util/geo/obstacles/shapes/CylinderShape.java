package eyesatop.util.geo.obstacles.shapes;

import eyesatop.math.Geometry.Point3D;
import eyesatop.util.geo.Shape3D;
import eyesatop.util.geo.Telemetry;

/**
 * Created by Idan on 17/10/2017.
 */

public class CylinderShape implements Shape3D {

    private final double height;
    private final double radius;

    public CylinderShape(double height, double radius) {
        this.height = height;
        this.radius = radius;
    }


    @Override
    public Point3D influence(Telemetry myCenter, Telemetry location) {
        return null;
    }
}
