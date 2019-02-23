package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskBlocker;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import logs.LoggerTypes;
//import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 28/08/2017.
 */

public abstract class GenericTaskManager<T extends EnumWithName,S extends TaskBlocker<T>> {

    private final Property<DroneTask<T>> currentTask = new Property<>();
    private final ObservableList<S> tasksBlockers = new ObservableList<>();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected final ExecutorService blockersExecutor = Executors.newSingleThreadExecutor();

    private List<S> cancelingBlockers = new ArrayList<>();

    public ObservableValue<DroneTask<T>> currentTask(){
        return currentTask;
    }

    public GenericTaskManager(){

        tasksBlockers.observe(new CollectionObserver<S>(){
            @Override
            public void added(S value, Observation<S> observation) {

                DroneTask<T> currentTask = currentTask().value();

                if(currentTask != null && !currentTask.status().value().isTaskDone()){
                    if(value.affectedTasks().contains(currentTask.taskType()) && !value.isBusy() && !value.isMissionPlanner()){
                        cancelCurrentTask();
                    }
                }
            }
        },blockersExecutor);
    }

    public synchronized  void startTask(final RunnableDroneTask<T> newTask,boolean isMissionPlannerTask) throws DroneTaskException {

//        MainLogger.logger.write_message(LoggerTypes.TASKS,taskCategory() + " : Trying to start " + newTask.taskType().getName());


        DroneTask<T> currentRunningTask = currentTask.value();

        if(currentRunningTask != null && !currentRunningTask.status().value().isTaskDone()) {
            String errorString = taskCategory() + ": Unable to start " + newTask.taskType().getName() +
                    ", Already has task in progress : " + currentRunningTask.taskType().getName();

//                MainLogger.logger.write_message(LoggerTypes.TASKS, errorString);
            throw new DroneTaskException(errorString);
        }

        ArrayList<S> newTaskBlockers = new ArrayList<>();

        for(S blocker : tasksBlockers){
            if(!blocker.isBusy() && blocker.affectedTasks().contains(newTask.taskType()) && !(isMissionPlannerTask && (blocker.isMissionPlanner() || blocker.isBusy()))){
                newTaskBlockers.add(blocker);
            }
        }

        if(newTaskBlockers.size() > 0){
            String errorString = taskCategory() + " :The task " + newTask.taskType() + " cannot be started since it has blockers. blockers List : ";
            for(S blocker : newTaskBlockers){
                errorString += "," + blocker.getName();
            }

//            MainLogger.logger.write_message(LoggerTypes.TASKS,errorString);
            throw new DroneTaskException(errorString);
        }

        currentTask.set(newTask);
        newTask.status().observe(new Observer<TaskStatus>() {
            @Override
            public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {

                if(newValue.isTaskDone()){

//                    MainLogger.logger.write_message(LoggerTypes.TASKS,taskCategory() + " : The task " + newTask.taskType().getName() + " ended with status: " + newValue);
                    if(newValue == TaskStatus.ERROR){
//                        MainLogger.logger.write_message(LoggerTypes.TASKS,taskCategory() + " : The task" + newTask.taskType().getName() + " Ended up with error : "
//                                + (newTask.error().isNull() ? "NULL" : newTask.error().value().getErrorString()));
                    }

                    if(newTask.equals(currentTask.value())) {
                        currentTask.set(null);
                    }
                    observation.remove();
                }
            }
        });
        executor.submit(newTask);
//        MainLogger.logger.write_message(LoggerTypes.TASKS,taskCategory() + " : Started " + newTask.taskType().getName());
    }

    protected synchronized void cancelCurrentTask(){

        DroneTask currentDroneTask = currentTask.value();

        try {
            if (currentDroneTask != null) {
                currentDroneTask.cancel();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ObservableList<S> getTasksBlockers() {
        return tasksBlockers;
    }

    protected void addBlocker(final S newBlocker){

        blockersExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(!tasksBlockers.contains(newBlocker)){
                    tasksBlockers.add(newBlocker);
//                    MainLogger.logger.write_message(LoggerTypes.TASKS_BLOCKERS,taskCategory() + " : Added blocker : " + newBlocker.getName());
                }
            }
        });
    }

    protected void removeBlocker(final S blockerToRemove){

        blockersExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(tasksBlockers.contains(blockerToRemove)){
                    tasksBlockers.remove(blockerToRemove);
//                    MainLogger.logger.write_message(LoggerTypes.TASKS_BLOCKERS,taskCategory() + " : Removed blocker : " + blockerToRemove.getName());
                }
            }
        });
    }

    public void setCancelingBlockers(List<S> cancelingBlockers) {
        this.cancelingBlockers = cancelingBlockers;
    }

    public abstract void startBlockersObservers(AbstractDroneController droneController);

    public abstract TaskCategory taskCategory();
}
