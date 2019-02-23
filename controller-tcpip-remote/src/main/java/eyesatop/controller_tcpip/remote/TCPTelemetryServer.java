package eyesatop.controller_tcpip.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.mock.MockController;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.telemetryupdate.ControllerUpdate;
import eyesatop.util.serialization.Serialization;

public class TCPTelemetryServer {

    private final int port;
    private final MockController controller;
    private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    private final ServerSocket welcomeSocket;

    public TCPTelemetryServer(int droneID, final MockController controller) throws IOException {
        this.controller = controller;

        port = ControllerTcpIPCommon.getTelemetryPortMap(droneID);
        welcomeSocket = new ServerSocket(port);

        serverExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String clientSentence;
                while(welcomeSocket != null){
                    Socket connectionSocket = null;

                    try {
                        clearFields();
                        connectionSocket = welcomeSocket.accept();
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        while((clientSentence = inFromClient.readLine()) != null){
                            ControllerUpdate controllerUpdate = Serialization.JSON.deserialize(clientSentence,ControllerUpdate.class);
                            controllerUpdate.updateController(controller);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            connectionSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void clearFields(){

        controller.flightTasks().tasksBlockers().clear();
        controller.camera().tasksBlockers().clear();
        controller.gimbal().tasksBlockers().clear();
        controller.droneHome().taskBlockers().clear();

        controller.telemetry().set(null);
        controller.gimbal().gimbalState().set(null);
        controller.lookAtLocation().set(null);
        controller.aboveGroundAltitude().set(null);
        controller.aboveSeaAltitude().set(null);
        controller.camera().mediaStorage().set(null);
        controller.rcSignalStrengthPercent().set(null);
        controller.model().setIfNew(null);
        controller.droneBattery().setIfNew(null);
        controller.camera().mode().setIfNew(null);
        controller.connectivity().setIfNew(null);
        controller.flightMode().setIfNew(null);
        controller.gps().setIfNew(null);
        controller.droneHome().homeLocation().setIfNew(null);
        controller.flying().setIfNew(null);
        controller.camera().recording().setIfNew(null);
        controller.camera().isShootingPhoto().setIfNew(null);
        controller.camera().isZoomSupported().setIfNew(null);
        controller.droneHome().limitationActive().setIfNew(null);
        controller.droneHome().maxAltitudeFromTakeOffLocation().setIfNew(null);
        controller.droneHome().maxDistanceFromHome().setIfNew(null);
        controller.motorsOn().setIfNew(null);
        controller.rcBattery().setIfNew(null);
        controller.rcInFunctionMode().setIfNew(null);
        controller.camera().recordTimeInSeconds().setIfNew(null);
        controller.droneHome().returnHomeAltitude().setIfNew(null);
        controller.camera().shootPhotoIntervalValue().setIfNew(null);
        controller.droneHome().takeOffDTM().setIfNew(null);
        controller.camera().zoomInfo().setIfNew(null);
        controller.camera().zoomLevel().setIfNew(null);
        controller.camera().streamState().setIfNew(null);
    }
}
