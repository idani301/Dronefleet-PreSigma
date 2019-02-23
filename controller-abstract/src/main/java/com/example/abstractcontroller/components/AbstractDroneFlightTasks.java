package com.example.abstractcontroller.components;

import com.example.abstractcontroller.taskmanager.FlightTaskManager;

import java.util.List;

import eyesatop.controller.DroneFlightTasks;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.controller.tasks.flight.FollowNavPlan;
import eyesatop.controller.tasks.flight.GoHome;
import eyesatop.controller.tasks.flight.Hover;
import eyesatop.controller.tasks.flight.LandAtLandingPad;
import eyesatop.controller.tasks.flight.LandAtLocation;
import eyesatop.controller.tasks.flight.LandInPlace;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.landingpad.LandingPad;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 28/08/2017.
 */

public abstract class AbstractDroneFlightTasks extends GeneralDroneComponent<FlightTaskType,FlightTaskBlockerType> implements DroneFlightTasks {

    private final Property<Telemetry> telemetry = new Property<>();
    private final Property<FlightMode> flightMode = new Property<>();
    private final BooleanProperty flying = new BooleanProperty();
    private final BooleanProperty motorsOn = new BooleanProperty();
    private final Property<Location> lastKnownLocation = new Property<>();
    private final Property<GpsState> gps = new Property<>();
    private final BooleanProperty hasCompassError = new BooleanProperty();
    private final Property<Double> ultrasonicHeight = new Property<>();
    private final BooleanProperty isConfirmLandRequired = new BooleanProperty(false);
//    private final Property<RtkFixes> rtkFixes = new Property<>();

    private final Property<LocationFix> locationFix = new Property<>();

//    private final Property<Location> rtk3DLocation = new Property<>();
//    private final Property<Location> rtkBaseStation = new Property<>();

    private final BooleanProperty sticksEnabled = new BooleanProperty();

    private final BooleanProperty preheating = new BooleanProperty();

    public BooleanProperty getSticksEnabled() {
        return sticksEnabled;
    }

    public AbstractDroneFlightTasks(){
        super(new FlightTaskManager());
    }

    public Property<GpsState> gps(){
        return gps;
    }

    public BooleanProperty hasCompassError(){
        return hasCompassError;
    }

    public Property<Location> lastKnownLocation(){
        return lastKnownLocation;
    }

    public BooleanProperty flying(){
        return flying;
    }

    public BooleanProperty motorsOn(){
        return motorsOn;
    }

    public Property<FlightMode> flightMode(){
        return flightMode;
    }

    public Property<Telemetry> telemetry(){
        return telemetry;
    }

//    public Property<RtkFixes> getRtkFixes() {
//        return rtkFixes;
//    }

    public Property<LocationFix> getLocationFix() {
        return locationFix;
    }

    @Override
    public ObservableValue<DroneTask<FlightTaskType>> current() {
        return taskManager.currentTask();
    }

    @Override
    public ObservableList<FlightTaskBlockerType> tasksBlockers() {
        return taskManager.getTasksBlockers();
    }

    @Override
    public TakeOff takeOff(double altitude) throws DroneTaskException {

        TakeOff.TakeOffStub stubTask = new TakeOff.TakeOffStub(altitude);
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public LandAtLandingPad land(final LandingPad landingPad) throws DroneTaskException {

        LandAtLandingPad.LandAtLandingPadStub stubTask = new LandAtLandingPad.LandAtLandingPadStub() {
            @Override
            public LandingPad landingPad() {
                return landingPad;
            }
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public LandAtLocation land(final Location location) throws DroneTaskException {

        LandAtLocation.LandAtLocationStub stubTask = new LandAtLocation.LandAtLocationStub() {
            @Override
            public Location location() {
                return location;
            }
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public LandInPlace land() throws DroneTaskException {

        LandInPlace.LandInPlaceStub stubTask = new LandInPlace.LandInPlaceStub() {
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public RotateHeading rotateHeading(double angle) throws DroneTaskException {

        RotateHeading.RotateHeadnigStub task = new RotateHeading.RotateHeadnigStub(angle);
        startStubTask(task,false);

        return task;
    }

    @Override
    public GoHome goHome() throws DroneTaskException {

        GoHome.GoHomeStub stubTask = new GoHome.GoHomeStub() {
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public FlyTo flyTo(final Location location, AltitudeInfo altitudeInfo,Double az,Double maxVelocity,Double radiusReached) throws DroneTaskException {

        FlyTo.FlyToStub stubTask = new FlyTo.FlyToStub(location, altitudeInfo, az, maxVelocity, radiusReached);

        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public FlyInCircle flyInCircle(final Location center,
                                   final double radius,
                                   final RotationType rotationType,
                                   final double degreesToCover,
                                   final double startingDegree,
                                   final AltitudeInfo altitudeInfo,
                                   final double velocity) throws DroneTaskException {
        FlyInCircle.FlyInCircleStub stubTask = new FlyInCircle.FlyInCircleStub(center, radius, rotationType, degreesToCover, startingDegree, altitudeInfo,velocity);

        startStubTask(stubTask,false);
        return stubTask;
    }

    public Property<Double> getUltrasonicHeight() {
        return ultrasonicHeight;
    }

    public BooleanProperty getPreheating() {
        return preheating;
    }

//    public Property<Location> getRtk3DLocation() {
//        return rtk3DLocation;
//    }

//    public Property<Location> getRtkBaseStation() {
//        return rtkBaseStation;
//    }

    @Override
    public void clearData() {

        Telemetry currentTelemetry = telemetry.value();
        Location currentLocation = currentTelemetry == null ? null : currentTelemetry.location();
        if(currentLocation != null){
            lastKnownLocation.set(null);
        }

        telemetry.set(null);
        flightMode.set(null);
        motorsOn.set(null);
        flying.set(null);
        gps.set(null);
        hasCompassError.set(null);
        ultrasonicHeight.set(null);
        locationFix.set(null);
        isConfirmLandRequired.set(null);
//        rtk3DLocation.set(null);
//        rtkBaseStation.set(null);
    }

    @Override
    public Hover hover(Integer hoverTime) throws DroneTaskException {

        Hover.HoverStub hoverStub = new Hover.HoverStub(hoverTime);
        startStubTask(hoverStub,false);
        return hoverStub;
    }

    @Override
    public FollowNavPlan followNavPlan(List<NavPlanPoint> navPlanPoints) throws DroneTaskException {

        FollowNavPlan.FollowNavPlanStub navPlanStub = new FollowNavPlan.FollowNavPlanStub(navPlanPoints);
        startStubTask(navPlanStub,false);
        return navPlanStub;
    }

    @Override
    public FlyToUsingDTM flyToUsingDTM(Location location,Double az,double agl, double underGrapPercent, double upperGapPercent, Double maxVelocity,Double radiusReached) throws DroneTaskException {

        FlyToUsingDTM.FlyToUsingDTMStub stubTask = new FlyToUsingDTM.FlyToUsingDTMStub(location, az, agl, underGrapPercent, upperGapPercent,maxVelocity, radiusReached);
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public FlyToSafeAndFast flySafeAndFastTo(Location targetLocation, AltitudeInfo altitudeInfo, Double minDistanceFromGround) throws DroneTaskException {

        FlyToSafeAndFast.FlySafeAndFastToStub stubTask = new FlyToSafeAndFast.FlySafeAndFastToStub(targetLocation,altitudeInfo);
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public BooleanProperty confirmLandRequire() {
        return isConfirmLandRequired;
    }

    public abstract void sendSpeedsCommand(double pitch, double roll, double yaw, double vertical);
    public abstract void internalTakeOff() throws DroneTaskException;
}
