package eyesatop.controllersimulatornew;


import com.example.abstractcontroller.components.AbstractDroneGimbal;

import java.util.ArrayList;
import java.util.Random;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controllersimulatornew.components.DroneFlightTasksSimulator;
import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.camera.ImageInfo;
import eyesatop.math.camera.PinHoleCameraModule;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.ObstacleProvider;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

import static eyesatop.math.camera.CameraName.MAVIC;

/**
 * Created by Einav on 15/11/2017.
 */

public class TestingSimulator extends ControllerSimulator {

    private long timeInFlightTask = 0;
    private long timeInCameraTask = 0;
    private final ArrayList<ImageInfo> imageInfos = new ArrayList<>();
    private final Property<eyesatop.util.geo.Location> lookAtLocation = new Property<>();
    private boolean isFinished = true;


    public TestingSimulator(ObstacleProvider obstacleProvider) {
        super(obstacleProvider);
        flightTasks().getTelemetrySimulator().setTesting(true);
        flightTasks().getTelemetrySimulator().setTelemetryInjectionInterval(0);
    }

    public void togglePower(Telemetry telemetry, GimbalState gimbalState) {
        Random rand = new Random();

        flightTasks().getTelemetrySimulator().simulatorsTelemetry().set(
                telemetry);

        boolean limitationActive = rand.nextBoolean();
        droneHome().limitationActive().set(limitationActive);

        rcInFunctionMode().set(true);
        flightMode().set(FlightMode.APP_CONTROL);
        droneBattery().set(new BatteryState(100, 100, 10, 100));
        flying().set(true);
        motorsOn().set(true);
        rcSignalStrengthPercent().set(100);

        gimbal().gimbalState().set(gimbalState);
        connectivity().set(DroneConnectivity.DRONE_CONNECTED);
    }

    public DroneFlightTasksSimulator flightTasksTesting() {
        timeInFlightTask = super.flightTasks().getTelemetrySimulator().getTimeInSimulator();
        return super.flightTasks();
    }

    public void startCameraShoot(final long interval, eyesatop.util.geo.Location LookAtLocation){
        if (!isFinished)
            return;
        isFinished = false;
        lookAtLocation.set(LookAtLocation);
        final TelemetrySimulator telemetrySimulator = super.flightTasks().getTelemetrySimulator();
        final AbstractDroneGimbal abstractDroneGimbal = gimbal();
        telemetrySimulator.simulatorsTelemetry().observe(new Observer<Telemetry>() {

            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                if (!isFinished) {
                    if ((telemetrySimulator.getTimeInSimulator() - timeInCameraTask) > interval) {
                        timeInCameraTask = telemetrySimulator.getTimeInSimulator();
                        double pitch = Math.toRadians(90);
                        if (newValue.location().distance(lookAtLocation.value()) != 0)
                            pitch = Math.tan(newValue.location().getAltitude() - lookAtLocation.value().getAltitude() / newValue.location().distance(lookAtLocation.value()));
                        double az = newValue.location().az(lookAtLocation.value());
                        imageInfos.add(
                                new ImageInfo(
                                        new PinHoleCameraModule(MAVIC),
                                        RotationMatrix3D.Body3DNauticalAngles(Math.toRadians(az), Math.toRadians(50), 0),
                                        new Location(newValue.location().getLatitude(), newValue.location().getLongitude(), newValue.location().getAltitude()),
                                        null
                                )
                        );
//                        isFinished = true;

                    }
                }
            }



        });
    }

    public void stopShooting(){
        isFinished = true;
    }

    public ArrayList<ImageInfo> getImageInfos() {
        return imageInfos;
    }

    public void setLookAtLocation(eyesatop.util.geo.Location lookAtLocation){
        this.lookAtLocation.set(lookAtLocation);
    }

    public long getTimeInFlightTask() {
        return super.flightTasks().getTelemetrySimulator().getTimeInSimulator() - timeInFlightTask;
    }
}
