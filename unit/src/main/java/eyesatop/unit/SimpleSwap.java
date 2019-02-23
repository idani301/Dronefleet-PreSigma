package eyesatop.unit;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.AltitudeType;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.tasks.ConfirmationData;
import eyesatop.controller.tasks.ConfirmationsType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.SetCameraMode;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.GoHome;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.unit.tasks.Swap;
import eyesatop.unit.tasks.UnitTaskType;
import eyesatop.util.Predicate;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

public class SimpleSwap extends RunnableDroneTask<UnitTaskType> implements Swap {

    private final DroneController controllerToSwap;
    private final DroneController swappingController;
    private final DtmProvider dtmProvider;
    private final RemovableCollection removables = new RemovableCollection();

    private Property<DroneController> controllerToReturnHomeIfCancel = new Property<>();
    private final String logsPrefix;


    public SimpleSwap(DroneController controllerToSwap, DroneController swappingController, DtmProvider dtmProvider) {
        this.controllerToSwap = controllerToSwap;
        this.swappingController = swappingController;
        controllerToReturnHomeIfCancel.set(swappingController);
        this.dtmProvider = dtmProvider;

        logsPrefix = "Swap of " + controllerToSwap.uuid() + " : ";
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,"Performing Swap : \n" +
                "We are swapping drone with UUID : " + controllerToSwap.uuid() + "\n" +
                "Swapping Controller UUID        : " + swappingController.uuid()
        );

        clearTasks(swappingController);

        Location controllerToSwapLocation = Telemetry.telemetryToLocation(controllerToSwap.telemetry().value());
        DroneTask<GimbalTaskType> currentDroneGimbalTask = controllerToSwap.gimbal().currentTask().value();

        if(controllerToSwapLocation == null){
            throw new DroneTaskException("Unknown DroneLocation");
        }

        int takeOffAltitude = (int) Math.max(50,controllerToSwapLocation.getAltitude() + 10);
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " waiting for confirm about take off to altitude : " + takeOffAltitude);
        waitForConfirmation(new ConfirmationData(ConfirmationsType.TAKE_OFF,"Aircract Will rise to altitude of : " + takeOffAltitude));
        TakeOff takeOff = swappingController.flightTasks().takeOff(takeOffAltitude);
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Started take off, waiting for take off to be done");
        waitForTaskFinish(takeOff);
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Take off done");

        Location controllerToSwapLookAtLocation = controllerToSwap.lookAtLocation().value();

        Location swappingControllerTargetLookAtLocation = DtmProvider.DtmTools.getGroundLocationRelativeToRefPoint(
                controllerToSwapLookAtLocation,
                swappingController.droneHome().takeOffDTM().value(),
                dtmProvider);

        LockGimbalAtLocation lockGimbalAtLocation = swappingController.gimbal().lockGimbalAtLocation(swappingControllerTargetLookAtLocation);

        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Flying to top of controller,Waiting for done");
        FlyTo flyToBeOnTopOfController = swappingController.flightTasks().flyTo(controllerToSwapLocation,new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION, (double) takeOffAltitude),null,null,5D);
        waitForTaskFinish(flyToBeOnTopOfController);
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Flying to top of controller, Done");

        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Lower altitude before swap, waiting for done");
        FlyTo lowerAltitudeBeforeSwap = swappingController.flightTasks().flyTo(controllerToSwapLocation,new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION,controllerToSwapLocation.getAltitude() + 10),null,null,5D);
        waitForTaskFinish(lowerAltitudeBeforeSwap);
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Lower altitude before swap, Done");

        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Copy Controller camera state, Waiting for done");
        try {
            copyControllerCameraState(swappingController, controllerToSwap);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Copy Controller camera state, Done");

        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " Waiting for confirm of swap");
        waitForConfirmation(new ConfirmationData(ConfirmationsType.SWAP,"Controllers will swap"));
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " swap confirmed");

        controllerToReturnHomeIfCancel.set(controllerToSwap);

        Boolean isRecording = controllerToSwap.camera().recording().value();
        if(isRecording != null && isRecording){
            try {
                controllerToSwap.camera().stopRecording();
            }
            catch (DroneTaskException e){
                e.printStackTrace();
            }
        }

        if(currentDroneGimbalTask != null){
            try {
                currentDroneGimbalTask.cancel();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " clear area swapping controller");

        Location currentControllerToSwapLocation = Telemetry.telemetryToLocation(controllerToSwap.telemetry().value());
        if(currentControllerToSwapLocation == null){
            throw new DroneTaskException("Unknown location of controller to swap");
        }

        Location controllerToSwapHome = controllerToSwap.droneHome().homeLocation().value();
        if(controllerToSwapHome == null){
            throw new DroneTaskException("Unknown home location of controller to swap");
        }

        double azToHome = currentControllerToSwapLocation.az(controllerToSwapHome);
        Location evacuationLocation = currentControllerToSwapLocation.getLocationFromAzAndDistance(15,azToHome);

        FlyTo evacuationFlyTo = controllerToSwap.flightTasks().flyTo(evacuationLocation,new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION,controllerToSwapLocation.getAltitude()),null,null,7D);

        waitForTaskFinish(evacuationFlyTo);

        clearTasks(controllerToSwap);
        controllerToSwap.flightTasks().goHome();

        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " swapping controller lower altitude, wait for done");
        FlyTo lowerAltitudeAfterSwap = swappingController.flightTasks().flyTo(controllerToSwapLocation,new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION,controllerToSwapLocation.getAltitude()),null,null,5D);
        waitForTaskFinish(lowerAltitudeAfterSwap);
        MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " swapping controller lower altitude");

        lockGimbalAtLocation.cancel();
        System.out.println("Swap : Waiting for lock gimbal to be finished");
        lockGimbalAtLocation.status().await(new Predicate<TaskStatus>() {
            @Override
            public boolean test(TaskStatus subject) {
                return subject.isTaskDone();
            }
        });
        System.out.println("Swap : Done waiting for lock gimbal to be finished");

        copyControllerGimbalTask(currentDroneGimbalTask);
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {

        removables.remove();

        if(exitStatus != TaskStatus.FINISHED){

            final DroneController controllerToCancel = controllerToReturnHomeIfCancel.value();

            MainLoggerJava.writeMessage(JavaLoggerType.SWAP,logsPrefix + " : Clean up for non finished swap");
            clearTasks(controllerToCancel);

            final ExecutorService goHomeExecutor = Executors.newSingleThreadExecutor();
            goHomeExecutor.execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        controllerToCancel.flightTasks().current().equalsTo(null).awaitTrue();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        Location currentControllerToCancelLocation = Telemetry.telemetryToLocation(controllerToCancel.telemetry().value());
                        if (currentControllerToCancelLocation == null) {
                            throw new DroneTaskException("Unknown location of controller to swap");
                        }

                        Location controllerToCanelHome = controllerToCancel.droneHome().homeLocation().value();
                        if (controllerToCanelHome == null) {
                            throw new DroneTaskException("Unknown home location of controller to swap");
                        }

                        double azToHome = currentControllerToCancelLocation.az(controllerToCanelHome);
                        Location evacuationLocation = currentControllerToCancelLocation.getLocationFromAzAndDistance(15, azToHome);

                        FlyTo evacuationFlyTo = controllerToCancel.flightTasks().flyTo(evacuationLocation, new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION, currentControllerToCancelLocation.getAltitude()), null, null, 7D);

                        waitForTaskFinish(evacuationFlyTo);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    try {
                        controllerToCancel.flightTasks().goHome();
                    } catch (DroneTaskException e) {
                        e.printStackTrace();
                    }

                    goHomeExecutor.shutdownNow();
                }
            });

        }
    }

    private void waitForTaskFinish(DroneTask task) throws InterruptedException, DroneTaskException {
        final CountDownLatch taskLatch = new CountDownLatch(1);
        task.status().observe(new Observer<TaskStatus>() {
            @Override
            public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                if(newValue.isTaskDone()){
                    observation.remove();
                    taskLatch.countDown();
                }
            }
        }).observeCurrentValue();
        taskLatch.await();

        if(task.status().value() != TaskStatus.FINISHED){
            throw new DroneTaskException("Failed inside task : " + task.taskType());
        }
    }

    @Override
    public UnitTaskType taskType() {
        return UnitTaskType.SWAP;
    }

    @Override
    public UUID controllerToSwap() {
        return controllerToSwap.uuid();
    }

    @Override
    public UUID swappingController() {
        return swappingController.uuid();
    }

    private void clearTasks(DroneController controller){

        ArrayList<DroneTask> tasks = new ArrayList<>();

        tasks.add(controller.flightTasks().current().value());
        tasks.add(controller.gimbal().currentTask().value());
        tasks.add(controller.camera().currentTask().value());

        for(DroneTask task : tasks){
            if(task != null && !((TaskStatus)task.status().value()).isTaskDone()){
                try{
                    task.cancel();
                }
                catch (Exception e){
                }
            }
        }
    }

    private void copyControllerCameraState(DroneController controller,DroneController controllerToCopy) throws DroneTaskException, InterruptedException {

        CameraMode controllerCameraMode = controller.camera().mode().value();
        CameraMode controllerToCopyCameraMode = controllerToCopy.camera().mode().value();

        if(controllerToCopyCameraMode == null){
            throw new DroneTaskException("Unknown camera mode of the controller we are trying to swap");
        }

        if(controllerCameraMode == null || controllerCameraMode != controllerToCopyCameraMode){
            SetCameraMode setMode = controller.camera().setMode(controllerToCopyCameraMode);
            waitForTaskFinish(setMode);
        }

        Boolean controllerRecording = controller.camera().recording().value();
        Boolean controllerToCopyRecording = controllerToCopy.camera().recording().value();

        if(controllerToCopyRecording == null){
            throw new DroneTaskException("Don't know if the controller we are swapping is recording");
        }

        if(controllerRecording == null || controllerRecording != controllerToCopyRecording){

            DroneTask task;

            if(controllerToCopyRecording){
                task = controller.camera().startRecording();
            }
            else{
                task= controller.camera().stopRecording();
            }
            waitForTaskFinish(task);
        }
    }

    private double calcDistance(Location a, Location b){

        if(a == null || b == null){
            return 100;
        }

        double altDistance = Math.abs(a.getAltitude() - b.getAltitude());
        double distance = a.distance(b);

        return Math.sqrt(altDistance*altDistance + distance*distance);
    }

    private void copyControllerGimbalTask(DroneTask<GimbalTaskType> currentGimbalTask){

        if(currentGimbalTask == null){
            return;
        }

        switch (currentGimbalTask.taskType()){

            case LOOK_AT_POINT:

                Location lookAtLocation = ((LookAtPoint)currentGimbalTask).location();

                try {
                    swappingController.gimbal().lookAtPoint(lookAtLocation);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                }
                break;
            case LOCK_LOOK_AT_LOCATION:
                Location location = ((LockGimbalAtLocation)currentGimbalTask).location();

                try {
                    swappingController.gimbal().lockGimbalAtLocation(location);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                }
                break;
            case LOCK_YAW_AT_LOCATION:
                Location yawLocation = ((LockYawAtLocation)currentGimbalTask).location();
                double degree = ((LockYawAtLocation)currentGimbalTask).degreeShiftFromLocation();

                try {
                    swappingController.gimbal().lockYawAtLocation(yawLocation,degree);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                }
                break;
            case LOCK_TO_FLIGHT_DIRECTION:

                try {
                    swappingController.gimbal().lockGimbalToFlightDirection();
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
