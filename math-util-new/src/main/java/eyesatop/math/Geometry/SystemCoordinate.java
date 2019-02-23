package eyesatop.math.Geometry;

/**
 * Created by Einav on 15/06/2017.
 */

public class SystemCoordinate {

    private final Point2D center;
    private final double angle;

    public SystemCoordinate(Point2D center, double angle) {
        this.center = center;
        this.angle = angle;
    }

    public static SystemCoordinate systemCoordinateFromTwoPoint(Point2D center, Point2D point2D){
        if(center.distance(point2D) == 0)
            return new SystemCoordinate(center,0);
        return new SystemCoordinate(center,center.angle(point2D));
    }


    public Point2D getCenter() {
        return center;
    }

    public double getAngle() {
        return angle;
    }

}

