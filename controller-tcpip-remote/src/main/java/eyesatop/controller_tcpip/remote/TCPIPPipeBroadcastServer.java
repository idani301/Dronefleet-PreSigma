package eyesatop.controller_tcpip.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller_tcpip.common.PipeBroadcastInfo;
import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

import static eyesatop.controller_tcpip.common.ControllerTcpIPCommon.PIPE_BROADCAST_START_PORT;

public class TCPIPPipeBroadcastServer {

    private final ExecutorService listenExecutor = Executors.newSingleThreadExecutor();
    private final HashMap<Integer,Property<String>> droneIDIPMap = new HashMap<>();
    private final HashMap<Integer,Integer> pingsCounter = new HashMap<>();
    private final HashMap<Integer,Property<Integer>> pingsGrade = new HashMap<>();
    private final ExecutorService pingsGradeExecutor = Executors.newSingleThreadExecutor();
    private final List<String> knownPipeIPs = new ArrayList<>();


    public TCPIPPipeBroadcastServer(List<Integer> droneIDs) {

        for(Integer droneID : droneIDs){
            droneIDIPMap.put(droneID,new Property<String>());
            pingsCounter.put(droneID,0);
            pingsGrade.put(droneID,new Property<Integer>());
        }

        listenExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DatagramSocket serverSocket = null;
                try {
                    serverSocket = new DatagramSocket(PIPE_BROADCAST_START_PORT);
                    serverSocket.setBroadcast(true);
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    while(true){
                        serverSocket.receive(receivePacket);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(receiveData);
                        try {
                            PipeBroadcastInfo broadcastInfo = Serialization.JSON.deserialize(inputStream, PipeBroadcastInfo.class);
                            Property<String> ipProperty = droneIDIPMap.get(broadcastInfo.getDroneID());
                            if(ipProperty != null){
                                ipProperty.setIfNew(broadcastInfo.getIp());
                            }
//                            String pipeIP = broadcastInfo.getIp();
//
//                            if(pipeIP != null && !knownPipeIPs.contains(pipeIP)){
//                                knownPipeIPs.add(pipeIP);
//                            }

//                            System.out.println("Got ping from " + broadcastInfo.getDroneID());
                            Integer currentCounter = pingsCounter.get(broadcastInfo.getDroneID());
                            pingsCounter.put(broadcastInfo.getDroneID(),currentCounter + 1);
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
                        for(int droneID : pingsCounter.keySet()){
                            Integer counter = pingsCounter.get(droneID);
                            pingsCounter.put(droneID,0);
                            pingsGrade.get(droneID).set(counter*2);

                            if(counter == 0){
                                droneIDIPMap.get(droneID).setIfNew(null);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
    }

    public HashMap<Integer, Property<Integer>> getPingsGrade() {
        return pingsGrade;
    }

    public HashMap<Integer, Property<String>> getDroneIDIPMap() {
        return droneIDIPMap;
    }

    public List<String> getKnownPipeIPs() {
        return knownPipeIPs;
    }
}
