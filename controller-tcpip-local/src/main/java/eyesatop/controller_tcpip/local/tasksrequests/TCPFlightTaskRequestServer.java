package eyesatop.controller_tcpip.local.tasksrequests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.DroneController;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.controller_tcpip.common.tasksrequests.flight.FlightTaskRequest;
import eyesatop.controller_tcpip.common.telemetryupdate.ControllerUpdate;
import eyesatop.util.serialization.Serialization;

public class TCPFlightTaskRequestServer {

    private final DroneController controller;
    private final int port;

    private final ServerSocket welcomeSocket;
    private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();

    public TCPFlightTaskRequestServer(final DroneController controller, int droneID) throws IOException {
        this.controller = controller;
        port = ControllerTcpIPCommon.getFlightTaskRequestPort(droneID);

        welcomeSocket = new ServerSocket(port);

        serverExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(welcomeSocket != null){
                    Socket connectionSocket = null;
                    String clientSentence;
                    try {

                        connectionSocket = welcomeSocket.accept();
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        while((clientSentence = inFromClient.readLine()) != null){
                            FlightTaskRequest flightTaskRequest = Serialization.JSON.deserialize(clientSentence,FlightTaskRequest.class);
                            TaskResponse taskResponse = flightTaskRequest.perform(controller);
                            outToClient.writeBytes(Serialization.JSON.serialize(taskResponse) + "\n");
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
}
