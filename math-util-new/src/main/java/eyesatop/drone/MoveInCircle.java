package eyesatop.drone;

import java.util.ArrayList;

import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 05/09/2017.
 */

public class MoveInCircle implements DroneMovement{

    public enum MoveInCircleStatus{
        MOVE_IN_CIRCLE,
        MOVE_TOWARDS_CIRCLE,
        FINISHED, SLOW_DOWN;
    }

    private final double radius;
    private final Angle angle;
    private final Location centerOfCircle;
    private final double velocityInsideCircle;
    private final double droneMaxSpeed;
    private final int isRotatingClockwise;
    private MoveInCircleStatus moveInCircleStatus = MoveInCircleStatus.MOVE_TOWARDS_CIRCLE;
    private int numberOfCircles;
    private Angle startingAngle = null;
    private double angleFromStart = 0;
    private Angle oldAngle;

    private double counter = 0;
    private double counterTimeInCircle = 0;

    public MoveInCircle(double radius, Location centerOfCircle, double velocityInsideCircle, boolean isRotatingClockwise, double droneMaxSpeed, DroneTelemetry droneTelemetry, Angle angle, int numberOfCircles) {
        this.radius = radius;
        this.angle = angle;
        this.centerOfCircle = centerOfCircle;
        this.velocityInsideCircle = velocityInsideCircle;
        this.droneMaxSpeed = droneMaxSpeed;
        this.numberOfCircles = numberOfCircles;
        if (isRotatingClockwise){
            this.isRotatingClockwise  = -1;
        } else {
            this.isRotatingClockwise = -1;
        }
        ArrayList<Point2D> point2Ds = new ArrayList<>();
        point2Ds.add(Point2D.zero());
        point2Ds.add(Point2D.cartesianPoint(calcCircledRadius(),1));
        point2Ds.add(Point2D.cartesianPoint(calcCircledRadius()/2,0.25));
    }

    public MoveInCircle(double radius, Location centerOfCircle, double velocityInsideCircle, boolean isRotatingClockwise, double droneMaxSpeed, DroneTelemetry droneTelemetry, int numberOfCircles) {
        this.radius = radius;
        this.numberOfCircles = numberOfCircles;
        this.angle = null;
        this.centerOfCircle = centerOfCircle;
        this.velocityInsideCircle = velocityInsideCircle;
        this.droneMaxSpeed = droneMaxSpeed;
        if (isRotatingClockwise){
            this.isRotatingClockwise  = -1;
        } else {
            this.isRotatingClockwise = 1;
        }
        ArrayList<Point2D> point2Ds = new ArrayList<>();
        point2Ds.add(Point2D.zero());
        point2Ds.add(Point2D.cartesianPoint(calcCircledRadius(),calcSpeedNearCircle()));
        point2Ds.add(Point2D.cartesianPoint(calcCircledRadius()/2,calcSpeedNearCircle()/4));
    }

    @Override
    public DroneSpeeds Movement(DroneTelemetry droneTelemetry) {
        counter++;
        double parallelVelocity = isRotatingClockwise*velocityInsideCircle;
        Angle angle = Angle.angleRadian(droneTelemetry.getLocation().azimuth(centerOfCircle));
        double distanceFromRadius = centerOfCircle.distance(droneTelemetry.getLocation()).get2dRadius() - radius;
        double perpendicularVelocity = 0;

        if (distanceFromRadius > calcCircledRadius()){
            moveInCircleStatus = MoveInCircleStatus.MOVE_TOWARDS_CIRCLE;
            return new MoveToPoint(droneMaxSpeed,radius, centerOfCircle,calcSpeedNearCircle()).Movement(droneTelemetry);
        }
        if (distanceFromRadius < -calcCircledRadius()) {
            moveInCircleStatus = MoveInCircleStatus.MOVE_TOWARDS_CIRCLE;
            double angleTemp = centerOfCircle.azimuth(droneTelemetry.getLocation());
            Location locationTemp = droneTelemetry.getLocation().findPosition(Math.abs(distanceFromRadius),Math.toDegrees(angleTemp),droneTelemetry.getLocation().Height());

            return new MoveToPoint(droneMaxSpeed,calcCircledRadius(), locationTemp,calcSpeedNearCircle()).Movement(droneTelemetry);
        }
        if (moveInCircleStatus != MoveInCircleStatus.MOVE_IN_CIRCLE && startingAngle == null){
            startingAngle = angle;
            oldAngle = new Angle(angle);
        }
        angleFromStart += oldAngle.distanceFromAngle(angle).degree();
        moveInCircleStatus = MoveInCircleStatus.MOVE_IN_CIRCLE;
        counterTimeInCircle++;
        if (radius < 100) {
            perpendicularVelocity = distanceFromRadius;
            if (Math.abs(perpendicularVelocity) > 10){
                perpendicularVelocity = Math.signum(distanceFromRadius)*10;
            }
        } else {
            perpendicularVelocity = 0.5 * distanceFromRadius;
        }
        Point2D velocity = Point2D.cartesianPoint(perpendicularVelocity,parallelVelocity);
        while (velocity.getRadius() > droneMaxSpeed) {
            parallelVelocity-=Math.signum(isRotatingClockwise);
            velocity = Point2D.cartesianPoint(perpendicularVelocity, parallelVelocity);
        }
        Point2D oldVelocity = droneTelemetry.getDroneSpeeds().getVelocity().getHorizontalVector();
        velocity = velocity.rotate(angle.radian());
        Angle angle1 = Angle.angleRadian(centerOfCircle.azimuth(droneTelemetry.getLocation()));
        if ((this.angle != null && angle1.distanceFromAngle(this.angle).degree() < Angle.angleDegree(velocityInsideCircle*100/radius).degree()) || (int)(angleFromStart/360) == numberOfCircles ) {
            if (oldVelocity.size() < 1) {
                moveInCircleStatus = MoveInCircleStatus.FINISHED;
                return new DroneSpeeds(Point3D.zero());
            }
            moveInCircleStatus = MoveInCircleStatus.SLOW_DOWN;
            velocity = oldVelocity.normal().multiple(oldVelocity.size() - 1);
        }

        Point2D dv = oldVelocity.minus(velocity);
            if (Math.abs(dv.getX()) > 1 ){
                velocity = Point2D.cartesianPoint(oldVelocity.getX() - 0.9*Math.signum(dv.getX()),velocity.getY());
            }
            if (Math.abs(dv.getY()) > 1 ){
                velocity = Point2D.cartesianPoint(velocity.getX(),oldVelocity.getY() - 0.9*Math.signum(dv.getY()));
            }

        oldAngle = new Angle(angle);
        return new DroneSpeeds(Point3D.cartesianPoint(velocity,0));


    }

    public double calcCircledRadius(){
        if (radius > 100)
            return 20;
        return 20 + 1000/radius;
    }

    public double calcSpeedNearCircle(){
        if (radius > 100)
            return 4;
        return (20 - 200/radius)/10;
    }

    public MoveInCircleStatus getMoveInCircleStatus() {
        return moveInCircleStatus;
    }

    public double getRadius() {
        return radius;
    }

    public Angle getAngle() {
        return angle;
    }

    public Location getCenterOfCircle() {
        return centerOfCircle;
    }

    public double getVelocityInsideCircle() {
        return velocityInsideCircle;
    }

    public double getDroneMaxSpeed() {
        return droneMaxSpeed;
    }

    public int getIsRotatingClockwise() {
        return isRotatingClockwise;
    }

    public int getNumberOfCircles() {
        return numberOfCircles;
    }

    public Angle getStartingAngle() {
        return startingAngle;
    }

    public double getAngleFromStart() {
        return angleFromStart;
    }

    public Angle getOldAngle() {
        return oldAngle;
    }

    public double getTimeInCircle() {
        return counterTimeInCircle*0.1;
    }

    public double getTimeInMovement(){
        return counter*0.1;
    }
}
