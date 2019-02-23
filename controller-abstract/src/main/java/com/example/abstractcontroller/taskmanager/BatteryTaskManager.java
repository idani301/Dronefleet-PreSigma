package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.ComponentConnectivityType;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.battery.BatteryTaskBlockerType;
import eyesatop.controller.tasks.battery.BatteryTaskType;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 09/09/2017.
 */

public class BatteryTaskManager extends GenericTaskManager<BatteryTaskType,BatteryTaskBlockerType> {

    @Override
    public void startBlockersObservers(AbstractDroneController droneController) {

        droneController.getDroneBattery().getConnectivity().observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                if(newValue != ComponentConnectivityType.CONNECTED){
                    addBlocker(BatteryTaskBlockerType.NOT_CONNECTED);
                }
                else{
                    removeBlocker(BatteryTaskBlockerType.NOT_CONNECTED);
                }
            }
        },blockersExecutor);

        currentTask().observe(new Observer<DroneTask<BatteryTaskType>>() {
            @Override
            public void observe(DroneTask<BatteryTaskType> oldValue, DroneTask<BatteryTaskType> newValue, Observation<DroneTask<BatteryTaskType>> observation) {

                if(newValue != null){
                    addBlocker(BatteryTaskBlockerType.BUSY);
                }
                else{
                    removeBlocker(BatteryTaskBlockerType.BUSY);
                }
            }
        },blockersExecutor);

        droneController.getMissionManager().getTakenResources().observe(new CollectionObserver<TaskCategory>(){
            @Override
            public void added(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == taskCategory()){
                    addBlocker(BatteryTaskBlockerType.MISSION_PLANNER);
                }
            }

            @Override
            public void removed(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == taskCategory()){
                    removeBlocker(BatteryTaskBlockerType.MISSION_PLANNER);
                }
            }
        },blockersExecutor);
    }

    @Override
    public TaskCategory taskCategory() {
        return TaskCategory.BATTERY;
    }
}
