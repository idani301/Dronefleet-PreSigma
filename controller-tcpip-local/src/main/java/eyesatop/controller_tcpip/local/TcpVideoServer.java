package eyesatop.controller_tcpip.local;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.VideoPacket;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.util.Removable;
import eyesatop.util.bitmath.ByteMath;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public class TcpVideoServer {

    private final DroneController controller;
    private final int port;
    private final ExecutorService listenExecutor = Executors.newSingleThreadExecutor();
    ServerSocket welcomeSocket;

    public TcpVideoServer(int droneID, DroneController controller) throws IOException {
        this.controller = controller;
        port = ControllerTcpIPCommon.getVideoPort(droneID);
        welcomeSocket = new ServerSocket(port);

        listenExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    startListen();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startListen() throws IOException {
        final Socket connectionSocket = welcomeSocket.accept();
        final DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

        final BlockingQueue<VideoPacket> packets = new LinkedBlockingQueue<>();
        final ExecutorService sendExecutor = Executors.newSingleThreadExecutor();


        final Removable observation = controller.camera().videoBuffer().observe(new Observer<VideoPacket>() {
            @Override
            public void observe(VideoPacket oldValue, VideoPacket newValue, Observation<VideoPacket> observation) {
                if(newValue == null){
                    return;
                }

                packets.add(newValue);
            }
        });

        sendExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){

                    VideoPacket packet = null;
                    try {
                        packet = packets.take();
                    } catch (InterruptedException e) {
                        return;
                    }
                    if(packets.size() > 100){
//                        System.out.println("Clear");
                        packets.clear();
                        continue;
                    }
                    try {
                        outToClient.write(ByteMath.intToByte(packet.getSize()));
                        outToClient.write(packet.getData());
                    } catch (IOException e) {
                        observation.remove();
                        packets.clear();
                        try {
                            connectionSocket.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        listenExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    startListen();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
                        return;
                    }
                }
            }
        });
    }
}
