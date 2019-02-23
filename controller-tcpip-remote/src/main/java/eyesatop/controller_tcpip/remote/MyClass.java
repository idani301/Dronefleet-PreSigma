package eyesatop.controller_tcpip.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.mock.MockController;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller_tcpip.common.telemetryupdate.FlightBlockersUpdate;
import eyesatop.controller_tcpip.remote.tcpipcontroller.TCPController;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;

public class MyClass {
    public static void main(String[] args){
        final Socket clientSocket;

        clientSocket = new Socket();

        ExecutorService stamExecutor = Executors.newSingleThreadExecutor();


        stamExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket.connect(new InetSocketAddress(InetAddress.getByName("192.168.1.23"), 3000));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("done");
            }
        });

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Trying shutdown");
        stamExecutor.shutdownNow();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Trying to close socket");
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
