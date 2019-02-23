package eyesatop.math.Geometry;

/**
 * Created by Einav on 29/06/2017.
 */

public class Ellipsoid {

    private final Point3D center;
    private final double a;
    private final double b;

    public Ellipsoid(Point3D center, double a, double b) {
        this.center = center;
        this.a = a;
        this.b = b;
    }

    public Ellipsoid(double a, double b){
        this.a = a;
        this.b = b;
        center = Point3D.zero();
    }

    public static Ellipsoid datumEllipse(Point3D deltaCenterFromWGS84, double a, double firstEccentricitySquared) {

        double b = a * Math.sqrt(1 - firstEccentricitySquared);
        return new Ellipsoid(deltaCenterFromWGS84, a, b);
    }

    public static Ellipsoid FlatteningEllipse(Point3D deltaCenter, double a, double f){
        return new Ellipsoid(deltaCenter,a,a-a*f);
    }

    public Point3D getCenter() {
        return center;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC(){
        return a*getFirstEccentricity();
    }

    public double getFlattening(){
        return (a-b)/a;
    }

    public double getFirstEccentricitySquared(){
        return 1 - (b*b)/(a*a);
    }

    public double getFirstEccentricity(){
        return Math.sqrt(1 - (b*b)/(a*a));
    }

    public double getRadiusOnEllipse(double angle){
        double x = a*(1-getFirstEccentricitySquared());
        double y = 1 + getFirstEccentricity()*Math.cos(angle);

        return x/y;
    }

    public Point2D getPointFromAngleOnEllipse(double angle){
        return Point2D.ellipsePointEccentricity(getRadiusOnEllipse(angle),angle,this);
    }

    @Override
    public String toString() {
        return "Ellipsoid{" +
                "center=" + center +
                ", a=" + a +
                ", b=" + b +
                '}';
    }
}
