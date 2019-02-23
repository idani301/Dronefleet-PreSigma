package eyesatop.eyesatop.camera.cameracalibration;

import java.io.File;

import eyesatop.eyesatop.camera.RawData;
import eyesatop.math.camera.Frame;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 06/06/2017.
 */

public class RunCalibration {

    private final FindFirstAproxTetaVsRadius findFirstAproxTetaVsRadius;
    private final FindSeccondAproxPsaiVsPhi findSeccondAproxPsaiVsPhi;
    private final Frame frameFirstApproximate;
    private final GeneralCameraModuleIn cameraModuleIn;
    private final RawData rawData;
    public final static double WIDTHFRAME = 4912;
    public final static double HIEGHTFRAME = 3684;

    private final double RMSangle;
    public final double FINALSTEP = 0.1;

    public RunCalibration(File file, int steps) throws Exception {
        Frame frameTemp = new Frame(WIDTHFRAME,HIEGHTFRAME);  //can be generalize
        rawData = RawData.ReadFromFileRawDataForCalibration(file,frameTemp);

        frameTemp = calculateCenter(frameTemp,steps,100);
        frameTemp = calculateCenter(frameTemp,FINALSTEP,2);


        frameFirstApproximate = frameTemp;
        rawData.setFrame(frameFirstApproximate);
        findFirstAproxTetaVsRadius = new FindFirstAproxTetaVsRadius(rawData);
        findSeccondAproxPsaiVsPhi = new FindSeccondAproxPsaiVsPhi(rawData);
        cameraModuleIn = new GeneralCameraModuleIn(findFirstAproxTetaVsRadius.getPolynomFunction(), rawData.getFrame(),1.6e-6,findSeccondAproxPsaiVsPhi.getAmplitudeFunction(),findSeccondAproxPsaiVsPhi.getPhaseFunction(),findSeccondAproxPsaiVsPhi.getConstParameter(), rawData.getCameraSN(), 0);
        cameraModuleIn.setModuleRMS(rawData.calcTotalRMS(cameraModuleIn));
        RMSangle = rawData.calcTotalRMSAngles(cameraModuleIn);
    }

    public RunCalibration(RawData rawData, int steps) throws Exception {
        Frame frameTemp = rawData.getFrame();  //can be generalize
        this.rawData = rawData;

        frameTemp = calculateCenter(frameTemp,steps,100);
        frameTemp = calculateCenter(frameTemp,FINALSTEP,2);


        frameFirstApproximate = frameTemp;
        rawData.setFrame(frameFirstApproximate);
        findFirstAproxTetaVsRadius = new FindFirstAproxTetaVsRadius(rawData);
        findSeccondAproxPsaiVsPhi = new FindSeccondAproxPsaiVsPhi(rawData);
        cameraModuleIn = new GeneralCameraModuleIn(findFirstAproxTetaVsRadius.getPolynomFunction(), rawData.getFrame(),1.6e-6,findSeccondAproxPsaiVsPhi.getAmplitudeFunction(),findSeccondAproxPsaiVsPhi.getPhaseFunction(),findSeccondAproxPsaiVsPhi.getConstParameter(), rawData.getCameraSN(), 0);
        cameraModuleIn.setModuleRMS(rawData.calcTotalRMS(cameraModuleIn));
        RMSangle = 0;
    }

    public Frame calculateCenter(Frame frame, double step, double radiusLimit){

        FindFirstAproxTetaVsRadius findFirstAproxTetaVsRadiusTemp = new FindFirstAproxTetaVsRadius(rawData);
        double minRMS = rawData.calcTotalRadiusRMS(findFirstAproxTetaVsRadiusTemp.getPolynomFunction());
        Pixel minCenter = frame.getCenter();
        for (double i = -radiusLimit; i < radiusLimit; i+=step) {
            for (double j = -radiusLimit; j < radiusLimit; j+=step) {
                rawData.setFrame(new Frame(new Pixel(frame.getCenter().getU()+i,frame.getCenter().getV()+j,-1),frame.getWidth(),frame.getHeight()));
                FindFirstAproxTetaVsRadius findFirstAproxTetaVsRadiusMin = new FindFirstAproxTetaVsRadius(rawData);
                FindSeccondAproxPsaiVsPhi findSeccondAproxPsaiVsPhiMin = new FindSeccondAproxPsaiVsPhi(rawData);
                GeneralCameraModuleIn cameraModuleInMin = new GeneralCameraModuleIn(findFirstAproxTetaVsRadiusMin.getPolynomFunction(), rawData.getFrame(),1.6e-6, findSeccondAproxPsaiVsPhiMin.getAmplitudeFunction(), findSeccondAproxPsaiVsPhiMin.getPhaseFunction(), findSeccondAproxPsaiVsPhiMin.getConstParameter(), rawData.getCameraSN());
                double tempRMS = rawData.calcTotalRMS(cameraModuleInMin);
                if(tempRMS <= minRMS){
                    minRMS = tempRMS;
                    minCenter = rawData.getFrame().getCenter();
                }
            }
        }
        return new Frame(minCenter,frame.getWidth(),frame.getHeight());
    }

    public FindFirstAproxTetaVsRadius getFindFirstAproxTetaVsRadius() {
        return findFirstAproxTetaVsRadius;
    }

    public Frame getFrameFirstApproximate() {
        return frameFirstApproximate;
    }

    public GeneralCameraModuleIn getCameraModuleIn() {
        return cameraModuleIn;
    }

    public RawData getRawData() {
        return rawData;
    }

    public double getRMSangle() {
        return RMSangle;
    }
}
