package eyesatop.controller_tcpip.local.tasksupdate;

import java.util.concurrent.BlockingQueue;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.tasks.CurrentTaskUpdateMessage;
import eyesatop.controller_tcpip.common.tasks.TaskUpdate;
import eyesatop.util.RemovableCollection;
import eyesatop.util.connections.tcp.oneway.OneWayStreamCallback;
import eyesatop.util.connections.tcp.oneway.OneWayStreamConnectionInfo;
import eyesatop.util.connections.tcp.oneway.OneWayStreamTCPClient;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public abstract class CurrentTaskUpdateClientManager<T extends EnumWithName> {

    private final int port;
    private final DroneController controller;
    private final RemovableCollection listenerBinding = new RemovableCollection();
    private final CurrentTaskUpdateMessage<T> nullMessage = new CurrentTaskUpdateMessage<T>(null);
    private final OneWayStreamTCPClient<CurrentTaskUpdateMessage<T>> client = new OneWayStreamTCPClient<>();
    private final OneWayStreamCallback<CurrentTaskUpdateMessage<T>> callback;

    public CurrentTaskUpdateClientManager(int droneID,DroneController controller, ObservableValue<String> remoteIP) {
        this.controller = controller;

        port = getPort();

        callback = new OneWayStreamCallback<CurrentTaskUpdateMessage<T>>() {
            @Override
            public void onConnectionActive(BlockingQueue<CurrentTaskUpdateMessage<T>> messagesToSend) {
                doBindings(messagesToSend);
            }

            @Override
            public void onConnectionLost(BlockingQueue<CurrentTaskUpdateMessage<T>> messagesToSend) {
                listenerBinding.remove();
                messagesToSend.clear();
            }
        };

        remoteIP.observe(new Observer<String>() {
            @Override
            public void observe(String oldValue, String newValue, Observation<String> observation) {
                client.connect(newValue == null ? null :
                        new OneWayStreamConnectionInfo<CurrentTaskUpdateMessage<T>>(
                                newValue,port,callback));
            }
        }).observeCurrentValue();
    }

    private void doBindings(final BlockingQueue<CurrentTaskUpdateMessage<T>> updates){

        listenerBinding.remove();
        updates.clear();

        listenerBinding.add(
                currentTask(controller).observe(new Observer<DroneTask<T>>() {
                    @Override
                    public void observe(DroneTask<T> oldValue, final DroneTask<T> newTask, Observation<DroneTask<T>> observation) {

                        if(newTask != null){
                            listenerBinding.add(newTask.status().observe(new Observer<TaskStatus>() {
                                @Override
                                public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                                    if(newValue.isTaskDone()){
                                        observation.remove();
                                    }
                                    try {
                                        CurrentTaskUpdateMessage<T> updateMessage = new CurrentTaskUpdateMessage<>(getFromTask(newTask));
                                        updates.add(updateMessage);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).observeCurrentValue());
                        }
                        else{
                            try {
                                updates.add(nullMessage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).observeCurrentValue()
        );
    }

    protected abstract int getPort();

    protected abstract ObservableValue<DroneTask<T>> currentTask(DroneController controller);

    protected abstract TaskUpdate<T> getFromTask(DroneTask<T> task);
}
