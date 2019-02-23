package eyesatop.controller.mock;

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
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.Property;

public abstract class MockDroneGimbal implements DroneGimbal {

    public static class Stub extends MockDroneGimbal {
        @Override public void addGimbalRequest(GimbalRequest gimbalRequest) throws DroneTaskException {}

        @Override
        public RotateGimbal rotateGimbal(GimbalRequest request,Integer timeoutInSeconds) throws DroneTaskException {
            return null;
        }

        @Override
        public Explore explore() throws DroneTaskException {
            return null;
        }

        @Override public LookAtPoint lookAtPoint(Location location) throws DroneTaskException {return null;}
        @Override public LockYawAtLocation lockYawAtLocation(Location location, double yawDegreeFromLocation) throws DroneTaskException {return null;}
        @Override public LockGimbalAtLocation lockGimbalAtLocation(Location location) throws DroneTaskException {return null;}
        @Override public LockGimbalAtFlightDirection lockGimbalToFlightDirection() throws DroneTaskException {return null;}
    }

    private final Property<DroneTask<GimbalTaskType>> currentTask;
    private final ObservableList<GimbalTaskBlockerType> tasksBlockers;
    private final Property<GimbalState> gimbalState;

    public MockDroneGimbal() {
        currentTask = new Property<>();
        tasksBlockers = new ObservableList<>();
        gimbalState = new Property<>();
    }

    @Override public Property<DroneTask<GimbalTaskType>> currentTask() {return currentTask;}
    @Override public ObservableList<GimbalTaskBlockerType> tasksBlockers() {return tasksBlockers;}
    @Override public Property<GimbalState> gimbalState() {return gimbalState;}

    @Override public abstract void addGimbalRequest(GimbalRequest gimbalRequest) throws DroneTaskException;
    @Override public abstract LookAtPoint lookAtPoint(Location location) throws DroneTaskException;
    @Override public abstract LockYawAtLocation lockYawAtLocation(Location location, double yawDegreeFromLocation) throws DroneTaskException;
    @Override public abstract LockGimbalAtLocation lockGimbalAtLocation(Location location) throws DroneTaskException;
    @Override public abstract LockGimbalAtFlightDirection lockGimbalToFlightDirection() throws DroneTaskException;
    @Override public abstract RotateGimbal rotateGimbal(GimbalRequest request,Integer timeoutInSeconds) throws DroneTaskException;
    @Override public abstract Explore explore() throws DroneTaskException;
}