package eyesatop.controller.djinew;

import com.example.abstractcontroller.AbstractDroneController;
import eyesatop.util.android.VideoCodec;
import com.example.abstractcontroller.components.AbstractDroneAirLink;
import com.example.abstractcontroller.components.AbstractDroneBattery;
import com.example.abstractcontroller.components.AbstractDroneRemoteController;

import java.util.UUID;

import eyesatop.controller.djinew.components.DroneAirLinkDji;
import eyesatop.controller.djinew.components.DroneBatteryDji;
import eyesatop.controller.djinew.components.DroneCameraDji;
import eyesatop.controller.djinew.components.DroneFlightTasksDji;
import eyesatop.controller.djinew.components.DroneGimbalDji;
import eyesatop.controller.djinew.components.DroneHomeDji;
import eyesatop.controller.djinew.components.DroneRemoteControllerDji;
import eyesatop.util.geo.ObstacleProvider;

/**
 * Created by Idan on 08/09/2017.
 */

public class ControllerDjiNew extends AbstractDroneController {

    private final DjiHardwareManager hardwareManager;
//    private final DjiMicroMovementManager microMovementManager = new DjiMicroMovementManager(this);
    private final DjiEnableSticks enableSticks;

    private static final VideoCodec djiVideoCodec = new DjiVideoCodec();


    public ControllerDjiNew(ObstacleProvider obstacleProvider,UUID uuid) {

        super(obstacleProvider,uuid);

        hardwareManager = new DjiHardwareManager(this);
        enableSticks = new DjiEnableSticks(this);
    }

    public ControllerDjiNew(ObstacleProvider obstacleProvider) {

        super(obstacleProvider,null);

        hardwareManager = new DjiHardwareManager(this);
        enableSticks = new DjiEnableSticks(this);
    }

    public DjiHardwareManager getHardwareManager() {
        return hardwareManager;
    }

//    public DjiMicroMovementManager getMovementManager() {
//        return microMovementManager;
//    }

    @Override
    protected DroneHomeDji createDroneHome() {
        return new DroneHomeDji(this);
    }

    @Override
    protected DroneCameraDji createDroneCamera() {
        return new DroneCameraDji(this);
    }

    @Override
    protected DroneFlightTasksDji createDroneFlightTasks() {
        return new DroneFlightTasksDji(this);
    }

    @Override
    protected DroneGimbalDji createDroneGimbal() {
        return new DroneGimbalDji(this);
    }

    @Override
    protected AbstractDroneAirLink createDroneAirLink() {
        return new DroneAirLinkDji(this);
    }

    @Override
    protected AbstractDroneBattery createDroneBattery() {
        return new DroneBatteryDji(this);
    }

    @Override
    protected AbstractDroneRemoteController createDroneRemoteController() {
        return new DroneRemoteControllerDji(this);
    }

    @Override
    public DroneHomeDji droneHome() {
        return (DroneHomeDji)droneHome;
    }

    @Override
    public DroneCameraDji camera() {
        return (DroneCameraDji)droneCamera;
    }

    @Override
    public DroneFlightTasksDji flightTasks() {
        return (DroneFlightTasksDji)flightTasks;
    }

    @Override
    public DroneGimbalDji gimbal() {
        return (DroneGimbalDji)gimbal;
    }

    @Override
    public AbstractDroneBattery getDroneBattery() {
        return super.getDroneBattery();
    }

    @Override
    public AbstractDroneAirLink getAirLink() {
        return super.getAirLink();
    }

    @Override
    public DroneRemoteControllerDji getRemoteController() {
        return (DroneRemoteControllerDji)remoteController;
    }

    public DjiEnableSticks getEnableSticks() {
        return enableSticks;
    }
}
