package eyesatop.util.connections.tcp.requestresponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import eyesatop.util.connections.ConnectionInfo;
import eyesatop.util.connections.TimeoutInfo;
import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;
import logs.JavaLoggerType;
import logs.MainLoggerJava;

public class RequestResponseTCPClient<T,S> {

    private final String name;
    private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();

    private final BlockingQueue<RequestResponseMessageInfo<T,S>> requestsToSend = new LinkedBlockingQueue<>();

    private boolean isAlive = true;
    private final Property<ConnectionInfo> connectionInfo = new Property<>();
    private Socket clientSocket = new Socket();
    private final int maxQueueSize;

    public RequestResponseTCPClient(Class<S> responseType,String name){
        this(responseType, 1000,name);
    }

    public RequestResponseTCPClient(final Class<S> responseType, final int maxQueueSize,String name){
        this.maxQueueSize = maxQueueSize;
        this.name = name;

        final String finalName = name;
        connectionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(isAlive){

                    ConnectionInfo currentConnectionInfo = connectionInfo.value();
                    if(currentConnectionInfo == null){
                        boolean dropMessage = true;
                        while(dropMessage) {
                            try {
                                RequestResponseMessageInfo<T,S> messageToDrop = requestsToSend.take();
                                messageToDrop.getError().set("Dropping Messages since no server");
                                messageToDrop.getLatch().countDown();
                            } catch (InterruptedException e) {
                                dropMessage = false;
                            }
                        }
                        continue;
                    }

                    clearQueue("Clearing before connecting to new socket");

                    while(currentConnectionInfo.equals(connectionInfo.value())){
                        clientSocket = new Socket();
                        try {
                            clientSocket.connect(new InetSocketAddress(InetAddress.getByName(currentConnectionInfo.getIp()),currentConnectionInfo.getPort()),2000);
                            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                            while(clientSocket.isConnected()){

                                RequestResponseMessageInfo<T,S> messageInfo = requestsToSend.take();

                                String messageAsString = null;
                                try {
                                    messageAsString = Serialization.JSON.serialize(messageInfo.getRequest());
                                }
                                catch (IOException e){
                                    messageInfo.getError().set("Unable to Serialize the request");
                                    messageInfo.getLatch().countDown();
                                    continue;
                                }
                                try {
                                    outputStream.writeBytes(messageAsString + "\n");
                                }
                                catch (IOException e){
                                    messageInfo.getError().set("IO Exception when trying to send the request");
                                    messageInfo.getLatch().countDown();
                                    throw new IOException(e);
                                }
                                String serverAnswer = null;
                                try {
                                    serverAnswer = inFromServer.readLine();
                                    if(serverAnswer == null){
                                        throw new IOException("Got null from Response");
                                    }
                                }
                                catch (IOException e){
                                    messageInfo.getError().set("IO Exception when trying to read the response");
                                    messageInfo.getLatch().countDown();
                                    throw new IOException(e);
                                }

                                try {
                                    S response = Serialization.JSON.deserialize(serverAnswer, responseType);
                                    messageInfo.getResponse().set(response);
                                    messageInfo.getLatch().countDown();
                                }
                                catch (IOException e){
                                    messageInfo.getError().set("Unable to deserialize the server answer");
                                    messageInfo.getLatch().countDown();
                                    e.printStackTrace();
                                }

                                clearQueueIfTooBig();
                            }
                        } catch (IOException e) {
                            MainLoggerJava.writeError(JavaLoggerType.ERROR,"Got Exception with " + finalName + " Request Response",e);
                        }
                        catch (InterruptedException e){
                        }
                        finally {
                            if(currentConnectionInfo.equals(connectionInfo.value())){
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
        name = null;
    }

    private void clearQueueIfTooBig(){
        if(requestsToSend.size() >= maxQueueSize){
            clearQueue("Queue is too big");
        }
    }

    private void clearQueue(String reason){
        while(requestsToSend.size() > 0){
            try {
                RequestResponseMessageInfo<T,S> requestInfo = requestsToSend.take();
                requestInfo.getError().set(reason);
                requestInfo.getLatch().countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void connect(ConnectionInfo connectionInfo){
        this.connectionInfo.set(connectionInfo);
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionExecutor.shutdownNow();
    }

    public S sendMessage(T message) throws InterruptedException, RequestResponseException {

        CountDownLatch latch = new CountDownLatch(1);
        RequestResponseMessageInfo<T,S> messageInfo = new RequestResponseMessageInfo<>(message);

        requestsToSend.add(messageInfo);
        messageInfo.getLatch().await();

        if(!messageInfo.getError().isNull()){
            throw new RequestResponseException(messageInfo.getError().value());
        }

        return messageInfo.getResponse().value();

    }

    public S sendMessage(T message, long timeout, TimeUnit timeUnit) throws InterruptedException,RequestResponseException {

        RequestResponseMessageInfo<T,S> messageInfo = new RequestResponseMessageInfo<>(message);

        requestsToSend.add(messageInfo);

        if(!messageInfo.getLatch().await(timeout,timeUnit)){
            throw new RequestResponseException("Timeout");
        }

        if(!messageInfo.getError().isNull()){
            throw new RequestResponseException(messageInfo.getError().value());
        }

        return messageInfo.getResponse().value();
    }

    public void shutdown(){

        isAlive = false;
        connectionInfo.set(null);

        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectionExecutor.shutdownNow();
    }
}
