package eyesatop.math.camera;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import eyesatop.eyesatop.camera.RawData;
import eyesatop.imageprocessing.LoadOpenCVDLL;
import eyesatop.math.Geometry.EstimatedPoint;
import eyesatop.math.Geometry.Line3D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.MathException;
import eyesatop.math.QuantaPoint;

/**
 * Created by Einav on 15/07/2017.
 */

public class CameraCalibration {
    static {
        new LoadOpenCVDLL();
    }

    private final ArrayList<Mat> images;
    private final ArrayList<String> imagesName = new ArrayList<>();
    private final String cameraName;
    private final String destDir = "E:\\Camera Auto Calibration\\Tests\\";
    private final ArrayList<DataForCalibration> dataForCalibrations = new ArrayList<>();
    private int numberOfImages = -1;

    public CameraCalibration(String cameraName) {
        this.cameraName = cameraName;
        images = new ArrayList<>();

    }

    public void addRawImage(File imageFile, File chessPointFile, double focalLength, QuantaPoint boardSize, double distanceBetweenPoints, Frame frame) throws Exception {
        imagesName.add(imageFile.getName().split("\\.")[0]);
        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        images.add(image);
        numberOfImages++;
        DataForCalibration dataForCalibration = new DataForCalibration(chessPointFile,boardSize,distanceBetweenPoints,
                new PinHoleCameraModule(frame,
                        focalLength));
        dataForCalibrations.add(dataForCalibration);
        estimateImageLocationFrom3dPoints(numberOfImages);
        estimateRotationMatrix(numberOfImages);
        estimateImageLocationFrom3dPoints(numberOfImages);
        estimateRotationMatrix(numberOfImages);
        estimateImageLocationFrom3dPoints(numberOfImages,0.07,-0.45,0.1);
        estimateRotationMatrix(numberOfImages);
        estimateImageLocationFrom3dPoints(numberOfImages);
        estimateRotationMatrix(numberOfImages);
    }

    public void addRawImage(File imageFile, File chessPointFile, CameraModule cameraModule, QuantaPoint boardSize, double distanceBetweenPoints) throws Exception {
        imagesName.add(imageFile.getName().split("\\.")[0]);
        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        images.add(image);
        numberOfImages++;
        dataForCalibrations.add(new DataForCalibration(chessPointFile,boardSize,distanceBetweenPoints,
                cameraModule));
        estimateImageLocationFrom3dPoints(numberOfImages);
        estimateRotationMatrix(numberOfImages);
        estimateImageLocationFrom3dPoints(numberOfImages);
        estimateRotationMatrix(numberOfImages);
        estimateImageLocationFrom3dPoints(numberOfImages,0.07,-0.45,0.1);
        estimateRotationMatrix(numberOfImages);
        estimateImageLocationFrom3dPoints(numberOfImages);
        estimateRotationMatrix(numberOfImages);
    }

    public void changeImage(int index, CameraModule cameraModule) throws Exception {
        dataForCalibrations.get(index).setCameraModule(cameraModule);
        estimateRotationMatrix(index);
        estimateImageLocationFrom3dPoints(index,-0.02,0.05,0.06);
        estimateRotationMatrix(index);
        estimateImageLocationFrom3dPoints(index);
        estimateRotationMatrix(index);
        estimateImageLocationFrom3dPoints(index);
    }

    public void changeImage1(int index, CameraModule cameraModule) throws Exception {
        dataForCalibrations.get(index).setCameraModule(cameraModule);
        estimateRotationMatrix(index);
        estimateImageLocationFrom3dPoints(index);
        estimateRotationMatrix(index);
        estimateImageLocationFrom3dPoints(index);
        estimateRotationMatrix(index);
        estimateImageLocationFrom3dPoints(index);
    }

    public void addRawImage(File imageFile, double focalLength, QuantaPoint boardSize, double distanceBetweenPoints) throws Exception {
        imagesName.add(imageFile.getName().split("\\.")[0]);
        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        images.add(image);
        numberOfImages++;
        dataForCalibrations.add(new DataForCalibration(getChessPoints(numberOfImages),boardSize,distanceBetweenPoints,new PinHoleCameraModule(new Frame(new Pixel(2050,1550,-1),image.width(),image.height()),focalLength)));
//        estimateImageLocationFrom3dPoints(numberOfImages);
//        estimateRotationMatrix(numberOfImages);
//        estimateImageLocationFrom3dPoints(numberOfImages);
//        estimateRotationMatrix(numberOfImages);
    }

    public ArrayList<Pixel> getChessPoints(int i) throws MathException {
        double time = System.currentTimeMillis();
        Mat mat = images.get(i);
        Mat grayMat = new Mat();
        Imgproc.cvtColor(mat,grayMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(mat,grayMat,125,255,Imgproc.THRESH_BINARY_INV);
        Imgcodecs.imwrite("E:\\saved\\try1.jpg",grayMat);
        MatOfPoint2f chessPoints = new MatOfPoint2f();
        Calib3d.findChessboardCorners(mat, new Size(9,7),chessPoints,Calib3d.CALIB_CB_NORMALIZE_IMAGE);
        Point[] points = chessPoints.toArray();
        ArrayList<Pixel> pixels = new ArrayList<>();
        System.out.println("points.length: " + points.length);
        for (int j = 0; j < points.length; j++) {
            pixels.add(new Pixel(points[j].x,points[j].y,-1));
            System.out.println(pixels.get(j));
        }

        try {
            File file = new File(destDir + imagesName.get(i) + ".txt");
            if (file.exists())
                file.delete();
            file.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(pixels);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print("Time to find chess patern: " + (System.currentTimeMillis() - time)/1000 + "(s)");
        return pixels;
    }

    public void findLocationAndRotationMatrixOfImage(int i) throws Exception {
        double rms = 1;
        double tempRms = 1;
        while (rms/tempRms > 0.1){
            tempRms = rms;
            estimateImageLocationFrom3dPoints(i);
            estimateRotationMatrix(i);

        }
    }

    public double estimateImageLocationFrom3dPoints(int i) throws MathException {

        ArrayList<Line3D> line3DsToFindCameraLocation = dataForCalibrations.get(i).getLinesFromPointsToFindEstimatedPoint();
        dataForCalibrations.get(i).setCameraLocation(new EstimatedPoint(line3DsToFindCameraLocation,0.001,0.1).getPoint3D());
        double rms = dataForCalibrations.get(i).RmsInTheRealSpace();
        System.out.println(dataForCalibrations.get(i).getCameraLocation().toStringCartesian());
        System.out.println("rms: " + 1000*rms);
        return rms;
    }

    public double estimateImageLocationFrom3dPoints(int i,double x, double y, double z) throws MathException {

        ArrayList<Line3D> line3DsToFindCameraLocation = dataForCalibrations.get(i).getLinesFromPointsToFindEstimatedPoint();
        EstimatedPoint estimatedPoint = new EstimatedPoint(line3DsToFindCameraLocation,0.001,0.1);
        dataForCalibrations.get(i).setCameraLocation(estimatedPoint.getPoint3D().add(Point3D.cartesianPoint(x,y,z)));
        double rms = dataForCalibrations.get(i).RmsInTheRealSpace();
        System.out.println(dataForCalibrations.get(i).getCameraLocation().toStringCartesian());
        System.out.println("rms: " + 1000*rms);
        return rms;
    }

    public void estimateRotationMatrix(int i) throws Exception {
        RotationMatrix3D rotationMatrix3D = RotationMatrix3D.findRotationMatrixFromMatchingPoints(dataForCalibrations.get(i).getDirectionFromRealPoints(),dataForCalibrations.get(i).getDirectionFromPixels());
        System.out.println("RMS rotation: " + rotationMatrix3D.getRms(dataForCalibrations.get(i).getDirectionFromRealPoints(),dataForCalibrations.get(i).getDirectionFromPixels()));
        dataForCalibrations.get(i).setRotationMatrix3D(rotationMatrix3D);
    }

    public void estimateRotationMatrix(int i, double yawRad, double pitchRad, double rollRad) throws Exception {
        RotationMatrix3D rotationMatrix3D = RotationMatrix3D.findRotationMatrixFromMatchingPoints(dataForCalibrations.get(i).getDirectionFromRealPoints(),dataForCalibrations.get(i).getDirectionFromPixels()).addAngles(yawRad,pitchRad,rollRad);
        System.out.println("RMS rotation: " + rotationMatrix3D.getRms(dataForCalibrations.get(i).getDirectionFromRealPoints(),dataForCalibrations.get(i).getDirectionFromPixels()));
        dataForCalibrations.get(i).setRotationMatrix3D(rotationMatrix3D);
    }

    public RawData getRawDataForCalibration(){
        ArrayList<Pixel> pixels = new ArrayList<>();
        ArrayList<Point3D> point3Ds = new ArrayList<>();
        for (int i = 0; i < dataForCalibrations.size(); i++) {
            ArrayList<Pixel> tempPixels = dataForCalibrations.get(i).getPixels();
            ArrayList<Point3D> tempPoints = dataForCalibrations.get(i).getPoint3dOnCamera();
            for (int j = 0; j < tempPixels.size(); j++) {
                pixels.add(tempPixels.get(j));
                point3Ds.add(tempPoints.get(j));
            }
        }
        return new RawData(pixels,point3Ds,0,dataForCalibrations.get(0).getImageInfo().getCameraModule().getFrame());
    }

    @Override
    public String toString() {
        return "CameraCalibration{" +
                "images=" + images +
                "\ncameraName=" + cameraName +
                '}';
    }
}
