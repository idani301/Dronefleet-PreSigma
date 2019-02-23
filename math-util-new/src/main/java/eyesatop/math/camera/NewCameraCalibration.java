package eyesatop.math.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import Jama.Matrix;
import eyesatop.eyesatop.camera.RawData;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.MathException;
import eyesatop.math.QuantaPoint;

/**
 * Created by Einav on 26/07/2017.
 */

public class NewCameraCalibration {

    private ArrayList<ImageToCalibrate> imageToCalibrates = new ArrayList<>();
    private double sumOfScaleErrors = 0;
    RawData rawData;

    public NewCameraCalibration(File folder) throws Exception {
        if(!folder.isDirectory())
            throw new Exception("Must get folder");
        ArrayList<RawData> rawDatas = new ArrayList<>();
        ArrayList<String> imageNames = new ArrayList<>();
        for (int i = 0; i < folder.listFiles().length; i++) {
            File file = folder.listFiles()[i];
            if (file.getName().split("\\.")[1].contains("txt")){
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                ArrayList<Pixel> myArrayList = (ArrayList<Pixel>) in.readObject();
                in.close();
                rawDatas.add(new RawData(myArrayList,setAllChessPoints(new QuantaPoint(7,7),46),0,new Frame(4000,3000)));
                imageNames.add(file.getName().split("\\.")[0]);
            }
        }

        double f = 2350;
        double minScaleError = 1;
        double fMin = 0;
        for (int j = 0; j < 20; j++) {

            double number = j*10 - 100;
            sumOfScaleErrors = 0;
            imageToCalibrates = new ArrayList<>();
            for (int i = 0; i < rawDatas.size(); i++) {
                imageToCalibrates.add(new ImageToCalibrate(rawDatas.get(i), new PinHoleCameraModule(new Frame(4000, 3000), f + number), imageNames.get(i)));
                sumOfScaleErrors += Math.pow(imageToCalibrates.get(i).getErrorScale(),2);
            }
            if (minScaleError > Math.sqrt(sumOfScaleErrors/rawDatas.size())){
                fMin = f + number;
                minScaleError = Math.sqrt(sumOfScaleErrors/rawDatas.size());
            }
        }
        sumOfScaleErrors = 0;
        imageToCalibrates = new ArrayList<>();
        for (int i = 0; i < rawDatas.size(); i++) {
            imageToCalibrates.add(new ImageToCalibrate(rawDatas.get(i), new PinHoleCameraModule(new Frame(4000, 3000), fMin), imageNames.get(i)));
            sumOfScaleErrors += Math.pow(imageToCalibrates.get(i).getRms(),2);
        }
        sumOfScaleErrors = Math.sqrt(sumOfScaleErrors/rawDatas.size());
        ArrayList<Pixel> pixels = new ArrayList<>();
        ArrayList<Point3D> point3Ds = new ArrayList<>();
        for (int i = 0; i < imageToCalibrates.size(); i++) {
            for (int j = 0; j < imageToCalibrates.get(i).getRawData().size(); j++) {
                pixels.add(imageToCalibrates.get(i).getRawData().getPixelsForCalibration().get(j));
                point3Ds.add(imageToCalibrates.get(i).getRawData().getPoint3DsForCalibration().get(j));
            }
        }

        rawData = new RawData(pixels,point3Ds,1,rawDatas.get(0).getFrame());


    }

    public NewCameraCalibration(File folder, CameraModule cameraModule) throws Exception {
        if (!folder.isDirectory())
            throw new Exception("Must get folder");
        ArrayList<RawData> rawDatas = new ArrayList<>();
        ArrayList<String> imageNames = new ArrayList<>();
        for (int i = 0; i < folder.listFiles().length; i++) {
            File file = folder.listFiles()[i];
            if (file.getName().split("\\.")[1].contains("txt")) {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
                ArrayList<Pixel> myArrayList = (ArrayList<Pixel>) in.readObject();
                in.close();
                rawDatas.add(new RawData(myArrayList, setAllChessPoints(new QuantaPoint(7, 7), 46), 0, new Frame(4000, 3000)));
                imageNames.add(file.getName().split("\\.")[0]);
            }
        }
        sumOfScaleErrors = 0;
        for (int i = 0; i < rawDatas.size(); i++) {
            imageToCalibrates.add(new ImageToCalibrate(rawDatas.get(i), cameraModule, imageNames.get(i)));
            sumOfScaleErrors += Math.pow(imageToCalibrates.get(i).getRms(), 2);
        }
        sumOfScaleErrors = Math.sqrt(sumOfScaleErrors/rawDatas.size());


    }

    public double getSumOfScaleErrors() {
        return sumOfScaleErrors;
    }

    public RawData getRawData() {
        return rawData;
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

//    public Matrix InternalParameters(){
//        double[][] doubles = new double[imageToCalibrates.size()*2][5];
//        for (int i = 0; i < imageToCalibrates.size(); i++) {
//            for (int j = 0; j < 5; j++) {
//                doubles[2*i][j] = imageToCalibrates.get(i).getInternalConstrain()[0][j];
//                doubles[2*i+1][j] = imageToCalibrates.get(i).getInternalConstrain()[1][j];
//            }
//        }
//        double[][] doubles1 = new double[imageToCalibrates.size()*2][1];
//        for (int i = 0; i < imageToCalibrates.size(); i++) {
//            doubles1[2*i][0] = imageToCalibrates.get(i).getInternalConstrain()[0][5];
//            doubles1[2*i+1][0] = imageToCalibrates.get(i).getInternalConstrain()[1][5];
//        }
//        Matrix v = new Matrix(doubles);
//        Matrix b = new Matrix(doubles1);
//        Matrix p = leastSquares(v,b);
//        printMatrix(p.transpose());
//
////        SingularValueDecomposition singularValueDecomposition = v.svd();
////        Matrix p = singularValueDecomposition.getU();
////        Matrix s = singularValueDecomposition.getS();
////
////        printMatrix(p);
////        System.out.println();
////        int index = 5;
//        Matrix B = new Matrix(3,3,0);
//        B.set(0,0,p.get(0,0));
//        B.set(0,1,p.get(1,0));
//        B.set(1,0,p.get(1,0));
//        B.set(2,0,p.get(3,0));
//        B.set(0,2,p.get(3,0));
//        B.set(1,1,p.get(2,0));
//        B.set(1,2,p.get(4,0));
//        B.set(2,1,p.get(4,0));
//        B.set(2,2,1);
////        B.set(0,0,p.get(index,0));
////        B.set(0,1,p.get(index,1));
////        B.set(1,0,p.get(index,1));
////        B.set(2,0,p.get(index,3));
////        B.set(0,2,p.get(index,3));
////        B.set(1,1,p.get(index,2));
////        B.set(1,2,p.get(index,4));
////        B.set(2,1,p.get(index,4));
////        B.set(2,2,p.get(index,5));
//        printMatrix(B);
//        System.out.println();
//
//        CholeskyDecomposition choleskyDecomposition = B.chol();
//        Matrix L = choleskyDecomposition.solve(B);
//        printMatrix(L);
//        System.out.println();
//
//        Matrix internal = L.transpose().inverse();
//        printMatrix(internal);
//
//        return null;
//    }

    private Matrix leastSquares(Matrix A, Matrix B) {
        Matrix temp = A.transpose().times(A);
        temp = temp.inverse();
        temp = temp.times(A.transpose());
        temp = temp.times(B);
        return temp;
    }
    @Override
    public String toString() {
        return "NewCameraCalibration{" +
                "imageToCalibrates=" + imageToCalibrates +
                '}';
    }
}


