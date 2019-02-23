package eyesatop.drone;

import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 16/08/2017.
 */

public class MicroDroneMovement {

    final private double maxHorizontalSpeed;
    final private double maxVerticalSpeed;

    final private double maxSpeedsChange;


    public MicroDroneMovement(double maxHorizontalSpeed, double maxVerticalSpeed, double maxSpeedsChange) {
        this.maxHorizontalSpeed = maxHorizontalSpeed;
        this.maxVerticalSpeed = maxVerticalSpeed;
        this.maxSpeedsChange = maxSpeedsChange;
    }

    public static MicroDroneMovement Mavic(){
        return new MicroDroneMovement(15,5,2);
    }

    public static MicroDroneMovement Phantom3(){
        return new MicroDroneMovement(8,4,2);
    }

    public static MicroDroneMovement Phantom4(){
        return new MicroDroneMovement(12,5,2);
    }

    public DroneSpeeds speedsToDrone(DroneMovement droneMovement, DroneTelemetry droneTelemetry) throws Exception {

        DroneSpeeds droneSpeeds = droneMovement.Movement(droneTelemetry);
        Point2D point2D = droneSpeeds.getVelocity().getHorizontalVector();
        Point2D check = point2D.minus(droneTelemetry.getDroneSpeeds().getVelocity().getHorizontalVector());
        if (check.getX() > maxSpeedsChange || check.getY() > maxSpeedsChange){
            throw new DroneSimulatorException(DroneSimulatorException.DroneSimulatorExceptionReason.ChangeVelocityTooBig);
        }
        if (droneSpeeds.getVelocity().get2dRadius() > maxHorizontalSpeed){
            point2D = point2D.normal().multiple(maxHorizontalSpeed);
        }
        return new DroneSpeeds(Point3D.cartesianPoint(point2D,droneSpeeds.getVelocity().getZ()));
    }

    public double getMaxHorizontalSpeed() {
        return maxHorizontalSpeed;
    }

    public double getMaxVerticalSpeed() {
        return maxVerticalSpeed;
    }
}
