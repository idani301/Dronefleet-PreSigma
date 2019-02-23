package eyesatop.math.cameracalibration;

import java.util.ArrayList;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import eyesatop.eyesatop.camera.RawData;
import eyesatop.math.Geometry.Line3D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.MathException;
import eyesatop.math.camera.CameraModule;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 23/08/2017.
 */

public class ImageInfoToCalibration {

    private final String imageName;
    private final CameraModule cameraModule;
    private final RawData rawData;


    private double errorScale = -1;
    private double rms = -1;

    private final Point3D cameraLocation;
    private final RotationMatrix3D rotationMatrix3D;

    public ImageInfoToCalibration(String imageName, CameraModule cameraModule, RawData rawData, Point3D cameraLocation, RotationMatrix3D rotationMatrix3D) throws Exception {
        this.imageName = imageName;
        this.cameraModule = cameraModule;
        this.cameraLocation = cameraLocation;
        if (rawData.size() == 0) {
            this.rotationMatrix3D = rotationMatrix3D;
        }
        else {
            this.rotationMatrix3D = RotationMatrix3D.findRotationMatrixFromMatchingPoints(
                    calcArrayPointsFromArrayPixel(rawData.getPixelsForCalibration()),
                    calcArrayPointsFromArrayLocation(rawData.getPoint3DsForCalibration()));
            rms = this.rotationMatrix3D.getRms(
                    calcArrayPointsFromArrayPixel(rawData.getPixelsForCalibration()),
                    calcArrayPointsFromArrayLocation(rawData.getPoint3DsForCalibration()));
        }
        ArrayList<Point3D> locationsPoints = calcArrayPointsFromArrayLocation(rawData.getPoint3DsForCalibration());
        for (int i = 0; i < locationsPoints.size(); i++) {
            locationsPoints.set(i,locationsPoints.get(i).rotate(this.rotationMatrix3D));
        }
        this.rawData = new RawData(rawData.getPixelsForCalibration(),locationsPoints,cameraModule.getFrame());

    }

    public Line3D getLineFromPixel(Pixel pixel){

        Point3D point3D = cameraModule.getPoint3DFromPixel(pixel).rotate(rotationMatrix3D.getInverseRotationMatrix());
        return new Line3D(cameraLocation,point3D);

    }

    public ImageInfoToCalibration(RawData rawData, CameraModule cameraModule, String imageName) throws Exception {

        this.cameraModule = cameraModule;
        this.imageName = imageName;
        this.cameraLocation = calcLocationTranslationMatrix(calcMatrixForLeastSquares(rawData));//Point3D.cartesianPoint(-996.5,-95.07,-177.42);
        ArrayList<Point3D> pixelsPoints = calcArrayPointsFromArrayPixel(rawData.getPixelsForCalibration());
        ArrayList<Point3D> locationsPoints = calcArrayPointsFromArrayLocation(rawData.getPoint3DsForCalibration());
        this.rotationMatrix3D = RotationMatrix3D.findRotationMatrixFromMatchingPoints(pixelsPoints,locationsPoints);
        rms = this.rotationMatrix3D.getRms(pixelsPoints,locationsPoints);
        for (int i = 0; i < locationsPoints.size(); i++) {
            locationsPoints.set(i,locationsPoints.get(i).rotate(rotationMatrix3D));
        }
        this.rawData = new RawData(rawData.getPixelsForCalibration(),locationsPoints,rawData.getCameraSN(),rawData.getFrame());
    }

    private ArrayList<Point3D> calcArrayPointsFromArrayLocation(ArrayList<Point3D> locationsIn3dPoints) {
        ArrayList<Point3D> point3Ds = new ArrayList<>();
        for (int i = 0; i < locationsIn3dPoints.size(); i++) {
            Line3D line3D = Line3D.getLineOutOfTwoPoints(cameraLocation,locationsIn3dPoints.get(i));
            point3Ds.add(line3D.getDirection());
        }
        return point3Ds;
    }


    private ArrayList<Point3D> calcArrayPointsFromArrayPixel(ArrayList<Pixel> pixelsForCalibration) {
        ArrayList<Point3D> point3Ds = new ArrayList<>();
        for (int i = 0; i < pixelsForCalibration.size(); i++) {
            point3Ds.add(cameraModule.getPoint3DFromPixel(pixelsForCalibration.get(i)));
        }
        return point3Ds;
    }

    public Matrix calcMatrixForLeastSquares(RawData rawData) throws MathException {
        double[][] doubles = new double[(int) (rawData.size()*2)][9];
        for (int i = 0; i < rawData.size(); i++) {

            Point3D point3D = cameraModule.getPoint3DFromPixel(rawData.getPixelsForCalibration().get(i));
            double x = point3D.getYOnX1Plane();
            double y = point3D.getZOnX1Plane();

            doubles[2*i][0] = -rawData.getPoint3DsForCalibration().get(i).getY();
            doubles[2*i][1] = -rawData.getPoint3DsForCalibration().get(i).getZ();
            doubles[2*i][2] = -1;
            doubles[2*i][3] = 0;
            doubles[2*i][4] = 0;
            doubles[2*i][5] = 0;
            doubles[2*i][6] = x*rawData.getPoint3DsForCalibration().get(i).getY();
            doubles[2*i][7] = x*rawData.getPoint3DsForCalibration().get(i).getZ();
            doubles[2*i][8] = x;

            doubles[2*i+1][0] = 0;
            doubles[2*i+1][1] = 0;
            doubles[2*i+1][2] = 0;
            doubles[2*i+1][3] = -rawData.getPoint3DsForCalibration().get(i).getY();
            doubles[2*i+1][4] = -rawData.getPoint3DsForCalibration().get(i).getZ();
            doubles[2*i+1][5] = -1;
            doubles[2*i+1][6] = y*rawData.getPoint3DsForCalibration().get(i).getY();
            doubles[2*i+1][7] = y*rawData.getPoint3DsForCalibration().get(i).getZ();
            doubles[2*i+1][8] = y;
        }

        return new Matrix(doubles);
    }

    public Point3D calcLocationTranslationMatrix(Matrix A) {

        SingularValueDecomposition singularValueDecomposition = new SingularValueDecomposition(A);
        Matrix v = singularValueDecomposition.getV();
        double[] solution = new double[9];
        for (int i = 0; i < 9; i++) {
            solution[i] = v.get(i,v.getRowDimension()-1);
        }

        double scale1 = Math.sqrt(Math.pow(solution[0],2) + Math.pow(solution[3],2) + Math.pow(solution[6],2));
        double scale2 = Math.sqrt(Math.pow(solution[1],2) + Math.pow(solution[4],2) + Math.pow(solution[7],2));

        double scale = (scale1 + scale2)/2;

        if (solution[8] > 0)
            scale *= -1;
        errorScale = Math.pow(1 - scale1/scale2,2);

        return Point3D.cartesianPoint(solution[8]/scale,-solution[2]/scale,solution[5]/scale);
    }

    public String getImageName() {
        return imageName;
    }

    public CameraModule getCameraModule() {
        return cameraModule;
    }

    public double getErrorScale() {
        return errorScale;
    }

    public double getRms() {
        return rms;
    }

    public Point3D getCameraLocation() {
        return cameraLocation;
    }

    public RotationMatrix3D getRotationMatrix3D() {
        return rotationMatrix3D;
    }

    public RawData getRawData() {
        return rawData;
    }

    @Override
    public String toString() {
        return "ImageToCalibrate{" +
                "imageName='" + imageName + '\'' +
                ", cameraLocation=" + cameraLocation.toStringCartesian() +
                ",\n rotationMatrix3D=" + rotationMatrix3D +
                ",\n errorScale=" + errorScale +
                ",\n rms=" + rms +
                '}' + "\n";
    }
}
