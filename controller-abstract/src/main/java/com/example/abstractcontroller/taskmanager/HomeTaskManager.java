package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.ComponentConnectivityType;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 29/08/2017.
 */
public class HomeTaskManager extends GenericTaskManager<HomeTaskType,HomeTaskBlockerType> {

    @Override
    public TaskCategory taskCategory() {
        return TaskCategory.HOME;
    }

    @Override
    public void startBlockersObservers(AbstractDroneController droneController) {

        droneController.droneHome().getConnectivity().observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                if(newValue != ComponentConnectivityType.CONNECTED){
                    addBlocker(HomeTaskBlockerType.NOT_CONNECTED);
                }
                else{
                    removeBlocker(HomeTaskBlockerType.NOT_CONNECTED);
                }
            }
        },blockersExecutor).observeCurrentValue();

        currentTask().observe(new Observer<DroneTask<HomeTaskType>>() {
            @Override
            public void observe(DroneTask<HomeTaskType> oldValue, DroneTask<HomeTaskType> newValue, Observation<DroneTask<HomeTaskType>> observation) {

                if(newValue != null) {
                    newValue.status().observe(new Observer<TaskStatus>() {
                        @Override
                        public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                            if (newValue.isTaskDone()) {
                                removeBlocker(HomeTaskBlockerType.BUSY);
                                observation.remove();
                            } else {
                                addBlocker(HomeTaskBlockerType.BUSY);
                            }
                        }
                    }).observeCurrentValue();
                }
                else{
                    removeBlocker(HomeTaskBlockerType.BUSY);
                }
            }
        },blockersExecutor).observeCurrentValue();

        droneController.getMissionManager().getTakenResources().observe(new CollectionObserver<TaskCategory>(){
            @Override
            public void added(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == TaskCategory.HOME){
                    addBlocker(HomeTaskBlockerType.MISSION_PLANNER);
                }
            }

            @Override
            public void removed(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == TaskCategory.HOME){
                    removeBlocker(HomeTaskBlockerType.MISSION_PLANNER);
                }
            }
        },blockersExecutor);
    }
}
