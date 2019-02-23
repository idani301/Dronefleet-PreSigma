package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.ComponentConnectivityType;

import eyesatop.controller.mission.MissionTaskType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 29/08/2017.
 */
public class CameraTaskManager extends GenericTaskManager<CameraTaskType,CameraTaskBlockerType> {

    @Override
    public TaskCategory taskCategory() {
        return TaskCategory.CAMERA;
    }

    @Override
    public void startBlockersObservers(AbstractDroneController droneController) {

        droneController.camera().getConnectivity().observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                if(newValue != ComponentConnectivityType.CONNECTED){
                    addBlocker(CameraTaskBlockerType.NOT_CONNECTED);
                }
                else{
                    removeBlocker(CameraTaskBlockerType.NOT_CONNECTED);
                }
            }
        },blockersExecutor).observeCurrentValue();

        currentTask().observe(new Observer<DroneTask<CameraTaskType>>() {
            @Override
            public void observe(DroneTask<CameraTaskType> oldValue, DroneTask<CameraTaskType> newValue, Observation<DroneTask<CameraTaskType>> observation) {
                if(newValue != null) {
                    newValue.status().observe(new Observer<TaskStatus>() {
                        @Override
                        public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                            if (newValue.isTaskDone()) {
                                removeBlocker(CameraTaskBlockerType.BUSY);
                                observation.remove();
                            } else {
                                addBlocker(CameraTaskBlockerType.BUSY);
                            }
                        }
                    }).observeCurrentValue();
                }
                else{
                    removeBlocker(CameraTaskBlockerType.BUSY);
                }
            }
        },blockersExecutor).observeCurrentValue();

        droneController.getMissionManager().getTakenResources().observe(new CollectionObserver<TaskCategory>(){
            @Override
            public void added(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == TaskCategory.CAMERA){
                    addBlocker(CameraTaskBlockerType.MISSION_PLANNER);
                }
            }

            @Override
            public void removed(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == TaskCategory.CAMERA){
                    removeBlocker(CameraTaskBlockerType.MISSION_PLANNER);
                }
            }
        },blockersExecutor);
    }
}
