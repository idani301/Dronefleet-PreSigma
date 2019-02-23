package eyesatop.controller_tcpip.remote;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import eyesatop.controller.DroneController;
import eyesatop.controller_tcpip.remote.tcpipcontroller.TCPController;
import eyesatop.util.model.ObservableList;

public class TCPControllerServer {

//    private final TCPIPPipeBroadcastServer pipeBroadcastServer;
    private final ObservableList<DroneController> controllers = new ObservableList<>();
    private final HashMap<Integer,TCPIPPipeBroadcastServerNew> pipeBroadcastServerNewHashMap = new HashMap<>();

    public TCPControllerServer(List<Integer> droneIDs) throws IOException {
//        pipeBroadcastServer = new TCPIPPipeBroadcastServer(droneIDs);

        for(Integer droneID : droneIDs){
            TCPIPRemoteBroadcastEmitterNew remoteBroadcastEmitterNew = new TCPIPRemoteBroadcastEmitterNew(droneID);
            TCPIPPipeBroadcastServerNew broadcastServer = new TCPIPPipeBroadcastServerNew(droneID);
            pipeBroadcastServerNewHashMap.put(droneID,broadcastServer);
            TCPController newController = new TCPController(droneID,broadcastServer.getPipeIP(), broadcastServer.getPingsGrade());
            controllers.add(newController);
        }
    }

    public void setVideoForController(DroneController controller){

        UUID controllerUUID = controller == null ? null : controller.uuid();

        for(DroneController tempController : controllers){
            TCPController tempControllerCasted = (TCPController) tempController;

            if(tempControllerCasted.uuid().equals(controllerUUID)){
                tempControllerCasted.getVideoClient().startVideo();
            }
            else{
                tempControllerCasted.getVideoClient().stopVideo();
            }
        }
    }

    public ObservableList<DroneController> getControllers() {
        return controllers;
    }
}
