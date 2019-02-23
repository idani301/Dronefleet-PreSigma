package eyesatop.controllersimulatornew;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.Velocities;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 16/10/2017.
 */

public class TelemetrySimulator {

    private long timeInSimulator = 0;

    private boolean everStarted = false;

    private long telemetryInjectionInterval = 100;
    private final long DELAY_FROM_RC_TO_DRONE = 2;

    private double compassError = 0;

    private boolean isTesting = false;

    private final ExecutorService injectTelemetryExecutor = Executors.newSingleThreadExecutor();

    private final Property<Telemetry> telemetry;

    private final Property<Telemetry> ourTelemetry = new Property<>();

    public TelemetrySimulator(Property<Telemetry> telemetry) {
        this.telemetry = telemetry;
    }

    public void start() throws DroneTaskException {

        if(everStarted == true){
            throw new DroneTaskException("Already Started");
        }
        everStarted = true;

        ourTelemetry.set(telemetry.value());

        injectTelemetryExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){

                    try {
                        Thread.sleep(telemetryInjectionInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }

                    telemetry.set(ourTelemetry.value());
                }
            }
        });
    }

    public void destroy(){
        injectTelemetryExecutor.shutdownNow();
    }

    long lastTime = 0;

    /**
     * @param pitch
     * @param roll
     * @param yaw
     * @param vertical
     */
    public void sendSpeedCommand(final double pitch, final double roll, final double yaw, final double vertical){

        long currentTime = System.currentTimeMillis();
        final long interval = lastTime == 0 ? 0 : currentTime - lastTime;
        lastTime = currentTime;

        // Modify ourTelemetry...but not now.

        Thread newThread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (!isTesting) {

//                    try {
//                        Thread.sleep(telemetryInjectionInterval * DELAY_FROM_RC_TO_DRONE);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    try {
                        calcNewTelemetry(pitch, roll, yaw, vertical, interval);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    timeInSimulator += 100;
                    try {
                        calcNewTelemetry(pitch, roll, yaw, vertical, 100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        newThread.start();
    }

    public void calcNewTelemetry(double pitch,double roll, double yaw, double vertical, long travelingTime) throws Exception {

        Double pitchCheck = pitch;
        Double rollCheck = roll;
        if (pitchCheck.isNaN() || rollCheck.isNaN()){
            throw new Exception("You Entered NaN roll pitch please check your calculations");
        }

        Telemetry currentTelemetry = ourTelemetry.value();

        if(currentTelemetry == null){
            return;
        }

        Velocities currentVelocities = currentTelemetry.velocities();
        Velocities newVelocities = getNewVelocities(currentVelocities,new Velocities(roll,pitch,vertical),(double)travelingTime/1000D);
        Location newLocation = getNewLocation(currentTelemetry,(double)travelingTime/1000D);
        Telemetry newTelemetry = new Telemetry(newLocation,newVelocities,Location.degreeBetween0To360(currentTelemetry.heading() + yaw/10));
        ourTelemetry.set(newTelemetry);

    }

    private Location getNewLocation(Telemetry currentTelemetry,double travelingTime){
        double horizontalDistance = currentTelemetry.velocities().toPoint3D().get2dRadius()*travelingTime;
        double verticalDistance = currentTelemetry.velocities().getZ()*travelingTime;
        double angle = currentTelemetry.velocities().toPoint3D().getHorizontalVector().getDJIAzimuthDegree();

        return currentTelemetry.location().getLocationFromAzAndDistance(horizontalDistance,angle + compassError,verticalDistance);
    }

    private Velocities getNewVelocities(Velocities current, Velocities wanted,double travelingTime){

        double vx = -travelingTime*(current.getX() - wanted.getX()) - Math.signum(current.getX() - wanted.getX())*travelingTime*2 + current.getX();
        if (Math.abs(vx - wanted.getX()) < 0.1 || Math.abs(current.getX() - wanted.getX()) < 0.1 || Math.signum(wanted.getX() - current.getX())*vx > Math.signum(wanted.getX() - current.getX())*wanted.getX()){
            vx = wanted.getX();
        }
        double vy = -travelingTime*(current.getY() - wanted.getY()) - Math.signum(current.getY() - wanted.getY())* travelingTime*2 + current.getY();
        if (Math.abs(vy - wanted.getY()) < 0.1 || Math.abs(current.getY() - wanted.getY()) < 0.1 || Math.signum(wanted.getY() - current.getY())*vy > Math.signum(wanted.getY() - current.getY())*wanted.getY()){
            vy = wanted.getY();
        }
        double vz = -travelingTime*(current.getZ() - wanted.getZ()) - Math.signum(current.getZ() - wanted.getZ())* travelingTime*2 + current.getZ();
        if (Math.abs(vz - wanted.getZ()) < 0.1 || Math.abs(current.getZ() - wanted.getZ()) < 0.1 || Math.signum(wanted.getZ() - current.getZ())*vz > Math.signum(wanted.getZ() - current.getZ())*wanted.getZ()){
            vz = wanted.getZ();
        }

        return new Velocities(vx,vy,vz);
    }

    public Property<Telemetry> simulatorsTelemetry(){
        return ourTelemetry;
    }

    public long getTelemetryInjectionInterval() {
        return telemetryInjectionInterval;
    }

    public void setTelemetryInjectionInterval(long telemetryInjectionInterval) {
        this.telemetryInjectionInterval = telemetryInjectionInterval;
    }

    public void setTesting(boolean testing) {
        isTesting = testing;
    }

    public long getTimeInSimulator() {
        return timeInSimulator;
    }
}
