package eyesatop.controller;

import eyesatop.controller.tasks.gimbal.Explore;
import eyesatop.util.geo.GimbalState;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtFlightDirection;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;

/**
 * Created by einav on 23/03/2017.
 */
public interface DroneGimbal {

    ObservableValue<DroneTask<GimbalTaskType>> currentTask();
    ObservableList<GimbalTaskBlockerType> tasksBlockers();

    ObservableValue<GimbalState> gimbalState();

    void addGimbalRequest(GimbalRequest gimbalRequest) throws DroneTaskException;

    RotateGimbal rotateGimbal(GimbalRequest request,Integer timeoutInSeconds)            throws DroneTaskException;
    LookAtPoint lookAtPoint(Location location)                                           throws DroneTaskException;
    LockYawAtLocation lockYawAtLocation(Location location, double yawDegreeFromLocation) throws DroneTaskException;
    LockGimbalAtLocation lockGimbalAtLocation(Location location)                         throws DroneTaskException;
    LockGimbalAtFlightDirection lockGimbalToFlightDirection()                            throws DroneTaskException;
    Explore explore()                                                                    throws DroneTaskException;
//    GimbalVerify verify()                                                                throws DroneTaskException;
}
