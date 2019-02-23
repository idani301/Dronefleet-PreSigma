package eyesatop.controller.djinew.components;

import com.example.abstractcontroller.components.AbstractDroneFlightTasks;
import com.example.abstractcontroller.components.LocationFix;
import com.example.abstractcontroller.tasks.flight.FlyInCircleAbstract;
import com.example.abstractcontroller.tasks.flight.FlyToAbstract;
import com.example.abstractcontroller.tasks.flight.FlyToSafeAndFastAbstract;
import com.example.abstractcontroller.tasks.flight.FlyToUsingDTMAbstract;
import com.example.abstractcontroller.tasks.flight.FollowNavPlanAbstract;
import com.example.abstractcontroller.tasks.flight.HoverAbstract;
import com.example.abstractcontroller.tasks.flight.RotateHeadingAbstract;
import com.example.abstractcontroller.tasks.flight.TakeOffAbstract;

import java.util.concurrent.CountDownLatch;

import dji.common.error.DJIError;
import dji.common.flightcontroller.FlightControllerState;
import dji.common.flightcontroller.GPSSignalLevel;
import dji.common.flightcontroller.LocationCoordinate3D;
import dji.common.flightcontroller.ReceiverInfo;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.Compass;
import dji.sdk.flightcontroller.FlightController;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsSignalLevel;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.djinew.tasks.flight.DjiGoHome;
import eyesatop.controller.djinew.tasks.flight.DjiLandAtLandingPad;
import eyesatop.controller.djinew.tasks.flight.DjiLandInPlace;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.controller.tasks.flight.FollowNavPlan;
import eyesatop.controller.tasks.flight.Hover;
import eyesatop.controller.tasks.flight.LandAtLandingPad;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.util.Function;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.Velocities;
import eyesatop.util.model.Property;
import logs.LoggerTypes;

//import com.example.abstractcontroller.components.RtkFixes;

/**
 * Created by Idan on 09/09/2017.
 */

public class DroneFlightTasksDji extends AbstractDroneFlightTasks {

    private final ControllerDjiNew controller;
    private Compass djiCompass;
    private boolean everStartedCallbacks = false;

    private final Property<Location> gpsBarometerLocation = new Property<>();
    private final Property<Double> heading = new Property<>();
    private final Property<Velocities> velocities = new Property<>();

    public DroneFlightTasksDji(final ControllerDjiNew controller) {

        this.controller = controller;

        telemetry().bind(gpsBarometerLocation.transform(new Function<Location, Telemetry>() {
            @Override
            public Telemetry apply(Location input) {

//                MainLoggerJava.logger.write_message(LoggerTypes.DEBUG,"inside gps barometer transform, with input : " + (input == null ? "Null" : input.toString()));

                if(input == null){
                    return null;
                }

//                RtkFixes currentFixes = getRtkFixes().value();
//                if(currentFixes != null && currentFixes.isRelevant()){
//                    Location newLocation = input.getLocationFromAzAndDistance(currentFixes.getDistance(),currentFixes.getAz(),0);
//                    return new Telemetry(newLocation,velocities.value(),heading.value());
//                }
//                else{

//                LocationFix locationFix = getLocationFix().value();
//
//                Location finalDroneLocation;
//
//                if(locationFix != null){
//                    Location newLocation = input.getLocationFromAzAndDistance(locationFix.getDistance(),locationFix.getAz(),0);
//                    finalDroneLocation = newLocation;
////                    return new Telemetry(newLocation,velocities.value(),heading.value());
//                }
//                else {
//                    finalDroneLocation = input;
////                    return new Telemetry(input, velocities.value(), heading.value());
//                }

//                Double ultrasonic = getUltrasonicHeight().value();
//
//                if(ultrasonic != null){
//                    try {
//                        if(!(controller.getDtmProvider() instanceof DtmProviderWrapper)){
//                            throw new TerrainNotFoundException("Not provider wrapper");
//                        }
//                        DtmProviderWrapper wrapper = (DtmProviderWrapper) controller.getDtmProvider();
//
//                        double actualASL = wrapper.getSemiProvider().terrainAltitude(finalDroneLocation) + ultrasonic;
//                        double asl = wrapper.getSemiProvider().terrainAltitude(controller.droneHome().homeLocation().value()) + finalDroneLocation.getAltitude();
//                        barometerFix.set(actualASL - asl);
////                        finalDroneLocation = finalDroneLocation.altitude(finalDroneLocation.getAltitude() + barometerFix);
//                    } catch (TerrainNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }

//                Double barometerFixValue = barometerFix.value();
//                if(barometerFixValue != null){
//                    finalDroneLocation = finalDroneLocation.altitude(finalDroneLocation.getAltitude() + barometerFixValue);
//                }

                return new Telemetry(input,velocities.value(),heading.value());
//                }
            }
        }));


//        if(MainLogger.logger.is_log_exists(LoggerTypes.YOSSI)) {
//
//            controller.aboveGroundAltitude().observe(new Observer<Double>() {
//                @Override
//                public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
//
//                    DroneTask<FlightTaskType> currentFlightTask = current().value();
//
//                    Telemetry currentTelemetry = telemetry().value();
//                    Location currentLocation = Telemetry.telemetryToLocation(controller.telemetry().value());
//
//                    Velocities velocities = currentTelemetry == null ? null : currentTelemetry.velocities();
//
//                    if (currentLocation == null) {
//                        MainLogger.logger.write_message(LoggerTypes.YOSSI, "No Location, skipping");
//                        return;
//                    }
//
//                    Location homeLocation = controller.droneHome().homeLocation().value();
//
//                    if (homeLocation == null) {
//                        MainLogger.logger.write_message(LoggerTypes.YOSSI, "No Home Location, skipping");
//                        return;
//                    }
//
//                    Boolean isFlying = controller.flying().value();
//
//                    if(isFlying == null){
//                        MainLogger.logger.write_message(LoggerTypes.YOSSI, "Unknown Flying State, skipping");
//                        return;
//                    }
//
//                    if(!isFlying){
//                        MainLogger.logger.write_message(LoggerTypes.YOSSI, "Drone is not flying, skipping");
//                        return;
//                    }
//
//                    Location originalLocation = gpsBarometerLocation.value();
//                    if(originalLocation == null){
//                        MainLogger.logger.write_message(LoggerTypes.YOSSI, "No Original Location, skipping");
//                        return;
//                    }
//
//                    String taskString;
//
//                    if(currentFlightTask == null || currentFlightTask.status().value().isTaskDone()){
//                        taskString = "N/A";
//                    }
//                    else{
//                        taskString = currentFlightTask.taskType().getName();
//                        if(currentFlightTask.taskType() == FlightTaskType.FLY_TO_USING_DTM){
//                            taskString += ", Target AGL : " + ((FlyToUsingDTM)currentFlightTask).agl();
//                        }
//                    }
//
//                    LocationFix locationFix = getLocationFix().value();
//                    Double ultrasonicHeight = getUltrasonicHeight().value();
//                    String ultrasonicString = ultrasonicHeight == null ? "N/A" : ultrasonicHeight + "(m)";
//
//                    String velocityString = velocities == null ? "N/A" : velocities.getVelocity() + "(m/s)";
//
//                    Location gpsBarometer = gpsBarometerLocation.value();
//                    Double barometerFixValue = barometerFix.value();
//                    String barometerBeforeFixString = gpsBarometer == null ? "N/A" : gpsBarometer.getAltitude() + "(m)";
//
//                    String barometerFixString = barometerFixValue == null ? "N/A" : (barometerFixValue + "(m)");
//
//                    if(locationFix == null){
//
//                        try {
//                            MainLogger.logger.write_message(LoggerTypes.YOSSI, "Got new Telemetry : " +
//                                    MainLogger.TAB + "Lat                   : " + currentLocation.getLatitude() +
//                                    MainLogger.TAB + "Lon                   : " + currentLocation.getLongitude() +
//                                    MainLogger.TAB + "Home Lat              : " + homeLocation.getLatitude() +
//                                    MainLogger.TAB + "Home Lon              : " + homeLocation.getLongitude() +
//                                    MainLogger.TAB + "Barometer before Fix  : " + barometerBeforeFixString +
//                                    MainLogger.TAB + "Barometer after Fix   : " + currentLocation.getAltitude() +
//                                    MainLogger.TAB + "Barometer Fix         : " + barometerFixString +
//                                    MainLogger.TAB + "Dtm At Home           : " + controller.getDtmProvider().terrainAltitude(homeLocation) +
//                                    MainLogger.TAB + "Dtm At Drone Location : " + controller.getDtmProvider().terrainAltitude(currentLocation) +
//                                    MainLogger.TAB + "AGL                   : " + newValue +
//                                    MainLogger.TAB + "Ultrasonic Value      : " + ultrasonicString +
//                                    MainLogger.TAB + "Velocity              : " + velocityString +
//                                    MainLogger.TAB + "Active Task           : " + taskString
//                            );
//                        } catch (TerrainNotFoundException e) {
//                            MainLogger.logger.write_message(LoggerTypes.YOSSI, "Dtm Error, Skipping");
//                        }
//                    }
//                    else{
//                        try {
//                            MainLogger.logger.write_message(LoggerTypes.YOSSI, "Got new Telemetry : " +
//                                    MainLogger.TAB + "Original Lat          : " + originalLocation.getLatitude() +
//                                    MainLogger.TAB + "Original Lon          : " + originalLocation.getLongitude() +
//                                    MainLogger.TAB + "Lat                   : " + currentLocation.getLatitude() +
//                                    MainLogger.TAB + "Lon                   : " + currentLocation.getLongitude() +
//                                    MainLogger.TAB + "Location Fix          : " + locationFix.toString() +
//                                    MainLogger.TAB + "Home Lat              : " + homeLocation.getLatitude() +
//                                    MainLogger.TAB + "Home Lon              : " + homeLocation.getLongitude() +
//                                    MainLogger.TAB + "Barometer before Fix  : " + barometerBeforeFixString +
//                                    MainLogger.TAB + "Barometer after Fix   : " + currentLocation.getAltitude() +
//                                    MainLogger.TAB + "Barometer Fix         : " + barometerFixString +
//                                    MainLogger.TAB + "Dtm At Home           : " + controller.getDtmProvider().terrainAltitude(homeLocation) +
//                                    MainLogger.TAB + "Dtm At Drone Location : " + controller.getDtmProvider().terrainAltitude(currentLocation) +
//                                    MainLogger.TAB + "AGL                   : " + newValue +
//                                    MainLogger.TAB + "Ultrasonic Value      : " + ultrasonicString +
//                                    MainLogger.TAB + "Velocity              : " + velocityString +
//                                    MainLogger.TAB + "Active Task           : " + taskString
//                            );
//                        } catch (TerrainNotFoundException e) {
//                            MainLogger.logger.write_message(LoggerTypes.YOSSI, "Dtm Error, Skipping");
//                        }
//                    }
//                }
//            });
//        }
    }

    @Override
    protected RunnableDroneTask<FlightTaskType> stubToRunnable(StubDroneTask<FlightTaskType> stubDroneTask) throws DroneTaskException {

        switch (stubDroneTask.taskType()){

            case FLY_SAFE_TO:
                FlyToSafeAndFast flyToSafeAndFast = (FlyToSafeAndFast)stubDroneTask;
                return new FlyToSafeAndFastAbstract(controller,flyToSafeAndFast.targetLocation(),flyToSafeAndFast.altitudeInfo());
            case FLY_IN_CIRCLE:
                FlyInCircle flyInCircle = (FlyInCircle)stubDroneTask;
                return new FlyInCircleAbstract(
                        controller,
                        flyInCircle.center(),
                        flyInCircle.radius(),
                        flyInCircle.rotationType(),
                        flyInCircle.degreesToCover(),
                        flyInCircle.startingDegree(),
                        flyInCircle.altitudeInfo(),
                        flyInCircle.velocity());
            case TAKE_OFF:
                TakeOff takeOff = (TakeOff)stubDroneTask;
                return new TakeOffAbstract(controller,takeOff.altitude());
            case GOTO_POINT:
                FlyTo flyTo = (FlyTo)stubDroneTask;
                return new FlyToAbstract(controller,flyTo.location(), flyTo.altitudeInfo(), flyTo.az(), flyTo.maxVelocity(), flyTo.radiusReached());
            case GO_HOME:
                return new DjiGoHome(controller);
            case FLY_TO_USING_DTM:
                FlyToUsingDTM flyToUsingDTM = (FlyToUsingDTM)stubDroneTask;
                return new FlyToUsingDTMAbstract(controller,
                        flyToUsingDTM.location(),
                        flyToUsingDTM.az(),
                        flyToUsingDTM.agl(),
                        flyToUsingDTM.underGapInMeter(),
                        flyToUsingDTM.upperGapInMeter(),
                        flyToUsingDTM.maxVelocity(),
                        flyToUsingDTM.radiusReached());
            case LAND_IN_LANDING_PAD:
                LandAtLandingPad landAtLandingPad = (LandAtLandingPad)stubDroneTask;
                return new DjiLandAtLandingPad(controller,landAtLandingPad.landingPad());
            case ROTATE_HEADING:
                RotateHeading rotateHeading = (RotateHeading)stubDroneTask;
                return new RotateHeadingAbstract(controller,rotateHeading.angle());
            case LAND_IN_PLACE:
                return new DjiLandInPlace(controller);
//            case LAND_AT_LOCATION:
//                LandAtLocation landAtLocation = (LandAtLocation)stubDroneTask;
//                break;
//            case LAND_IN_PLACE:
//                break;
            case HOVER:
                Hover hoverTask = (Hover)stubDroneTask;
                return new HoverAbstract(hoverTask.hoverTime());
            case FOLLOW_NAV_PLAN:
                FollowNavPlan navPlan = (FollowNavPlan)stubDroneTask;
                return new FollowNavPlanAbstract(controller,navPlan.navPlanPoints());
            default:
                throw new DroneTaskException("Not implemented :" + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {

        FlightController djiController = controller.getHardwareManager().getDjiFlightController();
//        final RTK droneRTK = djiController == null ? null : djiController.getRTK();

//        if(djiController != null) {
//
//            if(droneRTK != null && droneRTK.isConnected()){
//
//                djiController.getRTK().setRtkEnabled(true, new CommonCallbacks.CompletionCallback() {
//                    @Override
//                    public void onResult(DJIError djiError) {
//                        MainLoggerJava.logger.write_message(LoggerTypes.RTK_FULL,"RTK Enable : " + djiError == null ? "Success" : djiError.getDescription());
//                    }
//                });
//            }
//        }

        if(everStartedCallbacks == true){

            MainLogger.logger.write_message(LoggerTypes.CALLBACKS,"Flight Controller : aborting start since already started");
            return;
        }

        everStartedCallbacks = true;
        djiCompass = controller.getHardwareManager().getDjiFlightController().getCompass();

        controller.getHardwareManager().getDjiFlightController().setStateCallback(new FlightControllerState.Callback() {
            @Override
            public void onUpdate(FlightControllerState flightControllerState) {

                DroneModel model = controller.model().value();

                if(model != null && model == DroneModel.MAVIC) {
                    confirmLandRequire().setIfNew(flightControllerState.isLandingConfirmationNeeded());
                }

//                MainLogger.logger.write_message(LoggerTypes.DEBUG,"Got new State callback data");

                LocationCoordinate3D locationCoordinate3D = flightControllerState.getAircraftLocation();

                // Setting Telemetry
                float barometerAltitude = locationCoordinate3D.getAltitude();

                if(flightControllerState.isUltrasonicBeingUsed() && flightControllerState.getUltrasonicHeightInMeters() <= 4 ){
                    controller.flightTasks().getUltrasonicHeight().set((double) flightControllerState.getUltrasonicHeightInMeters());
                }
                else{
                    controller.flightTasks().getUltrasonicHeight().set(null);
                }

//                MainLoggerJava.logger.write_message(LoggerTypes.ALTITUDE_CALCS,"Got Data From Dji : " +
//                MainLoggerJava.TAB + "Barometer             : " + barometerAltitude +
//                MainLoggerJava.TAB + "Ultrasonic Being Used : " + flightControllerState.isUltrasonicBeingUsed() +
//                MainLoggerJava.TAB + "Ultrasonic Height     : " + flightControllerState.getUltrasonicHeightInMeters());

                Location newLocation = new Location(locationCoordinate3D.getLatitude(),locationCoordinate3D.getLongitude(),barometerAltitude);

                velocities.set(new Velocities(flightControllerState.getVelocityX(),flightControllerState.getVelocityY(),flightControllerState.getVelocityZ()));
                heading.set((double) djiCompass.getHeading());

                Boolean isFullGimbal = controller.gimbal().fullGimbalSupported().value();

                if(isFullGimbal == null || !isFullGimbal){

                    try {
                        controller.gimbal().gimbalState().setIfNew(controller.gimbal().gimbalState().value().yaw(djiCompass.getHeading()));
                    }
                    catch (Exception e){
                        controller.gimbal().gimbalState().setIfNew(new GimbalState(0,0,djiCompass.getHeading()));
                    }
                }

                getPreheating().setIfNew(flightControllerState.isIMUPreheating());
                hasCompassError().setIfNew(djiCompass.hasError());

                motorsOn().setIfNew(flightControllerState.areMotorsOn());
                flying().setIfNew(flightControllerState.isFlying());

                GpsState newGpsState = new GpsState((int) flightControllerState.getSatelliteCount(),signalLevelFromDji(flightControllerState.getGPSSignalLevel()));

                gps().setIfNew(newGpsState);

                Location newHomeLocation;

                if(Double.isNaN(flightControllerState.getHomeLocation().getLatitude()) ||
                        Double.isNaN(flightControllerState.getHomeLocation().getLongitude())){
                    newHomeLocation = null;
                }
                else{
                    newHomeLocation = new Location(flightControllerState.getHomeLocation().getLatitude(),flightControllerState.getHomeLocation().getLongitude());
                }

                LocationFix locationFix = getLocationFix().value();

                if(locationFix == null) {
                    controller.droneHome().homeLocation().setIfNew(newHomeLocation);
                }
                else{
                    controller.droneHome().homeLocation().setIfNew(newHomeLocation.getLocationFromAzAndDistance(locationFix.getDistance(),locationFix.getAz()));
                }

                flightMode().setIfNew(flightModeFromDji(flightControllerState.getFlightMode()));

//                MainLogger.logger.write_message(LoggerTypes.DEBUG,"Setting gps barometer location with : " + newLocation.toString());
                gpsBarometerLocation.set(newLocation.isValid() ? newLocation : null);
            }
        });

//        if(droneRTK != null && droneRTK.isConnected()) {
//
//            controller.getHardwareManager().getDjiFlightController().getRTK().setStateCallback(new RTKState.Callback() {
//                @Override
//                public void onUpdate(RTKState rtkState) {
//
//                    if(rtkState == null){
//                        return;
//                    }
//
//                    LocationCoordinate2D baseStationDjiLocation = rtkState.getBaseStationLocation();
//                    Location baseStationNewLocation = null;
//                    if(baseStationDjiLocation != null){
//                        baseStationNewLocation = new Location(baseStationDjiLocation.getLatitude(),baseStationDjiLocation.getLongitude(),rtkState.getBaseStationAltitude());
//                    }
//
//                    LocationCoordinate2D droneDjiRTKLocation = rtkState.getMobileStationLocation();
//                    Location droneNewRTKLocation = null;
//
//                    if(droneDjiRTKLocation != null){
//                        droneNewRTKLocation = new Location(droneDjiRTKLocation.getLatitude(),droneDjiRTKLocation.getLongitude(),rtkState.getMobileStationAltitude());
//                    }
//
//                    Location currentGpsBarometer = gpsBarometerLocation.value();
//
//                    Location currentDroneGPSLocation = currentGpsBarometer;
//                    String distanceBetweenRTKAndGPS = "N/A";
//                    if(currentDroneGPSLocation != null && droneNewRTKLocation != null){
//                        distanceBetweenRTKAndGPS = "" + currentDroneGPSLocation.distance(droneNewRTKLocation);
//                    }
//
//                    Double baseStationAboveSeaLevel = null;
//                    try {
//                        baseStationAboveSeaLevel = controller.getDtmProvider().terrainAltitude(baseStationNewLocation);
//                    } catch (TerrainNotFoundException e) {
//                        e.printStackTrace();
//                    }
//
//                    MainLoggerJava.logger.write_message(LoggerTypes.RTK_YOSSI,"New RTK Data : " +
//                            MainLoggerJava.TAB + "Drone Lat : " + (droneNewRTKLocation == null ? "N/A" : droneNewRTKLocation.getLatitude()) +
//                            MainLoggerJava.TAB + "Drone Lon : " + (droneNewRTKLocation == null ? "N/A" : droneNewRTKLocation.getLongitude()) +
//                            MainLoggerJava.TAB + "Drone ASL : " + (droneNewRTKLocation == null ? "N/A" : droneNewRTKLocation.getAltitude()) +
//                            MainLoggerJava.TAB + "Station Lat : " + (baseStationNewLocation == null ? "N/A" : baseStationNewLocation.getLatitude()) +
//                            MainLoggerJava.TAB + "Station Lon : " + (baseStationNewLocation == null ? "N/A" : baseStationNewLocation.getLongitude()) +
//                            MainLoggerJava.TAB + "Station ASL : " + (baseStationNewLocation == null ? "N/A" : baseStationNewLocation.getAltitude())
//                    );
//
//                    MainLoggerJava.logger.write_message(LoggerTypes.RTK_BASIC, "Got RTK Data : " +
//                            MainLoggerJava.TAB + "DJI Base Station Above Sea Level                : " + rtkState.getBaseStationAltitude() +
//                            MainLoggerJava.TAB + "Internal Base Station Ground Above Sea Level    : " + baseStationAboveSeaLevel +
//
//                            MainLoggerJava.TAB + "RTK Drone Above Sea Level                       : " + rtkState.getMobileStationAltitude() +
//                            MainLoggerJava.TAB + "Internal Drone Above Sea Level                  : " + controller.aboveSeaAltitude().value() +
//
//                            MainLoggerJava.TAB + "Distance Between RTK and Gps Location           : " + distanceBetweenRTKAndGPS
//                    );
////
////                    MainLoggerJava.logger.write_message(LoggerTypes.RTK_BUZAGLO,"Drone RTK Location :  " +
////                            MainLoggerJava.TAB + "Latitude        : " + (droneNewRTKLocation == null ? "N/A" : droneNewRTKLocation.getLatitude()) +
////                            MainLoggerJava.TAB + "Longitude       : " + (droneNewRTKLocation == null ? "N/A" : droneNewRTKLocation.getLongitude()) +
////                            MainLoggerJava.TAB + "Above Sea Level : " + (rtkState == null ? "N/A" : rtkState.getMobileStationAltitude())
////                    );
//
//                    MainLoggerJava.logger.write_message(LoggerTypes.RTK_FULL, "Got RTK Data : " +
//                            MainLoggerJava.TAB + "" + rtkState.getBaseStationAltitude() +
//                            MainLoggerJava.TAB + "" + rtkState.getBaseStationLocation() +
//                            MainLoggerJava.TAB + "" + rtkState.getHeading() +
//                            MainLoggerJava.TAB + "" + rtkState.getMobileStationAltitude() +
//                            MainLoggerJava.TAB + "" + rtkState.getMobileStationLocation() +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getBaseStationReceiverBeiDouInfo()) +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getBaseStationReceiverGLONASSInfo()) +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getBaseStationReceiverGPSInfo()) +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getMobileStationReceiver1BeiDouInfo()) +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getMobileStationReceiver2BeiDouInfo()) +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getMobileStationReceiver1GLONASSInfo()) +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getMobileStationReceiver2GLONASSInfo()) +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getMobileStationReceiver1GPSInfo()) +
//                            MainLoggerJava.TAB + "" + receiverInfoString(rtkState.getMobileStationReceiver2GPSInfo())
//                    );
//
//                    RtkFixes newFixes = calcRTKFixes(currentDroneGPSLocation,droneNewRTKLocation,baseStationNewLocation,controller.droneHome().homeLocation().value());
//                    MainLoggerJava.logger.write_message(LoggerTypes.RTK_FIXES,"Setting the RTK Fixes : " + (newFixes == null ? "N/A" :  newFixes.toString()));
//                    getRtkFixes().set(newFixes);
//
//                    try {
//                        printBuzagloLog(droneNewRTKLocation.getLatitude(), droneNewRTKLocation.getLongitude(), rtkState.getMobileStationAltitude());
//                    }
//                    catch (Exception e){
//                        MainLoggerJava.logger.writeError(LoggerTypes.ERROR,e);
//                    }
//                }
//            });
//        }
//        else{
//            if(droneRTK == null){
//                MainLoggerJava.logger.write_message(LoggerTypes.RTK_BASIC,"RTK IS NULL");
//            }
//            else if(droneRTK != null && !droneRTK.isConnected()){
//                MainLoggerJava.logger.write_message(LoggerTypes.RTK_BASIC,"RTK is not connected");
//            }
//        }

        MainLogger.logger.write_message(LoggerTypes.PRODUCT_CHANGES,"onComponentAvailable flight controller done");
    }

//    private RtkFixes calcRTKFixes(Location currentGPSLocation,
//                              Location currentRTKLocation,
//                              Location baseStationRTKLocation,
//                              Location homeLocation){
//
//        if(currentGPSLocation == null || currentRTKLocation == null || baseStationRTKLocation == null || homeLocation == null){
//            return null;
//        }
//
//        try {
//
//            Double distanceFromGPSToRTK = currentGPSLocation.distance(currentRTKLocation);
//            Double azFromGPSToRTK = currentGPSLocation.az(currentRTKLocation);
//
////            Location fixedLocation = currentGPSLocation.getLocationFromAzAndDistance(distanceFromGPSToRTK,azFromGPSToRTK);
//
//            Double currentAboveSeaLevel = currentGPSLocation.getAltitude() + controller.getDtmProvider().terrainAltitude(homeLocation);
//
//            Double shiftBetweenYossiAndRTK = baseStationRTKLocation.getAltitude() - (1 + controller.getDtmProvider().terrainAltitude(baseStationRTKLocation));
//            Double barometerFix = currentRTKLocation.getAltitude() - (currentAboveSeaLevel + shiftBetweenYossiAndRTK);
////            Double barometerFix = 0D;
//            return new RtkFixes(distanceFromGPSToRTK,azFromGPSToRTK,barometerFix,System.currentTimeMillis());
//        } catch (TerrainNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private String receiverInfoString(ReceiverInfo info){
        if(info == null){
            return "N/A";
        }
        return "Sat Count : " + info.getSatelliteCount() + ", isConstellationSupported : " + info.isConstellationSupported();
    }

    @Override
    public void onComponentConnected() {

    }

    public GpsSignalLevel signalLevelFromDji(GPSSignalLevel djigpsSignalStatus){
        switch (djigpsSignalStatus){

            case LEVEL_0:
                return GpsSignalLevel.LEVEL0;
            case LEVEL_1:
                return GpsSignalLevel.LEVEL1;
            case LEVEL_2:
                return GpsSignalLevel.LEVEL2;
            case LEVEL_3:
                return GpsSignalLevel.LEVEL3;
            case LEVEL_4:
                return GpsSignalLevel.LEVEL4;
            case LEVEL_5:
                return GpsSignalLevel.LEVEL5;
            case NONE:
                return GpsSignalLevel.NONE;
        }
        return GpsSignalLevel.UNKNOWN;
    }

    public FlightMode flightModeFromDji(dji.common.flightcontroller.FlightMode djiFlightControllerFlightMode){

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_MODES,"Dji Flight Mode : " + djiFlightControllerFlightMode);

        switch (djiFlightControllerFlightMode){
            case ASSISTED_TAKEOFF:
                return FlightMode.AUTO_TAKE_OFF;
            case AUTO_TAKEOFF:
                return FlightMode.AUTO_TAKE_OFF;
            case CONFIRM_LANDING:
                return FlightMode.AUTO_GO_HOME;
            case AUTO_LANDING:
                return FlightMode.AUTO_GO_HOME;
            case ATTI_LANDING:
                return FlightMode.AUTO_GO_HOME;
            case GO_HOME:
                return FlightMode.AUTO_GO_HOME;
            case ATTI:
                return FlightMode.APP_CONTROL;
            case CLICK_GO:
                return FlightMode.AUTO_GO_HOME;
            case JOYSTICK:
                return FlightMode.APP_CONTROL;
            case GPS_ATTI:
                return FlightMode.APP_CONTROL;
        }
        return FlightMode.EXTERNAL;
    }

    @Override
    public void sendSpeedsCommand(double pitch, double roll, double yaw, double vertical) {

        dji.sdk.flightcontroller.FlightController flightController = controller.getHardwareManager().getDjiFlightController();
        if(flightController != null) {
            flightController.sendVirtualStickFlightControlData(new FlightControlData((float) pitch, (float) roll, (float) yaw, (float) vertical),null);
        }
    }

//    private void printBuzagloLog(double rtkLat,double rtkLon,double rtkASL){
//
//        DroneTask<FlightTaskType> currentFlightTask = controller.flightTasks().current().value();
//
//        String currentFlightTaskString = "N/A";
//        if(currentFlightTask != null){
//            currentFlightTaskString = currentFlightTask.taskType() + " , ";
//            switch (currentFlightTask.taskType()){
//
//                case TAKE_OFF:
//                    TakeOff takeOff = (TakeOff)currentFlightTask;
//                    currentFlightTaskString += "Altitude : " + takeOff.altitude();
//                    break;
//                case FLY_IN_CIRCLE:
//                    break;
//                case GOTO_POINT:
//                    break;
//                case FLY_TO_USING_DTM:
//                    FlyToUsingDTM flyToUsingDTM = (FlyToUsingDTM)currentFlightTask;
//                    currentFlightTaskString += "AGL : " + flyToUsingDTM.agl();
//                    break;
//                case GO_HOME:
//                    break;
//                case LAND_IN_LANDING_PAD:
//                    break;
//                case LAND_AT_LOCATION:
//                    break;
//                case ROTATE_HEADING:
//                    break;
//                case FLY_SAFE_TO:
//                    FlyToSafeAndFast flyToSafeAndFast = (FlyToSafeAndFast)currentFlightTask;
//                    currentFlightTaskString += "Altitude Info : " + flyToSafeAndFast.altitudeInfo().toString();
//                    break;
//                case HOVER:
//                    break;
//                case LAND_IN_PLACE:
//                    break;
//            }
//        }
//
//        Double currentDTMRaiseValue = controller.getDtmProvider().dtmRaiseValue().value();
//
//        Location gpsLocation = gpsBarometerLocation.value();
//        RtkFixes currentRTKFix = getRtkFixes().value();
//        Location currentCalculatedLocation = Telemetry.telemetryToLocation(telemetry().value());
//        Double terrainUnderCalculatedLocation = null;
//        Double takeOffDTM = controller.droneHome().takeOffDTM().value();
//        Location takeOffLocation = controller.droneHome().homeLocation().value();
//        Double asl = controller.aboveSeaAltitude().value();
//        Double agl = controller.aboveGroundAltitude().value();
//
//        try {
//            terrainUnderCalculatedLocation = controller.getDtmProvider().terrainAltitude(currentCalculatedLocation);
//        } catch (TerrainNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        MainLoggerJava.logger.write_message(LoggerTypes.RTK_BUZAGLO,"New Data : " +
//                MainLoggerJava.TAB + "Gps Lat : " + (gpsLocation == null ? "N/A" : gpsLocation.getLatitude()) +
//                MainLoggerJava.TAB + "Gps Lon : " + (gpsLocation == null ? "N/A" : gpsLocation.getLongitude()) +
//                MainLoggerJava.TAB + "Rtk Lat : " + rtkLat +
//                MainLoggerJava.TAB + "Rtk Lon : " + rtkLon +
//                MainLoggerJava.TAB + "Rtk Distance Fix : " + (currentRTKFix == null ? "N/A" : currentRTKFix.getDistance()) +
//                MainLoggerJava.TAB + "Rtk Az Fix : " + (currentRTKFix == null ? "N/A" : currentRTKFix.getAz()) +
//                MainLoggerJava.TAB + "Rtk Fix Valid : " + (currentRTKFix == null ? "N/A" : currentRTKFix.isRelevant()) +
//                MainLoggerJava.TAB + "Rtk Fix Valid : " + (currentRTKFix == null ? "N/A" : currentRTKFix.isRelevant()) +
//                MainLoggerJava.TAB + "Calculated Lat : " + (currentCalculatedLocation == null ? "N/A" : currentCalculatedLocation.getLatitude()) +
//                MainLoggerJava.TAB + "Calculated Lon : " + (currentCalculatedLocation == null ? "N/A" : currentCalculatedLocation.getLongitude()) +
//                MainLoggerJava.TAB + "Terrain Under Calculated Location : " + (terrainUnderCalculatedLocation == null ? "N/A" : terrainUnderCalculatedLocation) +
//                MainLoggerJava.TAB + "Barometer : " + (gpsLocation == null ? "N/A" : gpsLocation.getAltitude()) +
//                MainLoggerJava.TAB + "Take off Location Terrain : " + (takeOffDTM == null ? "N/A" : takeOffDTM) +
//                MainLoggerJava.TAB + "Take off Location : " + (takeOffLocation == null ? "N/A" : takeOffLocation.toString()) +
//                MainLoggerJava.TAB + "ASL(Should be like Take off terrain + barometer) : " + (asl == null ? "N/A" : asl) +
//                MainLoggerJava.TAB + "AGL(Should be like Take off terrain + barometer - terrain under calc location) : " + (agl == null ? "N/A" : agl) +
//                MainLoggerJava.TAB + "DTM Delta : " + (currentDTMRaiseValue == null ? "N/A" : currentDTMRaiseValue) +
//                MainLoggerJava.TAB + "Current Task Info : " + currentFlightTaskString
//        );
//    }

    @Override
    public void internalTakeOff() throws DroneTaskException {
        dji.sdk.flightcontroller.FlightController flightController = controller.getHardwareManager().getDjiFlightController();

        if(flightController == null){
            throw new DroneTaskException("No Flight Controller");
        }

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final Property<DJIError> djiErrorProperty = new Property<>();
        flightController.startTakeoff(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                djiErrorProperty.set(djiError);
                taskLatch.countDown();
            }
        });

        try {
            taskLatch.await();
        } catch (InterruptedException e) {
            throw new DroneTaskException("Cancelled");
        }

        if(djiErrorProperty.value() != null){
            throw new DroneTaskException("Dji Take-off internal Error : " + djiErrorProperty.value().getDescription());
        }
    }

    @Override
    public void confirmLand() throws DroneTaskException {
        try {
            controller.getHardwareManager().getDjiFlightController().confirmLanding(null);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public Property<Double> getHeading() {
        return heading;
    }
}
