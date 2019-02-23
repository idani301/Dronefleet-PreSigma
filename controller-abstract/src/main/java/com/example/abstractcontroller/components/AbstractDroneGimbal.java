package com.example.abstractcontroller.components;

import com.example.abstractcontroller.taskmanager.GimbalTaskManager;

import eyesatop.controller.DroneGimbal;
import eyesatop.controller.GimbalRequest;
import eyesatop.controller.tasks.gimbal.Explore;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.util.geo.GimbalState;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtFlightDirection;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 28/08/2017.
 */

public abstract class AbstractDroneGimbal extends GeneralDroneComponent<GimbalTaskType,GimbalTaskBlockerType> implements DroneGimbal {

    private final Property<GimbalState> gimbalState;
    private final BooleanProperty fullGimbalSupported;

    public AbstractDroneGimbal(){
        super(new GimbalTaskManager());
        fullGimbalSupported = new BooleanProperty(true);
        gimbalState = new Property<>();
    }

    public BooleanProperty fullGimbalSupported(){
        return fullGimbalSupported;
    }

    @Override
    public ObservableValue<DroneTask<GimbalTaskType>> currentTask() {
        return taskManager.currentTask();
    }

    @Override
    public ObservableList<GimbalTaskBlockerType> tasksBlockers() {
        return taskManager.getTasksBlockers();
    }

    @Override
    public Property<GimbalState> gimbalState() {
        return gimbalState;
    }

    @Override
    public LookAtPoint lookAtPoint(final Location location) throws DroneTaskException {

        LookAtPoint.LookAtPointStub stubTask = new LookAtPoint.LookAtPointStub() {
            @Override
            public Location location() {
                return location;
            }
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public RotateGimbal rotateGimbal(final GimbalRequest request,Integer timeoutInSeconds) throws DroneTaskException {

        RotateGimbal.RotateGimbalStub stubTask = new RotateGimbal.RotateGimbalStub(request, timeoutInSeconds);
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public LockYawAtLocation lockYawAtLocation(final Location location, final double yawDegreeFromLocation) throws DroneTaskException {

        LockYawAtLocation.LockYawAtLocationStub stubTask = new LockYawAtLocation.LockYawAtLocationStub(location, yawDegreeFromLocation);
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public LockGimbalAtLocation lockGimbalAtLocation(final Location location) throws DroneTaskException {

        LockGimbalAtLocation.LockGimbalAtLocationStub stubTask = new LockGimbalAtLocation.LockGimbalAtLocationStub(location) {
            @Override
            public Location location() {
                return location;
            }
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public LockGimbalAtFlightDirection lockGimbalToFlightDirection() throws DroneTaskException {

        LockGimbalAtFlightDirection.LockGimbalAtFlightDirectionStub stubTask = new LockGimbalAtFlightDirection.LockGimbalAtFlightDirectionStub() {
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public Explore explore() throws DroneTaskException {

        Explore.ExploreStub exploreStub = new Explore.ExploreStub();
        startStubTask(exploreStub,false);

        return exploreStub;
    }

    @Override
    public void clearData() {

        gimbalState.set(null);
    }

    public abstract void internalGimbalRotation(GimbalRequest request) throws DroneTaskException;
}
