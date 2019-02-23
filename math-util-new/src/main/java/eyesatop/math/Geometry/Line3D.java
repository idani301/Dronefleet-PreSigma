package eyesatop.math.Geometry;

/**
 * Created by Einav on 25/05/2017.
 */

public class Line3D {

    private final Point3D point3D;
    private final Point3D direction;

    public Line3D(Point3D point3D, Point3D direction) {
        this.point3D = point3D;
        this.direction = direction.normal();
    }

    public static Line3D getLineOutOfTwoPoints(Point3D point3D1, Point3D point3D2){

        Point3D direction = point3D2.vectorToPoint(point3D1).normal();
        return new Line3D(point3D1,direction);
    }

    public Point3D getPointOnLine(double distanceFromStartingPoint){
        return direction.multipal(distanceFromStartingPoint).add(point3D); // p = t*v + v0
    }

    public Point3D getPointOnLineFromAxisX(double x){
        return getPointOnLine((x-point3D.getX())/direction.getX());
    }
    public Point3D getPointOnLineFromAxisY(double y){
        return getPointOnLine((y-point3D.getY())/direction.getY());
    }
    public Point3D getPointOnLineFromAxisZ(double z){
        return getPointOnLine((z-point3D.getZ())/direction.getZ());
    }

    public double distance(Point3D point3D){
        double t = -this.point3D.minus(point3D).dot(direction);
        return getPointOnLine(t).distance(point3D);
    }

    public Point3D getPoint3D() {
        return point3D;
    }

    public Point3D getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "Line3D{" +
                "point3D=" + point3D.toStringCartesian() +
                ", direction=" + direction.toStringCartesian() +
                '}';
    }
}
