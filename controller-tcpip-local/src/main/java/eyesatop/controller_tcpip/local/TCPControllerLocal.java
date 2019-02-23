package eyesatop.controller_tcpip.local;

import java.io.IOException;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.tasks.TaskUpdate;
import eyesatop.controller_tcpip.common.tasks.camera.CameraTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.flight.FlightTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.gimbal.GimbalTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.home.HomeTaskUpdate;
import eyesatop.controller_tcpip.local.tasksrequests.TCPCameraTaskRequestServer;
import eyesatop.controller_tcpip.local.tasksrequests.TCPFlightTaskRequestServer;
import eyesatop.controller_tcpip.local.tasksrequests.TCPGimbalTaskRequestServer;
import eyesatop.controller_tcpip.local.tasksrequests.TCPHomeTaskRequestServer;
import eyesatop.controller_tcpip.local.tasksupdate.CurrentTaskUpdateClientManager;
import eyesatop.util.model.ObservableValue;

public class TCPControllerLocal {

    private final TCPIPPipeBroadcastEmitter pipeBroadcastEmitter;
    private final TCPIPRemoteBroadcastServer remoteBroadcastServer;
    private final TCPTelemetryClientManager telemetryClient;
//    private final UDPTelemetryClient udpTelemetryClient;
    private final DroneController controller;
    private final TcpVideoServer videoServer;

    private final TCPFlightTaskRequestServer flightTaskRequestServer;
    private final TCPHomeTaskRequestServer homeTaskRequestServer;
    private final TCPCameraTaskRequestServer cameraTaskRequestServer;
    private final TCPGimbalTaskRequestServer gimbalTaskRequestServer;

    private final CurrentTaskUpdateClientManager<FlightTaskType> currentFlightTaskUpdateClient;
    private final CurrentTaskUpdateClientManager<CameraTaskType> currentCameraTaskUpdateClient;
    private final CurrentTaskUpdateClientManager<GimbalTaskType> currentGimbalTaskUpdateClient;
    private final CurrentTaskUpdateClientManager<HomeTaskType> currentHomeTaskUpdateClient;

    private final CancelTasksServer cancelTasksServer;
    private final VirtualSticksServer virtualSticksServer;

    public TCPControllerLocal(DroneController controller, final int droneID) throws IOException {
        this.controller = controller;
        pipeBroadcastEmitter = new TCPIPPipeBroadcastEmitter(droneID);
        remoteBroadcastServer = new TCPIPRemoteBroadcastServer(false, droneID);
        telemetryClient = new TCPTelemetryClientManager(droneID,controller,remoteBroadcastServer.getRemoteIP());
//        udpTelemetryClient = new UDPTelemetryClient(droneID,controller);

        videoServer = new TcpVideoServer(droneID,controller);

        flightTaskRequestServer = new TCPFlightTaskRequestServer(controller,droneID);
        homeTaskRequestServer = new TCPHomeTaskRequestServer(controller,droneID);
        cameraTaskRequestServer = new TCPCameraTaskRequestServer(controller,droneID);
        gimbalTaskRequestServer = new TCPGimbalTaskRequestServer(controller,droneID);

        currentFlightTaskUpdateClient = new CurrentTaskUpdateClientManager<FlightTaskType>(droneID,controller,remoteBroadcastServer.getRemoteIP()) {
            @Override
            protected int getPort() {
                return ControllerTcpIPCommon.getFlightCurrentTaskUpdatePort(droneID);
            }

            @Override
            protected ObservableValue<DroneTask<FlightTaskType>> currentTask(DroneController controller) {
                return controller.flightTasks().current();
            }

            @Override
            protected TaskUpdate<FlightTaskType> getFromTask(DroneTask<FlightTaskType> task) {
                return FlightTaskUpdate.getFromTask(task);
            }
        };

        currentCameraTaskUpdateClient = new CurrentTaskUpdateClientManager<CameraTaskType>(droneID,controller,remoteBroadcastServer.getRemoteIP()) {
            @Override
            protected int getPort() {
                return ControllerTcpIPCommon.getCameraCurrentTaskUpdatePort(droneID);
            }

            @Override
            protected ObservableValue<DroneTask<CameraTaskType>> currentTask(DroneController controller) {
                return controller.camera().currentTask();
            }

            @Override
            protected TaskUpdate<CameraTaskType> getFromTask(DroneTask<CameraTaskType> task) {
                return CameraTaskUpdate.getFromTask(task);
            }
        };
        currentHomeTaskUpdateClient = new CurrentTaskUpdateClientManager<HomeTaskType>(droneID,controller,remoteBroadcastServer.getRemoteIP()) {
            @Override
            protected int getPort() {
                return ControllerTcpIPCommon.getHomeCurrentTaskUpdatePort(droneID);
            }

            @Override
            protected ObservableValue<DroneTask<HomeTaskType>> currentTask(DroneController controller) {
                return controller.droneHome().currentTask();
            }

            @Override
            protected TaskUpdate<HomeTaskType> getFromTask(DroneTask<HomeTaskType> task) {
                return HomeTaskUpdate.getFromTask(task);
            }
        };
        currentGimbalTaskUpdateClient = new CurrentTaskUpdateClientManager<GimbalTaskType>(droneID,controller,remoteBroadcastServer.getRemoteIP()) {
            @Override
            protected int getPort() {
                return ControllerTcpIPCommon.getGimbalCurrentTaskUpdatePort(droneID);
            }

            @Override
            protected ObservableValue<DroneTask<GimbalTaskType>> currentTask(DroneController controller) {
                return controller.gimbal().currentTask();
            }

            @Override
            protected TaskUpdate<GimbalTaskType> getFromTask(DroneTask<GimbalTaskType> task) {
                return GimbalTaskUpdate.getFromTask(task);
            }
        };

        cancelTasksServer = new CancelTasksServer(droneID,controller);

        virtualSticksServer = new VirtualSticksServer(droneID,controller);
    }

    public ObservableValue<String> remoteIP(){
        return remoteBroadcastServer.getRemoteIP();
    }
}
