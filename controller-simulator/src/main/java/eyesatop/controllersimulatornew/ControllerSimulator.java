package eyesatop.controllersimulatornew;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.AbstractDroneAirLink;
import com.example.abstractcontroller.components.AbstractDroneBattery;
import com.example.abstractcontroller.components.AbstractDroneCamera;
import com.example.abstractcontroller.components.AbstractDroneFlightTasks;
import com.example.abstractcontroller.components.AbstractDroneGimbal;
import com.example.abstractcontroller.components.AbstractDroneHome;
import com.example.abstractcontroller.components.AbstractDroneRemoteController;
import com.example.abstractcontroller.components.ComponentConnectivityType;
import com.example.abstractcontroller.functions.ConnectivityToComponentConnectivity;

import java.util.Random;
import java.util.UUID;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsSignalLevel;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.beans.MediaStorage;
import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.airlink.AirLinkTaskType;
import eyesatop.controller.tasks.battery.BatteryTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.remotecontroller.RemoteControllerTaskType;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.controllersimulatornew.beans.SimulatorZoomSpec;
import eyesatop.controllersimulatornew.components.DroneBatterySimulator;
import eyesatop.controllersimulatornew.components.DroneCameraSimulator;
import eyesatop.controllersimulatornew.components.DroneFlightTasksSimulator;
import eyesatop.controllersimulatornew.components.DroneGimbalSimulator;
import eyesatop.controllersimulatornew.components.DroneHomeSimulator;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.drone.DroneModel;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.ObstacleProvider;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.Velocities;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 29/08/2017.
 */

public class ControllerSimulator extends AbstractDroneController {

    private final BooleanProperty failEnable = new BooleanProperty(true);
    private final Property<ComponentConnectivityType> cameraConnectivity = new Property<>();
    RemovableCollection cameraConnectivityRemovable = new RemovableCollection();

    public ControllerSimulator(ObstacleProvider obstacleProvider,UUID uuid) {

        super(obstacleProvider,uuid);

        cameraConnectivityRemovable.add(cameraConnectivity.bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance())));

        this.droneCamera.getConnectivity().bind(cameraConnectivity);
        this.gimbal.getConnectivity().bind(cameraConnectivity);
        this.flightTasks.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));
        this.droneHome.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));
        this.battery.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));
        this.remoteController.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));
        this.airLink.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));

        this.model().set(DroneModel.SIMULATOR);
    }

    public ControllerSimulator(ObstacleProvider obstacleProvider) {

        super(obstacleProvider,null);

        cameraConnectivityRemovable.add(cameraConnectivity.bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance())));

        this.droneCamera.getConnectivity().bind(cameraConnectivity);
        this.gimbal.getConnectivity().bind(cameraConnectivity);
        this.flightTasks.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));
        this.droneHome.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));
        this.battery.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));
        this.remoteController.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));
        this.airLink.getConnectivity().bind(connectivity().transform(ConnectivityToComponentConnectivity.getInstance()));

        this.model().set(DroneModel.SIMULATOR);
    }

    public void removeCamera(){
        cameraConnectivityRemovable.remove();
        cameraConnectivity.set(ComponentConnectivityType.NOT_CONNECTED);
    }

    public void togglePower(Location crosshairLocation) {
        DroneConnectivity currentConnecivity = connectivity().value();
        switch (currentConnecivity){

            case NOT_CONNECTED:
                turnOn(crosshairLocation);
                break;
            case CONTROLLER_CONNECTED:
                break;
            case REFRESHING:
                break;
            case DRONE_CONNECTED:
                connectivity().set(DroneConnectivity.NOT_CONNECTED);
                getDroneBattery().stopBatterySimulator();
                break;
        }
    }

    private void turnOn(Location crosshairLocation)  {


        Random rand = new Random();

        flightTasks().getTelemetrySimulator().simulatorsTelemetry().set(
                new Telemetry(crosshairLocation.altitude(0),new Velocities(0,0,0),randomInt(rand,0,359)));

        boolean limitationActive = rand.nextBoolean();
        droneHome().limitationActive().set(limitationActive);
        droneHome().maxDistanceFromHome().set(limitationActive ? (double) randomInt(rand,30,5000) : null);
        droneHome().maxAltitudeFromTakeOffLocation().set(limitationActive ? (double) randomInt(rand,30,5000) : null);
        droneHome().returnHomeAltitude().set((double) randomInt(rand,30,1000));
        droneHome().homeLocation().set(crosshairLocation);

        rcInFunctionMode().set(true);
        flightMode().set(FlightMode.APP_CONTROL);

        int batteryPercent = 100;
        droneBattery().set(new BatteryState(100, batteryPercent, 10, 100));
        rcBattery().set(new BatteryState(100,batteryPercent,10,100));
        getDroneBattery().startBatterySimulator();

        flying().set(false);
        motorsOn().set(false);
        rcSignalStrengthPercent().set(randomInt(rand,0,100));

        int maxOpticalZoomFactor = randomInt(rand,21,30);
        camera().getZoomSpec().set(new SimulatorZoomSpec(maxOpticalZoomFactor,1));

        camera().zoomInfo().set(new ZoomInfo(randomInt(rand,20,maxOpticalZoomFactor),randomInt(rand,1,2)));

        gps().set(new GpsState(randomInt(rand,5,30),GpsSignalLevel.values()[randomInt(rand,1,GpsSignalLevel.values().length-1)]));
        gimbal().gimbalState().set(new GimbalState(0, randomDouble(rand,-90,0),randomDouble(rand,0,360)));

        int maxMediaStorageSpace = randomInt(rand,1000,50000);
        camera().mediaStorage().set(new MediaStorage(
                randomInt(rand,0,maxMediaStorageSpace),
                randomInt(rand,0,maxMediaStorageSpace),
                maxMediaStorageSpace));
        camera().mode().set(CameraMode.STILLS);
        camera().recording().set(false);
        camera().isShootingPhoto().set(false);
        connectivity().set(DroneConnectivity.DRONE_CONNECTED);
    }

    public static int randomInt(Random rand,int minValue, int maxValue){
        return rand.nextInt(maxValue +1 - minValue) + minValue;
    }

    private double randomDouble(Random rand,int minValue, int maxValue){
        return minValue + (maxValue - minValue)*rand.nextDouble();
    }

    @Override
    protected AbstractDroneHome createDroneHome() {
        return new DroneHomeSimulator(this);
    }

    @Override
    protected AbstractDroneCamera createDroneCamera() {
        return new DroneCameraSimulator(this);
    }

    @Override
    protected AbstractDroneFlightTasks createDroneFlightTasks() {
        return new DroneFlightTasksSimulator(this);
    }

    @Override
    public DroneCameraSimulator camera() {
        return (DroneCameraSimulator) droneCamera;
    }

    @Override
    public DroneFlightTasksSimulator flightTasks() {
        return (DroneFlightTasksSimulator) super.flightTasks();
    }

    @Override
    protected AbstractDroneGimbal createDroneGimbal() {
        return new DroneGimbalSimulator(this);
    }

    @Override
    protected AbstractDroneAirLink createDroneAirLink() {
        return new AbstractDroneAirLink() {
            @Override
            protected RunnableDroneTask<AirLinkTaskType> stubToRunnable(StubDroneTask<AirLinkTaskType> stubDroneTask) throws DroneTaskException {
                return null;
            }

            @Override
            public void onComponentAvailable() {

            }

            @Override
            public void onComponentConnected() {

            }
        };
    }

    @Override
    protected DroneBatterySimulator createDroneBattery() {
        return new DroneBatterySimulator(this);
    }

    @Override
    protected AbstractDroneRemoteController createDroneRemoteController() {
        return new AbstractDroneRemoteController() {
            @Override
            protected RunnableDroneTask<RemoteControllerTaskType> stubToRunnable(StubDroneTask<RemoteControllerTaskType> stubDroneTask) throws DroneTaskException {
                return null;
            }

            @Override
            public void onComponentAvailable() {

            }

            @Override
            public void onComponentConnected() {

            }
        };
    }

    @Override
    public DroneBatterySimulator getDroneBattery() {
        return (DroneBatterySimulator) super.getDroneBattery();
    }

    public BooleanProperty getFailEnable() {
        return failEnable;
    }

    private final double DELAY_FROM_RC_TO_DRONE = 200;
    private final double TELEMETRY_INTERVAL = 100;

}
