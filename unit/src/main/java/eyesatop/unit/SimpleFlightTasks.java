package eyesatop.unit;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.imageprocess.DetectionData;
import eyesatop.unit.tasks.AnytargetAction;
import eyesatop.unit.tasks.SimpleAnytargetAction;
import eyesatop.unit.tasks.Swap;
import eyesatop.unit.tasks.UnitTaskType;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.videoclicks.VideoClickInfo;

public class SimpleFlightTasks implements UnitFlightTasks {

    private final ObservableList<DroneController> controllers;
    private final DtmProvider dtmProvider;

    private final ObservableList<UUID> busyList = new ObservableList<>();

    private final Lock busyListLock = new ReentrantLock();
    private final HashMap<UUID,Property<DroneTask<UnitTaskType>>> taskHashMap;

    public SimpleFlightTasks(ObservableList<DroneController> controllers, DtmProvider dtmProvider, HashMap<UUID, Property<DroneTask<UnitTaskType>>> taskHashMap) {
        this.controllers = controllers;
        this.dtmProvider = dtmProvider;
        this.taskHashMap = taskHashMap;
    }

    @Override
    public ObservableList<UUID> busyList() {
        return busyList;
    }

    public AnytargetAction anytargetAction(final DroneController holderController,
                                           final HashMap<DroneController,Property<VideoClickInfo>> videoClickedLocations,
                                           ObservableValue<DetectionData> faceRecognitionDetections) throws DroneTaskException {
        busyListLock.lock();
        try{

            DroneController explorerController = null;

            for(DroneController controller : controllers){
                if(!controller.uuid().equals(holderController.uuid())){
                    explorerController = controller;
                    break;
                }
            }

            if(explorerController == null){
                throw new DroneTaskException("Unable to find explorer Controller");
            }

            SimpleAnytargetAction anytargetAction = new SimpleAnytargetAction(
                    holderController,
                    explorerController,
                    videoClickedLocations.get(holderController),
                    videoClickedLocations.get(explorerController), faceRecognitionDetections);
            busyList.add(holderController.uuid());
            busyList.add(explorerController.uuid());
            taskHashMap.get(holderController.uuid()).set(anytargetAction);
            taskHashMap.get(explorerController.uuid()).set(anytargetAction);

            final ExecutorService anytargetExecutor = Executors.newSingleThreadExecutor();

            final DroneController finalExplorerController = explorerController;
            anytargetAction.status().observe(new Observer<TaskStatus>() {
                @Override
                public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                    if(newValue.isTaskDone()){
                        observation.remove();
                        busyListLock.lock();
                        try{

                            busyList.remove(holderController.uuid());
                            busyList.remove(finalExplorerController.uuid());

                            taskHashMap.get(holderController.uuid()).set(null);
                            taskHashMap.get(finalExplorerController.uuid()).set(null);
                        }
                        finally {
                            busyListLock.unlock();
                        }
                    }
                }
            }).observeCurrentValue();

            anytargetExecutor.execute(anytargetAction);
        }
        finally {
            busyListLock.unlock();
        }

        return null;
    }

    @Override
    public synchronized Swap swap(final DroneController controllerToSwap) throws DroneTaskException {

        busyListLock.lock();

        try {

            DroneController bestControllerForSwap = null;

            for (DroneController tempController : controllers) {
                if (isRelevantForSwap(tempController)) {
                    bestControllerForSwap = getBetterControllerForSwap(bestControllerForSwap, tempController);
                }
            }

            if (bestControllerForSwap == null) {
                throw new DroneTaskException("Couldn't find good drone for swap");
            }

            busyList().add(controllerToSwap.uuid());
            busyList.add(bestControllerForSwap.uuid());


            SimpleSwap simpleSwap = new SimpleSwap(controllerToSwap,bestControllerForSwap, dtmProvider);
            taskHashMap.get(controllerToSwap.uuid()).set(simpleSwap);
            taskHashMap.get(bestControllerForSwap.uuid()).set(simpleSwap);

            final DroneController finalBestControllerForSwap = bestControllerForSwap;

            final ExecutorService swapExecutor = Executors.newSingleThreadExecutor();

                    simpleSwap.status().observe(new Observer<TaskStatus>() {
                @Override
                public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                    if(newValue.isTaskDone()){
                        observation.remove();
                        busyListLock.lock();
                        try{
                            busyList.remove(controllerToSwap.uuid());
                            busyList.remove(finalBestControllerForSwap.uuid());

                            taskHashMap.get(controllerToSwap.uuid()).set(null);
                            taskHashMap.get(finalBestControllerForSwap.uuid()).set(null);
                        }
                        finally {
                            busyListLock.unlock();
                        }
                        swapExecutor.shutdownNow();
                    }
                }
            }).observeCurrentValue();
            swapExecutor.execute(simpleSwap);

            return simpleSwap;
        }
        finally {
            busyListLock.unlock();
        }
    }

    private boolean isRelevantForSwap(DroneController controller){

        if(busyList.contains(controller.uuid())){
            return false;
        }

        if(controller.connectivity().value() != DroneConnectivity.DRONE_CONNECTED){
            return false;
        }

        BatteryState droneBattery = controller.droneBattery().value();
        if(droneBattery == null || BatteryState.getPercent(droneBattery) < 50){
            return false;
        }

        if(controller.flightTasks().tasksBlockers().size() > 0){
            return false;
        }

        Boolean motorsOn = controller.motorsOn().value();
        if(motorsOn == null || motorsOn){
            return false;
        }

        Boolean isFlying = controller.flying().value();
        if(isFlying == null || isFlying){
            return false;
        }

        return true;
    }

    public static DroneController getBetterControllerForSwap(DroneController controllerA,DroneController controllerB){

        if(controllerA == null){
            return controllerB;
        }

        if(controllerB == null){
            return controllerA;
        }

        BatteryState batteryA = controllerA.droneBattery().value();
        BatteryState batteryB = controllerB.droneBattery().value();

        if(batteryA == null){
            return controllerB;
        }

        if(batteryB == null){
            return controllerA;
        }

        int batteryPercentA = BatteryState.getPercent(batteryA);
        int batteryPercentB = BatteryState.getPercent(batteryB);

        if(batteryPercentA > batteryPercentB){
            return controllerA;
        }
        else{
            return controllerB;
        }
    }
}
