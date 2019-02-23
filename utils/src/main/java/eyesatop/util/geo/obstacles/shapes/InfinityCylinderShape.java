package eyesatop.util.geo.obstacles.shapes;

import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Shape3D;
import eyesatop.util.geo.Telemetry;

/**
 * Created by Einav on 25/10/2017.
 */

public class InfinityCylinderShape implements Shape3D {

    private final double radius;
    private double timeInterval = 0.3;

    public InfinityCylinderShape(double radius) {
        this.radius = radius;
    }

    @Override
    public Point3D influence(Telemetry my, Telemetry obstacleTelemetry) {

        if(obstacleTelemetry == null || obstacleTelemetry.location() == null || obstacleTelemetry.velocities() == null){
            return Point3D.zero();
        }

        Point2D myVelocity = Point2D.GeographicPoint(my.velocities().getX(),my.velocities().getY());
        Point2D obstacleVelocity = Point2D.GeographicPoint(obstacleTelemetry.velocities().getX(),obstacleTelemetry.velocities().getY());
        Location myLocation = my.location().getLocationFromAzAndDistance(myVelocity.getRadius()*timeInterval,myVelocity.getAngleDegree());
        Location obstacleLocation = obstacleTelemetry.location().getLocationFromAzAndDistance(obstacleVelocity.getRadius()*timeInterval,obstacleVelocity.getAngleDegree());
        double distance = myLocation.distance(obstacleLocation);
        if(distance > radius*3 && myVelocity.getRadius() < 1)
            return Point3D.zero();
        Angle azimuth = Angle.angleDegree(obstacleLocation.az(myLocation));
        double constNumber = Math.pow(radius,6);
        double forceSize = distance == 0 ? Double.MAX_VALUE :  25*constNumber/Math.pow(distance,6);
        Point2D force = Point2D.GeographicPointAzimuthAndRadius(forceSize,azimuth.radian());
        Point2D weightedVelocity = myVelocity.minus(obstacleVelocity);
        double direction = force.getAngle(weightedVelocity);
        double directionSign = 1;
        if (direction < 0)
            directionSign = -1;


        force = force.rotate(Math.toRadians(directionSign*45));


        Point3D point3D = Point3D.cartesianPoint(force,0);

        return point3D;
    }
}
