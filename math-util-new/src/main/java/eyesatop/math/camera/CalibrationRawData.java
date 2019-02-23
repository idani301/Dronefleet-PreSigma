package eyesatop.math.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import eyesatop.eyesatop.camera.RawData;
import eyesatop.math.Geometry.EstimatedPoint;
import eyesatop.math.Geometry.Line3D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.MathException;
import eyesatop.math.QuantaPoint;

/**
 * Created by Einav on 26/07/2017.
 */

public class CalibrationRawData {

    private final RawData rawData;
    private final ImageInfo imageInfo;

    public CalibrationRawData(RawData rawData, ImageInfo imageInfo) {
        this.rawData = rawData;
        this.imageInfo = imageInfo;
    }

    public CalibrationRawData(File file, QuantaPoint sizeOfChessPoints, double distanceBetweenPoints, ImageInfo imageInfo) throws IOException, ClassNotFoundException, MathException {
        this.imageInfo = imageInfo;
        rawData = readPixelToCalibrationFromFile(file,sizeOfChessPoints,distanceBetweenPoints);
    }

    public RawData readPixelToCalibrationFromFile(File file,QuantaPoint sizeOfChessPoints, double distanceBetweenPoints) throws IOException, ClassNotFoundException, MathException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        ArrayList<Pixel> myArrayList = (ArrayList<Pixel>) in.readObject();
        in.close();
        return new RawData(myArrayList,setAllChessPoints(sizeOfChessPoints,distanceBetweenPoints),0,imageInfo.getCameraModule().getFrame());
    }

    private ArrayList<Point3D> setAllChessPoints(QuantaPoint sizeOfChessPoints, double distanceBetweenPoints) throws MathException {
        ArrayList<Point3D> chessPoints = new ArrayList<>();
        for (int j = 0; j < sizeOfChessPoints.getN(); j++) {
            for (int k = 0; k < sizeOfChessPoints.getM(); k++) {
                Point3D point3D = Point3D.cartesianPoint(0, -k * distanceBetweenPoints, -j * distanceBetweenPoints);
                chessPoints.add(point3D);
            }
        }
        return chessPoints;
    }

    public ArrayList<Point3D> getDirectionFromPixels(){
        ArrayList<Point3D> directions = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            directions.add(imageInfo.getLineOfSightFromPixel(rawData.getPixelsForCalibration().get(i),1));
        }
        return directions;
    }

    public ArrayList<Point3D> getDirectionFromRealPoints(){
        ArrayList<Point3D> directions = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            directions.add(rawData.getPoint3DsForCalibration().get(i).vectorToPoint(imageInfo.getCameraLocation().getLocation()).normal());
        }
        return directions;
    }

    public ArrayList<Line3D> getLinesFromPointsToFindEstimatedPoint() throws MathException {
        ArrayList<Line3D> line3Ds = new ArrayList<>();
        ArrayList<Point3D> directions = new ArrayList<>();
        System.out.println(imageInfo.getRotationMatrix3D());
        for (int i = 0; i < rawData.size(); i++) {
            directions.add(imageInfo.getLineOfSightFromPixel(rawData.getPixelsForCalibration().get(i),1));
            line3Ds.add(new Line3D(rawData.getPoint3DsForCalibration().get(i),directions.get(i)));
        }
        return line3Ds;
    }

    public ArrayList<Line3D> getLinesFromChessBoardToEstimatedPoint(){
        ArrayList<Line3D> line3Ds = new ArrayList<>();
        ArrayList<Point3D> directions = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            directions.add(rawData.getPoint3DsForCalibration().get(i).vectorToPoint(imageInfo.getCameraLocation().getLocation()).normal());
            line3Ds.add(new Line3D(rawData.getPoint3DsForCalibration().get(i),directions.get(i)));
        }
        return line3Ds;
    }

    public ArrayList<Line3D> getLinesFromEstimatedCameraLocationPixelToChessBoard() throws MathException {
        ArrayList<Line3D> line3Ds = new ArrayList<>();
        ArrayList<Point3D> directions = new ArrayList<>();
        for (int i = 0; i < rawData.size(); i++) {
            directions.add(imageInfo.getLineOfSightFromPixel(rawData.getPixelsForCalibration().get(i),1));
            line3Ds.add(new Line3D(imageInfo.getCameraLocation().getLocation(),directions.get(i)));
        }
        return line3Ds;
    }

    public double getRmsFromRealPoint() throws MathException {
        ArrayList<Line3D> line3Ds;
        double rms = 0;
        line3Ds = getLinesFromEstimatedCameraLocationPixelToChessBoard();
        for (int i = 0; i < rawData.size(); i++) {
            rms += Math.pow(line3Ds.get(i).getPointOnLineFromAxisX(0).distance(rawData.getPoint3DsForCalibration().get(i)),2);
        }
        return Math.sqrt(rms/line3Ds.size());
    }

    public double getRmsFromRotationMatrix(){
        ArrayList<Line3D> line3Ds;
        double rms = 0;
        line3Ds = getLinesFromChessBoardToEstimatedPoint();
        for (int i = 0; i < rawData.size(); i++) {
            rms += Math.pow(imageInfo.getCameraModule().getPoint3DFromPixel(rawData.getPixelsForCalibration().get(i)).distance(rawData.getPoint3DsForCalibration().get(i)),2);
        }
        return Math.sqrt(rms/line3Ds.size());
    }

    public EstimatedPoint estimateImageLocationFrom3dPoints() throws MathException {
        ArrayList<Line3D> line3DsToFindCameraLocation = getLinesFromPointsToFindEstimatedPoint();
        return new EstimatedPoint(line3DsToFindCameraLocation,0.001,0.1);
    }



}
