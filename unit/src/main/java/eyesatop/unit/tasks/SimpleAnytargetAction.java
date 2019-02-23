package eyesatop.unit.tasks;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.DroneController;
import eyesatop.controller.GimbalRequest;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.AltitudeType;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.imageprocess.DetectionData;
import eyesatop.util.Predicate;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.videoclicks.VideoClickInfo;

public class SimpleAnytargetAction extends RunnableDroneTask<UnitTaskType> implements AnytargetAction {

    private final DroneController holderController;
    private final DroneController explorerController;
    private final ObservableValue<VideoClickInfo> holderClicks;
    private final ObservableValue<VideoClickInfo> explorerClicks;
    private final ObservableValue<DetectionData> explorerFaceRecognition;

    private final ExecutorService holderActionsExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService explorerFlightActionsExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService explorerGimbalActionsExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService explorerCameraActionsExecutor = Executors.newSingleThreadExecutor();

    private final BlockingQueue<DroneAction> holderActions = new LinkedBlockingQueue<>();
    private final BlockingQueue<DroneAction> explorerGimbalActions = new LinkedBlockingQueue<>();
    private final BlockingQueue<DroneAction> explorerFlightActions = new LinkedBlockingQueue<>();
    private final BlockingQueue<DroneAction> explorerCameraActions = new LinkedBlockingQueue<>();


    private final RemovableCollection bindings = new RemovableCollection();

    public SimpleAnytargetAction(final DroneController holderController,
                                 final DroneController explorerController,
                                 ObservableValue<VideoClickInfo> holderClicks,
                                 ObservableValue<VideoClickInfo> explorerClicks,
                                 ObservableValue<DetectionData> explorerFaceRecognition) {
        this.holderController = holderController;
        this.explorerController = explorerController;
        this.holderClicks = holderClicks;
        this.explorerClicks = explorerClicks;
        this.explorerFaceRecognition = explorerFaceRecognition;

        holderActionsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DroneAction action = holderActions.take();
                        while(holderActions.size() > 0){
                            action = holderActions.take();
                        }

                        DroneTask<GimbalTaskType> currentGimbalTask = holderController.gimbal().currentTask().value();

                        if(currentGimbalTask != null && !currentGimbalTask.status().value().isTaskDone()){
                            currentGimbalTask.cancel();
                            currentGimbalTask.status().await(new Predicate<TaskStatus>() {
                                @Override
                                public boolean test(TaskStatus subject) {
                                    return subject.isTaskDone();
                                }
                            },3, TimeUnit.SECONDS);
                        }

                        try {
                            action.perform(holderController);
                        } catch (DroneTaskException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });

        explorerFlightActionsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DroneAction action = explorerFlightActions.take();

                        Boolean isExplorerFlying = explorerController.flying().value();

                        if(isExplorerFlying != null && !isExplorerFlying){
                            try {
                                TakeOff takeOff = explorerController.flightTasks().takeOff(40D);
                                takeOff.status().await(new Predicate<TaskStatus>() {
                                    @Override
                                    public boolean test(TaskStatus subject) {
                                        return subject.isTaskDone();
                                    }
                                });
                            } catch (DroneTaskException e) {
                                e.printStackTrace();
                                continue;
                            }
                        }

                        while(explorerFlightActions.size() > 0){
                            action = explorerFlightActions.take();
                        }

                        DroneTask<FlightTaskType> currentFlightTask = explorerController.flightTasks().current().value();

                        if(currentFlightTask != null && !currentFlightTask.status().value().isTaskDone()){
                            currentFlightTask.cancel();
                            currentFlightTask.status().await(new Predicate<TaskStatus>() {
                                @Override
                                public boolean test(TaskStatus subject) {
                                    return subject.isTaskDone();
                                }
                            },3, TimeUnit.SECONDS);
                        }

                        try {
                            action.perform(explorerController);
                        } catch (DroneTaskException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });

        explorerGimbalActionsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DroneAction action = explorerGimbalActions.take();

                        while(explorerGimbalActions.size() > 0){
                            action = explorerGimbalActions.take();
                        }

                        DroneTask<GimbalTaskType> currentGimbalTask = explorerController.gimbal().currentTask().value();

                        if(currentGimbalTask != null && !currentGimbalTask.status().value().isTaskDone()){
                            currentGimbalTask.cancel();
                            currentGimbalTask.status().await(new Predicate<TaskStatus>() {
                                @Override
                                public boolean test(TaskStatus subject) {
                                    return subject.isTaskDone();
                                }
                            },3, TimeUnit.SECONDS);
                        }

                        try {
                            action.perform(explorerController);
                        } catch (DroneTaskException e) {
                            e.printStackTrace();
                        }

                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });

        explorerCameraActionsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        DroneAction action = explorerCameraActions.take();
                        while(explorerCameraActions.size() > 0){
                            action = explorerCameraActions.take();
                        }

                        try {
                            action.perform(explorerController);
                        } catch (DroneTaskException e) {
                            e.printStackTrace();
                        }

                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });

        bindings.add(new Removable() {
            @Override
            public void remove() {
                holderActionsExecutor.shutdownNow();
                explorerFlightActionsExecutor.shutdownNow();
                explorerGimbalActionsExecutor.shutdownNow();
                explorerCameraActionsExecutor.shutdownNow();
            }
        });
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        final CountDownLatch taskLatch = new CountDownLatch(1);

        bindings.add(
                holderClicks.observe(new Observer<VideoClickInfo>() {
                    @Override
                    public void observe(VideoClickInfo oldValue, final VideoClickInfo newValue, Observation<VideoClickInfo> observation) {

                        if(newValue == null){
                            return;
                        }

                        if(newValue.isAtLimit()){
                            holderActions.add(new DroneAction() {
                                @Override
                                public void perform(DroneController controller) throws DroneTaskException {
                                    controller.gimbal().lookAtPoint(newValue.getLocation());
                                }
                            });
                        }

                        explorerGimbalActions.add(new DroneAction() {
                            @Override
                            public void perform(DroneController controller) throws DroneTaskException {
                                controller.gimbal().lockGimbalAtLocation(newValue.getLocation());
                            }
                        });

                        if(oldValue == null || newValue.getTime() - oldValue.getTime() > 5000 || oldValue.getLocation().distance(newValue.getLocation()) <= 1){
                            explorerFlightActions.add(new DroneAction() {
                                @Override
                                public void perform(DroneController controller) throws DroneTaskException {

                                    Location currentDroneLocation = Telemetry.telemetryToLocation(controller.telemetry().value());
                                    if(currentDroneLocation == null){
                                        return;
                                    }

                                    double azToDrone = newValue.getLocation().az(currentDroneLocation);
                                    Location locationToFlyTo = newValue.getLocation().getLocationFromAzAndDistance(120D,azToDrone);
                                    controller.flightTasks().flyTo(locationToFlyTo,new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION,40D),null,null,3D);

                                }
                            });
                        }
                        else{
                            double az = oldValue.getLocation().az(newValue.getLocation());
                            final Location locationOfExplorer = newValue.getLocation().getLocationFromAzAndDistance(120D,az);
                            explorerFlightActions.add(new DroneAction() {
                                @Override
                                public void perform(DroneController controller) throws DroneTaskException {
                                    controller.flightTasks().flyTo(locationOfExplorer,new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION,40D),null,null,3D);
                                }
                            });
                        }
                    }
                })
        );

        bindings.add(
                explorerClicks.observe(new Observer<VideoClickInfo>() {
                    @Override
                    public void observe(VideoClickInfo oldValue, final VideoClickInfo newValue, Observation<VideoClickInfo> observation) {
                        if(newValue == null){
                            return;
                        }

                        if(newValue.isAtCenter()){
                            explorerCameraActions.add(new DroneAction() {
                                @Override
                                public void perform(DroneController controller) throws DroneTaskException {
                                    controller.camera().zoomIn();
                                }
                            });
                        }

                        explorerGimbalActions.add(new DroneAction() {
                            @Override
                            public void perform(DroneController controller) throws DroneTaskException {
                                controller.gimbal().rotateGimbal(new GimbalRequest(newValue.getGimbalState(),true,false,true),3);
                            }
                        });
                    }
                })
        );

//        bindings.add(
//                explorerFaceRecognition.observe(new Observer<DetectionData>() {
//                    @Override
//                    public void observe(DetectionData oldValue, DetectionData newValue, Observation<DetectionData> observation) {
//                        taskLatch.countDown();
//
////                        try {
////                            explorerController.flightTasks().goHome();
////                        } catch (DroneTaskException e) {
////                            e.printStackTrace();
////                        }
//                    }
//                })
//        );

        taskLatch.await();
        bindings.remove();
    }

    @Override
    public DroneController holderController() {
        return holderController;
    }

    @Override
    public DroneController explorerController() {
        return explorerController;
    }

    @Override
    public ObservableValue<Location> holderBodyDetections() {
        return null;
    }

    @Override
    public ObservableValue<Location> explorerBodyDetections() {
        return null;
    }

    @Override
    public ObservableBoolean explorerFaceRecognition() {
        return null;
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        bindings.remove();
    }

    @Override
    public UnitTaskType taskType() {
        return UnitTaskType.ANYTARGET;
    }

    private interface DroneAction {
        void perform(DroneController controller) throws DroneTaskException;
    }
}
