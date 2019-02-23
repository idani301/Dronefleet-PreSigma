package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.ComponentConnectivityType;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.airlink.AirLinkTaskBlockerType;
import eyesatop.controller.tasks.airlink.AirLinkTaskType;
import eyesatop.controller.tasks.battery.BatteryTaskBlockerType;
import eyesatop.controller.tasks.battery.BatteryTaskType;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 09/09/2017.
 */

public class AirLinkTaskManager extends GenericTaskManager<AirLinkTaskType,AirLinkTaskBlockerType>{

    @Override
    public void startBlockersObservers(AbstractDroneController droneController) {

        droneController.getAirLink().getConnectivity().observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                if(newValue == ComponentConnectivityType.NULL){
                    addBlocker(AirLinkTaskBlockerType.NOT_CONNECTED);
                }
                else{
                    removeBlocker(AirLinkTaskBlockerType.NOT_CONNECTED);
                }
            }
        },blockersExecutor);

        currentTask().observe(new Observer<DroneTask<AirLinkTaskType>>() {
            @Override
            public void observe(DroneTask<AirLinkTaskType> oldValue, DroneTask<AirLinkTaskType> newValue, Observation<DroneTask<AirLinkTaskType>> observation) {
                if(newValue != null){
                    addBlocker(AirLinkTaskBlockerType.BUSY);
                }
                else{
                    removeBlocker(AirLinkTaskBlockerType.BUSY);
                }
            }
        },blockersExecutor);

        droneController.getMissionManager().getTakenResources().observe(new CollectionObserver<TaskCategory>(){
            @Override
            public void added(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == taskCategory()){
                    addBlocker(AirLinkTaskBlockerType.MISSION_PLANNER);
                }
            }

            @Override
            public void removed(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == taskCategory()){
                    removeBlocker(AirLinkTaskBlockerType.MISSION_PLANNER);
                }
            }
        },blockersExecutor);
    }

    @Override
    public TaskCategory taskCategory() {
        return TaskCategory.AIR_LINK;
    }
}
