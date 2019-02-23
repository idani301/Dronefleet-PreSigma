package com.example.abstractcontroller.taskmanager;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.ComponentConnectivityType;

import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.mission.MissionTaskType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 29/08/2017.
 */
public class FlightTaskManager extends GenericTaskManager<FlightTaskType,FlightTaskBlockerType> {

    @Override
    public void startBlockersObservers(AbstractDroneController droneController) {

        droneController.flightTasks().getConnectivity().observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                if(newValue != ComponentConnectivityType.CONNECTED){
                    addBlocker(FlightTaskBlockerType.NOT_CONNECTED);
                }
                else{
                    removeBlocker(FlightTaskBlockerType.NOT_CONNECTED);
                }
            }
        },blockersExecutor).observeCurrentValue();

        currentTask().observe(new Observer<DroneTask<FlightTaskType>>() {
            @Override
            public void observe(DroneTask<FlightTaskType> oldValue, DroneTask<FlightTaskType> newValue, Observation<DroneTask<FlightTaskType>> observation) {
                if(newValue != null) {
                    newValue.status().observe(new Observer<TaskStatus>() {
                        @Override
                        public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                            if (newValue.isTaskDone()) {
                                removeBlocker(FlightTaskBlockerType.BUSY);
                                observation.remove();
                            } else {
                                addBlocker(FlightTaskBlockerType.BUSY);
                            }
                        }
                    }).observeCurrentValue();
                }
                else{
                    removeBlocker(FlightTaskBlockerType.BUSY);
                }
            }
        },blockersExecutor).observeCurrentValue();

        droneController.flightMode().observe(new Observer<FlightMode>() {
            @Override
            public void observe(FlightMode oldValue, FlightMode newValue, Observation<FlightMode> observation) {
                if(newValue == null || newValue != FlightMode.APP_CONTROL) {
                    addBlocker(FlightTaskBlockerType.NOT_IN_APP_CONTROL);
                }
                else{
                    removeBlocker(FlightTaskBlockerType.NOT_IN_APP_CONTROL);
                }
            }
        },blockersExecutor);

        droneController.rcInFunctionMode().observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                if(newValue == null || newValue == false) {
                    addBlocker(FlightTaskBlockerType.NOT_IN_F_MODE);
                }
                else{
                    removeBlocker(FlightTaskBlockerType.NOT_IN_F_MODE);
                }
            }
        },blockersExecutor).observeCurrentValue();

        droneController.gps().observe(new Observer<GpsState>() {
            @Override
            public void observe(GpsState oldValue, GpsState newValue, Observation<GpsState> observation) {
                if(newValue != null && newValue.hasGpsError()){
                    addBlocker(FlightTaskBlockerType.GPS_ERROR);
                }
                else{
                    removeBlocker(FlightTaskBlockerType.GPS_ERROR);
                }
            }
        },blockersExecutor).observeCurrentValue();

        droneController.preheating().observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                if(newValue != null && newValue){
                    addBlocker(FlightTaskBlockerType.PREHEATING);
                }
                else{
                    removeBlocker(FlightTaskBlockerType.PREHEATING);
                }
            }
        },blockersExecutor).observeCurrentValue();

        droneController.hasCompassError().observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                if(newValue != null && newValue){
                    addBlocker(FlightTaskBlockerType.COMPASS_ERROR);
                }
                else{
                    removeBlocker(FlightTaskBlockerType.COMPASS_ERROR);
                }
            }
        },blockersExecutor).observeCurrentValue();

        droneController.getMissionManager().getTakenResources().observe(new CollectionObserver<TaskCategory>(){
            @Override
            public void added(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == TaskCategory.FLIGHT){
                    addBlocker(FlightTaskBlockerType.MISSION_PLANNER);
                }
            }

            @Override
            public void removed(TaskCategory value, Observation<TaskCategory> observation) {
                if(value == TaskCategory.FLIGHT){
                    removeBlocker(FlightTaskBlockerType.MISSION_PLANNER);
                }
            }
        },blockersExecutor);
    }

    @Override
    public TaskCategory taskCategory() {
        return TaskCategory.FLIGHT;
    }
}
