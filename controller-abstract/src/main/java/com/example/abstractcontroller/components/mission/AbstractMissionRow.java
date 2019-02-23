package com.example.abstractcontroller.components.mission;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import eyesatop.controller.mission.IteratorCommandInfo;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.MissionTaskType;
import eyesatop.controller.mission.MissionTaskInfo;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.Removable;
import logs.LoggerTypes;
//import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 31/08/2017.
 */

public class AbstractMissionRow extends RunnableDroneTask<MissionTaskType> implements MissionRow{

    private final List<TaskCategory> preRunCleanUpList;
    private final HashMap<TaskCategory,MissionTaskInfo> tasksMap;
    private final List<TaskCategory> postRunCleanUpList;
    private final IteratorCommandInfo iteratorUpdate;
    private AbstractDroneController controller;
    private ArrayList<Removable> observerList = new ArrayList<>();

    public AbstractMissionRow(
            List<TaskCategory> preCleanUpArray, HashMap<TaskCategory, MissionTaskInfo> tasksMap,
            List<TaskCategory> postRunCleanUpList,
            IteratorCommandInfo iteratorUpdate) {
        this.preRunCleanUpList = preCleanUpArray;
        this.tasksMap = tasksMap;
        this.postRunCleanUpList = postRunCleanUpList;
        this.iteratorUpdate = iteratorUpdate;
    }

    public void setController(AbstractDroneController controller){
        this.controller = controller;
    }

    @Override
    public List<TaskCategory> getPreRunCleanUpList() {
        return preRunCleanUpList;
    }

    @Override
    public HashMap<TaskCategory, MissionTaskInfo> getTasksMap() {
        return tasksMap;
    }

    @Override
    public List<TaskCategory> getPostRunCleanUpList() {
        return postRunCleanUpList;
    }

    @Override
    public IteratorCommandInfo getIteratorUpdate() {
        return iteratorUpdate;
    }

    @Override
    public MissionTaskType taskType() {
        return MissionTaskType.NORMAL;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

//        String rowDetails = "";
//        rowDetails += MainLogger.TAB + "Camera : " + (tasksMap.get(TaskCategory.CAMERA) == null ? "NULL" : tasksMap.get(TaskCategory.CAMERA).getStubDroneTask().taskType());
//        rowDetails += MainLogger.TAB + "FLIGHT : " + (tasksMap.get(TaskCategory.FLIGHT) == null ? "NULL" : tasksMap.get(TaskCategory.FLIGHT).getStubDroneTask().taskType());
//        rowDetails += MainLogger.TAB + "GIMBAL : " + (tasksMap.get(TaskCategory.GIMBAL) == null ? "NULL" : tasksMap.get(TaskCategory.GIMBAL).getStubDroneTask().taskType());
//
//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Starting to Execute Row:" + rowDetails);
//
//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Cancel Pre Tasks");
        cancelTasksAndWait(preRunCleanUpList);

        for(TaskCategory category : tasksMap.keySet()){

            StubDroneTask tempTask = tasksMap.get(category).getStubDroneTask();

            switch (category){

                case CAMERA:
                    try {
                        controller.camera().startStubTask(tempTask, true);
                    }
                    catch (DroneTaskException e){
                        if(!tasksMap.get(TaskCategory.CAMERA).isAllowedToCrush()){
                            throw new DroneTaskException(e.getErrorString());
                        }
                        else{
                            tasksMap.remove(TaskCategory.CAMERA);
                        }
                    }
                    break;
                case FLIGHT:
                    try {
                        controller.flightTasks().startStubTask(tempTask, true);
                    }
                    catch (DroneTaskException e){
                        if(!tasksMap.get(TaskCategory.FLIGHT).isAllowedToCrush()){
                            throw new DroneTaskException(e.getErrorString());
                        }
                        else{
                            tasksMap.remove(TaskCategory.FLIGHT);
                        }
                    }
                    break;
                case HOME:
                    try {
                        controller.droneHome().startStubTask(tempTask,true);
                    } catch (DroneTaskException e) {
                        if(!tasksMap.get(TaskCategory.HOME).isAllowedToCrush()){
                            throw new DroneTaskException(e.getErrorString());
                        }
                        else {
                            tasksMap.remove(TaskCategory.HOME);
                        }
                    }
                    break;
                case GIMBAL:
                    try {
                        controller.gimbal().startStubTask(tempTask,true);
                    } catch (DroneTaskException e) {
                        if(!tasksMap.get(TaskCategory.GIMBAL).isAllowedToCrush()){
                            throw new DroneTaskException(e.getErrorString());
                        }
                        else{
                            tasksMap.remove(TaskCategory.GIMBAL);
                        }
                    }
                    break;
            }
        }
        final CountDownLatch runningMissionsLatch = new CountDownLatch(tasksMap.values().size());
        for(final MissionTaskInfo info : tasksMap.values()){

            if(!info.isNeccesseryForFinish()){
                runningMissionsLatch.countDown();
                continue;
            }

            observerList.add(info.getStubDroneTask().status().observe(new Observer<TaskStatus>() {
                @Override
                public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {

//                    MainLogger.logger.write_message(LoggerTypes.MISSION,"Change inside status of Task " + info.getStubDroneTask().taskType().getName() + ":" + newValue);

                    if(newValue.isTaskDone()){

                        if((newValue == TaskStatus.ERROR || newValue == TaskStatus.CANCELLED)){
                            if(!info.isAllowedToCrush()) {
                                cancel();
                            }
                            runningMissionsLatch.countDown();
                        }
                        else if(info.isNeccesseryForFinish() && newValue == TaskStatus.FINISHED){
                            runningMissionsLatch.countDown();
                        }
                        else{
//                            MainLogger.logger.write_message(LoggerTypes.ERROR,"Error : mission manager got to illegal task end");
                            cancel();
                        }

                        observation.remove();
                    }
                }
            }).observeCurrentValue());
        }
        runningMissionsLatch.await();

        for(Removable removable : observerList){
            removable.remove();
        }

//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Cancel Post Tasks");
        cancelTasksAndWait(postRunCleanUpList);

//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Done Executo Row");
    }

    private void cancelTasksAndWait(List<TaskCategory> categories) throws InterruptedException {


//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Execute Row : cancel tasks and wait started");

        if(categories == null){
            return;
        }

        ArrayList<DroneTask> tasksCancelled = new ArrayList<>();
        for(TaskCategory category : categories){

            DroneTask tempTask;
            switch (category){

                case CAMERA:
                    tempTask = controller.camera().currentTask().value();
                    cancelAndAddToList(tempTask,tasksCancelled);
                    break;
                case FLIGHT:
                    tempTask = controller.flightTasks().current().value();
                    cancelAndAddToList(tempTask,tasksCancelled);
                    break;
                case HOME:
                    tempTask = controller.droneHome().currentTask().value();
                    cancelAndAddToList(tempTask,tasksCancelled);
                    break;
                case GIMBAL:
                    tempTask = controller.gimbal().currentTask().value();
                    cancelAndAddToList(tempTask,tasksCancelled);
                    break;
            }
        }

        if(tasksCancelled.size() > 0){
            final CountDownLatch countDownLatch = new CountDownLatch(tasksCancelled.size());

            for(DroneTask task : tasksCancelled){
                task.status().observe(new Observer<TaskStatus>() {

                    @Override
                    public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                        if(newValue.isTaskDone()){
                            countDownLatch.countDown();
                            observation.remove();
                        }
                    }
                }).observeCurrentValue();
            }
            countDownLatch.await();
        }
//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Execute Row : cancel tasks and wait done");
    }

    private void cancelAndAddToList(DroneTask task,ArrayList<DroneTask> list){

        if(task != null){
            try{
                task.cancel();
                list.add(task);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {

//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Cleanning Mission row, Exit status :" + exitStatus);

        for(Removable removable : observerList){
            try {
                removable.remove();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if(status().value() != TaskStatus.FINISHED){
            for(MissionTaskInfo info : tasksMap.values()){
                try {
                    StubDroneTask droneTask = info.getStubDroneTask();
                    if(droneTask != null && !((TaskStatus)droneTask.status().value()).isTaskDone()){
                        droneTask.cancel();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }


//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Done Cleanning Mission row, exit status: " + exitStatus);

    }
}
