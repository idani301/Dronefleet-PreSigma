package com.example.abstractcontroller.components;

import com.example.abstractcontroller.taskmanager.GenericTaskManager;

import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskBlocker;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 29/08/2017.
 */

public abstract class GeneralDroneComponent<T extends EnumWithName,S extends TaskBlocker<T>> {

    protected final Property<ComponentConnectivityType> connectivity = new Property<>(ComponentConnectivityType.NULL);

    protected final GenericTaskManager<T,S> taskManager;

    public GeneralDroneComponent(GenericTaskManager<T, S> taskManager) {
        this.taskManager = taskManager;

        connectivity.observe(new Observer<ComponentConnectivityType>() {
            @Override
            public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {
                onConnectivityChange(oldValue,newValue);
            }
        });
    }

    public Property<ComponentConnectivityType> getConnectivity() {
        return connectivity;
    }

    public void startStubTask(StubDroneTask<T> stubDroneTask, boolean isMissionPlannerTask) throws DroneTaskException {

        RunnableDroneTask<T> runnableDroneTask = stubToRunnable(stubDroneTask);
        stubDroneTask.bindToTask(runnableDroneTask);
        taskManager.startTask(runnableDroneTask,isMissionPlannerTask);
    }

    public GenericTaskManager<T, S> getTaskManager() {
        return taskManager;
    }

    protected abstract RunnableDroneTask<T> stubToRunnable(StubDroneTask<T> stubDroneTask) throws DroneTaskException;

    public void onConnectivityChange(ComponentConnectivityType oldValue,ComponentConnectivityType newValue){

        switch (newValue){

            case NULL:
                if(oldValue != ComponentConnectivityType.NULL){
                    clearData();
                }
                break;
            case NOT_CONNECTED:
                if(oldValue == ComponentConnectivityType.CONNECTED){
                    clearData();
                }

                if(oldValue == ComponentConnectivityType.NULL){
                    onComponentAvailable();
                }
                break;
            case CONNECTED:
                if(oldValue == ComponentConnectivityType.NULL){
                    onComponentAvailable();
                    onComponentConnected();
                }
                else if(oldValue == ComponentConnectivityType.NOT_CONNECTED){
                    onComponentConnected();
                }
                break;
        }
    }

    public abstract void onComponentAvailable();
    public abstract void onComponentConnected();
    public abstract void clearData();
}
