package com.example.abstractcontroller;

//import android.util.Log;

import com.example.abstractcontroller.tasks.flight.FlyInCircleAbstract;
import com.example.abstractcontroller.tasks.flight.FlyToSafeAndFastAbstract;
import com.example.abstractcontroller.tasks.flight.FollowNavPlanAbstract;
import com.example.abstractcontroller.tasks.flight.TakeOffAbstract;

import java.util.ArrayList;

import eyesatop.IdansFunctions.DroneMovement;
import eyesatop.controller.GimbalRequest;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.beans.SticksPosition;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.math.DtmFlightFunction;
import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.LinearEquations;
import eyesatop.math.MathException;
import eyesatop.math.Polynom;
import eyesatop.util.Removable;
//import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.dtmflight.DTMLine;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.ObstacleObject;
import eyesatop.util.geo.ObstacleProvider;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.Velocities;
import eyesatop.util.geo.WorldSpeeds;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableList;

import static eyesatop.util.geo.Location.degreeBetween0To360;

/**
 * Created by Idan on 16/10/2017.
 */

public class DroneMicroMovement {

    public static final float MAX_ROLL_PITCH_VELOCITY = 15.0F;
    public static final float MAX_YAW_VELOCITY        = 100.0F;
    public static final float MAX_HEIGHT_VELOCITY     = 4.0F;

    private Removable gimbalTaskObserver = Removable.STUB;
    private Removable flightTaskObserver = Removable.STUB;


    private ObservableBoolean readyToGiveInstructions;

    private final AbstractDroneController controller;
    private final ObstacleProvider obstacleProvider;

    private float maxRollPitchVelocity = MAX_ROLL_PITCH_VELOCITY;
    private float maxVerticalVelocity = MAX_HEIGHT_VELOCITY;

    public DroneMicroMovement(AbstractDroneController controller, ObstacleProvider obstacleProvider) {
        this.controller = controller;
        this.obstacleProvider = obstacleProvider;

        readyToGiveInstructions = controller.motorsOn()
                .and(controller.flying())
                .and(controller.rcInFunctionMode())
                .and(controller.connectivity().equalsTo(DroneConnectivity.DRONE_CONNECTED))
                .and(controller.flightMode().equalsTo(FlightMode.APP_CONTROL)
                .and(controller.flightTasks().getSticksEnabled()));

//        startObservers();
    }

    public void calcNextStep(
            SticksPosition sticksPosition,
            DroneTask<FlightTaskType> currentFlightTask,
            DroneTask<GimbalTaskType> currentGimbalTask,
            Telemetry currentTelemetry,
            Double aboveGroundLevel,
            Double aboveSeaLevel){

//        HorizontalFlightData horizontalFlightData = calcHorizontalFlightData(currentFlightTask,currentTelemetry);
//        double verticalSpeed = calcVerticalSpeed(currentFlightTask,currentTelemetry);

        if(currentTelemetry == null){
            if(currentGimbalTask != null && currentGimbalTask.taskType() == GimbalTaskType.ROTATE_GIMBAL && controller.gimbal.fullGimbalSupported().value()){
                GimbalData gimbalData = calcGimbalData(currentGimbalTask,currentTelemetry);
                if(gimbalData.getGimbalRequest() != null) {
                    try {
                        controller.gimbal().internalGimbalRotation(gimbalData.getGimbalRequest());
                    } catch (DroneTaskException e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }

        FlightSpeedsData flightSpeedsData = calcFlightSpeedsData(currentFlightTask,currentTelemetry,aboveGroundLevel,aboveSeaLevel);
        GimbalData gimbalData = calcGimbalData(currentGimbalTask,currentTelemetry);

        boolean hasFlightTask = currentFlightTask == null ? false : !currentFlightTask.status().value().isTaskDone();
        boolean hasGimbalTask = currentGimbalTask == null ? false : !currentGimbalTask.status().value().isTaskDone();

//        WorldSpeeds worldSpeeds = obstacleAvoidance(currentTelemetry,obstacleProvider,flightSpeedsData);

//        MainLogger.logger.write_message(LoggerTypes.MICRO_MOVE,"Current Flight speed : " +
//                flightSpeedsData.getHorizontalFlightData().getPitch() + "," +
//                flightSpeedsData.getHorizontalFlightData().getRoll() + "," +
//                flightSpeedsData.getYawSpeed() + "," +
//                flightSpeedsData.getVerticalSpeed() + " , " + "Gimbal Yaw Speed : " + gimbalData.yawSpeed +
//                MainLogger.TAB + "Current Flight Task: " + (currentFlightTask == null ? "None" : currentFlightTask.taskType().getName()));

        double finalYawSpeed = 0;

        if(hasGimbalTask){
            finalYawSpeed = gimbalData.yawSpeed;
        }
        else{
            finalYawSpeed = flightSpeedsData.getYawSpeed() == null ? 0 : flightSpeedsData.getYawSpeed();
        }

        flightSpeedsData = new FlightSpeedsData(flightSpeedsData.getHorizontalFlightData(),flightSpeedsData.isHorizontalEnabled,flightSpeedsData.getVerticalSpeed(),flightSpeedsData.isVerticalSpeedEnabled,finalYawSpeed);

        if(sticksPosition != null && sticksPosition.isRelevant()) {

            double maxRollPitchPossible = Math.min(maxRollPitchVelocity/1.5D,currentTelemetry.velocities().getVelocity() + 2);

            if (!hasFlightTask) {

                double heading = currentTelemetry.heading();
                double headingInRad = heading * Math.PI / 180D;

                double north = 0;
                double east = 0;

                double forwardVelocity = fromPercentToVelocity(sticksPosition.getForwardStick().getValueInPercent(), maxRollPitchPossible);
                north += forwardVelocity*Math.cos(headingInRad);
                east += forwardVelocity * Math.sin(headingInRad);

                double rightVelocity = fromPercentToVelocity(sticksPosition.getRightStick().getValueInPercent(), maxRollPitchPossible);
                north += -rightVelocity * Math.sin(headingInRad);
                east += rightVelocity * Math.cos(headingInRad);

                double verticalSpeed = fromPercentToVelocity(sticksPosition.getVerticalStick().getValueInPercent(), maxVerticalVelocity / 1.5D);
                flightSpeedsData = new FlightSpeedsData(new HorizontalFlightData(north, east), true, verticalSpeed, true, flightSpeedsData.getYawSpeed());
            }

            if (!hasGimbalTask && (currentFlightTask == null || currentFlightTask.taskType() != FlightTaskType.ROTATE_HEADING)) {
                double yawVelocity = fromPercentToVelocity(sticksPosition.getYawStick().getValueInPercent(), MAX_YAW_VELOCITY / 3D);
                flightSpeedsData = new FlightSpeedsData(flightSpeedsData.getHorizontalFlightData(), flightSpeedsData.isHorizontalEnabled(), flightSpeedsData.getVerticalSpeed(), flightSpeedsData.isVerticalSpeedEnabled, yawVelocity);
            }
        }

        if(sticksPosition != null && sticksPosition.getVerticalStick() != null && Math.abs(sticksPosition.getVerticalStick().getValueInPercent()) >= 50 && hasFlightTask){

            try {
                currentFlightTask.cancel();
            }
            catch (Exception e){
            }

            double maxRollPitchPossible = Math.min(maxRollPitchVelocity/1.5D,currentTelemetry.velocities().getVelocity() + 2);
            double heading = currentTelemetry.heading();
            double headingInRad = heading * Math.PI / 180D;

            double north = 0;
            double east = 0;

            double forwardVelocity = fromPercentToVelocity(sticksPosition.getForwardStick().getValueInPercent(), maxRollPitchPossible);
            north += forwardVelocity*Math.cos(headingInRad);
            east += forwardVelocity * Math.sin(headingInRad);

            double rightVelocity = fromPercentToVelocity(sticksPosition.getRightStick().getValueInPercent(), maxRollPitchPossible);
            north += -rightVelocity * Math.sin(headingInRad);
            east += rightVelocity * Math.cos(headingInRad);

            double verticalSpeed = fromPercentToVelocity(sticksPosition.getVerticalStick().getValueInPercent(), maxVerticalVelocity / 1.5D);
            flightSpeedsData = new FlightSpeedsData(new HorizontalFlightData(north, east), true, verticalSpeed, true, flightSpeedsData.getYawSpeed());
        }

        sendSpeedsMessage(flightSpeedsData.getHorizontalFlightData().getPitch(),flightSpeedsData.getHorizontalFlightData().getRoll(),
                flightSpeedsData.getYawSpeed(),flightSpeedsData.getVerticalSpeed());
//        sendSpeedsMessage(worldSpeeds.getPitch(),worldSpeeds.getRoll(),
//                gimbalData.getYawSpeed(),worldSpeeds.getVertical());

        if(gimbalData.getGimbalRequest() != null) {
            try {
                controller.gimbal().internalGimbalRotation(gimbalData.getGimbalRequest());
            } catch (DroneTaskException e) {
                e.printStackTrace();
            }
        }
    }

    private double fromPercentToVelocity(double percent,double maxVelocity){
        return maxVelocity * percent/100D;
    }

    /*
        roll == north
        pitch == east
    */
    public WorldSpeeds obstacleAvoidance(Telemetry currentTelemetry, ObstacleProvider obstacleProvider, FlightSpeedsData flightSpeedsData) {

        Point2D velocity = Point2D.GeographicPoint(flightSpeedsData.getHorizontalFlightData().getRoll(),flightSpeedsData.getHorizontalFlightData().getPitch());
        WorldSpeeds worldSpeeds = new WorldSpeeds(velocity.getX(),velocity.getY(),flightSpeedsData.getVerticalSpeed());

        if (obstacleProvider != null && currentTelemetry != null && obstacleProvider.obstacleObjects() != null) {
            ObservableList<ObstacleObject> obstacleObjectObservableList = obstacleProvider.obstacleObjects();
            for (int i = 0; i < obstacleObjectObservableList.size(); i++) {

                if (!controller.uuid().equals(obstacleObjectObservableList.get(i).getObstacleUUID()) ) {
                    Point3D velocityFromObstacle = getVelocityFromObstacle(currentTelemetry, obstacleObjectObservableList.get(i),maxRollPitchVelocity);
                    velocity = velocity.add(velocityFromObstacle.getHorizontalVector());
                }
            }
            double size = Math.min(velocity.getRadius(),maxRollPitchVelocity);
            if (size != 0) {
                velocity = velocity.normal().multiple(size);
            }
            return new WorldSpeeds(velocity.getNorth(),velocity.getEast(),flightSpeedsData.getVerticalSpeed());
        }
        return worldSpeeds;
    }

    public static Point3D getVelocityFromObstacle(Telemetry my, ObstacleObject obstacleObject,float maxRollPitchVelocity) {
        Point3D force = obstacleObject.getInfluence(my);
        return force.multipal(maxRollPitchVelocity*2);
    }

    private GimbalData calcGimbalData(DroneTask<GimbalTaskType> currentGimbalTask,
                                      Telemetry currentTelemetry){

        DegreeData gimbalPitch = calcGimbalPitch(currentGimbalTask,currentTelemetry);
        YawDegreeData gimbalYaw = calcGimbalYaw(currentGimbalTask,currentTelemetry);

        GimbalState gimbalState = new GimbalState(0,gimbalPitch.getDegree(),gimbalYaw.getDegree());
        GimbalRequest gimbalRequest = new GimbalRequest(
                gimbalState,
                gimbalPitch.isEnabaled(),
                false,
                controller.gimbal.fullGimbalSupported().value() && gimbalYaw.isEnabaled());

        double yawVelocity = 0;

        if(controller.gimbal().fullGimbalSupported().value()){

            if(currentGimbalTask != null && currentGimbalTask.taskType() == GimbalTaskType.EXPLORE) {
                yawVelocity = 15D;
            }

            return new GimbalData(yawVelocity,gimbalRequest);
        }

        if(gimbalRequest.isPitchEnable() && gimbalRequest.getGimbalState().getPitch() > 0){
            gimbalRequest = new GimbalRequest(gimbalRequest.getGimbalState().pitch(0),true,gimbalRequest.isRollEnable(),gimbalRequest.isYawEnable());
        }

        if(currentGimbalTask != null && currentGimbalTask.taskType() == GimbalTaskType.EXPLORE){
            yawVelocity = 15D;
        }

        if(gimbalYaw.isEnabaled()){

            double currentHeading = degreeBetween0To360(currentTelemetry.heading());
            double targetHeading = gimbalYaw.getDegree();
            double angularDistance = targetHeading - currentHeading;
            yawVelocity = calcAngularVelocity(angularDistance);

            Location locationToLookAt = gimbalYaw.getLocationOfInterest();
            if(locationToLookAt != null){
                double distanceFromLookAtLocation = locationToLookAt.distance(currentTelemetry.location());
                double azFromLookAtLocation = currentTelemetry.location().az(locationToLookAt);
                double radialCurrentV =
                        Math.abs(currentTelemetry.velocities().getY() * Math.cos(Math.toRadians(azFromLookAtLocation))) +
                                Math.abs(currentTelemetry.velocities().getX() * Math.sin(Math.toRadians(azFromLookAtLocation)));
                double deltaTime = 0.4;
                double velocityAdd = (radialCurrentV/distanceFromLookAtLocation + (1/24)*Math.pow(radialCurrentV/distanceFromLookAtLocation,3)*Math.pow(deltaTime,2))*180/Math.PI;
                yawVelocity+= velocityAdd;
            }

            if(yawVelocity > 60.0F){
                yawVelocity = 60.0F;
            }

            yawVelocity = calcSignAngularVelocity(angularDistance) * yawVelocity;
        }


        return new GimbalData(yawVelocity,gimbalRequest);
    }

    private DegreeData calcGimbalPitch(DroneTask<GimbalTaskType> currentGimbalTask,
                                   Telemetry currentTelemetry){

        if(currentGimbalTask == null || currentGimbalTask.status().value().isTaskDone()){
            return new DegreeData(0,false);
        }

        Location locationToLookAt = null;
        switch (currentGimbalTask.taskType()){

            case LOOK_AT_POINT:
                LookAtPoint lookAtPoint = (LookAtPoint)currentGimbalTask;
                locationToLookAt = lookAtPoint.location();
                break;
            case LOCK_LOOK_AT_LOCATION:
                LockGimbalAtLocation lockGimbalAtLocation = (LockGimbalAtLocation)currentGimbalTask;
                locationToLookAt = lockGimbalAtLocation.location();
                break;
            case LOCK_TO_FLIGHT_DIRECTION:
                return new DegreeData(0,true);
            case ROTATE_GIMBAL:
                RotateGimbal rotateGimbal = (RotateGimbal)currentGimbalTask;
                if(rotateGimbal.rotationRequest().isPitchEnable()){
                    return new DegreeData(rotateGimbal.rotationRequest().getGimbalState().getPitch(),true);
                }
                else{
                    return new DegreeData(0,false);
                }
        }

        if(locationToLookAt != null) {

            if(locationToLookAt.getAltitude() == Location.UNSET_VALUE){
                try {
                    double terrainUnderLookAtLocation = obstacleProvider.dtmProvider().terrainAltitude(locationToLookAt);
                    Location homeLocation = controller.droneHome.homeLocation().value();
                    if(homeLocation == null){
                        throw new TerrainNotFoundException("Unknown home location");
                    }
                    double homeLocationDTM = obstacleProvider.dtmProvider().terrainAltitude(homeLocation);
                    locationToLookAt = locationToLookAt.altitude(terrainUnderLookAtLocation - homeLocationDTM);
                } catch (TerrainNotFoundException e) {
                    e.printStackTrace();
                }
            }

            double distanceFromLocationToLookAt = locationToLookAt.distance(currentTelemetry.location());
            double altitudeDistance = currentTelemetry.location().getAltitude() - locationToLookAt.getAltitude();

            if(distanceFromLocationToLookAt == 0){
                return new DegreeData(-Math.signum(altitudeDistance)*90,true);
            }

            double gimbalPitchRotation = -Math.toDegrees(Math.atan(altitudeDistance / distanceFromLocationToLookAt));
            return new DegreeData(gimbalPitchRotation,true);
        }
        else{
            return new DegreeData(0,false);
        }
    }

    private YawDegreeData calcGimbalYaw(DroneTask<GimbalTaskType> currentGimbalTask,
                                       Telemetry currentTelemetry){
        if(currentGimbalTask == null || currentGimbalTask.status().value().isTaskDone()){
            return new YawDegreeData(0,false,null);
        }

        switch (currentGimbalTask.taskType()){

            case LOOK_AT_POINT:
                LookAtPoint lookAtPoint = (LookAtPoint)currentGimbalTask;
                return new YawDegreeData(
                        currentTelemetry.location().az(lookAtPoint.location()),
                        true,
                        lookAtPoint.location());
            case LOCK_LOOK_AT_LOCATION:
                LockGimbalAtLocation lockGimbalAtLocation = (LockGimbalAtLocation)currentGimbalTask;
                return new YawDegreeData(
                        currentTelemetry.location().az(lockGimbalAtLocation.location()),
                        true,
                        lockGimbalAtLocation.location());
            case LOCK_YAW_AT_LOCATION:
                LockYawAtLocation lockYawAtLocation = (LockYawAtLocation)currentGimbalTask;
                return new YawDegreeData(
                        currentTelemetry.location().az(lockYawAtLocation.location()) + lockYawAtLocation.degreeShiftFromLocation(),
                        true,
                        lockYawAtLocation.location());
            case ROTATE_GIMBAL:
                RotateGimbal rotateGimbal = (RotateGimbal)currentGimbalTask;
                if(rotateGimbal.rotationRequest().isYawEnable()){
                    return new YawDegreeData(rotateGimbal.rotationRequest().getGimbalState().getYaw(),true,null);
                }
                else{
                    return new YawDegreeData(0,false,null);
                }
            default:
                return new YawDegreeData(0,false,null);
        }
    }

    private FlightSpeedsData calcFlightSpeedsData(DroneTask<FlightTaskType> currentFlightTask,
                                                  Telemetry currentTelemetry,Double aboveGroundLevel,Double aboveSeaLevel){

        if(currentFlightTask == null || currentFlightTask.status().value().isTaskDone() || currentTelemetry.location() == null){
            return new FlightSpeedsData(null,false,0D,false, null);
        }

        HorizontalFlightData horizontalSpeeds = null;
        Double verticalSpeed = null;
        Double yawSpeed = null;
        double verticalDistance = 0;

        switch (currentFlightTask.taskType()){

            case FLY_SAFE_TO:
                FlyToSafeAndFastAbstract flyToSafeAndFast = (FlyToSafeAndFastAbstract)currentFlightTask;
                FlyToSafeAndFastAbstract.FlySafeOperationType flySafeOperationType = flyToSafeAndFast.getOperationType().value();

                switch (flySafeOperationType){

                    case NONE:
                        break;
                    case RAISE_AT_START_LOCATION:

                        AltitudeInfo raiseAltitudeInfo = flyToSafeAndFast.getRaiseAtStartLocationAltitudeInfo().value();

                        switch (raiseAltitudeInfo.getAltitudeType()){

                            case ABOVE_GROUND_LEVEL:
                                if(aboveGroundLevel != null){
                                    verticalDistance = raiseAltitudeInfo.getValueInMeters() - aboveGroundLevel;
                                }
                                break;
                            case ABOVE_SEA_LEVEL:
                                if(aboveSeaLevel != null){
                                    verticalDistance = raiseAltitudeInfo.getValueInMeters() - aboveSeaLevel;
                                }
                                break;
                            case FROM_TAKE_OFF_LOCATION:
                                verticalDistance = raiseAltitudeInfo.getValueInMeters() - currentTelemetry.location().getAltitude();
                                break;
                        }
                        verticalSpeed = calcVerticalSpeedFromDistance(verticalDistance);
                        break;
                    case FLY_TO_USING_AZ:
                        Location gotoTargetLocation = flyToSafeAndFast.targetLocation();
                        double radialDistance = currentTelemetry.location().distance(gotoTargetLocation);
                        Double azFromTarget = currentTelemetry.location().az(gotoTargetLocation);
                        if (azFromTarget.isNaN())
                            azFromTarget = 0D;
                        double rollPitchSpeed = calcRollPitchSpeed(radialDistance,currentTelemetry.velocities(), (double) maxRollPitchVelocity);
                        double radianAzFromTarget = Math.toRadians(azFromTarget);

                        double roll = rollPitchSpeed * Math.cos(radianAzFromTarget);
                        double pitch = rollPitchSpeed * Math.sin(radianAzFromTarget);

                        horizontalSpeeds = new HorizontalFlightData(roll,pitch);

                        horizontalSpeeds = calcFlyWithAzimuth(gotoTargetLocation,currentTelemetry.location(),flyToSafeAndFast.getAzForFlyToTarget().value(),horizontalSpeeds);
                        break;
                    case LOWER_TO_TARGET:

                        switch (flyToSafeAndFast.altitudeInfo().getAltitudeType()){

                            case ABOVE_GROUND_LEVEL:
                                if(aboveGroundLevel != null) {
                                    verticalDistance = flyToSafeAndFast.altitudeInfo().getValueInMeters() - aboveGroundLevel;
                                }
                                break;
                            case ABOVE_SEA_LEVEL:
                                if(aboveSeaLevel != null){
                                    verticalDistance = flyToSafeAndFast.altitudeInfo().getValueInMeters() - aboveSeaLevel;
                                }
                                break;
                            case FROM_TAKE_OFF_LOCATION:
                                verticalDistance = flyToSafeAndFast.altitudeInfo().getValueInMeters() - currentTelemetry.location().getAltitude();
                                break;
                        }
                        verticalSpeed = calcVerticalSpeedFromDistance(verticalDistance);
                        break;
                }

                break;
            case ROTATE_HEADING:
                RotateHeading rotateHeading = (RotateHeading) currentFlightTask;
                double currentHeading = degreeBetween0To360(currentTelemetry.heading());
                double targetHeading = rotateHeading.angle();
                double angularDistance = targetHeading - currentHeading;

                double yawVelocity = calcAngularVelocity(angularDistance);
                if(yawVelocity > 60.0F){
                    yawVelocity = 60.0F;
                }

                yawSpeed = calcSignAngularVelocity(angularDistance)*yawVelocity;
                break;
            case TAKE_OFF:
                TakeOffAbstract takeOff = (TakeOffAbstract) currentFlightTask;
                verticalDistance = takeOff.altitude() - currentTelemetry.location().getAltitude();
                verticalSpeed = calcVerticalSpeedFromDistance(verticalDistance);
                break;
            case FLY_IN_CIRCLE:
                FlyInCircleAbstract flyInCircle = (FlyInCircleAbstract) currentFlightTask;
                AltitudeInfo flyInCircleAltitudeInfo = flyInCircle.altitudeInfo();
                switch (flyInCircleAltitudeInfo.getAltitudeType()){

                    case ABOVE_GROUND_LEVEL:
                        if(aboveGroundLevel != null) {
                            verticalDistance = flyInCircleAltitudeInfo.getValueInMeters() - aboveGroundLevel;
                        }
                        break;
                    case ABOVE_SEA_LEVEL:
                        if(aboveSeaLevel != null){
                            verticalDistance = flyInCircleAltitudeInfo.getValueInMeters() - aboveSeaLevel;
                        }
                        break;
                    case FROM_TAKE_OFF_LOCATION:
                        verticalDistance = flyInCircleAltitudeInfo.getValueInMeters() - currentTelemetry.location().getAltitude();
                        break;
                }

                verticalSpeed = calcVerticalSpeedFromDistance(verticalDistance);
                switch (flyInCircle.getOperationType()) {

                    case FLY_TO_CIRCLE:
                        Location gotoTargetLocation = flyInCircle.getStartingDegreeLocation();
                        double radialDistance = currentTelemetry.location().distance(gotoTargetLocation);
                        Double azFromTarget = currentTelemetry.location().az(gotoTargetLocation);
                        double rollPitchSpeed = calcRollPitchSpeed(radialDistance, currentTelemetry.velocities(),null);
                        double radianAzFromTarget = Math.toRadians(azFromTarget);

                        double roll = rollPitchSpeed * Math.cos(radianAzFromTarget);
                        double pitch = rollPitchSpeed * Math.sin(radianAzFromTarget);

                        horizontalSpeeds = new HorizontalFlightData(roll, pitch);
                        break;
                    case FLY_IN_CIRCLE:
                        Location centerLocation = flyInCircle.center();

                        Point2D targetPoint = Point2D.cartesianPoint(centerLocation.getLatitude(), centerLocation.getLongitude());
                        Point2D currentPoint = Point2D.cartesianPoint(currentTelemetry.location().getLatitude(), currentTelemetry.location().getLongitude());

                        Point2D speeds = DroneMovement.getVelocityToFlyInCircle(targetPoint, flyInCircle.radius(), flyInCircle.velocity(), currentPoint);
                        horizontalSpeeds = new HorizontalFlightData(speeds.getX(), speeds.getY());
                        break;
                }
                break;
            case FLY_TO_USING_DTM:

                FlyToUsingDTM flyToUsingDTM = (FlyToUsingDTM)currentFlightTask;

                ArrayList<Location> nextLocationsOnLine = new ArrayList<>();
                ArrayList<Location> dronePlace = new ArrayList<>();
                DtmProvider provider = obstacleProvider.dtmProvider();

                Location currentLocation = Telemetry.telemetryToLocation(currentTelemetry);
                double azToTarget = currentLocation.az(flyToUsingDTM.location());

                Double currentLocationDTM = null;
                try {
                    currentLocationDTM = provider.terrainAltitude(currentLocation);
                } catch (TerrainNotFoundException e) {
                    e.printStackTrace();
                }

                int numOfSteps = (int) ((8+ flyToUsingDTM.maxVelocity() *2)/provider.stepDistanceInMeters());
                double droneSize = 1;
                int numOfBackStep = (int) (droneSize/provider.stepDistanceInMeters()) + 1;
                for (int i = 0; i < numOfBackStep; i++) {
                    dronePlace.add(currentLocation.getLocationFromAzAndDistance(i*provider.stepDistanceInMeters(),azToTarget + 180));
                    dronePlace.add(currentLocation.getLocationFromAzAndDistance(i*provider.stepDistanceInMeters(),azToTarget));
                }
                double droneHeightPosition = 0;
                try {
                droneHeightPosition = provider.terrainAltitude(dronePlace.get(0)) - currentLocationDTM;
                for(int i=0; i<dronePlace.size();i++){
                    double newHeight = provider.terrainAltitude(dronePlace.get(0)) - currentLocationDTM;
                    if (droneHeightPosition < newHeight){
                        droneHeightPosition = newHeight;
                    }
                }
//                droneHeightPosition = provider.terrainAltitude(currentLocation) - currentLocationDTM;
                } catch (TerrainNotFoundException e) {
                    e.printStackTrace();
//                    MainLogger.logger.writeError(LoggerTypes.ERROR,e);
                    return new FlightSpeedsData(new HorizontalFlightData(0,0),true,0D,true,0D);
                }

                for(int i=0; i < numOfSteps; i++){
                    nextLocationsOnLine.add(currentLocation.getLocationFromAzAndDistance(i*provider.stepDistanceInMeters(),azToTarget));
                }

                ArrayList<Double> nextDtms = new ArrayList<>();
                nextDtms.add(droneHeightPosition + flyToUsingDTM.agl());
                for(int i=0; i<nextLocationsOnLine.size();i++){
                    try {
                        nextDtms.add(provider.terrainAltitude(nextLocationsOnLine.get(i)) - currentLocationDTM + flyToUsingDTM.agl());
                    } catch (TerrainNotFoundException e) {
                        e.printStackTrace();
//                        MainLogger.logger.writeError(LoggerTypes.ERROR,e);
                        return new FlightSpeedsData(new HorizontalFlightData(0,0),true,0D,true,0D);
                    }
                }

                DTMLine dtmLine = DTMLine.createNewLine(provider.stepDistanceInMeters(),nextDtms).calculateScaledDTMLine(1D);
                Point2D velocitiesToGive = dtmLine.
                        calculateVelocityToFly(Point2D.cartesianPoint(flyToUsingDTM.maxVelocity(),1D),
                                Point2D.cartesianPoint(currentTelemetry.velocities().getVelocity(),
                                        currentTelemetry.velocities().getZ()),
                                aboveGroundLevel,3D,0.3D,1D);

                verticalSpeed = velocitiesToGive.getY();


                double rollForDTM = velocitiesToGive.getX() * Math.cos(Math.toRadians(azToTarget));
                double pitchForDTM = velocitiesToGive.getX()* Math.sin(Math.toRadians(azToTarget));

                horizontalSpeeds = new HorizontalFlightData(rollForDTM,pitchForDTM);
                horizontalSpeeds = calcFlyWithAzimuth(flyToUsingDTM.location(),currentTelemetry.location(),flyToUsingDTM.az(),horizontalSpeeds);

//                MainLogger.logger.write_message(LoggerTypes.FLY_USING_DTM_DEBUG,"New Step in Fly to using DTM :" +
//                        MainLogger.TAB + "Current Velocities     : " + currentTelemetry.velocities().toString() +
//                        MainLogger.TAB + "Current Velocity XY    : " + currentTelemetry.velocities().getVelocity() +
//                        MainLogger.TAB + "Distance to next point : " + provider.stepDistanceInMeters() +
//                        MainLogger.TAB + "Next DTM 0             : " + dtmLine.getdTMPoint2DS().get(0) +
//                        MainLogger.TAB + "Next DTM 1             : " + dtmLine.getdTMPoint2DS().get(1) +
//                        MainLogger.TAB + "Next DTM 2             : " + dtmLine.getdTMPoint2DS().get(2) +
//                        MainLogger.TAB + "Current AGL            : " + aboveGroundLevel +
//                        MainLogger.TAB + "Fly To Using DTM output XY: " + horizontalSpeeds.toString() +
//                        MainLogger.TAB + "Fly To Using DTM Output Z : " + verticalSpeed
//                );
                break;
            case FOLLOW_NAV_PLAN:
                FollowNavPlanAbstract followNavPlan = (FollowNavPlanAbstract) currentFlightTask;

                int currentIndex = followNavPlan.currentIndex();

                if(currentIndex >= followNavPlan.navPlanPoints().size()){
                    break;
                }
                NavPlanPoint currentNavPlanPoint = followNavPlan.navPlanPoints().get(currentIndex);

                Location navPlanLocation = currentNavPlanPoint.getLocation();
                AltitudeInfo altitudeInfo = currentNavPlanPoint.getAltitudeInfo();
                double radiusReached = currentNavPlanPoint.getRadiusReached();
                double maxVelocity = currentNavPlanPoint.getMaxVelocity();

                double navPlanVerticalDistance = 0;
                if(altitudeInfo == null){
                    navPlanVerticalDistance = 0;
                }
                else{
                    switch (altitudeInfo.getAltitudeType()){

                        case ABOVE_GROUND_LEVEL:
                            if(aboveGroundLevel != null){
                                if(currentTelemetry.location().distance(navPlanLocation) <= radiusReached) {
                                    navPlanVerticalDistance = (altitudeInfo.getValueInMeters() - aboveGroundLevel);
                                }
                            }
                            break;
                        case ABOVE_SEA_LEVEL:
                            if(aboveSeaLevel != null){
                                navPlanVerticalDistance = altitudeInfo.getValueInMeters() - aboveSeaLevel;
                            }
                            break;
                        case FROM_TAKE_OFF_LOCATION:
                            navPlanVerticalDistance = altitudeInfo.getValueInMeters() - currentTelemetry.location().getAltitude();
                            break;
                    }
                }

                double navPlanVerticalSpeed = calcVerticalSpeedFromDistance(navPlanVerticalDistance);

                double navPlanRadialDistance = currentTelemetry.location().distance(navPlanLocation);
                Double navPlanAzFromTarget = currentTelemetry.location().az(navPlanLocation);
                if (navPlanAzFromTarget.isNaN())
                    navPlanAzFromTarget = 0D;
                double navPlanRollPitchSpeed = calcRollPitchSpeed(navPlanRadialDistance,currentTelemetry.velocities(),maxVelocity);
                double navPlanRadianAzFromTarget = Math.toRadians(navPlanAzFromTarget);

                if(navPlanRadialDistance != 0 && navPlanVerticalDistance != 0 && navPlanRollPitchSpeed != 0 && navPlanVerticalSpeed != 0) {
                    double alpha = Math.atan(Math.abs(navPlanVerticalDistance / navPlanRadialDistance));

                    double vh = Math.signum(navPlanVerticalSpeed) * navPlanRollPitchSpeed * Math.tan(alpha);
                    double vxy = Math.abs(navPlanVerticalSpeed) / Math.tan(alpha);

                    if(Math.abs(vh) < Math.abs(navPlanVerticalSpeed)){
                        navPlanVerticalSpeed = vh;
                    }
                    else{
                        navPlanRollPitchSpeed = vxy;
                    }
                }

                verticalSpeed = navPlanVerticalSpeed;

                double navPlanRoll = navPlanRollPitchSpeed * Math.cos(navPlanRadianAzFromTarget);
                double navPlanPitch = navPlanRollPitchSpeed * Math.sin(navPlanRadianAzFromTarget);

                horizontalSpeeds = new HorizontalFlightData(navPlanRoll,navPlanPitch);

//                horizontalSpeeds = calcFlyWithAzimuth(gotoTargetLocation,currentTelemetry.location(),null,horizontalSpeeds);

                break;
            case GOTO_POINT:
                FlyTo flyTo = (FlyTo)currentFlightTask;
                Location gotoTargetLocation = flyTo.location();

                double flyToVerticalDistance = 0;
                if(flyTo.altitudeInfo().isNull()){
                    flyToVerticalDistance = 0;
                }
                else{
                    switch (flyTo.altitudeInfo().getAltitudeType()){

                        case ABOVE_GROUND_LEVEL:
                            if(aboveGroundLevel != null){
                                if(currentTelemetry.location().distance(gotoTargetLocation) <= flyTo.radiusReached()) {
                                    flyToVerticalDistance = (flyTo.altitudeInfo().getValueInMeters() - aboveGroundLevel);
                                }
                            }
                            break;
                        case ABOVE_SEA_LEVEL:
                            if(aboveSeaLevel != null){
                                flyToVerticalDistance = flyTo.altitudeInfo().getValueInMeters() - aboveSeaLevel;
                            }
                            break;
                        case FROM_TAKE_OFF_LOCATION:
                            flyToVerticalDistance = flyTo.altitudeInfo().getValueInMeters() - currentTelemetry.location().getAltitude();
                            break;
                    }
                }

                double flyToVerticalSpeed = calcVerticalSpeedFromDistance(flyToVerticalDistance);

                double radialDistance = currentTelemetry.location().distance(gotoTargetLocation);
                Double azFromTarget = currentTelemetry.location().az(gotoTargetLocation);
                if (azFromTarget.isNaN())
                    azFromTarget = 0D;
                double rollPitchSpeed = calcRollPitchSpeed(radialDistance,currentTelemetry.velocities(),flyTo.maxVelocity());
                if(Math.abs(flyToVerticalDistance) > 1){
                    rollPitchSpeed = 0;
                }
                double radianAzFromTarget = Math.toRadians(azFromTarget);
//
//                if(radialDistance != 0 && flyToVerticalDistance != 0 && rollPitchSpeed != 0 && flyToVerticalSpeed != 0) {
//                    double alpha = Math.atan(Math.abs(flyToVerticalDistance / radialDistance));
//
//                    double vh = Math.signum(flyToVerticalSpeed) * rollPitchSpeed * Math.tan(alpha);
//                    double vxy = Math.abs(flyToVerticalSpeed) / Math.tan(alpha);
//
//                    if(Math.abs(vh) < Math.abs(flyToVerticalSpeed)){
//                        flyToVerticalSpeed = vh;
//                    }
//                    else{
//                        rollPitchSpeed = vxy;
//                    }
//                }

                verticalSpeed = flyToVerticalSpeed;

                double roll = rollPitchSpeed * Math.cos(radianAzFromTarget);
                double pitch = rollPitchSpeed * Math.sin(radianAzFromTarget);

                horizontalSpeeds = new HorizontalFlightData(roll,pitch);

                horizontalSpeeds = calcFlyWithAzimuth(flyTo.location(),currentTelemetry.location(),flyTo.az(),horizontalSpeeds);

                break;
            case GO_HOME:
                break;
            case LAND_IN_LANDING_PAD:
                break;
            case LAND_AT_LOCATION:
                break;
            case LAND_IN_PLACE:
                Location homeLocation = controller.droneHome().homeLocation().value();

                try{
                    if(aboveGroundLevel == null){
                        throw new TerrainNotFoundException("Unknown AGL");
                    }

                    double absVelocity = Math.abs(calcVerticalSpeedFromDistance(aboveGroundLevel));
                }
                catch (TerrainNotFoundException e){
                    verticalSpeed = -1D;
                }

                break;
        }

        return new FlightSpeedsData(horizontalSpeeds,horizontalSpeeds != null,verticalSpeed,verticalSpeed != null, yawSpeed);
    }

    private HorizontalFlightData calcFlyWithAzimuth(Location targetLocation, Location droneLocation, Double azimuth, HorizontalFlightData horizontalSpeeds) {
        if (azimuth == null)
            return horizontalSpeeds;
        Angle wantedAzimuth = Angle.angleDegree(azimuth);
//        Log.i("flyazi", "wantedAzi: " + wantedAzimuth.degree());
        Angle realAzimuth = Angle.angleDegree(droneLocation.az(targetLocation));
//        Log.i("flyazi", "realAzi: " + realAzimuth.degree());
        Angle beta = wantedAzimuth.sub(realAzimuth);
//        Log.i("flyazi", "beta: " + beta.degree());
        double distance = targetLocation.distance(droneLocation)*beta.sin();
        double scale = 0.15 * Math.signum(distance);
        double velocity = scale*Math.pow(Math.abs(distance),0.5);

        double roll = velocity * Math.cos(wantedAzimuth.sub(Angle.QuarterCircle()).radian()) + horizontalSpeeds.getRoll();
        double pitch = velocity * Math.sin(wantedAzimuth.sub(Angle.QuarterCircle()).radian()) + horizontalSpeeds.getPitch();

        double s1 = Math.sqrt(horizontalSpeeds.squaredSize() / (roll*roll + pitch*pitch));

        return new HorizontalFlightData(roll*s1,pitch*s1);
    }


    private FlightSpeedsData flyToUsingDTM(FlyToUsingDTM flyToUsingDTMTask, Telemetry currentTelemetry, DtmProvider dtmProvider, Double currentAGL){
        return null;
    }

    private FlightSpeedsData moveWithDTMMicroMovement(FlyToUsingDTM flyToUsingDTM, Telemetry currentTelemetry, ObstacleProvider obstacleProvider, Double aboveGroundLevel) {
        if (aboveGroundLevel == null)
            return new FlightSpeedsData(new HorizontalFlightData(0,0),true,0D,true, null);

//        MainLogger.logger.write_message(LoggerTypes.EINAV,"agl: " + aboveGroundLevel);


        Point3D realVelocity3d = currentTelemetry.velocities().toPoint3D();
        realVelocity3d = Point3D.cartesianPoint(Point2D.GeographicPoint(realVelocity3d.getX(),realVelocity3d.getY()),realVelocity3d.getZ());

//        MainLogger.logger.write_message(LoggerTypes.EINAV,"vz: " + realVelocity3d.getZ());
//        MainLogger.logger.write_message(LoggerTypes.EINAV,"vh: " + realVelocity3d.get2dRadius());

        DtmProvider dtmProvider = obstacleProvider.dtmProvider();

        // To Ignore
        double downGapInMeters = 0;
        double upGapInMeters = 0;
//
//        double downGapInMeters = flyToUsingDTM.underGapPercent()*flyToUsingDTM.agl()/100;
//        double upGapInMeters = flyToUsingDTM.upperGapPercent()*flyToUsingDTM.agl()/100;
//        double downGapInMeters = 0.2;
//        double upGapInMeters = 0.2;
//        upGapInMeters = upGapInMeters*0.6 > downGapInMeters ? upGapInMeters*0.6 : upGapInMeters;
//        downGapInMeters = downGapInMeters > upGapInMeters ? upGapInMeters : downGapInMeters;
        double agl = flyToUsingDTM.agl();

        double maxVelocity = flyToUsingDTM.maxVelocity();
        Angle azimuthToTarget = Angle.angleDegree(currentTelemetry.location().az(flyToUsingDTM.location()));

        double realHorizontalVelocity = realVelocity3d.get2dRadius();
        Angle realVelocityAngle = Angle.angleRadian(realVelocity3d.getHorizontalVector().getAngle());

        double density = 0.1;  //must get better: function of flight height, speed, and gaps.
        double droneSize = 1; //general drone size can be more generic
        double safeDistance = 5; //default setting

        double computedVelocity = realHorizontalVelocity + 0.2 > maxVelocity ? maxVelocity : realHorizontalVelocity + 0.2;
        Location currentLocation = currentTelemetry.location();
        double distanceToTarget = currentLocation.distance(flyToUsingDTM.location());

//        MainLogger.logger.write_message(LoggerTypes.EINAV,"d: " + distanceToTarget);

        computedVelocity = calcDeceleration(distanceToTarget,computedVelocity);


        Location computedLocation = currentTelemetry.location().getLocationFromAzAndDistance(droneSize,realVelocityAngle.degree());

        double currentLocationDTMAltitude = getDroneDTMAltitude(dtmProvider,droneSize,density,currentLocation,realVelocityAngle);

        double distanceXY = maxVelocity > safeDistance ? maxVelocity : safeDistance;
        ArrayList<Double> dtmHeights = getDtmMap(dtmProvider,density,computedLocation,distanceXY,azimuthToTarget);

        double fromHomeDTM = 0;
        try {
            fromHomeDTM = dtmProvider.terrainAltitude(currentLocation);
        } catch (TerrainNotFoundException e) {
            e.printStackTrace();
        }

        aboveGroundLevel += fromHomeDTM - currentLocationDTMAltitude;
        ArrayList<Point2D> pointsOutSideGaps = new ArrayList<>();
        double distanceZ = 0;
        double distanceZAdd = 0;
        if(realHorizontalVelocity == 0){
            for (int i = 0; i < dtmHeights.size(); i++) {
                double distance = droneSize + density * i;
                double aglFromDtmPoint = dtmHeights.get(i) - currentLocationDTMAltitude  + agl;
                pointsOutSideGaps.add(Point2D.cartesianPoint(distance,aglFromDtmPoint));
            }
        }
        else {
            distanceZ = calcDistanceZAdd(realVelocity3d.getZ(),realHorizontalVelocity,distanceXY,downGapInMeters,upGapInMeters,agl,dtmHeights,droneSize,density,aboveGroundLevel,currentLocationDTMAltitude,true);
            if(distanceZ == -999){
                for (int i = 0; i < dtmHeights.size(); i++) {
                    double distance = droneSize + density * i;
                    double aglFromDtmPoint = dtmHeights.get(i) - currentLocationDTMAltitude  + agl;
                    pointsOutSideGaps.add(Point2D.cartesianPoint(distance,aglFromDtmPoint));
                }
            }
        }

        double horizontalVelocity = 0;
        double verticalVelocity = 0;

        if (pointsOutSideGaps.size() == 0) {
            double tanAngle = distanceZ / distanceXY;
            horizontalVelocity = distanceXY > computedVelocity ? computedVelocity : distanceXY;
            verticalVelocity = horizontalVelocity*tanAngle;
            if (Math.abs(verticalVelocity) > 4){
                verticalVelocity = 4*Math.signum(verticalVelocity);
                horizontalVelocity = verticalVelocity/tanAngle;
            }

//            distanceZ += 0.5*calcDistanceZAdd(verticalVelocity,horizontalVelocity,distanceXY,downGapInMeters,upGapInMeters,agl,dtmHeights,droneSize,density,aboveGroundLevel,currentLocationDTMAltitude,false);
//
//            tanAngle = distanceZ / distanceXY;
//            horizontalVelocity = distanceXY > computedVelocity ? computedVelocity : distanceXY;
//            verticalVelocity = horizontalVelocity*tanAngle;
//            if (Math.abs(verticalVelocity) > 4){
//                verticalVelocity = 4*Math.signum(verticalVelocity);
//                horizontalVelocity = verticalVelocity/tanAngle;
//            }
        } else {
            Point2D maxPoint = getMaxPoint(pointsOutSideGaps);
            distanceXY = maxPoint.getX()/2;
            distanceZ = (maxPoint.getY() - aboveGroundLevel + upGapInMeters)/2;
            double distanceFromUpperAGL = aboveGroundLevel - (flyToUsingDTM.agl() + upGapInMeters);
            double tanAngle = distanceZ/distanceXY;
            horizontalVelocity = distanceXY > computedVelocity ? computedVelocity : distanceXY;
            verticalVelocity = horizontalVelocity*tanAngle;
            if(tanAngle > 1){
                horizontalVelocity = 0;
                verticalVelocity = distanceZ*2;
                verticalVelocity = verticalVelocity > 4 ? 4 : verticalVelocity;
            }
            if (Math.abs(verticalVelocity) > 4){
                verticalVelocity = 4*Math.signum(verticalVelocity);
                horizontalVelocity = 0;
            }
        }

        double roll = horizontalVelocity * Math.cos(azimuthToTarget.radian());
        double pitch = horizontalVelocity * Math.sin(azimuthToTarget.radian());

        return new FlightSpeedsData(new HorizontalFlightData(roll,pitch),true,verticalVelocity,true, null);
    }

    private double calcDeceleration(double distanceToTarget, double velocity) {
        if (distanceToTarget > 2.5*velocity)
            return velocity;
        double v = velocity/2.5;
        return v > 0.2 ? v : 0.2;
    }

    private double calcDistanceZAdd(double velocityZ, double horizontalVelocity, double distanceXY, double downGapInMeters, double upGapInMeters, double agl, ArrayList<Double> dtmHeights, double droneSize, double density, double aboveGroundLevel, double currentLocationDTMAltitude, boolean isAddDz) {
        double tanRealVelocityPitchAngle = velocityZ / horizontalVelocity;
        double distanceZ = distanceXY * tanRealVelocityPitchAngle;
        double distanceZAdd = calcDeltaZ(dtmHeights,droneSize,density,tanRealVelocityPitchAngle,aboveGroundLevel,currentLocationDTMAltitude,agl,upGapInMeters,downGapInMeters,distanceZ);
        if (distanceZAdd == -999){
            return -999;
        }
        return isAddDz ? distanceZ + distanceZAdd : distanceXY;
    }

    private double calcDeltaZ(ArrayList<Double> dtmHeights, double droneSize, double density, double tanRealVelocityPitchAngle, double aboveGroundLevel, double currentLocationDTMAltitude, double agl, double upGapInMeters, double downGapInMeters,double distanceZ) {
        Polynom polynomForWeightedCalc = getPolynom(downGapInMeters, upGapInMeters);
        DtmFlightFunction dtmFlightFunction = DtmFlightFunction.createDTMFlightFunctionFromGaps(agl - downGapInMeters, agl + upGapInMeters);
        double distanceZAdd = 0;
        for (int i = 0; i < dtmHeights.size(); i++) {
            double distance = droneSize + density * i;
            double deltaHeight = distance * tanRealVelocityPitchAngle;
            double aglFromDtmPoint = aboveGroundLevel + currentLocationDTMAltitude - dtmHeights.get(i) ;
            double z = aglFromDtmPoint + deltaHeight;
            double x = z - agl;
            double w;
            if(aglFromDtmPoint < agl - 2*downGapInMeters - deltaHeight){
                return -999;
            }
            if (aglFromDtmPoint > agl + upGapInMeters - deltaHeight || aglFromDtmPoint < agl - downGapInMeters - deltaHeight){
                if (x < 0){
                    if (x + agl < 0) {
                        w = Math.abs(distanceZ) * 2 + 1e10;
                    } else {
                        w = dtmFlightFunction.value(x + agl) * downGapInMeters;
                    }
                } else {
                    w = -dtmFlightFunction.value(x + agl) * upGapInMeters;
                }
            }
            else {
                if (x > 0) {
                    w = -polynomForWeightedCalc.value(x) * upGapInMeters;
                } else {
                    w = polynomForWeightedCalc.value(x) * downGapInMeters;
                }
            }
            distanceZAdd += w / dtmHeights.size();
        }

        return distanceZAdd;
    }

    private Point2D getMaxPoint(ArrayList<Point2D> pointsOutSideGaps) {
        double maxY = pointsOutSideGaps.get(0).getY();
        int maxIndex = 0;
        for (int i = 0; i < pointsOutSideGaps.size(); i++) {
            if (maxY < pointsOutSideGaps.get(i).getY()) {
                maxY = pointsOutSideGaps.get(i).getY();
                maxIndex = i;
            }
        }
        return pointsOutSideGaps.get(maxIndex);
    }

    public static Polynom getPolynom(double downGapInMeters, double upGapInMeters) {

        double upVar = 1/upGapInMeters;
        double downVar = 1/downGapInMeters;

        if (downGapInMeters == upGapInMeters){
            ArrayList<Point2D> point2Ds = new ArrayList<>();
            point2Ds.add(Point2D.cartesianPoint(0,0));
            point2Ds.add(Point2D.cartesianPoint(-downGapInMeters,1));
            point2Ds.add(Point2D.cartesianPoint(upGapInMeters,1));
            return Polynom.calcPolynomWithPoints(point2Ds);
        }

        ArrayList<Double> parameters = new ArrayList<>();
        parameters.add(0D);
        parameters.add(0D);
        LinearEquations linearEquations = new LinearEquations(2);

        try {
            linearEquations.addEquation(new Double[]{Math.pow(upGapInMeters,3),Math.pow(upGapInMeters,2),-upVar});
            linearEquations.addEquation(new Double[]{Math.pow(-downGapInMeters,3),Math.pow(-downGapInMeters,2),-downVar});
        } catch (MathException e) {
            e.printStackTrace();
        }
        double[] doubles = new double[2];
        try {
            doubles = linearEquations.solve();
        } catch (MathException e) {
            e.printStackTrace();
        }
        parameters.add(doubles[1]);
        parameters.add(doubles[0]);

        return new Polynom(parameters);
    }

    public ArrayList<Double> getDtmMap(DtmProvider dtmProvider, double density, Location computedLocation, double distance, Angle azimuthToTarget) {
        ArrayList<Double> map = new ArrayList<>();
        for (int i = 0; i < distance/density; i++) {
            try {
                map.add(dtmProvider.terrainAltitude(computedLocation.getLocationFromAzAndDistance(density * i, azimuthToTarget.degree())));
            } catch (TerrainNotFoundException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public double getDroneDTMAltitude(DtmProvider dtmProvider, double droneSize, double density, Location currentLocation, Angle realVelocityAngle) {

        double currentLocationDTMAltitude = 0;
        try {
            currentLocationDTMAltitude = dtmProvider.terrainAltitude(currentLocation.getLocationFromAzAndDistance(droneSize,realVelocityAngle.add(Angle.halfCircle()).degree()));
        for (int i = 1; i < 2/density; i++) {
            currentLocationDTMAltitude = Math.max(dtmProvider.terrainAltitude(currentLocation.getLocationFromAzAndDistance(i*density,realVelocityAngle.degree())),currentLocationDTMAltitude);
        }
        } catch (TerrainNotFoundException e) {
            e.printStackTrace();
        }
        return currentLocationDTMAltitude;
    }

    public void sendSpeedsMessage(double pitch,double roll,double yaw,double height){

        if(!speedsInRange(pitch,roll,yaw,height)){

//            MainLogger.logger.write_message(LoggerTypes.MICRO_MOVE,"aborting speeds message since speeds not in range : "
//                    + MainLogger.TAB + "Pitch    : " + pitch
//                    + MainLogger.TAB + "roll     : " + roll
//                    + MainLogger.TAB + "yaw      : " + yaw
//                    + MainLogger.TAB + "vertical : " + height
//            );
            controller.flightTasks().sendSpeedsCommand(0,0,0,0);
            return;
        }

        if(readyToGiveInstructions.isNull() || !readyToGiveInstructions.value()){
//            MainLogger.logger.write_message(LoggerTypes.MICRO_MOVE,"aborting speeds message since not ready to give drone micro move.");
            return;
        }

//        MainLogger.logger.write_message(LoggerTypes.MICRO_MOVE,"send speeds to Internal Controller : " + pitch + "," + roll + "," + yaw + "," + height);

        controller.flightTasks().sendSpeedsCommand(pitch,roll,yaw,height);
    }


    private class YawDegreeData extends DegreeData{

        private final Location locationOfInterest;

        private YawDegreeData(double degree, boolean isEnabaled, Location locationOfInterest) {
            super(degree, isEnabaled);
            this.locationOfInterest = locationOfInterest;
        }

        public Location getLocationOfInterest() {
            return locationOfInterest;
        }
    }

    private class FlightSpeedsData {
        private final HorizontalFlightData horizontalFlightData;
        private final boolean isHorizontalEnabled;
        private final Double verticalSpeed;
        private final boolean isVerticalSpeedEnabled;
        private final Double yawSpeed;

        private FlightSpeedsData(HorizontalFlightData horizontalFlightData, boolean isHorizontalEnabled, Double verticalSpeed, boolean isVerticalSpeedEnabled, Double yawSpeed) {
            this.horizontalFlightData = horizontalFlightData;
            this.isHorizontalEnabled = isHorizontalEnabled;
            this.verticalSpeed = verticalSpeed;
            this.isVerticalSpeedEnabled = isVerticalSpeedEnabled;
            this.yawSpeed = yawSpeed;
        }

        public Double getYawSpeed() {
            return yawSpeed;
        }

        public HorizontalFlightData getHorizontalFlightData() {

            return isHorizontalEnabled ? horizontalFlightData : new HorizontalFlightData(0,0);
        }

        @Override
        public String toString() {
            return "FlightSpeedsData{" +
                    "horizontalFlightData=" + (horizontalFlightData == null ? "N/A" : horizontalFlightData.toString())+
                    ", isHorizontalEnabled=" + isHorizontalEnabled +
                    ", verticalSpeed=" + verticalSpeed +
                    ", isVerticalSpeedEnabled=" + isVerticalSpeedEnabled +
                    ", yawSpeed=" + yawSpeed +
                    '}';
        }

        public boolean isHorizontalEnabled() {
            return isHorizontalEnabled;
        }

        public double getVerticalSpeed() {

            return isVerticalSpeedEnabled ? verticalSpeed : 0;
        }

        public boolean isVerticalSpeedEnabled() {
            return isVerticalSpeedEnabled;
        }

        public Point3D getVelocities() {
            Point3D point3D = Point3D.cartesianPoint(getHorizontalFlightData().getRoll(),getHorizontalFlightData().getPitch(),getVerticalSpeed());
            return point3D;
        }
    }

    private class DegreeData{
        private final double degree;
        private final boolean isEnabaled;

        private DegreeData(double degree, boolean isEnabaled) {
            this.degree = degree;
            this.isEnabaled = isEnabaled;
        }

        public double getDegree() {
            return degree;
        }

        public boolean isEnabaled() {
            return isEnabaled;
        }
    }

    private class GimbalData{
        private final double yawSpeed;
        private final GimbalRequest gimbalRequest;

        private GimbalData(double yawSpeed, GimbalRequest gimbalRequest) {
            this.yawSpeed = yawSpeed;
            this.gimbalRequest = gimbalRequest;
        }

        public double getYawSpeed() {
            return yawSpeed;
        }

        public GimbalRequest getGimbalRequest() {
            return gimbalRequest;
        }
    }

    private class HorizontalFlightData{
        private final double roll;
        private final double pitch;

        private HorizontalFlightData(double roll, double pitch) {
            this.roll = roll;
            this.pitch = pitch;
        }

        public double getRoll() {
            return roll;
        }

        public double getPitch() {
            return pitch;
        }

        public double squaredSize(){
            return roll*roll + pitch*pitch;
        }

        @Override
        public String toString() {
            return "HorizontalFlightData{" +
                    "roll=" + roll +
                    ", pitch=" + pitch +
                    '}';
        }
    }

    public double calcSignAngularVelocity(double angularDistance){

        angularDistance = degreeBetween0To360(angularDistance);

        if(angularDistance > 0){
            if(angularDistance < 180){
                return 1;
            }
            else{
                return -1;
            }
        }

        if(angularDistance < -180){
            return 1;
        }
        else{
            return -1;
        }
    }

    private double calcRollPitchSpeed(double distance, Velocities velocities,Double maxVelocity){

        double finalVelocity = maxVelocity != null ? maxVelocity : maxRollPitchVelocity;

        if(distance >= 2.5f*finalVelocity){
            return finalVelocity;
        }

        if(distance < 0.5){
            return 0;
        }

        return Math.max(0.5D,distance/2.5f);
    }

    private double calcVerticalSpeedFromDistance(double distance){
        if(Math.abs(distance) >= 2*maxVerticalVelocity){
            return Math.signum(distance) * maxVerticalVelocity;
        }

        return Math.signum(distance) * Math.abs(distance / 2);
    }

    public double calcAngularVelocity(double angularDistance){

        double newAngularDistance = degreeBetween0To360(angularDistance);

        if(newAngularDistance > 180)
            newAngularDistance =  360-newAngularDistance;

        if(newAngularDistance > 20){
            return 20F;
        }

        double a = 13.0/150.0;
        double b = -7.0/30.0;

        double velocity = newAngularDistance;

        if(!controller.model().isNull() && controller.model().value() == DroneModel.MAVIC){
            velocity = 0.6 * velocity;
        }
        return velocity;
    }

    private boolean speedsInRange(double pitch,double roll, double yaw,double height){

        boolean inRange = true;

        if(Math.abs(roll) > MAX_ROLL_PITCH_VELOCITY){
            inRange = false;
        }
        if(Math.abs(pitch) > MAX_ROLL_PITCH_VELOCITY){
            inRange = false;
        }
        if(Math.abs(yaw) > MAX_YAW_VELOCITY){
            inRange = false;
        }
        if(Math.abs(height) > MAX_HEIGHT_VELOCITY){
            inRange = false;
        }

        return inRange;
    }


    public static boolean isSticksRelevant(DroneTask<FlightTaskType> flightTask,DroneTask<GimbalTaskType> gimbalTask,SticksPosition position){

        boolean hasFlightTask = flightTask == null ? false : !flightTask.status().value().isTaskDone();
        boolean hasGimbalTask = gimbalTask == null ? false : !gimbalTask.status().value().isTaskDone();

        if(!hasFlightTask && !hasGimbalTask){
            return true;
        }

        if(!hasFlightTask){
            if(position.getForwardStick().getValueInPercent() != 0 || position.getRightStick().getValueInPercent() != 0 || position.getVerticalStick().getValueInPercent() != 0){
                return true;
            }
        }

        if(!hasGimbalTask){
            if(position.getYawStick().getValueInPercent() != 0){
                return true;
            }
        }

        return false;
    }

    public void setMaxRollPitchVelocity(float maxRollPitchVelocity) {
        this.maxRollPitchVelocity = maxRollPitchVelocity;
    }

    public void setMaxVerticalVelocity(float maxVerticalVelocity) {
        this.maxVerticalVelocity = maxVerticalVelocity;
    }
}
