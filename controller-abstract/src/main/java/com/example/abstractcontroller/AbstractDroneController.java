package com.example.abstractcontroller;

import com.example.abstractcontroller.components.AbstractDroneAirLink;
import com.example.abstractcontroller.components.AbstractDroneBattery;
import com.example.abstractcontroller.components.AbstractDroneCamera;
import com.example.abstractcontroller.components.AbstractDroneFlightTasks;
import com.example.abstractcontroller.components.AbstractDroneGimbal;
import com.example.abstractcontroller.components.AbstractDroneHome;
import com.example.abstractcontroller.components.AbstractDroneRemoteController;
import com.example.abstractcontroller.components.GeneralDroneComponent;
import com.example.abstractcontroller.components.mission.AbstractMissionManager;
import com.example.abstractcontroller.taskmanager.MissionTaskManager;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.beans.RCFlightModeSwitchPosition;
import eyesatop.controller.beans.SticksPosition;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.ObstacleProvider;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

//import eyesatop.util.android.logs.MainLogger;

/**
 * Created by einav on 03/08/2017.
 */

public abstract class AbstractDroneController implements DroneController {

    private final UUID uuid;

    private final Property<Double> aboveGroundAltitude = new Property<>();
    private final Property<Double> aboveSeaAltitude = new Property<>();

    private final Property<SticksPosition> virtualSticksPosition = new Property<>();

    final Property<LookAtLocationInfo> lastInfoUsed = new Property<>();
//    private final ExecutorService altitudesExecutors = Executors.newSingleThreadExecutor();

    private final ExecutorService lookAtLocationExecutor = Executors.newSingleThreadExecutor();
    private final Property<DroneModel> model;
    private final Property<DroneConnectivity> connectivity;
    private final Property<Location> lookAtLocation = new Property<>();

    private final BlockingQueue<LookAtLocationInfo> lookAtLocationInfos = new LinkedBlockingDeque<>();

    protected AbstractDroneHome droneHome;
    protected AbstractDroneCamera droneCamera;
    protected AbstractDroneFlightTasks flightTasks;
    protected AbstractDroneGimbal gimbal;
    protected AbstractDroneBattery battery;
    protected AbstractDroneAirLink airLink;
    protected AbstractDroneRemoteController remoteController;
    protected AbstractMissionManager missionManager;
    protected DroneMicroMovement microMovement;

    private final DtmProvider dtmProvider;

    public AbstractDroneController(ObstacleProvider obstacleProvider,UUID uuid){

        if(uuid == null){
            this.uuid = UUID.randomUUID();
        }
        else{
            this.uuid = uuid;
        }

        model = new Property<>();
        connectivity = new Property<>(DroneConnectivity.NOT_CONNECTED);
        missionManager = new AbstractMissionManager(new MissionTaskManager(), this);
        droneHome = createDroneHome();
        droneCamera = createDroneCamera();
        flightTasks = createDroneFlightTasks();
        gimbal = createDroneGimbal();
        battery = createDroneBattery();
        airLink = createDroneAirLink();
        remoteController = createDroneRemoteController();

        microMovement = new DroneMicroMovement(this,obstacleProvider);

        dtmProvider = obstacleProvider.dtmProvider();

        if(dtmProvider != null) {

            final DtmProvider altitudesDtmProvider = dtmProvider.duplicate();
            final DtmProvider lookAtLocationDtmProvider = altitudesDtmProvider;

            lookAtLocationExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        try {
                            LookAtLocationInfo currentInfo = lookAtLocationInfos.take();
                            while(lookAtLocationInfos.size() > 0){
                                currentInfo = lookAtLocationInfos.take();
                            }
                            calcLookAtLocation(currentInfo,lookAtLocationDtmProvider);
                        } catch (InterruptedException e) {
                            return;
                        }

                    }
                }
            });

//            rtk3DLocation().observe(new Observer<Location>() {
//                @Override
//                public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                    calcAltitudes();
//                }
//            },altitudesExecutors);

            telemetry().observe(new Observer<Telemetry>() {
                @Override
                public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                    calcAltitudes();
                    microMovement.calcNextStep(getSticksPosition(),flightTasks.current().value(),gimbal.currentTask().value(),telemetry().value(),aboveGroundAltitude.value(),aboveSeaAltitude.value());
                }
            });

            droneHome().homeLocation().observe(new Observer<Location>() {
                @Override
                public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                    if(newValue == null){
                        droneHome.takeOffDTM().set(null);
                    }
                    else {
                        try {
                            droneHome.takeOffDTM().set(altitudesDtmProvider.terrainAltitude(newValue));
                        } catch (TerrainNotFoundException e) {
                            droneHome.takeOffDTM().set(null);
                        }
                    }
                }
            });

            telemetry().observe(new Observer<Telemetry>() {
                @Override
                public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {

                    GimbalState currentGimbalState = gimbal().gimbalState().value();
                    Double takeOffDTM = droneHome().takeOffDTM().value();

                    LookAtLocationInfo currentInfo = new LookAtLocationInfo(newValue,currentGimbalState,takeOffDTM);
                    lookAtLocationInfos.add(currentInfo);
//                    calcLookAtLocation(currentInfo,dtmProvider);
                }
            });
        }
        else{
            telemetry().observe(new Observer<Telemetry>() {
                @Override
                public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                    microMovement.calcNextStep(getSticksPosition(),flightTasks.current().value(),gimbal.currentTask().value(),telemetry().value(),aboveGroundAltitude.value(),aboveSeaAltitude.value());
                }
            });
        }

        remoteController.getSticksPosition().observe(new Observer<SticksPosition>() {

            @Override
            public void observe(SticksPosition oldValue, SticksPosition newValue, Observation<SticksPosition> observation) {

                if(newValue == null){
                    return;
                }

                SticksPosition finalSticksPosition = getSticksPosition();

                if(!newValue.equals(finalSticksPosition)){
                    return;
                }

                if(DroneMicroMovement.isSticksRelevant(flightTasks.current().value(),gimbal.currentTask().value(),newValue)){
                    microMovement.calcNextStep(finalSticksPosition,flightTasks.current().value(),gimbal.currentTask().value(),flightTasks.telemetry().value(),aboveGroundAltitude().value(),aboveSeaAltitude.value());
                }
            }
        });

        virtualSticksPosition.observe(new Observer<SticksPosition>() {
            @Override
            public void observe(SticksPosition oldValue, SticksPosition newValue, Observation<SticksPosition> observation) {

                if(newValue == null){
                    return;
                }

                SticksPosition finalSticksPosition = getSticksPosition();

                if(!newValue.equals(finalSticksPosition)){
                    return;
                }

                if(DroneMicroMovement.isSticksRelevant(flightTasks.current().value(),gimbal.currentTask().value(),finalSticksPosition)){
                    microMovement.calcNextStep(finalSticksPosition,flightTasks.current().value(),gimbal.currentTask().value(),flightTasks.telemetry().value(),aboveGroundAltitude().value(),aboveSeaAltitude.value());
                }
            }
        });

        startBlockersObservers();
    }

    public SticksPosition getSticksPosition(){

        SticksPosition sticksPosition = remoteController.getSticksPosition().value();
        SticksPosition virtualSticksPosition = virtualSticksPosition().value();

        if(sticksPosition != null && sticksPosition.isRelevant()){
            return sticksPosition;
        }

        if(virtualSticksPosition != null && virtualSticksPosition.isRelevant()){
            return virtualSticksPosition;
        }

        return null;
    }

    private void calcAltitudes(){

        Telemetry currentTelemetry = telemetry().value();
        Location currentLocation = Telemetry.telemetryToLocation(currentTelemetry);

        Double takeoffDtm = droneHome.takeOffDTM().value();
        Double ultrasonicHeight = flightTasks().getUltrasonicHeight().value();
        Double currentLocationDTM = null;
        Double barometer = currentLocation == null ? null : currentLocation.getAltitude();


        Location finalLocation = currentLocation;

        if(finalLocation != null){
            try {
                currentLocationDTM = dtmProvider.terrainAltitude(finalLocation);
            } catch (TerrainNotFoundException e) {
                e.printStackTrace();
            }
        }

        Double aboveSeaAltitude = calcAboveSeaAltitude(takeoffDtm,barometer);
        Double aboveGroundAltitude = calcAboveGroundLevel(ultrasonicHeight,takeoffDtm,currentLocationDTM,barometer);

        this.aboveSeaAltitude.set(aboveSeaAltitude);
        this.aboveGroundAltitude.set(aboveGroundAltitude);


//        MainLogger.logger.write_message(LoggerTypes.ALTITUDE_CALCS,"Altitude Calcs : " +
//                MainLogger.TAB + "TakeOff DTM                                : " + (takeoffDtm == null ? "NULL" : takeoffDtm.toString()) +
//                MainLogger.TAB + "UltrasonicHeight                           : " + (ultrasonicHeight == null ? "NULL" : ultrasonicHeight.toString()) +
//                MainLogger.TAB + "currentLocationDTM                         : " + (currentLocationDTM == null ? "NULL" : currentLocationDTM.toString()) +
//                MainLogger.TAB + "Above Ground Altitude                      : " + (aboveGroundAltitude == null ? "NULL" : aboveGroundAltitude.toString()) +
//                MainLogger.TAB + "Above Sea Altitude                         : " + (aboveSeaAltitude == null ? "NULL" : aboveSeaAltitude.toString()) +
//                MainLogger.TAB + "Barometer Altitude(From Take Off Location) : " + ((barometer == null) ? "NULL" : barometer.toString())
//        );
    }

    private void calcLookAtLocation(LookAtLocationInfo currentInfo,DtmProvider dtmProvider){

        Location currentLocation = Telemetry.telemetryToLocation(currentInfo.getTelemetry());

        LookAtLocationInfo lastInfo = lastInfoUsed.value();
        if(lastInfo != null && !lastInfo.isNewInfo(currentInfo)){
            return;
        }

        lastInfoUsed.set(currentInfo);

        if(currentLocation == null){
            lookAtLocation.setIfNew(null);
            return;
        }

        if(currentInfo.getGimbalState() == null){
            lookAtLocation.setIfNew(null);
        }

        Location newLocation = DtmProvider.DtmTools.cutWithDTM(currentLocation,currentInfo.getGimbalState(),currentInfo.getTakeOffLocationDTM(), dtmProvider);
        lookAtLocation.setIfNew(newLocation);
    }

    protected abstract AbstractDroneHome createDroneHome();
    protected abstract AbstractDroneCamera createDroneCamera();
    protected abstract AbstractDroneFlightTasks createDroneFlightTasks();
    protected abstract AbstractDroneGimbal createDroneGimbal();
    protected abstract AbstractDroneAirLink createDroneAirLink();
    protected abstract AbstractDroneBattery createDroneBattery();
    protected abstract AbstractDroneRemoteController createDroneRemoteController();

    protected void startBlockersObservers(){
        startBlockersObserversForComponent(droneHome);
        startBlockersObserversForComponent(droneCamera);
        startBlockersObserversForComponent(flightTasks);
        startBlockersObserversForComponent(gimbal);
        startBlockersObserversForComponent(battery);
        startBlockersObserversForComponent(airLink);
        startBlockersObserversForComponent(remoteController);
        startBlockersObserversForComponent(missionManager);
    }

    private void startBlockersObserversForComponent(GeneralDroneComponent component){
        if(component != null && component.getTaskManager() != null){
            component.getTaskManager().startBlockersObservers(this);
        }
    }

    public AbstractMissionManager getMissionManager() {
        return missionManager;
    }

    public BooleanProperty hasCompassError() {
        return flightTasks.hasCompassError();
    }

    public BooleanProperty preheating() {
        return flightTasks.getPreheating();
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public Property<DroneModel> model() {
        return model;
    }

    @Override
    public Property<DroneConnectivity> connectivity() {
        return connectivity;
    }

    @Override
    public Property<Telemetry> telemetry() {
        return flightTasks.telemetry();
    }

    @Override
    public Property<BatteryState> droneBattery() {
        return battery.getDroneBattery();
    }

    @Override
    public BooleanProperty flying() {
        return flightTasks.flying();
    }

    @Override
    public BooleanProperty motorsOn() {
        return flightTasks.motorsOn();
    }

    @Override
    public Property<BatteryState> rcBattery() {
        return remoteController.getRcBattery();
    }

    @Override
    public Property<GpsState> gps() {
        return flightTasks.gps();
    }

    @Override
    public AbstractDroneHome droneHome() {
        return droneHome;
    }

    @Override
    public AbstractDroneCamera camera() {
        return droneCamera;
    }

    @Override
    public AbstractDroneFlightTasks flightTasks() {
        return flightTasks;
    }

    @Override
    public AbstractDroneGimbal gimbal() {
        return gimbal;
    }

    @Override
    public Property<Location> rcLocation() {
        return remoteController.getRcLocation();
    }

    @Override
    public Property<Location> lastKnownLocation() {
        return flightTasks.lastKnownLocation();
    }

    @Override
    public Property<RCFlightModeSwitchPosition> rcFlightModeSwitchPosition() {
        return remoteController.getRcFlightModeSwitchPosition();
    }

    @Override
    public BooleanProperty rcInFunctionMode() {
        return remoteController.getRcInFunctionMode();
    }

    @Override
    public Property<FlightMode> flightMode() {
        return flightTasks.flightMode();
    }

    @Override
    public Property<Integer> rcSignalStrengthPercent() {
        return airLink.rcSignalStrengthPercent();
    }

    @Override
    public ObservableValue<Location> lookAtLocation() {
        return lookAtLocation;
    }

//    @Override
//    public ObservableValue<Location> rtk3DLocation() {
//        return flightTasks.getRtk3DLocation();
//    }
//
//    @Override
//    public ObservableValue<Location> rtkBaseStationLocation() {
//        return flightTasks().getRtkBaseStation();
//    }

    @Override
    public void close() {

    }

    @Override
    public ObservableValue<Double> aboveGroundAltitude() {
        return aboveGroundAltitude;
    }

    @Override
    public ObservableValue<Double> aboveSeaAltitude() {
        return aboveSeaAltitude;
    }

    public AbstractDroneBattery getDroneBattery() {
        return battery;
    }

    public AbstractDroneAirLink getAirLink() {
        return airLink;
    }

    public AbstractDroneRemoteController getRemoteController() {
        return remoteController;
    }

    private Double calcAboveGroundLevel(
            Double ultrasonicHeight,
            Double takeOffDTM,
            Double currentLocationDTM,
            Double barometerHeightFromTakeOff
    ){
        if(ultrasonicHeight != null){
            return ultrasonicHeight;
        }

        Double aboveSeaAltitude = calcAboveSeaAltitude(takeOffDTM,barometerHeightFromTakeOff);

        if(aboveSeaAltitude == null || currentLocationDTM == null){
            return null;
        }

        return aboveSeaAltitude - currentLocationDTM;
    }

    private Double calcAboveSeaAltitude(Double takeOffDTM,
            Double barometerHeightFromTakeOff
    ){

        if(takeOffDTM == null || barometerHeightFromTakeOff == null){
            return null;
        }
        return takeOffDTM + barometerHeightFromTakeOff;
    }

    @Override
    public void setVirtualSticksPosition(SticksPosition position) {
        virtualSticksPosition.set(position);
    }

    public DroneMicroMovement getMicroMovement() {
        return microMovement;
    }

    public DtmProvider getDtmProvider() {
        return dtmProvider;
    }

    @Override
    public ObservableValue<SticksPosition> virtualSticksPosition() {
        return virtualSticksPosition;
    }
}
