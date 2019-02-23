package eyesatop.math.Geometry;

import Jama.Matrix;
import eyesatop.math.MathException;

/**
 * Created by Einav on 06/05/2017.
 */

public class Point3D implements Comparable<Point3D>{

    private static final Point3D ZERO = new Point3D(0,0,0);

    private final double x;
    private final double y;
    private final double z;

    protected Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Point3D cartesianPoint(double x, double y, double z){
        return new Point3D(x,y,z);
    }

    public static Point3D cartesianPoint(Point2D point2D, double z){
        return new Point3D(point2D.getX(),point2D.getY(),z);
    }

    public static Point3D spherePoint(double radius, double teta, double phi){

        double z = radius*Math.cos(phi);
        double ro = radius*Math.sin(phi);
        double x = ro*Math.cos(teta);
        double y = ro*Math.sin(teta);

        return new Point3D(x,y,z);
    }

    public static Point3D cameraPointOfView(double x, double y, double z){
        return new Point3D(y,z,x);
    }

    public static Point3D cameraSpherePointOfView(double teta, double psai){
        double x = Math.cos(teta);
        double ro = Math.sin(teta);
        double y = ro*Math.cos(psai);
        double z = ro*Math.sin(psai);

        return new Point3D(x,y,z);
    }

    public static Point3D pointFromHorizontalDistanceAltitudeDistanceAzimuth(double horizontalDistance, double deltaHeights, double azimuth){
        double z = deltaHeights;
        double x = horizontalDistance*Math.cos(azimuth);
        double y = horizontalDistance*Math.sin(azimuth);

        return new Point3D(x,y,z);
    }

    public static Point3D ElevationAzimuthPointOfView(double elevationRadian, double azimuthRadian){

        double z = Math.sin(elevationRadian);
        double ro = Math.cos(elevationRadian);
        double x = ro*Math.cos(azimuthRadian);
        double y = ro*Math.sin(azimuthRadian);

        return new Point3D(x,y,z);
    }

    public static Point3D ElevationAzimuthCameraOfView(double elevation, double azimuth){

        double z = Math.sin(-elevation);
        double ro = Math.cos(-elevation);
        double x = ro*Math.cos(azimuth);
        double y = ro*Math.sin(azimuth);

        return new Point3D(x,y,z);
    }

    public double getSphereRadius(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    public double get2dRadius(){
        return Math.sqrt(x*x + y*y);
    }

    public double getTeta(){
        return Math.atan2(y,x);
    }

    public double getAzimuthDegree(){
        return Math.toDegrees(Math.atan2(y,x));
    }

    public double getAzimuth(){
        return Math.atan2(y,x);
    }

    public double getPhi(){
        return Math.acos(z/getSphereRadius());
    }

    public double getElevationDegree(){
        return Math.toDegrees(Math.asin(z/getSphereRadius()));
    }

    public double getElevation(){
        return Math.asin(z/getSphereRadius());
    }


    public double getTetaCameraPointOfView(){
        return Math.acos(x/getSphereRadius());
    }

    public double getPsaiCameraPointOfView(){
        return Math.atan2(y,z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double[] getPoint3DAsArray(){
        return new double[]{x,y,z};
    }

    public double getZCameraPointOfView(){
        return y;
    }

    public double getYCameraPointOfView(){
        return z;
    }

    public double getXCameraPointOfView(){
        return x;
    }

    public double getXOnZ1Plane() throws MathException {
        if (z == 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        double scale = 1/z;
        return x*scale;
    }

    public double getYOnZ1Plane() throws MathException {
        if (z == 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        double scale = 1/z;
        return y*scale;
    }

    public double getZOnX1Plane() throws MathException {
        if (x == 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        double scale = 1/x;
        return z*scale;
    }

    public double getYOnX1Plane() throws MathException {
        if (x == 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        double scale = 1/x;
        return y*scale;
    }

    public double getXOnY1Plane() throws MathException {
        if (y == 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        double scale = 1/y;
        return x*scale;
    }

    public double getZOnY1Plane() throws MathException {
        if (y == 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        double scale = 1/y;
        return z*scale;
    }


    public Point3D rotate(RotationMatrix3D rotationMatrix3D){

        double[][] matrixVectorParameters = {{x},{y},{z}};

        Matrix matrixVector = new Matrix(matrixVectorParameters);
        Matrix VectorResult = rotationMatrix3D.getRotationMatrix().times(matrixVector);

        return new Point3D(VectorResult.get(0, 0), VectorResult.get(1, 0), VectorResult.get(2, 0));
    }

    public double distance(Point3D point3D){
        return Math.sqrt(Math.pow(x - point3D.x,2) + Math.pow(y - point3D.y,2) + Math.pow(z-point3D.z,2));
    }

    public double horizontalDistance(Point3D point3D){
        return Math.sqrt(Math.pow(x - point3D.x,2) + Math.pow(y - point3D.y,2));
    }

    public Point3D vectorToPoint(Point3D point3D){

        return new Point3D(x-point3D.x,y-point3D.y,z-point3D.z);

    }

    public Point3D normal(){
        double size = getSphereRadius();
        return new Point3D(x/size,y/size,z/size);
    }

    public Point3D multipal(double c){
        return new Point3D(c*x,c*y,c*z);
    }

    public double dot(Point3D point3D){
        return x*point3D.x + y*point3D.y + z*point3D.z;
    }

    public Point3D minus(Point3D point3D){
        return new Point3D(x-point3D.x,y-point3D.y,z-point3D.z);
    }

    public Point3D multiple(Matrix matrix){

        double[][] matrixVectorParameters = {{x,y,z}};

        Matrix matrixVector = new Matrix(matrixVectorParameters);
        Matrix VectorResult = matrixVector.times(matrix);

        return new Point3D(VectorResult.get(0, 0), VectorResult.get(0, 1), VectorResult.get(0, 2));

    }

    public Point3D add(Point3D point3D){
        return new Point3D(x+point3D.x,y+point3D.y,z+point3D.z);
    }

    public Point2D getHorizontalVector(){
        return Point2D.cartesianPoint(x,y);
    }

//    @Override
//    public String toString() {
//        return "Point3D{" +
//                "x=" + x +
//                ", y=" + y +
//                ", z=" + z +
//                '}';
//    }

    public String toString(){
        return "Point3D{" +
                "Elevation=" + getElevationDegree() +
                ", Azimuth=" + getAzimuthDegree() +
                '}';
    }

    public String toStringCartesian(){
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public static Point3D zero() {
        return ZERO;
    }

    @Override
    public int compareTo(Point3D point3D) {
        double r = getSphereRadius();
        double r1 = point3D.getSphereRadius();
        if (r > r1)
            return 1;
        if (r1 < r)
            return -1;
        return 0;
    }
}
