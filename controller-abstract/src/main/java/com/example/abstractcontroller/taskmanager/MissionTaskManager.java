package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;

import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.mission.MissionTaskBlockerType;
import eyesatop.controller.mission.MissionTaskType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 03/09/2017.
 */

public class MissionTaskManager extends GenericTaskManager<MissionTaskType,MissionTaskBlockerType> {

    @Override
    public void startBlockersObservers(AbstractDroneController droneController) {
        droneController.connectivity().observe(new Observer<DroneConnectivity>() {
            @Override
            public void observe(DroneConnectivity oldValue, DroneConnectivity newValue, Observation<DroneConnectivity> observation) {
                if(newValue == null || newValue != DroneConnectivity.DRONE_CONNECTED){
                    addBlocker(MissionTaskBlockerType.NOT_CONNECTED);
                }
                else{
                    removeBlocker(MissionTaskBlockerType.NOT_CONNECTED);
                }
            }
        },blockersExecutor).observeCurrentValue();

        currentTask().observe(new Observer<DroneTask<MissionTaskType>>() {
            @Override
            public void observe(DroneTask<MissionTaskType> oldValue, DroneTask<MissionTaskType> newValue, Observation<DroneTask<MissionTaskType>> observation) {
                if(newValue != null){
                    addBlocker(MissionTaskBlockerType.BUSY);
                }
                else{
                    removeBlocker(MissionTaskBlockerType.BUSY);
                }
            }
        },blockersExecutor).observeCurrentValue();
    }

    @Override
    public TaskCategory taskCategory() {
        return TaskCategory.MISSION;
    }
}
