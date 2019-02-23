package eyesatop.IdansFunctions;

import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.Point2D;

/**
 * Created by Einav on 14/06/2017.
 */

public class DroneMovement {

    public static final double MAXSPEED = 15;
    public static final double TIMEBETWEENCALCINSECONDS = 0.1;

    public static Point2D getVelocityToFlyInCircle(Point2D centerOfTheCircle, double radius, double droneVelocityOnCircle, Point2D droneLocation){

        Location drone = new Location(droneLocation.getX(),droneLocation.getY());
        Location center = new Location(centerOfTheCircle.getX(),centerOfTheCircle.getY());
        Angle angle = Angle.angleRadian(drone.azimuth(center));
        double parallelVelocity = droneVelocityOnCircle;
        double maxVelocity = calcMaxVelocityInCircle(radius);
        if (parallelVelocity > maxVelocity)
            parallelVelocity = maxVelocity;
        double distance = 0.5*(drone.distance(center).get2dRadius() - radius);
        double perpendicularVelocity = distance;
        if(perpendicularVelocity > MAXSPEED)
            perpendicularVelocity = MAXSPEED;
        if (perpendicularVelocity < -MAXSPEED)
            perpendicularVelocity = -MAXSPEED;
        if (Math.abs(perpendicularVelocity) > MAXSPEED/4)
            parallelVelocity = 0;

        Point2D velocity = Point2D.cartesianPoint(perpendicularVelocity,parallelVelocity);
        //double maxSpeedToCircle = Math.min(MAXSPEED,MAXSPEED*Math.abs(distance/radius)*3);
        while (velocity.size() > MAXSPEED){
            velocity = Point2D.cartesianPoint(perpendicularVelocity,parallelVelocity--);
        }
        velocity = velocity.rotate(angle.radian());
        return velocity;

    }

    private static double calcMaxVelocityInCircle(double radius) {
        return 0.25*radius; //SQRT((R+delta)^2 - R^2) = v*t => t = 0.4 (s)
    }

}
