package eyesatop.util.connections.tcp.oneway;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;

public class OneWayStreamTCPClientSendOnlyLatest<T> {
    private final BlockingQueue<T> messagesToSend = new LinkedBlockingQueue<>();
    private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();
    private boolean isAlive = true;
    private final Property<OneWayStreamConnectionInfo<T>> connectionInfo = new Property<>();
    private Socket clientSocket = new Socket();

    public OneWayStreamTCPClientSendOnlyLatest() {

        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (isAlive) {

                    messagesToSend.clear();
                    OneWayStreamConnectionInfo<T> currentConnectionInfo = connectionInfo.value();
                    if (currentConnectionInfo == null) {
                        boolean dropMessage = true;
                        while (dropMessage) {
                            try {
                                T messageToDrop = messagesToSend.take();
                            } catch (InterruptedException e) {
                                dropMessage = false;
                            }
                        }
                        continue;
                    }

                    while (currentConnectionInfo.equals(connectionInfo.value())) {
                        OneWayStreamCallback<T> callback = currentConnectionInfo.getCallback();

                        clientSocket = new Socket();
                        try {
                            clientSocket.connect(new InetSocketAddress(InetAddress.getByName(currentConnectionInfo.getIp()), currentConnectionInfo.getPort()), 2000);

                            if(callback != null){
                                callback.onConnectionActive(messagesToSend);
                            }

                            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                            while (clientSocket.isConnected()) {

                                T messageToSend = messagesToSend.take();

                                while(messagesToSend.size() > 0){
                                    messageToSend = messagesToSend.take();
                                }

                                String messageAsString = null;
                                try {
                                    messageAsString = Serialization.JSON.serialize(messageToSend);
                                } catch (IOException e) {
                                    continue;
                                }
                                outputStream.writeBytes(messageAsString + "\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {

                            if(callback != null){
                                callback.onConnectionLost(messagesToSend);
                            }

                            if (currentConnectionInfo.equals(connectionInfo.value())) {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                clearQueueIfTooBig();
                            }
                        }
                    }
                }
            }
        });
    }

    public synchronized void connect(OneWayStreamConnectionInfo<T> connectionInfo){
        this.connectionInfo.set(connectionInfo);

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionExecutor.shutdownNow();
    }

    public void addMessage(T message){
        messagesToSend.add(message);
    }

    private void clearQueueIfTooBig(){
        if(messagesToSend.size() >= 100){
            while(messagesToSend.size() > 1){
                try {
                    messagesToSend.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
