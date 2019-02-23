package eyesatop.controller_tcpip.remote.tcpipcontroller;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.mock.MockDroneHome;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import eyesatop.controller.tasks.home.SetLimitationEnabled;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;
import eyesatop.controller_tcpip.common.tasks.home.SetHomeLocationTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.home.SetLimitationEnabledTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.home.SetMaxAltitudeTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.home.SetMaxDistanceFromHomeTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.home.SetReturnHomeAltitudeTaskUpdate;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.controller_tcpip.common.tasksrequests.home.HomeTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.home.SetHomeLocationRequest;
import eyesatop.controller_tcpip.common.tasksrequests.home.SetLimitationEnabledRequest;
import eyesatop.controller_tcpip.common.tasksrequests.home.SetMaxAltitudeRequest;
import eyesatop.controller_tcpip.common.tasksrequests.home.SetMaxDistanceFromHomeRequest;
import eyesatop.controller_tcpip.common.tasksrequests.home.SetReturnHomeAltitudeRequest;
import eyesatop.controller_tcpip.remote.tasks.taskmanager.TaskManager;
import eyesatop.util.connections.TimeoutInfo;
import eyesatop.util.connections.tcp.requestresponse.RequestResponseException;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

public class TCPDroneHome extends MockDroneHome {

    private final TCPController controller;

    private final TaskManager<HomeTaskType> taskManager;

    public TCPDroneHome(TCPController controller) {

        this.controller = controller;
        this.taskManager = new TaskManager<>(controller);
    }

    private synchronized UUID startTask(final HomeTaskRequest taskRequest) throws DroneTaskException {
        try {
            TaskResponse response = controller.getHomeTaskRequestsClient().sendMessage(taskRequest,3,TimeUnit.SECONDS);

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

    public TaskManager<HomeTaskType> getTaskManager() {
        return taskManager;
    }

    @Override
    public SetReturnHomeAltitude setReturnHomeAltitude(double altitude) throws DroneTaskException {
        UUID uuid = startTask(new SetReturnHomeAltitudeRequest(altitude));
        SetReturnHomeAltitudeTaskUpdate taskUpdate = new SetReturnHomeAltitudeTaskUpdate(uuid,null, TaskStatus.NOT_STARTED,altitude);
        return (SetReturnHomeAltitude) taskManager.getTask(taskUpdate);
    }

    @Override
    public SetHomeLocation setHomeLocation(Location newHomeLocation) throws DroneTaskException {
        UUID uuid = startTask(new SetHomeLocationRequest(newHomeLocation));
        SetHomeLocationTaskUpdate taskUpdate = new SetHomeLocationTaskUpdate(uuid,null, TaskStatus.NOT_STARTED,newHomeLocation);
        return (SetHomeLocation) taskManager.getTask(taskUpdate);
    }

    @Override
    public SetLimitationEnabled setFlightLimitationEnabled(boolean enabled) throws DroneTaskException {
        UUID uuid = startTask(new SetLimitationEnabledRequest(enabled));
        SetLimitationEnabledTaskUpdate taskUpdate = new SetLimitationEnabledTaskUpdate(uuid,null, TaskStatus.NOT_STARTED,enabled);
        return (SetLimitationEnabled) taskManager.getTask(taskUpdate);
    }

    @Override
    public SetMaxDistanceFromHome setMaxDistanceFromHome(double maxDistanceFromHome) throws DroneTaskException {
        UUID uuid = startTask(new SetMaxDistanceFromHomeRequest(maxDistanceFromHome));
        SetMaxDistanceFromHomeTaskUpdate taskUpdate = new SetMaxDistanceFromHomeTaskUpdate(uuid,null, TaskStatus.NOT_STARTED,maxDistanceFromHome);
        return (SetMaxDistanceFromHome) taskManager.getTask(taskUpdate);
    }

    @Override
    public SetMaxAltitudeFromHomeLocation setMaxAltitudeFromTakeOffLocation(double maxAltitudeFromTakeOffLocation) throws DroneTaskException {
        UUID uuid = startTask(new SetMaxAltitudeRequest(maxAltitudeFromTakeOffLocation));
        SetMaxAltitudeTaskUpdate taskUpdate = new SetMaxAltitudeTaskUpdate(uuid,null, TaskStatus.NOT_STARTED,maxAltitudeFromTakeOffLocation);
        return (SetMaxAltitudeFromHomeLocation) taskManager.getTask(taskUpdate);
    }
}
