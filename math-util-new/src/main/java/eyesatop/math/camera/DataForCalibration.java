package eyesatop.math.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import eyesatop.math.Geometry.Line3D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.MathException;
import eyesatop.math.QuantaPoint;

/**
 * Created by Einav on 18/07/2017.
 */


public class DataForCalibration {

    private final ArrayList<Pixel> pixels;
    private final ArrayList<Point3D> chessPoints = new ArrayList<>();
    private final QuantaPoint sizeOfChessPoints;
    private final double distanceBetweenPoints;

    private ImageInfo imageInfo;
    private Point3D cameraLocation;

    public DataForCalibration(ArrayList<Pixel> pixels, QuantaPoint sizeOfChessPoints, double distanceBetweenPoints, CameraModule cameraModule) throws MathException {
        this.pixels = pixels;
        this.sizeOfChessPoints = sizeOfChessPoints;
        this.distanceBetweenPoints = distanceBetweenPoints;
        imageInfo = new ImageInfo(cameraModule,RotationMatrix3D.Body3DNauticalAngles(0,0,0),null,null);
        setAllChessPoints();
    }

    public DataForCalibration(File file, QuantaPoint sizeOfChessPoints, double distanceBetweenPoints, CameraModule cameraModule) throws IOException, ClassNotFoundException, MathException {
        pixels = readPixelToCalibrationFromFile(file);
        this.sizeOfChessPoints = sizeOfChessPoints;
        this.distanceBetweenPoints = distanceBetweenPoints;
        imageInfo = new ImageInfo(cameraModule,RotationMatrix3D.Body3DNauticalAngles(0,0,Math.toRadians(0)),null,null);
        setAllChessPoints();
    }

    public void setCameraModule(CameraModule cameraModule) throws MathException {
        imageInfo = new ImageInfo(cameraModule,imageInfo.getRotationMatrix3D(),null,null);
    }


    public ArrayList<Pixel> readPixelToCalibrationFromFile(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        ArrayList<Pixel> myArrayList = (ArrayList<Pixel>) in.readObject();
        in.close();
        return myArrayList;
    }

    public Point3D getCameraLocation() {
        return cameraLocation;
    }

    public RotationMatrix3D getRotationMatrix3D() {
        return imageInfo.getRotationMatrix3D();
    }

    public void setCameraLocation(Point3D cameraLocation) {
        this.cameraLocation = cameraLocation;
    }

    public ArrayList<Pixel> getPixels() {
        return pixels;
    }

    public ArrayList<Point3D> getPoint3dOnCamera(){
        ArrayList<Point3D> point3Ds = new ArrayList<>();
        ArrayList<Point3D> temp = getDirectionFromRealPoints();
        for (int i = 0; i < chessPoints.size(); i++) {
            point3Ds.add(temp.get(i).rotate(getRotationMatrix3D().getInverseRotationMatrix()));
        }
        return point3Ds;
    }

    public void setRotationMatrix3D(RotationMatrix3D rotationMatrix3D) {
        imageInfo = new ImageInfo(imageInfo.getCameraModule(),imageInfo.getRotationMatrix3D().times(rotationMatrix3D),null,null);
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    private void setAllChessPoints() throws MathException {
        for (int j = 0; j < sizeOfChessPoints.getN(); j++) {
            for (int k = 0; k < sizeOfChessPoints.getM(); k++) {
                Point3D point3D = Point3D.cartesianPoint(0, k * distanceBetweenPoints, -j * distanceBetweenPoints);
                chessPoints.add(point3D);
            }
        }
    }

    public ArrayList<Point3D> getDirectionFromPixels(){
        ArrayList<Point3D> directions = new ArrayList<>();
        for (int i = 0; i < chessPoints.size(); i++) {
            directions.add(imageInfo.getLineOfSightFromPixel(pixels.get(i),1));
        }
        return directions;
    }

    public ArrayList<Point3D> getDirectionFromRealPoints(){
        ArrayList<Point3D> directions = new ArrayList<>();
        for (int i = 0; i < chessPoints.size(); i++) {
            directions.add(chessPoints.get(i).vectorToPoint(cameraLocation).normal());
        }
        return directions;
    }

    public ArrayList<Line3D> getLinesFromPointsToFindEstimatedPoint() throws MathException {
        ArrayList<Line3D> line3Ds = new ArrayList<>();
        ArrayList<Point3D> directions = new ArrayList<>();
        System.out.println(imageInfo.getRotationMatrix3D());
        for (int i = 0; i < pixels.size(); i++) {
            directions.add(imageInfo.getLineOfSightFromPixel(pixels.get(i),1));
            line3Ds.add(new Line3D(chessPoints.get(i),directions.get(i)));
        }
       return line3Ds;
    }

    public ArrayList<Line3D> getLinesFromPointsToFindRotationMatrix(){
        ArrayList<Line3D> line3Ds = new ArrayList<>();
        ArrayList<Point3D> directions = new ArrayList<>();
        for (int i = 0; i < chessPoints.size(); i++) {
            directions.add(chessPoints.get(i).vectorToPoint(cameraLocation).normal());
            line3Ds.add(new Line3D(chessPoints.get(i),directions.get(i)));
        }
        return line3Ds;
    }

    public ArrayList<Line3D> getLinesFromEstimatedCameraLocationToPixel() throws MathException {
        ArrayList<Line3D> line3Ds = new ArrayList<>();
        ArrayList<Point3D> directions = new ArrayList<>();
        for (int i = 0; i < chessPoints.size(); i++) {
            directions.add(imageInfo.getLineOfSightFromPixel(pixels.get(i),1));
            line3Ds.add(new Line3D(cameraLocation,directions.get(i)));
        }
        return line3Ds;
    }

    public double RmsInTheRealSpace() throws MathException {
        double rms = 0;
        ArrayList<Line3D> line3Ds = getLinesFromEstimatedCameraLocationToPixel();
        for (int j = 0; j < line3Ds.size(); j++) {
            rms += Math.pow(line3Ds.get(j).getPointOnLineFromAxisX(0).distance(chessPoints.get(j)),2);
        }
        return Math.sqrt(rms/line3Ds.size());
    }

//    public double RmsInAngleSpace(){
//        ArrayList<Point3D> point3D1 = getDirectionFromRealPoints();
//        ArrayList<Point3D> point3D2 = getDirectionFromPixels();
//
//
//        for (int i = 0; i < point3D1.size(); i++) {
//
//        }
//    }

}
