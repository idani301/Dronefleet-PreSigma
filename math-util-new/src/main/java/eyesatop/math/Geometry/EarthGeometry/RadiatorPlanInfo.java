package eyesatop.math.Geometry.EarthGeometry;

import eyesatop.math.Geometry.Angle;
import eyesatop.math.camera.CameraModule;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 25/09/2017.
 */


public class RadiatorPlanInfo {

    public enum RadiatorType{
        NORMAL,
        OBLIMAPPER
    }

    private final RadiatorPlan radiatorPlan;
    private final double horizontalVelocity;
    private final double timeLapse;

    private final RadiatorType radiatorType;
    private static final double TIME_LAPSE = 3;


    public RadiatorPlanInfo(RadiatorPlan radiatorPlan, double horizontalVelocity, double timeLapse, RadiatorType radiatorType) {
        this.radiatorPlan = radiatorPlan;
        this.horizontalVelocity = horizontalVelocity;
        this.timeLapse = timeLapse;
        this.radiatorType = radiatorType;
    }

    public static RadiatorPlanInfo buildRadiatorPlan(Location centerLocations, double lengthFlightInMeters, double widthFlightInMeters, Angle azimuth, double heightInMeters, RadiatorType radiatorType, CameraModule cameraModule, double maxVelocity) throws Exception {

        double gap = calcGapBetweenLags(radiatorType,heightInMeters,cameraModule);
        double distanceToFly = calcGapBetweenShots(radiatorType,heightInMeters,cameraModule);

        widthFlightInMeters += gap/2;
        lengthFlightInMeters += distanceToFly/2;

        RadiatorPlan radiatorPlan = new RadiatorPlan(centerLocations,
                lengthFlightInMeters,
                widthFlightInMeters,
                gap,
                heightInMeters,
                azimuth.degree());


        double timeLapse = distanceToFly/maxVelocity;
        double velocity = maxVelocity;
        if (timeLapse < TIME_LAPSE){
            timeLapse = TIME_LAPSE;
            velocity = distanceToFly/timeLapse;
            if (velocity > maxVelocity){
                throw new Exception("can't make this plan");
            }
        }

        return new RadiatorPlanInfo(radiatorPlan,velocity,timeLapse,radiatorType);
    }

    private static double calcGapBetweenLags(RadiatorType radiatorType, double heightInMeters, CameraModule cameraModule) {
        double percentsOfOverLap = 0;
        switch (radiatorType){
            case NORMAL:
                percentsOfOverLap = 0.4;
                break;
            case OBLIMAPPER:
                percentsOfOverLap = 0.9;
                break;
        }
        Pixel pixel = cameraModule.getFrame().getCenter();
        double angle = cameraModule.getPoint3DFromPixel(new Pixel(pixel.getU() - cameraModule.getFrame().getWidth()/2,pixel.getV(),pixel.getSize())).getElevation();

        return 2*heightInMeters*Math.tan(angle)*percentsOfOverLap;
    }

    private static double calcGapBetweenShots(RadiatorType radiatorType, double heightInMeters, CameraModule cameraModule) {
        double percentsOfOverLap = 0;
        switch (radiatorType){
            case NORMAL:
                percentsOfOverLap = 0.4;
                break;
            case OBLIMAPPER:
                percentsOfOverLap = 0.4;
                break;
        }
        Pixel pixel = cameraModule.getFrame().getCenter();
        double angle = cameraModule.getPoint3DFromPixel(new Pixel(pixel.getU(),pixel.getV() - cameraModule.getFrame().getHeight()/2,pixel.getSize())).getAzimuth();

        return 2*heightInMeters*Math.tan(angle)*percentsOfOverLap;
    }

    public RadiatorPlan getRadiatorPlan() {
        return radiatorPlan;
    }

    public double getHorizontalVelocity() {
        return horizontalVelocity;
    }

    public double getTimeLapse() {
        return timeLapse;
    }

    public RadiatorType getRadiatorType() {
        return radiatorType;
    }

    public double getEstimatedTime(){

        return radiatorPlan.getDistanceCovered()/horizontalVelocity;

    }
}
