package eyesatop.controller_tcpip.remote;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.RemoteBroadcastInfo;
import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

import static eyesatop.controller_tcpip.common.ControllerTcpIPCommon.REMOTE_BROADCAST_START_PORT;

public class TCPIPRemoteBroadcastEmitterNew {
    private final ExecutorService broadcastExecutor = Executors.newSingleThreadExecutor();
    private static final int INTERVAL_IN_SECONDS = 1;


    public TCPIPRemoteBroadcastEmitterNew(final int droneID) {

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
                                    socket.send(new DatagramPacket(payload, payload.length, interfaceAddress.getBroadcast(), ControllerTcpIPCommon.getRemoteBroadcastStartPort(droneID)));

//                                    List<String> knownIpsValue = knownIPS.value();
//                                    if(knownIpsValue != null){
//
//                                        lastKnownIPSentTime = System.currentTimeMillis();
//
//                                        for(String knownIP : knownIpsValue){
//                                            socket.send(new DatagramPacket(payload,payload.length, InetAddress.getByName(knownIP),REMOTE_BROADCAST_START_PORT));
//                                        }
//                                    }
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
        Serialization.JSON.serialize(new RemoteBroadcastInfo(myIP),out);

        return out.toByteArray();
    }
}
