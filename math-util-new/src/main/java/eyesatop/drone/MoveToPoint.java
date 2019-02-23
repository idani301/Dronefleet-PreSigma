package eyesatop.drone;

import java.util.ArrayList;

import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Polynom;

/**
 * Created by Einav on 17/08/2017.
 */

public class MoveToPoint implements DroneMovement{

    private final double horizontalSpeed;
    private final double radiusReached;
    private final Location location;
    private final Polynom slowDownSpeedPolynom;
    private final double slowDownRadius;
    private final double finalSpeed;


    private boolean isTargetReached;
    private long counter = 0;

    public MoveToPoint(double horizontalSpeed, double radiusReached, Location location, Point2D finalSpeed, DroneTelemetry droneTelemetry) {
        this.horizontalSpeed = Math.abs(horizontalSpeed);
        this.radiusReached = radiusReached;
        this.location = location;
        if (finalSpeed.getRadius() > horizontalSpeed) {
            finalSpeed = finalSpeed.normal().multiple(horizontalSpeed);
        }
        this.finalSpeed = getFinalSpeedValue(finalSpeed,location.azimuth(droneTelemetry.getLocation()));
        isTargetReached = false;
        slowDownRadius = calcSlowDownRadius();
        slowDownSpeedPolynom = calcSlowDownPolynom();
    }

    public MoveToPoint(double horizontalSpeed, double radiusReached, Location location, double finalSpeed) {
        this.horizontalSpeed = Math.abs(horizontalSpeed);
        this.radiusReached = radiusReached;
        this.location = location;
        this.finalSpeed = finalSpeed;
        isTargetReached = false;
        slowDownRadius = calcSlowDownRadius();
        slowDownSpeedPolynom = calcSlowDownPolynom();
    }


    @Override
    public DroneSpeeds Movement(DroneTelemetry droneTelemetry) {
        counter++;
        double angle = droneTelemetry.getLocation().azimuth(location);
        double speed = calcSpeed(droneTelemetry);
        Point2D horizontalSpeeds = Point2D.cartesianPoint(speed,0);
        horizontalSpeeds = horizontalSpeeds.rotate(angle);
        return new DroneSpeeds(Point3D.cartesianPoint(horizontalSpeeds,0));
    }


    public double calcSpeed(DroneTelemetry droneTelemetry){
        double speed = horizontalSpeed;
        if (droneTelemetry.getLocation().distance(location).get2dRadius() < slowDownRadius){
            speed = slowDownSpeedPolynom.value(droneTelemetry.getLocation().distance(location).get2dRadius());
//            speed = 1;
        }
        if (droneTelemetry.getLocation().distance(location).get2dRadius() < radiusReached){
            if (droneTelemetry.getDroneSpeeds().getVelocity().getSphereRadius() <= finalSpeed){
                isTargetReached = true;
            }
            speed = 0;
        }

        double check = speed - droneTelemetry.getDroneSpeeds().getVelocity().get2dRadius();
        if (Math.abs(check) > 1 ){
            speed = droneTelemetry.getDroneSpeeds().getVelocity().get2dRadius() + 1*Math.signum(check);
        }
        return speed;
    }

    private double calcSlowDownRadius() {
        return 3*(horizontalSpeed - finalSpeed)/(radiusReached/6) + 2*radiusReached;
    }

    private double getFinalSpeedValue(Point2D finalSpeed, double angleToTarget){
        double speed = 0;
        if (finalSpeed.rotate(angleToTarget).getX() + radiusReached > horizontalSpeed){
            speed = horizontalSpeed - 6/radiusReached;
        }
        else {
            speed = finalSpeed.rotate(angleToTarget).getX();
            if (speed < 0)
                speed = 0;
        }
        return speed;
    }

    private Polynom calcSlowDownPolynom(){
        ArrayList<Point2D> point2Ds = new ArrayList<>();
        double speed = finalSpeed;
        if (speed < 1){
            speed = 1;
        }
        point2Ds.add(Point2D.cartesianPoint(radiusReached,speed));
        point2Ds.add(Point2D.cartesianPoint((radiusReached+slowDownRadius)/2,0.75*horizontalSpeed));
        point2Ds.add(Point2D.cartesianPoint(slowDownRadius,horizontalSpeed));
        return Polynom.calcPolynomWithPoints(point2Ds);
    }

    public double getSlowDownRadius() {
        return slowDownRadius;
    }

    public double getHorizontalSpeed() {
        return horizontalSpeed;
    }

    public double getRadiusReached() {
        return radiusReached;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isTargetReached() {
        return isTargetReached;
    }

    public double getTimeInTask(){
        return (double)(counter)*0.1;
    }
}
