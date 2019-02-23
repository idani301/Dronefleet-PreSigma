package eyesatop.math.cameracalibration;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import eyesatop.eyesatop.camera.RawData;
import eyesatop.imageprocessing.LoadOpenCVDLL;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.MathException;
import eyesatop.math.QuantaPoint;
import eyesatop.math.camera.Frame;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 22/08/2017.
 */

public class ImageToCalibration {

    static {
        new LoadOpenCVDLL();
    }

    private final Mat image;
    private final File imagePath;
    private Frame frame;

    public ImageToCalibration(File imagePath) {

        this.imagePath = imagePath;
        image = Imgcodecs.imread(imagePath.getAbsolutePath());
        frame = new Frame(image.width(),image.height());
    }

    public ImageToCalibration(File imagePath, Frame frame) {

        this.imagePath = imagePath;
        image = Imgcodecs.imread(imagePath.getAbsolutePath());
        this.frame = frame;
    }

    public String getImageName(){
        return imagePath.getName().split("\\.")[0];
    }

    public Mat getImage() {
        return image;
    }

    public File getImagePath() {
        return imagePath;
    }

    public ArrayList<Pixel> findChessBoardCorners(QuantaPoint quantaPoint, boolean isSaveImage) throws MathException {
        ArrayList<Pixel> pixels = new ArrayList<>();
        ArrayList<Double> areas = new ArrayList<>();
        Mat grayScale = new Mat();
        Mat logic = new Mat();
        Mat conMat = new Mat();
        Imgproc.cvtColor(image,grayScale,Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(grayScale,logic,125,255,Imgproc.THRESH_BINARY_INV);

        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(20,20));
        Imgproc.morphologyEx(logic, logic, Imgproc.MORPH_CLOSE, element,new Point(-1,-1),1, Core.BORDER_ISOLATED,new Scalar(0));
        Imgproc.Canny(logic, logic, 120, 120);
        element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(20,20));
        Imgproc.morphologyEx(logic, logic, Imgproc.MORPH_CLOSE, element,new Point(-1,-1),1, Core.BORDER_ISOLATED,new Scalar(0));
        element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3));
        Imgproc.morphologyEx(logic, logic, Imgproc.MORPH_OPEN, element,new Point(-1,-1),2, Core.BORDER_ISOLATED,new Scalar(0));
        element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(20,20));
        Imgproc.morphologyEx(logic, logic, Imgproc.MORPH_CLOSE, element,new Point(-1,-1),2, Core.BORDER_ISOLATED,new Scalar(0));
        element = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3));
        Imgproc.morphologyEx(logic, logic, Imgproc.MORPH_OPEN, element,new Point(-1,-1),2, Core.BORDER_ISOLATED,new Scalar(0));
        if (isSaveImage)
            Imgcodecs.imwrite("E:\\saved\\" + getImageName() +".jpg",logic);

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(logic, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        int index = 0;
        for (int i = 0; i < contours.size(); i++) {
            double area = Imgproc.contourArea(contours.get(i));
            if (area > 10 && area < 100) {
                Moments countorMoments = Imgproc.moments(contours.get(i));
                Point point = new Point(countorMoments.m10 / countorMoments.m00, countorMoments.m01 / countorMoments.m00);
                pixels.add(new Pixel(point.x, point.y, -1));
                areas.add(area);
                index++;
            }
        }
        Collections.sort(pixels, new Comparator<Pixel>() {
            @Override
            public int compare(Pixel o1, Pixel o2) {
                if (Math.abs(o1.getV() - o2.getV()) < 30){
                    if (o1.getU() > o2.getU()) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
                if (o1.getV() > o2.getV())
                    return 1;
                else
                    return -1;
            }
        });
        for (int i = 0; i < pixels.size(); i++) {
            if (pixels.size() < quantaPoint.getN()*quantaPoint.getM()) {
                System.out.println("Image Name: " + getImageName());
                System.out.println("Number of pixels: " + pixels.size());
                throw new MathException(MathException.MathExceptionCause.general);
            }
            if (i < pixels.size() - quantaPoint.getN() - 1) {
                if (i%quantaPoint.getN() != quantaPoint.getN()-1) {
                    if (Math.abs(pixels.get(i).distance(pixels.get(i + 1)) - pixels.get(i).distance(pixels.get(i + quantaPoint.getN()))) > 20) {
                        pixels.remove(i);
                        i--;
                        continue;
                    }
                }
                else {
                    if (Math.abs(pixels.get(i).distance(pixels.get(i - 1)) - pixels.get(i).distance(pixels.get(i + quantaPoint.getN()))) > 20) {
                        pixels.remove(i);
                        i--;
                        continue;
                    }
                }
            }
            else {
                if (i%quantaPoint.getN() != quantaPoint.getN()-1) {
                    if (Math.abs(pixels.get(i).distance(pixels.get(i + 1)) - pixels.get(i).distance(pixels.get(i - quantaPoint.getN()))) > 20) {
                        pixels.remove(i);
                        i--;
                        continue;
                    }
                }
                else {
                    if (Math.abs(pixels.get(i).distance(pixels.get(i - 1)) - pixels.get(i).distance(pixels.get(i - quantaPoint.getN()))) > 20) {
                        pixels.remove(i);
                        i--;
                        continue;
                    }

                }
            }
            double blue = i%quantaPoint.getM()*16;
            double green = (i - (i%quantaPoint.getM()))*4;
            if (i != pixels.size()-1) {
                Imgproc.line(image, new Point(pixels.get(i).getU(), pixels.get(i).getV()), new Point(pixels.get(i + 1).getU(), pixels.get(i + 1).getV()), new Scalar(0, 0, 255),5);
            }
            Imgproc.circle(image,new Point(pixels.get(i).getU(),pixels.get(i).getV()),6,new Scalar(blue,green,0),3);

            Imgproc.circle(image,new Point(pixels.get(i).getU(),pixels.get(i).getV()),1,new Scalar(blue,green,0),1);

        }
        if (isSaveImage) {
            boolean b = Imgcodecs.imwrite("E:\\saved\\" + getImageName() + "color.jpg", image);
            Imgcodecs.imwrite("E:\\saved\\" + getImageName() + ".jpg", logic);
        }
        if (pixels.size() != quantaPoint.getN()*quantaPoint.getM()) {
            System.err.println("Number of pixels found: " + pixels.size());
            throw new MathException(MathException.MathExceptionCause.general);
        }

        return pixels;
    }

    public ArrayList<Point3D> setAllChessPoints(QuantaPoint sizeOfChessPoints, double distanceBetweenPoints) throws MathException {
        ArrayList<Point3D> chessPoints = new ArrayList<>();
        for (int j = 0; j < sizeOfChessPoints.getM(); j++) {
            for (int k = 0; k < sizeOfChessPoints.getN(); k++) {
                Point3D point3D = Point3D.cartesianPoint(0, -j * distanceBetweenPoints, -k * distanceBetweenPoints);
                chessPoints.add(point3D);
            }
        }
        return chessPoints;
    }

    public RawData imageCalibrationRawData(QuantaPoint sizeOfChessPoints, double distanceBetweenPoints, boolean isSaveImage) throws MathException {
        return new RawData(findChessBoardCorners(sizeOfChessPoints,isSaveImage),setAllChessPoints(sizeOfChessPoints,distanceBetweenPoints), frame);
    }

    @Override
    public String toString() {
        return "ImageToCalibration{" +
                ", imagePath=" + imagePath +
                '}';
    }
}
