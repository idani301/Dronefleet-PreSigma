package eyesatop.math.Geometry;

import java.io.Serializable;

import eyesatop.math.MathException;

/**
 * Created by Einav on 05/05/2017.
 */

public class Point2D implements Serializable{

    private final double x;
    private final double y;


    protected Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Point2D zero(){
        return new Point2D(0,0);
    }

    public static Point2D GeographicPoint(double north, double east){
        return new Point2D(north,east);
    }

    public static Point2D GeographicPointAzimuthAndRadius(double distance, double AzimuthRadians){

        double north = distance*Math.cos(AzimuthRadians);
        double east  = distance*Math.sin(AzimuthRadians);

        return Point2D.GeographicPoint(north,east);
    }

    public static Point2D cartesianPoint(double x, double y){
        return new Point2D(x,y);
    }

    public static Point2D polarPoint(double radius, double angle){

        double xx = radius*Math.sin(angle);
        double yy = radius*Math.cos(angle);
        return new Point2D(xx,yy);
    }

    public static Point2D pixelPoint(double u, double v){
        return new Point2D(u,-v);
    }

    public static Point2D getPointFromSystemCoordinate(double x, double y, SystemCoordinate systemCoordinate){
        Point2D point2D = new Point2D(x,y);
        point2D = point2D.rotate(-systemCoordinate.getAngle());
        return point2D.add(systemCoordinate.getCenter());
    }

    public static Point2D ellipsePointEccentricity(double r, double angle, Ellipsoid ellipsoid){
        double x = ellipsoid.getC() + r*Math.cos(angle);
        double y = r*Math.sin(angle);

        return new Point2D(x,y);
    }

    public double getX() {
        return x;
    }

    public double getNorth(){
        return x;
    }

    public double getEast(){
        return y;
    }

    public double getY() {
        return y;
    }

    public double getU(){
        return x;
    }

    public double getV(){
        return -y;
    }

    protected Point2D point2DInNewCoordinateSystem(SystemCoordinate systemCoordinate){
        double r = distance(systemCoordinate.getCenter());
        double xx = r*Math.cos(this.angle(systemCoordinate.getCenter()));
        double yy = r*Math.sin(this.angle(systemCoordinate.getCenter()));

        return new Point2D(xx,yy).rotate(systemCoordinate.getAngle());
    }

    public double getXFromReferencePoint(SystemCoordinate systemCoodrinate){
        return point2DInNewCoordinateSystem(systemCoodrinate).getX();
    }
    public double getYFromReferencePoint(SystemCoordinate systemCoodrinate){
        return point2DInNewCoordinateSystem(systemCoodrinate).getY();
    }

    public double getRadius(){
        return Math.sqrt(x*x + y*y);
    }

    public double getAngle(){
        if (x == 0 && y == 0)
            return 0;
        return Math.atan2(y,x);
    }

    public double getAngleDegree(){
        return Math.atan2(y,x)*180/Math.PI;
    }

    public double getEllipseAngle(Ellipsoid ellipsoid) {
        return Math.atan(y/(x- ellipsoid.getC()));
    }

    public double getEllipseRadius(Ellipsoid ellipsoid){
        return y/Math.sin(getEllipseAngle(ellipsoid));
    }

    public double distance(Point2D point2D){
        return Math.sqrt(Math.pow(x - point2D.x,2)+ Math.pow(y - point2D.y,2));
    }

    public double angle(Point2D point2D){
        return Math.atan2(y-point2D.y,x-point2D.x);
    }

    public double angleFromXaxis(Point2D point2D){
        return Math.atan2(x - point2D.x,y - point2D.y);
    }

    public Point2D add(Point2D point2D){
        return new Point2D(x + point2D.x,y + point2D.y);
    }

    public Point2D sub(Point2D point2D){
        return new Point2D(x - point2D.x,y - point2D.y);
    }

    public double angleBetweenToVectors(Point2D point2D){
        return Math.acos((x*point2D.x + y*point2D.y)/(size()*point2D.size()));
    }

    public double size(){
        return distance(new Point2D(0,0));
    }

    public Point2D rotate(double angle){
        double xx = x*Math.cos(angle) - y*Math.sin(angle);
        double yy = x*Math.sin(angle) + y*Math.cos(angle);

        return new Point2D(xx,yy);
    }

    public Point2D vectorToPoint(Point2D point2D){
        return new Point2D(x-point2D.x,y-point2D.y);
    }

    public Point2D normal() {
        double size = getRadius();
        if (size == 0)
            Point2D.zero();
        return new Point2D(x/size,y/size);
    }

    public double getDJIAzimuthDegree(){
        return Math.atan2(y,x)*180/Math.PI;
    }

    public Point2D rotateToNorth(){
        return new Point2D(size(),0);
    }

    public Point2D minus(Point2D point2D){
        return new Point2D(x - point2D.x,y - point2D.y);
    }

    public Point3D getPoint3D(){
        return Point3D.cartesianPoint(x,y,0);
    }

    public Point2D multiple(double t){
        return new Point2D(x*t,y*t);
    }

    public Point2D middlePoint(Point2D point2D){
        return new Point2D((x + point2D.x)/2,(y + point2D.y)/2);
    }

    public double getSlope(Point2D point2D) throws MathException {
        if (x - point2D.x == 0){
            throw new MathException(MathException.MathExceptionCause.infinity);
        }
        return (y - point2D.y)/(x - point2D.x);
    }

    public double getAngle(Point2D point2D){
        double r = getRadius()*point2D.getRadius();
        if (r == 0)
            return 0;
        double v = (x*point2D.x + y*point2D.y)/r;
        double angle = 0;
        if (Math.abs(v) <= 1) {
            angle = Math.acos(v);
        }
        Point2D test = point2D.rotate(angle);

        if(Math.abs((x*test.x + y*test.y)/r - 1) < 0.00001)
            return -angle;
        return angle;
    }

    public boolean equals(Point2D point2D) {
        if (x == point2D.x && y == point2D.y)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "Point2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
