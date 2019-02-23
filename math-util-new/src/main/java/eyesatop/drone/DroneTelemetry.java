package eyesatop.drone;

import java.util.Arrays;
import java.util.Random;

import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.RotationMatrix3D;

/**
 * Created by Einav on 16/08/2017.
 */

public class DroneTelemetry {

    final private Location location;
    final private RotationMatrix3D rotationMatrix3D;

    final private DroneSpeeds droneSpeeds;

    final private DroneSpeeds[] droneSpeedsMemory;

    final private double compassError;

    private double timeCounter;

    public DroneTelemetry(Location location, RotationMatrix3D rotationMatrix3D, DroneSpeeds droneSpeeds, DroneSpeeds[] droneSpeedsMemory, double timeInAir) throws Exception {
        this.location = location;
        this.rotationMatrix3D = rotationMatrix3D;
        this.droneSpeeds = droneSpeeds;
        Random random = new Random();
        this.compassError = 3*random.nextDouble() - 1.5;
        timeCounter = timeInAir;
        if (droneSpeedsMemory == null){
            droneSpeedsMemory = new DroneSpeeds[2];
        }
        this.droneSpeedsMemory = droneSpeedsMemory;
    }

    public DroneTelemetry(Location location, RotationMatrix3D rotationMatrix3D, DroneSpeeds droneSpeeds, DroneSpeeds[] droneSpeedsMemory, double compassError, double timeInAir) throws Exception {
        this.location = location;
        this.timeCounter = timeInAir;
        this.rotationMatrix3D = rotationMatrix3D;
        this.droneSpeeds = droneSpeeds;
        Random random = new Random();
        if (Math.abs(compassError) < 3) {
            this.compassError = compassError + 0.1 * random.nextDouble() - 0.05 * Math.signum(compassError);
        }
        else {
            if (Math.abs(compassError) > 4){
                System.out.print("WTF?!");
            }
            this.compassError = compassError + 0.1 * random.nextDouble() - 0.075 * Math.signum(compassError);
        }
        if (droneSpeedsMemory == null){
            droneSpeedsMemory = new DroneSpeeds[2];
        }
        this.droneSpeedsMemory = droneSpeedsMemory;
    }

    public DroneTelemetry setLocation(Location location) throws Exception {
        return new DroneTelemetry(location, rotationMatrix3D, droneSpeeds, droneSpeedsMemory, timeCounter);
    }

    public DroneTelemetry goForwardInTime(DroneSpeeds droneSpeeds) throws Exception {

        double TimeInSecond = 0.1;
        timeCounter += TimeInSecond;
        droneSpeeds = setMemorySpeeds(droneSpeeds);

        RotationMatrix3D rotationMatrix3D = this.rotationMatrix3D;
        rotationMatrix3D.addAngles(droneSpeeds.getAngularSpeed()*TimeInSecond,0,0);

        DroneSpeeds newDroneSpeeds = this.droneSpeeds.setNewSpeeds(droneSpeeds);

        double horizontalDistance = newDroneSpeeds.getVelocity().get2dRadius()*TimeInSecond;
        double verticalDistance = newDroneSpeeds.getVelocity().getZ()*TimeInSecond;

//        Location location = this.location.findPosition(horizontalDistance,newDroneSpeeds.getVelocity().getAzimuthDegree(),this.location.Height() + verticalDistance);
        Location location = this.location.findPosition(horizontalDistance,newDroneSpeeds.getVelocity().getAzimuthDegree() + compassError,this.location.Height() + verticalDistance);

        return new DroneTelemetry(location,rotationMatrix3D, newDroneSpeeds, droneSpeedsMemory,compassError, timeCounter);
    }


    private DroneSpeeds setMemorySpeeds(DroneSpeeds droneSpeeds){
        DroneSpeeds droneSpeedsTemp = droneSpeedsMemory[1];
        droneSpeedsMemory[1] = droneSpeedsMemory[0];
        droneSpeedsMemory[0] = droneSpeeds;
        if (droneSpeedsTemp == null){
            return this.droneSpeeds;
        }
        return droneSpeedsTemp;
    }

    public double getDroneHeading(){
        return rotationMatrix3D.getYaw();
    }

    public Location getLocation() {
        return location;
    }

    public RotationMatrix3D getRotationMatrix3D() {
        return rotationMatrix3D;
    }

    public double getCompassError() {
        return compassError;
    }

    public DroneSpeeds getDroneSpeeds() {
        return droneSpeeds;
    }

    public double getTimeCounter() {
        return timeCounter;
    }

    @Override
    public String toString() {
        return "DroneTelemetry{" +
                "location=" + location +
                ", rotationMatrix3D=" + rotationMatrix3D +
                ", droneSpeeds=" + droneSpeeds +
                ", droneSpeedsMemory=" + Arrays.toString(droneSpeedsMemory) +
                ", compassError=" + compassError +
                '}';
    }
}
