package eyesatop.eyesatop.camera;

import java.io.File;
import java.util.ArrayList;

import eyesatop.eyesatop.camera.cameracalibration.GeneralCameraModuleIn;
import eyesatop.eyesatop.util.readfromtxtfile.ReadObjectToPixel;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Polynom;
import eyesatop.math.camera.Frame;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 07/05/2017.
 */

public class RawData {

    private final ArrayList<Pixel> pixelsForCalibration;
    private final ArrayList<Point3D> point3DsForCalibration;
    private final long cameraSN;

    private double sizeOfData;

    private Frame frame;

    public RawData(ArrayList<Pixel> pixelsForCalibration, ArrayList<Point3D> point3DsForCalibration, long cameraSN, Frame frame) {
        this.pixelsForCalibration = pixelsForCalibration;
        this.point3DsForCalibration = point3DsForCalibration;
        this.cameraSN = cameraSN;
        this.frame = frame;

        sizeOfData = pixelsForCalibration.size();
    }

    public RawData(ArrayList<Pixel> pixelsForCalibration, ArrayList<Point3D> point3DsForCalibration, Frame frame) {
        this.pixelsForCalibration = pixelsForCalibration;
        this.point3DsForCalibration = point3DsForCalibration;
        this.cameraSN = -1;
        this.frame = frame;

        sizeOfData = pixelsForCalibration.size();
    }

    public RawData(Frame frame){
        cameraSN = -1;
        sizeOfData = 0;
        this.frame = frame;
        pixelsForCalibration = new ArrayList<>();
        point3DsForCalibration = new ArrayList<>();
    }

    public void add(RawData rawData){
        for (int i = 0; i < rawData.size(); i++) {
            pixelsForCalibration.add(rawData.pixelsForCalibration.get(i));
            point3DsForCalibration.add(rawData.point3DsForCalibration.get(i));
        }
        sizeOfData = pixelsForCalibration.size();
    }

    public static RawData ReadFromFileRawDataForCalibration(File file, Frame frame) throws Exception {
        ReadObjectToPixel readObjectToPixel = new ReadObjectToPixel(file);
        ArrayList<Pixel> pixels = readObjectToPixel.getPixelsForCalibration();
        ArrayList<Point3D> point3Ds = readObjectToPixel.getStarVectorsForCalibration();

        return new RawData(pixels,point3Ds, readObjectToPixel.getCameraSN(), frame);
    }

    public static RawData ReadFromFileRawDataFromElevationAzimuth(File file, Frame frame) throws Exception {
        ReadObjectToPixel readObjectToPixel = new ReadObjectToPixel(file);
        ArrayList<Pixel> pixels = readObjectToPixel.getPixelsForCalibration();
        ArrayList<Point3D> point3Ds = readObjectToPixel.getStarVectors();

        return new RawData(pixels,point3Ds, readObjectToPixel.getCameraSN(), frame);
    }

    public double getRadiusFromPixel(int i){
        return pixelsForCalibration.get(i).getRadius(frame);
    }

    public double getPhiFromPixel(int i){
        return pixelsForCalibration.get(i).getAngle(frame);
    }

    public double getTetaFromPoint3d(int i){
        return point3DsForCalibration.get(i).getTetaCameraPointOfView();
    }

    public double getPsaiFromPoint3d(int i){
        return point3DsForCalibration.get(i).getPsaiCameraPointOfView();
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
    }

    public ArrayList<Pixel> getPixelsForCalibration() {
        return pixelsForCalibration;
    }

    public ArrayList<Point3D> getPoint3DsForCalibration() {
        return point3DsForCalibration;
    }

    public Frame getFrame() {
        return frame;
    }

    public double size() {
        return sizeOfData;
    }

    public double getMinRadiusFromPixels(){
        double minRadius = pixelsForCalibration.get(0).getRadius(frame);
        for (int i = 1; i < pixelsForCalibration.size(); i++) {
            double temp = pixelsForCalibration.get(i).getRadius(frame);
            if(temp < minRadius)
                minRadius = temp;
        }
        return minRadius;
    }

    public double getMaxRadiusFromPixels(){
        double maxRadius = pixelsForCalibration.get(0).getRadius(frame);
        for (int i = 1; i < pixelsForCalibration.size(); i++) {
            double temp = pixelsForCalibration.get(i).getRadius(frame);
            if(temp > maxRadius)
                maxRadius = temp;
        }
        return maxRadius;
    }

    public double calcTotalRadiusRMS(GeneralCameraModuleIn generalCameraModuleIn){
        double RMS = 0;
        for (int i = 0; i < sizeOfData; i++) {
            RMS += Math.pow(generalCameraModuleIn.getRadiusFirstCorrection(point3DsForCalibration.get(i))- pixelsForCalibration.get(i).getRadius(frame),2);
        }
        return Math.sqrt(RMS/sizeOfData);
    }

    public double calcTotalRadiusRMS(Polynom polynom){
        double RMS = 0;
        for (int i = 0; i < sizeOfData; i++) {
            double r = Math.pow(polynom.value(point3DsForCalibration.get(i).getTetaCameraPointOfView()) - pixelsForCalibration.get(i).getRadius(frame),2);
            RMS += r;
        }
        return Math.sqrt(RMS/sizeOfData);
    }

    public double calcTotalRMS(GeneralCameraModuleIn generalCameraModuleIn){

        double RMS = 0;
        for (int i = 0; i < sizeOfData; i++) {

            double psai = getPsaiFromPoint3d(i);
            double phi  = getPhiFromPixel(i);
            Pixel pixel = generalCameraModuleIn.getPixelFromPoint3D(point3DsForCalibration.get(i));
            double distance = pixel.distance(pixelsForCalibration.get(i));

            RMS += distance*distance;
        }

        return Math.sqrt(RMS/sizeOfData);

    }

    public double calcTotalRMSAngles(GeneralCameraModuleIn generalCameraModuleIn){
        double RMS = 0;
        for (int i = 0; i < sizeOfData; i++) {

            Point3D point3D = generalCameraModuleIn.getPoint3DFromPixel(pixelsForCalibration.get(i));
            double distance = point3D.distance(point3DsForCalibration.get(i));

            RMS += distance*distance;
        }
        return Math.sqrt(RMS/sizeOfData);
    }

    public long getCameraSN() {
        return cameraSN;
    }

    @Override
    public String toString() {
        return "RawData{" +
                "pixelsForCalibration=" + pixelsForCalibration +
                ", point3DsForCalibration=" + point3DsForCalibration +
                ", cameraSN=" + cameraSN +
                ", sizeOfData=" + sizeOfData +
                ", frame=" + frame +
                '}';
    }
}
