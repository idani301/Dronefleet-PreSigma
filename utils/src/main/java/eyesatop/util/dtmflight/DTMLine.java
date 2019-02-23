package eyesatop.util.dtmflight;

import java.util.ArrayList;
import java.util.Random;

import eyesatop.math.Geometry.Point2D;

public class DTMLine {

    private final ArrayList<Point2D> dTMPoint2DS;
    private final double dx;

    private DTMLine(ArrayList<Point2D> dTMPoint2DS, double dx) {
        this.dTMPoint2DS = dTMPoint2DS;
        this.dx = dx;
    }

    public static DTMLine createNewLine(double dx, ArrayList<Double> heights){
        ArrayList<Point2D> dtm = new ArrayList<>();
        for (double i = 0; i < heights.size(); i++) {
            dtm.add(Point2D.cartesianPoint(dx*i,heights.get((int) i)));
        }
        return new DTMLine(dtm, dx);
    }

    public static DTMLine makeRandomDTMLine(double length, double dxInMeter, double maxDeltaHeightPerSection){
        ArrayList<Point2D> dtm = new ArrayList<>();
        Random random = new Random();
        int numberOfSteps = (int) (length/dxInMeter);
        dtm.add(Point2D.zero());
        for (double i = 0; i < numberOfSteps; i++) {
            dtm.add(Point2D.cartesianPoint(dxInMeter*i,dtm.get(dtm.size()-1).getY() + random.nextDouble()*maxDeltaHeightPerSection - maxDeltaHeightPerSection/3));
        }
        return new DTMLine(dtm, dxInMeter);
    }

    public DTMLine calculateScaledDTMLine(double droneScale)  {
//        if (droneScale < dx)
//            throw new Exception("drone is to big for this dtm flight");
        int scale = (int) (droneScale/dx);
        ArrayList<Point2D> dtm = new ArrayList<>();
        int index = 0;
        dtm.add(dTMPoint2DS.get(0));
        for (int i = 1; i < dTMPoint2DS.size(); i++) {
            Point2DArrayList point2DS = new Point2DArrayList();
            point2DS.add(dTMPoint2DS.get(i));
            if (i%scale == 0){
                index++;
                dtm.add(Point2D.cartesianPoint(index*droneScale,point2DS.getMaxY().getY()));
                point2DS = new Point2DArrayList();
            }
        }
        return new DTMLine(dtm,droneScale);
    }

    public Point2D calculateVelocityToFly(Point2D maxVelocity, Point2D droneCurrentVelocity,double droneDeltaHeightFromFlightHeight, double safeDistance, double gapInMeter, double droneScale){

        maxVelocity = Point2D.cartesianPoint(Math.min(droneCurrentVelocity.getX()+0.2,maxVelocity.getX()),Math.min(droneCurrentVelocity.getY() + 0.2,maxVelocity.getY()));

        AngleAndDistance angleAndDistance = calculateBestAngleToFly(droneDeltaHeightFromFlightHeight,gapInMeter);

        double dAngle = droneCurrentVelocity.getAngle() - angleAndDistance.angle;

        double velocityX = (angleAndDistance.distance/3.5)*Math.cos(angleAndDistance.angle)*FactorDueToAngleChange(dAngle,droneCurrentVelocity.size());
        double velocityZ = (angleAndDistance.distance/3.5)*Math.sin(angleAndDistance.angle)*FactorDueToAngleChange(dAngle,droneCurrentVelocity.size());

        if (angleAndDistance.distance <= safeDistance){
//            angleAndDistance = this.calculateScaledDTMLine(dx*2).calculateBestAngleToFly(droneDeltaHeightFromFlightHeight,gapInMeter);

            double distanceOverTheGap = droneDeltaHeightFromFlightHeight - dTMPoint2DS.get(0).getY() + gapInMeter;
            Point2D velocityOutOfGap;
            double distanceOverTheGapNextStep = droneDeltaHeightFromFlightHeight - dTMPoint2DS.get(1).getY() - gapInMeter;
            velocityOutOfGap = outOfGapVelocityFunction(angleAndDistance,maxVelocity,droneCurrentVelocity,distanceOverTheGap,distanceOverTheGapNextStep);
            velocityX = velocityOutOfGap.getX();
            velocityZ = velocityOutOfGap.getY();
        }

        if (velocityX > maxVelocity.getX()){
            velocityZ = velocityZ * maxVelocity.getX()/velocityX;
            velocityX = maxVelocity.getX();
        }
        if (velocityZ > maxVelocity.getY()){
            velocityX = velocityX * maxVelocity.getY() / velocityZ;
            velocityZ = maxVelocity.getY();
        }

        return Point2D.cartesianPoint(velocityX,velocityZ);

    }

    /*
        must be fixed, need simulator for that
     */
    public Point2D outOfGapVelocityFunction(AngleAndDistance angleAndDistance, Point2D maxVelocity, Point2D droneCurrentVelocity, double distanceOverTheGap, double distanceOverTheGapNextStep) {
        double angle = angleAndDistance.angle + angleAndDistance.dAngle;
        if (distanceOverTheGap < 0) {
            return Point2D.cartesianPoint(maxVelocity.getX(), 0);
        }
        if (distanceOverTheGapNextStep < 0) {
            return Point2D.cartesianPoint(0, maxVelocity.getY());
        }

        double dAngle = droneCurrentVelocity.getAngle() - angle;
        return Point2D.cartesianPoint((1.0/3.5)*Math.cos(angle)*FactorDueToAngleChange(dAngle,droneCurrentVelocity.size()),(1.0/3.5)*Math.sin(angle)*FactorDueToAngleChange(dAngle,droneCurrentVelocity.size()));
    }

    public ArrayList<Point2D> getdTMPoint2DS() {
        return dTMPoint2DS;
    }

    public AngleAndDistance calculateBestAngleToFly(double droneDeltaHeightFromFlightHeight, double gapInMeter){
        double angleUp = Math.PI/2;
        double angleDown = -Math.PI/2;
        double angleToFly = 0;
        double dAngleToFly = 0;
        double calculatedDistance = 0;
        for (int i = 1; i < dTMPoint2DS.size(); i++) {
            double dx = dTMPoint2DS.get(i).getX();
            double dyUp = dTMPoint2DS.get(i).getY() + gapInMeter - droneDeltaHeightFromFlightHeight;
            double dyDown = dTMPoint2DS.get(i).getY() - gapInMeter - droneDeltaHeightFromFlightHeight;
            double angleUpTemp = Math.atan(dyUp/dx);
            double angleDownTemp = Math.atan(dyDown/dx);
            if (angleUp > angleUpTemp){
                angleUp = angleUpTemp;
            }
            if (angleDown < angleDownTemp){
                angleDown = angleDownTemp;
            }
            if (angleDown > angleUp)
                break;
            angleToFly = (angleUp + angleDown)/2;
            dAngleToFly = (angleUp - angleDown)/2;
            calculatedDistance = dx - dTMPoint2DS.get(0).getX();
        }

        return new AngleAndDistance(angleToFly, dAngleToFly, calculatedDistance);
    }


    public class AngleAndDistance{
        private final double angle;
        private final double dAngle;
        private final double distance;

        public AngleAndDistance(double angle, double dAngle, double distance) {
            this.angle = angle;
            this.dAngle = dAngle;
            this.distance = distance;
        }

        public double getAngle() {
            return angle;
        }

        public double getDistance() {
            return distance;
        }

        public double getdAngle() {
            return dAngle;
        }

        @Override
        public String toString() {
            return "AngleAndDistance{" +
                    "angle=" + angle +
                    ", dAngle=" + dAngle +
                    ", distance=" + distance +
                    '}';
        }
    }


    public double angleChangeAsFunctionOfVelocity(double velocitySize){
        if (velocitySize < 0.1){
            return Math.PI/2;
        }
        return Math.PI/2 - velocitySize*Math.PI/30;
    }

    public double FactorDueToAngleChange(double dAngle, double velocitySize){
        if(angleChangeAsFunctionOfVelocity(velocitySize) > Math.abs(dAngle)){
            return 1;
        }
        double factor = (angleChangeAsFunctionOfVelocity(velocitySize) - Math.abs(dAngle))/(Math.PI/2);
        return Math.max(factor,0);
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < dTMPoint2DS.size(); i++) {
            s+=dTMPoint2DS.get(i).toString() + "\n";
        }
        return s;
    }
}
