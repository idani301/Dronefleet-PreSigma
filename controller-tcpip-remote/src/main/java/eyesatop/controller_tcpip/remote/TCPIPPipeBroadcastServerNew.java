package eyesatop.controller_tcpip.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.PipeBroadcastInfo;
import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

public class TCPIPPipeBroadcastServerNew {

    private final ExecutorService listenExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService pingsGradeExecutor = Executors.newSingleThreadExecutor();
    private Integer pingsCounter = 0;
    private final Property<Integer> pingsGrade = new Property<>();
    private final Property<String> pipeIP = new Property<>();

    public TCPIPPipeBroadcastServerNew(final int droneID) {

        listenExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket serverSocket = null;
                try {
                    serverSocket = new DatagramSocket(ControllerTcpIPCommon.getPipeBroadcastStartPort(droneID));
                    serverSocket.setBroadcast(true);
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    while(true){
                        serverSocket.receive(receivePacket);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(receiveData);
                        try {
                            PipeBroadcastInfo broadcastInfo = Serialization.JSON.deserialize(inputStream, PipeBroadcastInfo.class);
                            pipeIP.setIfNew(broadcastInfo.getIp());

//                            String pipeIP = broadcastInfo.getIp();
//
//                            if(pipeIP != null && !knownPipeIPs.contains(pipeIP)){
//                                knownPipeIPs.add(pipeIP);
//                            }

//                            System.out.println("Got ping from " + broadcastInfo.getDroneID());
                            pingsCounter++;
                        }
                        catch (IOException e){
                            MainLoggerJava.writeError(JavaLoggerType.ERROR,"Error with deserilize broadcast info : ",e);
                        }
                    }
                } catch (SocketException e) {
                    MainLoggerJava.writeError(JavaLoggerType.ERROR,"Socket Error inside broadcast server",e);
                } catch (IOException e) {
                    MainLoggerJava.writeError(JavaLoggerType.ERROR,"IOException Error inside broadcast server",e);
                }
            }
        });

        pingsGradeExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
//                        long beforeSleepTime = System.currentTimeMillis();
                        Thread.sleep(1000 * 5);
//                        long afterSleepTime = System.currentTimeMillis();
//                        System.out.println("Sleeped for " + (afterSleepTime - beforeSleepTime));
                        if(pingsCounter == 0){
                            pipeIP.setIfNew(null);
                        }
                        pingsGrade.set(pingsCounter*2);
                        pingsCounter = 0;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
    }

    public Property<Integer> getPingsGrade() {
        return pingsGrade;
    }

    public Property<String> getPipeIP() {
        return pipeIP;
    }

}
