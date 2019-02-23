package eyesatop.drone;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 16/08/2017.
 */

public class DroneSpeeds {

    final private Point3D velocity;
    final private double angularSpeed;
    final private double pitchSpeed;

    public DroneSpeeds(Point3D velocity, double angularSpeed, double pitchSpeed) {
        this.velocity = velocity;
        this.angularSpeed = angularSpeed;
        this.pitchSpeed = pitchSpeed;
    }

    public DroneSpeeds(Point3D velocity) {
        this.velocity = velocity;
        angularSpeed = 0;
        pitchSpeed = 0;
    }

    public static DroneSpeeds zeroes(){
        return new DroneSpeeds(Point3D.zero(),0,0);
    }

    public DroneSpeeds setNewSpeeds(DroneSpeeds speeds){
        double TimeInSecond = 0.1;
        double vx = -TimeInSecond*(this.velocity.getX() - speeds.velocity.getX()) - Math.signum(this.velocity.getX() - speeds.velocity.getX())*TimeInSecond*2 + this.velocity.getX();
        if (Math.abs(vx) > Math.abs(speeds.velocity.getX()) && Math.signum(vx) == Math.signum(speeds.velocity.getX()+0.000001)){
            vx = speeds.velocity.getX();
        }
        double vy = -TimeInSecond*(this.velocity.getY() - speeds.velocity.getY()) - Math.signum(this.velocity.getY() - speeds.velocity.getY())* TimeInSecond*2 + this.velocity.getY();
        if (Math.abs(vy) > Math.abs(speeds.velocity.getY()) && Math.signum(vy) == Math.signum(speeds.velocity.getY()+0.000001)){
            vy = speeds.velocity.getY();
        }
        double vz = -TimeInSecond*(this.velocity.getZ() - speeds.velocity.getZ()) + TimeInSecond*2 + this.velocity.getZ();
        if (Math.abs(vz) > Math.abs(speeds.velocity.getZ()) && Math.signum(vz) == Math.signum(speeds.velocity.getZ()+0.00001)){
            vz = speeds.velocity.getZ();
        }

        return new DroneSpeeds(Point3D.cartesianPoint(vx, vy, vz),speeds.angularSpeed,speeds.pitchSpeed);
    }

    public Point3D getVelocity() {
        return velocity;
    }

    public double getAngularSpeed() {
        return angularSpeed;
    }

    public double getPitchSpeed() {
        return pitchSpeed;
    }

    @Override
    public String toString() {
        return "DroneSpeeds{" +
                "velocity=" + velocity.toStringCartesian() +
                ", angularSpeed=" + angularSpeed +
                ", pitchSpeed=" + pitchSpeed +
                '}';
    }
}
