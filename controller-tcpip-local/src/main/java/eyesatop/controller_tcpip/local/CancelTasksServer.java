package eyesatop.controller_tcpip.local;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.tasks.CancelTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.controller_tcpip.common.tasksrequests.flight.FlightTaskRequest;
import eyesatop.util.serialization.Serialization;

public class CancelTasksServer {

    private final DroneController controller;
    private final int port;

    private final ServerSocket welcomeSocket;
    private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService cancelExecutor = Executors.newSingleThreadExecutor();

    private final BlockingQueue<UUID> uuidsToCancel = new LinkedBlockingQueue<>();

    public CancelTasksServer(int droneID, final DroneController controller) throws IOException {
        this.controller = controller;
        port = ControllerTcpIPCommon.getCancelPort(droneID);

        welcomeSocket = new ServerSocket(port);

        cancelExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        UUID uuid = uuidsToCancel.take();

                        DroneTask<FlightTaskType> flightTask = controller.flightTasks().current().value();
                        if(shouldBeCancelled(flightTask,uuid)){
                            try {
                                flightTask.cancel();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        DroneTask gimbalTask = controller.gimbal().currentTask().value();
                        if(shouldBeCancelled(gimbalTask,uuid)){
                            try{
                                gimbalTask.cancel();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        DroneTask homeTask = controller.droneHome().currentTask().value();
                        if(shouldBeCancelled(homeTask,uuid)){
                            try{
                                homeTask.cancel();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        DroneTask cameraTask = controller.camera().currentTask().value();
                        if(shouldBeCancelled(cameraTask,uuid)){
                            try{
                                cameraTask.cancel();
                            }
                            catch (Exception e){
                                e.printStackTrace();
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
                        connectionSocket = welcomeSocket.accept();
                        BufferedReader inFromClient =
                                new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        while((clientSentence = inFromClient.readLine()) != null){
                            CancelTaskRequest cancelTaskRequest = Serialization.JSON.deserialize(clientSentence,CancelTaskRequest.class);
                            uuidsToCancel.add(cancelTaskRequest.getUuid());
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

    private boolean shouldBeCancelled(DroneTask task,UUID uuid){

        if(task == null){
            return false;
        }

        if(((TaskStatus)task.status().value()).isTaskDone()){
            return false;
        }

        if(!uuid.equals(task.uuid())){
            return false;
        }

        return true;
    }
}
