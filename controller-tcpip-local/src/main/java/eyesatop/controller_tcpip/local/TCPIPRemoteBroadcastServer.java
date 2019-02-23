package eyesatop.controller_tcpip.local;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.RemoteBroadcastInfo;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

import static eyesatop.controller_tcpip.common.ControllerTcpIPCommon.REMOTE_BROADCAST_START_PORT;

public class TCPIPRemoteBroadcastServer {

    private final ExecutorService listenExecutor = Executors.newSingleThreadExecutor();
    private final Property<String> remoteIP = new Property<>();

    private final ExecutorService resetExecutor = Executors.newSingleThreadExecutor();
    private final Property<Long> lastTimeGotPing = new Property<>(System.currentTimeMillis());
    private final int droneID;

    public TCPIPRemoteBroadcastServer(boolean isSimulator, final int droneID) {
        this.droneID = droneID;

        if(isSimulator){
            remoteIP.set("127.0.0.1");
            return;
        }

        resetExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(System.currentTimeMillis() - lastTimeGotPing.value() > 4000){
                        remoteIP.setIfNew(null);
                    }
                }
            }
        });

        listenExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket serverSocket = null;
                try {
                    serverSocket = new DatagramSocket(ControllerTcpIPCommon.getRemoteBroadcastStartPort(droneID));
                    serverSocket.setBroadcast(true);
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    while(true){
                        serverSocket.receive(receivePacket);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(receiveData);
                        try {
                            RemoteBroadcastInfo broadcastInfo = Serialization.JSON.deserialize(inputStream, RemoteBroadcastInfo.class);

                            remoteIP.setIfNew(broadcastInfo.getIp());
                            lastTimeGotPing.set(System.currentTimeMillis());
                        }
                        catch (IOException e){
                            MainLoggerJava.writeError(JavaLoggerType.ERROR,"Error with deserilize remote broadcast info : ",e);
                        }
                    }
                } catch (SocketException e) {
                    MainLoggerJava.writeError(JavaLoggerType.ERROR,"Socket Error inside remote broadcast server",e);
                } catch (IOException e) {
                    MainLoggerJava.writeError(JavaLoggerType.ERROR,"IOException Error inside remote  broadcast server",e);
                }
            }
        });
    }

    public ObservableValue<String> getRemoteIP() {
        return remoteIP;
    }
}
