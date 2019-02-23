package eyesatop.math.Geometry;

import java.io.File;
import java.util.ArrayList;

import Jama.Matrix;
import Jama.SingularValueDecomposition;
import eyesatop.eyesatop.camera.RawData;
import eyesatop.eyesatop.camera.cameracalibration.GeneralCameraModuleIn;

/**
 * Created by Einav on 06/05/2017.
 */

public class RotationMatrix3D {

    private final Matrix RotationMatrix;

    public RotationMatrix3D(Matrix rotationMatrix) {
        RotationMatrix = rotationMatrix;
    }

    public static RotationMatrix3D Body3DNauticalAngles(double yawRad, double pitchRad, double rollRad){

        double[][] x = new double[][]{
                        {Math.cos(yawRad)*Math.cos(pitchRad) , Math.cos(yawRad)*Math.sin(pitchRad)*Math.sin(rollRad)-Math.cos(rollRad)*Math.sin(yawRad) , Math.sin(yawRad)*Math.sin(rollRad)+Math.cos(yawRad)*Math.cos(rollRad)*Math.sin(pitchRad)},
                        {Math.cos(pitchRad)*Math.sin(yawRad) , Math.cos(yawRad)*Math.cos(rollRad)+Math.sin(yawRad)*Math.sin(pitchRad)*Math.sin(rollRad) , Math.cos(rollRad)*Math.sin(yawRad)*Math.sin(pitchRad)-Math.cos(yawRad)*Math.sin(rollRad)},
                        {-Math.sin(pitchRad)                 , Math.cos(pitchRad)*Math.sin(rollRad)                                                     , Math.cos(pitchRad)*Math.cos(rollRad)                                                    }
        };

        try {
            Matrix matrix = new Matrix(x);
            return new RotationMatrix3D(matrix);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static RotationMatrix3D ReadFromFileMatchingPoints(File file) throws Exception {
        RawData rawData = RawData.ReadFromFileRawDataFromElevationAzimuth(file,null);
        GeneralCameraModuleIn generalCameraModuleIn = GeneralCameraModuleIn.ReadGeneralCameraModule(new File("C:\\eyeatop\\cameraModule\\" +rawData.getCameraSN()+ ".txt"));
        rawData.setFrame(generalCameraModuleIn.getFrame());

        ArrayList<Point3D> point3Ds = generalCameraModuleIn.getArrayPoint3DFromArrayPixels(rawData.getPixelsForCalibration());
        RotationMatrix3D rotationMatrix3D = RotationMatrix3D.findRotationMatrixFromMatchingPoints(point3Ds,rawData.getPoint3DsForCalibration());
        double rms = 0;
        for (int i = 0; i < rawData.size(); i++) {
            Point3D point3D = point3Ds.get(i).rotate(rotationMatrix3D.getInverseRotationMatrix());
            rms += Math.pow(point3D.distance(rawData.getPoint3DsForCalibration().get(i)),2);
        }
        rms = Math.sqrt(rms/rawData.size());
        System.out.println("RMS of rotation matrix: " + rms);
        return rotationMatrix3D;

    }

    public static RotationMatrix3D findRotationMatrixFromMatchingPoints(ArrayList<Point3D> point3Ds1, ArrayList<Point3D> point3Ds2) throws Exception {
        if (point3Ds1.size() < 3){
            throw new Exception("Not enough points: must supply more than 3 points");
        }
        if(point3Ds1.size() != point3Ds2.size()){
            throw new Exception("Must have same array size");
        }

        Matrix matrix1 = getMatrixFromPoint3Ds(point3Ds1);
        Matrix matrix2 = getMatrixFromPoint3Ds(point3Ds2);

        Matrix H = matrix1.transpose().times(matrix2);

        SingularValueDecomposition SVD = H.svd();
        Matrix U = SVD.getU();
        Matrix V = SVD.getV();
        Matrix R = V.times(U.transpose()).transpose();
        return new RotationMatrix3D(R);
    }

    public double getRms(ArrayList<Point3D> point3Ds1, ArrayList<Point3D> point3Ds2){
        double rms = 0;
        for (int i = 0; i < point3Ds1.size(); i++) {
            Point3D point3D = point3Ds1.get(i).rotate(this.getInverseRotationMatrix());
            rms += Math.pow(point3D.distance(point3Ds2.get(i)),2);
        }
        return Math.sqrt(rms/point3Ds1.size());
    }

    public RotationMatrix3D addAngles(double yawRad, double pitchRad, double rollRad){
        return Body3DNauticalAngles(getYaw() + yawRad, getPitch() + pitchRad, getRoll() + rollRad);
    }

    public Matrix getRotationMatrix() {
        return RotationMatrix;
    }

    public double getPitch(){
        return -Math.asin(RotationMatrix.get(2, 0));
    }

    public double getYaw(){
        double pitch = getPitch();
        if(pitch != Math.PI/2 && pitch != 3*Math.PI/2)
            return Math.asin(RotationMatrix.get(1, 0)/Math.cos(pitch));
        return Math.asin(RotationMatrix.get(1, 0));
    }

    public double getRoll(){
        double pitch = getPitch();
        if(pitch != Math.PI/2 && pitch != 3*Math.PI/2)
            return Math.asin(RotationMatrix.get(2, 1)/Math.cos(pitch));
        return 0;
    }

    public RotationMatrix3D getInverseRotationMatrix(){
        return new RotationMatrix3D(RotationMatrix.inverse());
    }

    private static Matrix getMatrixFromPoint3Ds(ArrayList<Point3D> point3Ds){
        double[][] matrixParameters = new double[point3Ds.size()][3];
        for (int i = 0; i < point3Ds.size(); i++) {
                matrixParameters[i] = point3Ds.get(i).getPoint3DAsArray();
        }
        return new Matrix(matrixParameters);
    }

    public RotationMatrix3D times(RotationMatrix3D rotationMatrix3D){
        return new RotationMatrix3D(RotationMatrix.times(rotationMatrix3D.RotationMatrix));
    }

    public String toStringMatrix() {
        String string = "";
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                string += RotationMatrix.get(i,j);
                if (j != 2) {
                    string += " , ";
                }
            }
            string += "\n";
        }
        return string;
    }

    @Override
    public String toString() {
        return "RotationMatrix3D{" +
                "yaw=" + getYaw() +
                ", pitch=" + getPitch() +
                ", roll=" + getRoll()
                + "}";
    }
}
