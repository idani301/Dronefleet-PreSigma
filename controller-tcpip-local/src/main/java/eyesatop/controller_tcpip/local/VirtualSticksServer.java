package eyesatop.controller_tcpip.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.SingleStickPosition;
import eyesatop.controller.beans.SticksPosition;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.util.serialization.Serialization;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

public class VirtualSticksServer {
    private final DroneController controller;
    private final int port;
    private final BlockingQueue<SticksPosition> sticksReceived = new LinkedBlockingQueue<>();
    private final ServerSocket welcomeSocket;
    private final ExecutorService serverExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService setSticksExecutor = Executors.newSingleThreadExecutor();

    private static final SticksPosition ZERO = new SticksPosition(
            new SingleStickPosition(0),
            new SingleStickPosition(0),
            new SingleStickPosition(0),
            new SingleStickPosition(0));


    public VirtualSticksServer(int droneID, final DroneController controller) throws IOException {
        this.controller = controller;
        port = ControllerTcpIPCommon.getVirtualSticksStartPort(droneID);
        welcomeSocket = new ServerSocket(port);

        setSticksExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        SticksPosition position = sticksReceived.take();
                        while(sticksReceived.size() > 0){
                            position = sticksReceived.take();
                        }
                        controller.setVirtualSticksPosition(position);
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
                            SticksPosition newPosition = Serialization.JSON.deserialize(clientSentence,SticksPosition.class);
                            System.out.println(newPosition);
                            sticksReceived.add(newPosition);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            sticksReceived.add(ZERO);
                            connectionSocket.close();
                        } catch (IOException e) {
                            MainLoggerJava.writeError(JavaLoggerType.ERROR,"Virtual sticks server down",e);
                            System.exit(1);
                        }
                    }
                }
            }
        });
    }
}
