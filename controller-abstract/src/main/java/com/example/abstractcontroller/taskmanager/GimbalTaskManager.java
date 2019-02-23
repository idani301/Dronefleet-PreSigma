package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.ComponentConnectivityType;

import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 29/08/2017.
 */
public class GimbalTaskManager extends GenericTaskManager<GimbalTaskType,GimbalTaskBlockerType>{

    @Override
    public TaskCategory taskCategory() {
        return TaskCategory.GIMBAL;
    }

    @Override
    public void startBlockersObservers(AbstractDroneController droneController) {
        droneController.gimbal().getConnectivity().observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                if(newValue != ComponentConnectivityType.CONNECTED){
                    addBlocker(GimbalTaskBlockerType.NOT_CONNECTED);
                }
                else{
                    removeBlocker(GimbalTaskBlockerType.NOT_CONNECTED);
                }
            }
        },blockersExecutor).observeCurrentValue();

        currentTask().observe(new Observer<DroneTask<GimbalTaskType>>() {
            @Override
            public void observe(DroneTask<GimbalTaskType> oldValue, DroneTask<GimbalTaskType> newValue, Observation<DroneTask<GimbalTaskType>> observation) {
                if(newValue != null){
                    newValue.status().observe(new Observer<TaskStatus>() {
                        @Override
                        public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                            if(newValue.isTaskDone()){
                                removeBlocker(GimbalTaskBlockerType.BUSY);
                                observation.remove();
                            }
                            else{
                                addBlocker(GimbalTaskBlockerType.BUSY);
                            }
                        }
                    }).observeCurrentValue();
                }
                else{
                    removeBlocker(GimbalTaskBlockerType.BUSY);
                }
            }
        },blockersExecutor).observeCurrentValue();

        droneController.getMissionManager().getTakenResources().observe(new CollectionObserver<TaskCategory>(){
            @Override
            public void added(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == TaskCategory.GIMBAL){
                    addBlocker(GimbalTaskBlockerType.MISSION_PLANNER);
                }
            }

            @Override
            public void removed(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == TaskCategory.GIMBAL){
                    removeBlocker(GimbalTaskBlockerType.MISSION_PLANNER);
                }
            }
        },blockersExecutor);
    }
}
