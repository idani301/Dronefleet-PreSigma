package eyesatop.controller_tcpip.remote.tcpipcontroller;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.GimbalRequest;
import eyesatop.controller.mock.MockDroneGimbal;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.Explore;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtFlightDirection;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.controller_tcpip.common.tasks.gimbal.LockGimbalAtLocationTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.gimbal.LookAtPointTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.gimbal.RotateGimbalTaskUpdate;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.controller_tcpip.common.tasksrequests.gimbal.GimbalTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.gimbal.LockGimbalAtLocationRequest;
import eyesatop.controller_tcpip.common.tasksrequests.gimbal.LookAtPointRequest;
import eyesatop.controller_tcpip.common.tasksrequests.gimbal.RotateGimbalTaskRequest;
import eyesatop.controller_tcpip.remote.tasks.taskmanager.TaskManager;
import eyesatop.util.connections.TimeoutInfo;
import eyesatop.util.connections.tcp.requestresponse.RequestResponseException;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

public class TCPGimbal extends MockDroneGimbal {

    private final TCPController controller;

    private final TaskManager<GimbalTaskType> taskManager;

    public TCPGimbal(TCPController controller) {
        this.controller = controller;
        this.taskManager = new TaskManager<>(controller);
    }

    private synchronized UUID startTask(final GimbalTaskRequest taskRequest) throws DroneTaskException {

        try {
            TaskResponse response = controller.getGimbalTaskRequestsClient().sendMessage(taskRequest,3,TimeUnit.SECONDS);

            if(response == null){
                throw new DroneTaskException("Had no Response");
            }

            if(response.getTaskUUID() == null){
                throw new DroneTaskException(response.getError() == null ? "N/A" : response.getError());
            }

            return response.getTaskUUID();
        } catch (InterruptedException e) {
            throw new DroneTaskException("Interrupt");
        } catch (RequestResponseException e) {
            throw new DroneTaskException(e.getErrorMessage());
        }
    }

    public TaskManager<GimbalTaskType> getTaskManager() {
        return taskManager;
    }

    @Override
    public void addGimbalRequest(GimbalRequest gimbalRequest) throws DroneTaskException {

    }

    @Override
    public LookAtPoint lookAtPoint(Location location) throws DroneTaskException {
        UUID uuid = startTask(new LookAtPointRequest(location));
        LookAtPointTaskUpdate taskUpdate = new LookAtPointTaskUpdate(uuid,null, TaskStatus.NOT_STARTED,location);
        return (LookAtPoint) taskManager.getTask(taskUpdate);
    }

    @Override
    public LockYawAtLocation lockYawAtLocation(Location location, double yawDegreeFromLocation) throws DroneTaskException {
        return null;
    }

    @Override
    public LockGimbalAtLocation lockGimbalAtLocation(Location location) throws DroneTaskException {
        UUID uuid = startTask(new LockGimbalAtLocationRequest(location));
        LockGimbalAtLocationTaskUpdate taskUpdate = new LockGimbalAtLocationTaskUpdate(uuid,null, TaskStatus.NOT_STARTED,location);
        return (LockGimbalAtLocation) taskManager.getTask(taskUpdate);
    }

    @Override
    public LockGimbalAtFlightDirection lockGimbalToFlightDirection() throws DroneTaskException {
        return null;
    }

    @Override
    public RotateGimbal rotateGimbal(GimbalRequest request, Integer timeoutInSeconds) throws DroneTaskException {
        UUID uuid = startTask(new RotateGimbalTaskRequest(request,timeoutInSeconds));
        RotateGimbalTaskUpdate taskUpdate = new RotateGimbalTaskUpdate(uuid,null, TaskStatus.NOT_STARTED,request,timeoutInSeconds);
        return (RotateGimbal) taskManager.getTask(taskUpdate);
    }

    @Override
    public Explore explore() throws DroneTaskException {
        return null;
    }
}
