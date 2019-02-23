package eyesatop.controller.djinew.components;

import com.example.abstractcontroller.components.AbstractDroneGimbal;
import com.example.abstractcontroller.tasks.gimbal.ExploreAbstract;
import com.example.abstractcontroller.tasks.gimbal.LockGimbalAtLocationAbstract;
import com.example.abstractcontroller.tasks.gimbal.LockYawAtLocationAbstract;
import com.example.abstractcontroller.tasks.gimbal.LookAtPointAbstract;
import com.example.abstractcontroller.tasks.gimbal.RotateGimbalAbstract;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import dji.common.error.DJIError;
import dji.common.gimbal.GimbalMode;
import dji.common.gimbal.Rotation;
import dji.common.gimbal.RotationMode;
import dji.common.util.CommonCallbacks;
import dji.sdk.gimbal.Gimbal;
import eyesatop.controller.GimbalBehaviorType;
import eyesatop.controller.GimbalRequest;
import eyesatop.controller.beans.GimbalBehavior;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.djinew.tasks.gimbal.DjiLockGimbalAtFlightDirection;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.drone.DroneModel;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 10/09/2017.
 */

public class DroneGimbalDji extends AbstractDroneGimbal {

    private DJIError rotationDjiError = null;
    private final ControllerDjiNew controller;
    private final BlockingQueue<GimbalRequest> gimbalRequests = new LinkedBlockingQueue<>();
    private final Property<GimbalBehavior> gimbalBehavior = new Property<>(new GimbalBehavior(GimbalBehaviorType.NOTHING,null,0, null));
    private final Property<GimbalMode> currentGimbalMode = new Property<>();
    private final BooleanProperty isYawAtLimit = new BooleanProperty(false);
    private final BooleanProperty isYawFixing = new BooleanProperty(false);

    private final ExecutorService setGimbalModeExecutor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<GimbalMode> requestedGimbalMode = new LinkedBlockingQueue<>();

    @Override
    protected RunnableDroneTask<GimbalTaskType> stubToRunnable(StubDroneTask<GimbalTaskType> stubDroneTask) throws DroneTaskException {

        switch (stubDroneTask.taskType()){

            case LOOK_AT_POINT:
                LookAtPoint lookAtPoint = (LookAtPoint)stubDroneTask;
                return new LookAtPointAbstract(controller,lookAtPoint.location());
            case LOCK_LOOK_AT_LOCATION:
                LockGimbalAtLocation lockGimbalAtLocation = (LockGimbalAtLocation)stubDroneTask;
                return new LockGimbalAtLocationAbstract(lockGimbalAtLocation.location(), controller);
            case LOCK_YAW_AT_LOCATION:
                LockYawAtLocation lockYawAtLocation = (LockYawAtLocation)stubDroneTask;
                return new LockYawAtLocationAbstract(lockYawAtLocation.location(),lockYawAtLocation.degreeShiftFromLocation());
            case LOCK_TO_FLIGHT_DIRECTION:
                return new DjiLockGimbalAtFlightDirection(controller);
            case EXPLORE:
                return new ExploreAbstract();
            case ROTATE_GIMBAL:
                RotateGimbal rotateGimbal = (RotateGimbal)stubDroneTask;
                return new RotateGimbalAbstract(controller,rotateGimbal.rotationRequest(), rotateGimbal.timeoutInSeconds());
            default:
                throw new DroneTaskException("Not Implemented : " + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {

        controller.getHardwareManager().getDjiGimbal().setStateCallback(new dji.common.gimbal.GimbalState.Callback() {
            @Override
            public void onUpdate(dji.common.gimbal.GimbalState gimbalState) {

                if(gimbalState == null) {
                    return;
                }

                MainLogger.logger.write_message(LoggerTypes.MATRICE,"Dji got new Gimbal State : " +
                        MainLogger.TAB + "Pitch    : " + gimbalState.getAttitudeInDegrees().getPitch() +
                        MainLogger.TAB + "roll     : " + gimbalState.getAttitudeInDegrees().getRoll() +
                        MainLogger.TAB + "yaw      : " + gimbalState.getAttitudeInDegrees().getYaw() +
                        MainLogger.TAB + "YawLimit : " + gimbalState.isYawAtStop()
                );

                isYawAtLimit.setIfNew(gimbalState.isYawAtStop());

                currentGimbalMode.setIfNew(gimbalState.getMode());

                DroneModel droneModel = controller.model().value();
                if(droneModel != null && droneModel == DroneModel.MATRICE100 && gimbalState.getMode() != GimbalMode.FREE){
                    requestedGimbalMode.add(GimbalMode.FREE);
                }

                Boolean isFullGimbal = fullGimbalSupported().value();

                if(isFullGimbal != null && isFullGimbal){
                    gimbalState().setIfNew(new GimbalState(
                            gimbalState.getAttitudeInDegrees().getRoll(),
                            gimbalState.getAttitudeInDegrees().getPitch(),
                            gimbalState.getAttitudeInDegrees().getYaw()));
                }
                else {
                    try {
                        gimbalState().setIfNew(gimbalState().value().pitch(gimbalState.getAttitudeInDegrees().getPitch()));
                    } catch (Exception e) {
                        gimbalState().setIfNew(new GimbalState(0, gimbalState.getAttitudeInDegrees().getPitch(), 0));
                    }
                }
            }
        });
    }

    @Override
    public void onComponentConnected() {

//        try {
//            Map<CapabilityKey, DJIParamCapability> abilitiesMap = controller.getHardwareManager().getDjiGimbal().getCapabilities();
//            if (abilitiesMap.get(CapabilityKey.ADJUST_YAW) != null && abilitiesMap.get(CapabilityKey.ADJUST_YAW).isSupported()) {
//                fullGimbalSupported().set(true);
//
//            }
//        }
//        catch (Exception e){
//            fullGimbalSupported().set(false);
//        }
    }

    private class TakeGimbalRequestsThread extends Thread {

        public void run(){
            try {
                startGimbalRequestsLoop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public DroneGimbalDji(ControllerDjiNew droneController) {
        this.controller = droneController;

        TakeGimbalRequestsThread gimbalRequestsThread = new TakeGimbalRequestsThread();
        gimbalRequestsThread.start();

        setGimbalModeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        GimbalMode requestedMode = requestedGimbalMode.take();
                        while(requestedGimbalMode.size() > 0){
                            requestedMode = requestedGimbalMode.take();
                        }

                        GimbalMode currentMode = currentGimbalMode.value();

                        if(currentMode != null && currentMode == requestedMode){
                            Thread.sleep(5000);
                            continue;
                        }

                        final CountDownLatch latch = new CountDownLatch(1);
                        try {
                            getDjiGimbal().setMode(requestedMode, new CommonCallbacks.CompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    latch.countDown();
                                }
                            });
                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Thread.sleep(5000);
                            continue;
                        }

                        latch.await();
                        Thread.sleep(5000);


                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
    }

    public void startGimbalRequestsLoop() throws InterruptedException {

        while(true){

            MainLogger.logger.write_message(LoggerTypes.GIMBAL_REQUEST,"Waiting for gimbal request" );

            if(isYawFixing.value()){
                Thread.sleep(500);
                isYawFixing.setIfNew(false);
            }

            GimbalRequest gimbalRequest = gimbalRequests.take();

            MainLogger.logger.write_message(LoggerTypes.GIMBAL_REQUEST,"Found gimbal request, current request : " + gimbalRequest.toString());

            while(gimbalRequests.size() > 0){
                GimbalRequest newerRequest = gimbalRequests.take();
                gimbalRequest = gimbalRequest.addNewerRequest(newerRequest);
            }

            MainLogger.logger.write_message(LoggerTypes.GIMBAL_REQUEST,"final request : " + gimbalRequest.toString());

            try {
                rotateGimbalDji(gimbalRequest);
            } catch (DroneTaskException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void internalGimbalRotation(GimbalRequest request) {

//        MainLogger.logger.write_message(LoggerTypes.MATRICE,"Internal gimbal request : " +
//            MainLogger.TAB + request.getGimbalState().getPitch() + " is Pitch enable : " + request.isPitchEnable() +
//                MainLogger.TAB + request.getGimbalState().getYaw() + " is Yaw enable : " + request.isYawEnable()
//        );

        if(request.isPitchEnable()){
            if(request.getGimbalState().getPitch() > 0){
                request = new GimbalRequest(request.getGimbalState().pitch(0),true,request.isYawEnable(),request.isRollEnable());
            }
            else if(request.getGimbalState().getPitch() < -90){
                request = new GimbalRequest(request.getGimbalState().pitch(-90),true,request.isYawEnable(),request.isRollEnable());
            }
        }

        gimbalRequests.add(request);
    }

    public Property<GimbalBehavior> gimbalBehavior() {
        return gimbalBehavior;
    }

    @Override
    public synchronized void addGimbalRequest(GimbalRequest gimbalRequest) throws DroneTaskException {

        MainLogger.logger.write_message(LoggerTypes.GIMBAL_REQUEST,"Inside add gimbal request");

        if(gimbalRequest.isPitchEnable() && gimbalBehavior.value().isPitchLocked()){
            MainLogger.logger.write_message(LoggerTypes.GIMBAL_REQUEST,"pitch locked");
            throw new DroneTaskException("Pitch is locked, cannot rotate pitch");
        }

        if(gimbalRequest.isYawEnable() && gimbalBehavior.value().isYawLocked()){
            MainLogger.logger.write_message(LoggerTypes.GIMBAL_REQUEST,"Inside add gimbal request, yaw locked");
            throw new DroneTaskException("yaw is locked, cannot rotate pitch");
        }

        Gimbal djiGimbal = getDjiGimbal();
        if(djiGimbal == null || !djiGimbal.isConnected()) {
            MainLogger.logger.write_message(LoggerTypes.GIMBAL_REQUEST,"gimbal not connected");
            throw new DroneTaskException("Gimbal is not connected");
        }

        MainLogger.logger.write_message(LoggerTypes.GIMBAL_REQUEST,"Adding gimbal request : " + gimbalRequest.toString());
        gimbalRequests.add(gimbalRequest);
    }

    public synchronized void rotateGimbalDji(final GimbalRequest gimbalRequest) throws DroneTaskException {

        if(gimbalRequest == null || gimbalRequest.getGimbalState() == null){
            throw new DroneTaskException("GimbalState is null, cannot execute command");
        }

        if((gimbalRequest.isRollEnable()|| gimbalRequest.isYawEnable()) && !fullGimbalSupported().value()){
            throw new DroneTaskException("Not supporting roll/yaw gimbalRequest rotations for aircraft model : " + (controller.model().isNull() ? "NULL" : controller.model().value().name()));
        }

        GimbalState currentGimbalState = gimbalState().value();

        if(currentGimbalState == null){
            throw new DroneTaskException("Unknown gimbal state, can't rotate relative");
        }

        String log = "New Gimabl Request";
        log += MainLogger.TAB + gimbalRequest.toString();

        Rotation.Builder rotationBuilder = new Rotation.Builder();
        if(gimbalRequest.isPitchEnable()){

//            float pitchDifference = (float) Location.shortestDegreeToAdd(currentGimbalState.getPitch(),gimbalRequest.getGimbalState().getPitch());

            float pitch = (float) gimbalRequest.getGimbalState().getPitch();
            rotationBuilder.pitch(pitch);

            log += MainLogger.TAB + "Pitch   : " + pitch;
        }

        Double heading = controller.flightTasks().getHeading().value();
        if(heading == null){
            heading = 0D;
        }

        if(gimbalRequest.isYawEnable()){

//            double degreeToAddToYaw = 0;
//            if(isYawAtLimit.value()){
//                degreeToAddToYaw = Location.longestDegreeToAdd(currentGimbalState.getYaw(),gimbalRequest.getGimbalState().getYaw());
//            }
//            else{
//                degreeToAddToYaw = Location.shortestDegreeToAdd(currentGimbalState.getYaw(),gimbalRequest.getGimbalState().getYaw());
//            }

            float requestedDegree = (float) Location.degreeBetween180ToMinus180(gimbalRequest.getGimbalState().getYaw() - heading);

            if(isYawAtLimit.value()){
                requestedDegree = (float) Location.degreeBetween180ToMinus180(requestedDegree + 180);
            }
            isYawFixing.setIfNew(true);

            rotationBuilder.yaw(requestedDegree);
            log += MainLogger.TAB + "Yaw     : " + requestedDegree +
                    MainLogger.TAB + "Heading : " + heading;
        }

        MainLogger.logger.write_message(LoggerTypes.MATRICE,log);

        if(gimbalRequest.isRollEnable()){
            rotationBuilder.roll((float) Location.degreeBetween180ToMinus180(gimbalRequest.getGimbalState().getRoll()));
        }

        rotationBuilder.mode(RotationMode.ABSOLUTE_ANGLE);

        final CountDownLatch rotationLatch = new CountDownLatch(1);
        rotationDjiError = null;

        getDjiGimbal().rotate(rotationBuilder.build(), new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {

                if(djiError != null) {
                    MainLogger.logger.write_message(LoggerTypes.MATRICE, "done with rotation " + gimbalRequest.toString() + " With Error :" + (djiError.getDescription()));
//                MainLoggerJava.logger.write_message(LoggerTypes.MATRICE,"done with gimbal request : " + (djiError == null ? "NULL" : djiError.getDescription()));
//                MainLoggerJava.logger.write_message(LoggerTypes.MATRICE,"done with gimbal request : " + (djiError == null ? "NULL" : djiError.getDescription()));
                }
                rotationDjiError = djiError;
                rotationLatch.countDown();
            }
        });

        try {
            rotationLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(rotationDjiError != null){
            throw new DroneTaskException("Dji Error inside rotateGimbal : " + rotationDjiError.getDescription());
        }
    }

    private Gimbal getDjiGimbal() throws DroneTaskException{

        Gimbal djiGimbal = controller.getHardwareManager().getDjiGimbal();
        if(djiGimbal == null || !djiGimbal.isConnected()){
            throw new DroneTaskException("Gimbal is not connected.");
        }
        return djiGimbal;
    }

    @Override
    public void clearData() {
        super.clearData();
        this.currentGimbalMode.setIfNew(null);
    }
}
