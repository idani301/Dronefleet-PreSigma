package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.ComponentConnectivityType;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.battery.BatteryTaskBlockerType;
import eyesatop.controller.tasks.battery.BatteryTaskType;
import eyesatop.controller.tasks.remotecontroller.RemoteControllerTaskBlockerType;
import eyesatop.controller.tasks.remotecontroller.RemoteControllerTaskType;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 09/09/2017.
 */

public class RemoteControllerTaskManager extends GenericTaskManager<RemoteControllerTaskType,RemoteControllerTaskBlockerType> {

    @Override
    public void startBlockersObservers(AbstractDroneController droneController) {


        droneController.getRemoteController().getConnectivity().observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                if(newValue != ComponentConnectivityType.CONNECTED){
                    addBlocker(RemoteControllerTaskBlockerType.NOT_CONNECTED);
                }
                else{
                    removeBlocker(RemoteControllerTaskBlockerType.NOT_CONNECTED);
                }
            }
        },blockersExecutor);
        currentTask().observe(new Observer<DroneTask<RemoteControllerTaskType>>() {
            @Override
            public void observe(DroneTask<RemoteControllerTaskType> oldValue, DroneTask<RemoteControllerTaskType> newValue, Observation<DroneTask<RemoteControllerTaskType>> observation) {
                if(newValue != null){
                    addBlocker(RemoteControllerTaskBlockerType.BUSY);
                }
                else{
                    removeBlocker(RemoteControllerTaskBlockerType.BUSY);
                }
            }
        },blockersExecutor);

        droneController.getMissionManager().getTakenResources().observe(new CollectionObserver<TaskCategory>(){
            @Override
            public void added(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == taskCategory()){
                    addBlocker(RemoteControllerTaskBlockerType.MISSION_PLANNER);
                }
            }

            @Override
            public void removed(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == taskCategory()){
                    removeBlocker(RemoteControllerTaskBlockerType.MISSION_PLANNER);
                }
            }
        },blockersExecutor);
    }

    @Override
    public TaskCategory taskCategory() {
        return TaskCategory.REMOTE_CONTROLLER;
    }
}
