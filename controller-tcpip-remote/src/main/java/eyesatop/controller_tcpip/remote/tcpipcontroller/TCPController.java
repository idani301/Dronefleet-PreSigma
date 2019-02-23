package eyesatop.controller_tcpip.remote.tcpipcontroller;

import java.io.IOException;
import java.util.UUID;

import eyesatop.controller.beans.SticksPosition;
import eyesatop.controller.mock.MockController;
import eyesatop.controller.mock.MockDroneCamera;
import eyesatop.controller.mock.MockDroneGimbal;
import eyesatop.controller.mock.MockDroneHome;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.tasks.CancelTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.controller_tcpip.common.tasksrequests.camera.CameraTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.flight.FlightTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.gimbal.GimbalTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.home.HomeTaskRequest;
import eyesatop.controller_tcpip.remote.TCPTelemetryServer;
import eyesatop.controller_tcpip.remote.TCPVideoClient;
import eyesatop.controller_tcpip.remote.tasks.tasksupdate.CurrentCameraTaskUpdateServer;
import eyesatop.controller_tcpip.remote.tasks.tasksupdate.CurrentFlightTaskUpdateServer;
import eyesatop.controller_tcpip.remote.tasks.tasksupdate.CurrentGimbalTaskUpdateServer;
import eyesatop.controller_tcpip.remote.tasks.tasksupdate.CurrentHomeTaskUpdateServer;
import eyesatop.util.connections.ConnectionInfo;
import eyesatop.util.connections.tcp.oneway.OneWayStreamConnectionInfo;
import eyesatop.util.connections.tcp.oneway.OneWayStreamTCPClient;
import eyesatop.util.connections.tcp.oneway.OneWayStreamTCPClientSendOnlyLatest;
import eyesatop.util.connections.tcp.requestresponse.RequestResponseTCPClient;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public class TCPController extends MockController {

    private final int droneID;
    private final TCPTelemetryServer tcpTelemetryServer;
//    private final UDPTelemetryServer udpTelemetryServer;
    private final ObservableValue<String> pipeIP;
    private final ObservableValue<Integer> pingGrade;
    private final TCPVideoClient videoClient;

    private final RequestResponseTCPClient<FlightTaskRequest,TaskResponse> flightTaskRequestsClient;
    private final RequestResponseTCPClient<GimbalTaskRequest,TaskResponse> gimbalTaskRequestsClient;
    private final RequestResponseTCPClient<HomeTaskRequest,TaskResponse> homeTaskRequestsClient;
    private final RequestResponseTCPClient<CameraTaskRequest,TaskResponse> cameraTaskRequestsClient;

    private final CurrentFlightTaskUpdateServer currentFlightTaskUpdateServer;
    private final CurrentGimbalTaskUpdateServer currentGimbalTaskUpdateServer;
    private final CurrentCameraTaskUpdateServer currentCameraTaskUpdateServer;
    private final CurrentHomeTaskUpdateServer currentHomeTaskUpdateServer;

    private final OneWayStreamTCPClientSendOnlyLatest<SticksPosition> virtualSticksPositionClient;

    private final OneWayStreamTCPClient<CancelTaskRequest> cancelTasksClient;

    public TCPController(final int droneID, ObservableValue<String> pipeIP, ObservableValue<Integer> pingGrade) throws IOException {
        super(UUID.randomUUID());
        this.droneID = droneID;

        videoClient = new TCPVideoClient(droneID,this,pipeIP);
        tcpTelemetryServer = new TCPTelemetryServer(droneID,this);

        flightTaskRequestsClient = new RequestResponseTCPClient<>(TaskResponse.class,"Flight");
        homeTaskRequestsClient = new RequestResponseTCPClient<>(TaskResponse.class,"Home");
        gimbalTaskRequestsClient = new RequestResponseTCPClient<>(TaskResponse.class,"Gimbal");
        cameraTaskRequestsClient = new RequestResponseTCPClient<>(TaskResponse.class,"Camera");

        currentFlightTaskUpdateServer = new CurrentFlightTaskUpdateServer(droneID,this);
        currentGimbalTaskUpdateServer = new CurrentGimbalTaskUpdateServer(droneID,this);
        currentCameraTaskUpdateServer = new CurrentCameraTaskUpdateServer(droneID,this);
        currentHomeTaskUpdateServer = new CurrentHomeTaskUpdateServer(droneID,this);

        cancelTasksClient = new OneWayStreamTCPClient();

//        udpTelemetryServer = new UDPTelemetryServer(droneID,this);
        this.pipeIP = pipeIP;
        this.pingGrade = pingGrade;

        virtualSticksPositionClient = new OneWayStreamTCPClientSendOnlyLatest<>();

        pipeIP.observe(new Observer<String>() {
            @Override
            public void observe(String oldValue, String newValue, Observation<String> observation) {
                flightTaskRequestsClient.connect(newValue == null ? null : new ConnectionInfo(newValue, ControllerTcpIPCommon.getFlightTaskRequestPort(droneID)));
                gimbalTaskRequestsClient.connect(newValue == null ? null : new ConnectionInfo(newValue, ControllerTcpIPCommon.getGimbalTaskRequestPort(droneID)));
                homeTaskRequestsClient.connect(newValue == null ? null : new ConnectionInfo(newValue, ControllerTcpIPCommon.getHomeTaskRequestPort(droneID)));
                cameraTaskRequestsClient.connect(newValue == null ? null : new ConnectionInfo(newValue, ControllerTcpIPCommon.getCameraTaskRequestPort(droneID)));
                cancelTasksClient.connect(newValue == null ? null : new OneWayStreamConnectionInfo(newValue,ControllerTcpIPCommon.getCancelPort(droneID),null));
                virtualSticksPositionClient.connect(newValue == null ? null : new OneWayStreamConnectionInfo<SticksPosition>(newValue,ControllerTcpIPCommon.getVirtualSticksStartPort(droneID),null));
            }
        }).observeCurrentValue();
    }

    public OneWayStreamTCPClient<CancelTaskRequest> getCancelTasksClient() {
        return cancelTasksClient;
    }

    public RequestResponseTCPClient<FlightTaskRequest, TaskResponse> getFlightTaskRequestsClient() {
        return flightTaskRequestsClient;
    }

    public RequestResponseTCPClient<GimbalTaskRequest, TaskResponse> getGimbalTaskRequestsClient() {
        return gimbalTaskRequestsClient;
    }

    public RequestResponseTCPClient<HomeTaskRequest, TaskResponse> getHomeTaskRequestsClient() {
        return homeTaskRequestsClient;
    }

    public RequestResponseTCPClient<CameraTaskRequest, TaskResponse> getCameraTaskRequestsClient() {
        return cameraTaskRequestsClient;
    }

    public TCPVideoClient getVideoClient() {
        return videoClient;
    }

    @Override
    protected MockDroneCamera createDroneCamera() {
        return new TCPCamera(this);
    }

    @Override
    protected MockDroneGimbal createDroneGimbal() {
        return new TCPGimbal(this);
    }

    @Override
    protected TCPFlightTasks createDroneFlightTasks() {
        return new TCPFlightTasks(this);
    }

    @Override
    protected MockDroneHome createDroneHome() {
        return new TCPDroneHome(this);
    }

    @Override
    public TCPDroneHome droneHome() {
        return (TCPDroneHome) super.droneHome();
    }

    @Override
    public TCPCamera camera() {
        return (TCPCamera) super.camera();
    }

    @Override
    public TCPFlightTasks flightTasks() {
        return (TCPFlightTasks) super.flightTasks();
    }

    @Override
    public TCPGimbal gimbal() {
        return (TCPGimbal) super.gimbal();
    }

    @Override
    public void setVirtualSticksPosition(SticksPosition position) {
        virtualSticksPositionClient.addMessage(position);
    }

    public ObservableValue<Integer> getPingGrade() {
        return pingGrade;
    }

    @Override
    public void close() {

    }
}
