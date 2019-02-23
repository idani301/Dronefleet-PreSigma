package eyesatop.math.cameracalibration;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.ArrayList;

import eyesatop.eyesatop.camera.RawData;
import eyesatop.math.MathException;
import eyesatop.math.QuantaPoint;
import eyesatop.math.camera.CameraModule;

/**
 * Created by Einav on 23/08/2017.
 */

public class RunCalibration {

    private final ArrayList<RawData> rawDatas = new ArrayList<>();
    private final ArrayList<ImageInfoToCalibration> imageInfoToCalibrations = new ArrayList<>();
    private final ArrayList<String> imageNames = new ArrayList<>();
    private final Mat imageOfPointsToCalibrate;

    public RunCalibration(File imagesDir) throws Exception {
        File[] files = imagesDir.listFiles();
        ImageToCalibration imageToCalibration = new ImageToCalibration(files[0]);
        imageOfPointsToCalibrate = Mat.zeros(imageToCalibration.getImage().size(),imageToCalibration.getImage().type());
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            RawData rawData = null;
            imageToCalibration = new ImageToCalibration(file);
            try {
                rawData = imageToCalibration.imageCalibrationRawData(new QuantaPoint(9,7),26,false);
                rawDatas.add(rawData);
                imageNames.add(imageToCalibration.getImageName());
            }
            catch (MathException e){
                continue;
            }
        }
    }

    public void makeImageInfo(CameraModule cameraModule) throws Exception {
        for (int i = 0; i < rawDatas.size(); i++) {
            RawData rawData = rawDatas.get(i);
            ImageInfoToCalibration imageInfoToCalibration = new ImageInfoToCalibration(rawData,cameraModule,imageNames.get(i));
//            System.out.println("RMS: " + imageInfoToCalibration.getRms());
            imageInfoToCalibrations.add(imageInfoToCalibration);
//                for (int j = 0; j < rawData.size(); j++) {
//                    Imgproc.circle(imageOfPointsToCalibrate,new Point(rawData.getPixelsForCalibration().get(j).getU(),rawData.getPixelsForCalibration().get(j).getV()),3,new Scalar(0,255,0),3);
//                }
        }
    }

    public void resetImageInfo(){
        for (int i = 0; i < imageInfoToCalibrations.size(); i++) {
            imageInfoToCalibrations.remove(i);
            i--;
        }
    }

    public ArrayList<ImageInfoToCalibration> getImageInfoToCalibrations() {
        return imageInfoToCalibrations;
    }

    public ArrayList<String> getImageNames() {
        return imageNames;
    }

    public Mat getImageOfPointsToCalibrate() {
        return imageOfPointsToCalibrate;
    }

    public double getAverageDistanceFromChessPoints(){
        double averageDistance = 0;
        for (int i = 0; i < rawDatas.size(); i++) {
            RawData rawData = rawDatas.get(i);
            ImageInfoToCalibration imageInfoToCalibration = imageInfoToCalibrations.get(i);
            for (int j = 0; j < rawData.size(); j++) {
                averageDistance += imageInfoToCalibration.getLineFromPixel(rawData.getPixelsForCalibration().get(j)).distance(rawData.getPoint3DsForCalibration().get(j));
            }
        }
        return averageDistance/(rawDatas.size()*rawDatas.get(0).size());
    }

    public ArrayList<RawData> getRawDatas() {
        return rawDatas;
    }

    public RawData getFullRawData(){
        RawData rawData = new RawData(rawDatas.get(0).getFrame());
        for (int i = 0; i < rawDatas.size(); i++) {
            rawData.add(rawDatas.get(i));
        }
        return rawData;
    }

    public boolean drawPointImage(){
        return Imgcodecs.imwrite("E:\\saved\\imagePoints.jpg",imageOfPointsToCalibrate);
    }


}
