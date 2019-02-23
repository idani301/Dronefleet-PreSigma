package eyesatop.controller_tcpip.local;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.PipeBroadcastInfo;
import eyesatop.util.serialization.Serialization;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

public class TCPIPPipeBroadcastEmitter {

    private static final int INTERVAL_IN_SECONDS = 1;
    private final int droneID;
    private final ExecutorService broadcastExecutor = Executors.newSingleThreadExecutor();

    public TCPIPPipeBroadcastEmitter(final int droneID) {
        this.droneID = droneID;

        broadcastExecutor.execute(new Runnable() {
            @Override
            public void run() {

                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket();
                } catch (SocketException e) {
                    MainLoggerJava.writeError(JavaLoggerType.ERROR,"Socket Exception Error inside broadcast emitter function",e);
                }
                byte[] payload;

                while(true){
                    try {

                        for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {

                                if (interfaceAddress.getBroadcast() != null && !interfaceAddress.getAddress().getHostAddress().equals("127.0.0.1")) {
                                    payload = getControllerBroadcastInfo(interfaceAddress.getAddress().getHostAddress());
                                    socket.setBroadcast(true);
                                    socket.send(new DatagramPacket(payload, payload.length, interfaceAddress.getBroadcast(), ControllerTcpIPCommon.getPipeBroadcastStartPort(droneID)));
                                }
                            }
                        }

                        Thread.sleep(INTERVAL_IN_SECONDS * 1000);
                    } catch (InterruptedException e) {
                        return;
                    } catch (SocketException e) {
                        MainLoggerJava.writeError(JavaLoggerType.ERROR,"Socket Exception Error inside broadcast emitter loop",e);
                    } catch (IOException e) {
                        MainLoggerJava.writeError(JavaLoggerType.ERROR,"IOException Error inside broadcast emitter loop",e);
                    }
                }
            }
        });
    }


    public byte[] getControllerBroadcastInfo(String myIP) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Serialization.JSON.serialize(new PipeBroadcastInfo(droneID,myIP),out);

        return out.toByteArray();
    }

    public void shutdown(){
        broadcastExecutor.shutdownNow();
    }

    public int getDroneID() {
        return droneID;
    }
}
