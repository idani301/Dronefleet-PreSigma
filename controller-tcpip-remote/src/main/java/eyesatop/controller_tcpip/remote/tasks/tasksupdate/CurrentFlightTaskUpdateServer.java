package eyesatop.controller_tcpip.remote.tasks.tasksupdate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.tasks.CurrentTaskUpdateMessage;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.controller_tcpip.common.tasksrequests.gimbal.GimbalTaskRequest;
import eyesatop.controller_tcpip.remote.tcpipcontroller.TCPController;
import eyesatop.util.serialization.Serialization;

public class CurrentFlightTaskUpdateServer {

    private final TCPController controller;
    private final int port;

    private final ServerSocket welcomeSocket;
    private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService updateExecutor = Executors.newSingleThreadExecutor();

    private final BlockingQueue<CurrentTaskUpdateMessage<FlightTaskType>> currentTaskUpdates = new LinkedBlockingQueue<>();

    public CurrentFlightTaskUpdateServer(int droneID, final TCPController controller) throws IOException {
        this.controller = controller;
        this.port = ControllerTcpIPCommon.getFlightCurrentTaskUpdatePort(droneID);

        welcomeSocket = new ServerSocket(port);

        updateExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        CurrentTaskUpdateMessage<FlightTaskType> currentUpdate = currentTaskUpdates.take();
                        if(currentUpdate.getTaskUpdate() == null){
                            controller.flightTasks().current().set(null);
                        }
                        else{
                            MockDroneTask<FlightTaskType> task = controller.flightTasks().getTasksManager().updateTask(currentUpdate.getTaskUpdate());
                            if(!task.status().value().isTaskDone()) {
                                controller.flightTasks().current().setIfNew(task);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        serverExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(welcomeSocket != null){
                    Socket connectionSocket = null;
                    String clientSentence;
                    try {
                        currentTaskUpdates.clear();
                        controller.flightTasks().getTasksManager().clearMap();
                        controller.flightTasks().current().set(null);

                        connectionSocket = welcomeSocket.accept();
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        while((clientSentence = inFromClient.readLine()) != null){
                            try {
                                CurrentTaskUpdateMessage<FlightTaskType> taskUpdateMessage = Serialization.JSON.deserialize(clientSentence, CurrentTaskUpdateMessage.class);
                                currentTaskUpdates.add(taskUpdateMessage);
                            }
                            catch (IOException e){
//                                System.out.println("Was unable to deserialize the message : " + clientSentence);
                                e.printStackTrace();
                            }
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
