package eyesatop.controller_tcpip.remote;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eyesatop.controller.beans.VideoCodecType;
import eyesatop.controller.beans.VideoPacket;
import eyesatop.controller.mock.MockController;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.util.RemovableCollection;
import eyesatop.util.bitmath.ByteMath;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public class TCPVideoClient {

//    private final ArrayList<Byte> dataSoFar = new ArrayList<>();
    private final MockController controller;
    private final int port;
    private final ObservableValue<String> pipeIP;
    private final RemovableCollection pipeIPRemovable = new RemovableCollection();
    private final ExecutorService injectExecutor = Executors.newSingleThreadExecutor();
    private final BlockingQueue<byte[]> dataToInjectQueue = new LinkedBlockingQueue<>();
    private int packetsNumber = 0;

//    private final ExecutorService stamExecutor = Executors.newSingleThreadExecutor();

    boolean listenToVideo = false;

    private Lock lock = new ReentrantLock();

    private ExecutorService receiveVideoExecutor = null;

    Socket clientSocket = new Socket();

    public TCPVideoClient(int droneID, final MockController controller, ObservableValue<String> pipeIP) {
        this.controller = controller;
        port = ControllerTcpIPCommon.getVideoPort(droneID);
        this.pipeIP = pipeIP;
    }

    public void startVideo(){

        lock.lock();

        listenToVideo = true;

        pipeIPRemovable.add(
                pipeIP.observe(new Observer<String>() {
                    @Override
                    public void observe(String oldValue, String newValue, Observation<String> observation) {
                        lock.lock();
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        lock.unlock();
                    }
                })
        );
        receiveVideoExecutor = Executors.newSingleThreadExecutor();
        receiveVideoExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(listenToVideo){

                    String currentIP = pipeIP.value();

                    if(currentIP == null){
                        try {
                            pipeIP.notNull().awaitTrue();
                            continue;
                        } catch (InterruptedException e) {
                            return;
                        }
                    }

                    clientSocket = new Socket();
                    try {
                        clientSocket.connect(new InetSocketAddress(InetAddress.getByName(currentIP), port));
                        InputStream clientInputStream = clientSocket.getInputStream();
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(clientInputStream);

                        byte[] sizeToRead = new byte[2];

                        while(true) {

                            sizeToRead[0] = (byte) bufferedInputStream.read();
                            sizeToRead[1] = (byte) bufferedInputStream.read();

                            int numOfBytesToRead = ByteMath.fromByteArrayToInt(sizeToRead);
//                            System.out.println("Num of Bytes to Read : " + +numOfBytesToRead);

                            final byte[] dataToSend = new byte[numOfBytesToRead];
                            int bytesLeftToRead = numOfBytesToRead;
                            int bytesJustRead;

                            bytesJustRead = bufferedInputStream.read(dataToSend);
                            if(bytesJustRead == -1){
                                bufferedInputStream.close();
                                throw new IOException();
                            }

                            bytesLeftToRead = numOfBytesToRead - bytesJustRead;
                            while(bytesLeftToRead > 0){
                                byte[] tempArray = new byte[bytesLeftToRead];
                                bytesJustRead = bufferedInputStream.read(tempArray);
                                if(bytesJustRead == -1){
                                    bufferedInputStream.close();
                                    throw new IOException();
                                }
                                System.arraycopy(tempArray,0,dataToSend,dataToSend.length-bytesLeftToRead,bytesJustRead);
                                bytesLeftToRead -= bytesJustRead;
                            }

                            controller.camera().videoBuffer().set(new VideoPacket(VideoCodecType.H264,dataToSend,dataToSend.length));
                        }

//                        while((numOfBytesRead = clientInputStream.read(dataToRead)) != -1){
//
//                            final int finalNumOfBytesRead = numOfBytesRead;
//                            final byte[] finalDataToRead = dataToRead;
//
//                            for(int i = 0; i< finalNumOfBytesRead; i++){
//                                byte readedByte = finalDataToRead[i];
//                                addToDataSoFar(readedByte);
//                            }
//                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        return;
//                    }
                }
            }
        });

        lock.unlock();
    }

    public void stopVideo(){

        lock.lock();

        listenToVideo = false;
        pipeIPRemovable.remove();
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(receiveVideoExecutor != null){
            receiveVideoExecutor.shutdownNow();
        }

        lock.unlock();
    }

//    private void addToDataSoFar(byte newByte){
//        if(dataSoFar.size() >= 3 && newByte == 10){
//            if(dataSoFar.get(dataSoFar.size()-2) == 10 && dataSoFar.get(dataSoFar.size()-1) == 10){
//                byte[] newArray = new byte[dataSoFar.size()-2];
//                for(int i=0; i< dataSoFar.size()-2; i++){
//                    newArray[i] = dataSoFar.get(i);
//                }
//                controller.camera().videoBuffer().set(new VideoPacket(VideoCodecType.H264,newArray,newArray.length));
//                dataSoFar.clear();
//            }
//            else{
//                dataSoFar.add((byte) newByte);
//            }
//        }
//        else{
//            dataSoFar.add((byte) newByte);
//        }
//    }
}
