package eyesatop.math.Geometry.EarthGeometry;

import eyesatop.math.Geometry.Ellipsoid;
import eyesatop.math.Geometry.Point2D;

/**
 * Created by Einav on 29/06/2017.
 */

public class Datum {

    private final Ellipsoid earthEllipsoid;
    private final double scale;
    private final Point2D false_Point;

    public Datum(Ellipsoid earthEllipsoid, double scale, Point2D false_Point) {
        this.earthEllipsoid = earthEllipsoid;
        this.scale = scale;
        this.false_Point = false_Point;
    }

    public Ellipsoid getEarthEllipsoid() {
        return earthEllipsoid;
    }

    public double getScale() {
        return scale;
    }

    public Point2D getFalse_Point() {
        return false_Point;
    }
}
