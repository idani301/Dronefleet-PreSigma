package eyesatop.controllersimulatornew.components;

import com.example.abstractcontroller.components.AbstractDroneCamera;
import com.example.abstractcontroller.components.ComponentConnectivityType;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.beans.VideoPacket;
import eyesatop.controller.livestream.RTMPLiveStreamer;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetCameraMode;
import eyesatop.controller.tasks.camera.StartLiveStream;
import eyesatop.controller.tasks.camera.TakePhotoInInterval;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.controllersimulatornew.beans.SimulatorZoomSpec;
import eyesatop.controllersimulatornew.tasks.camera.ChangeCameraModeSimulator;
import eyesatop.controllersimulatornew.tasks.camera.StartLiveStreamSimulator;
import eyesatop.controllersimulatornew.tasks.camera.StartRecordSimulator;
import eyesatop.controllersimulatornew.tasks.camera.StopLiveStreamSimulator;
import eyesatop.controllersimulatornew.tasks.camera.StopRecordSimulator;
import eyesatop.controllersimulatornew.tasks.camera.StopShootingPhotosSimulator;
import eyesatop.controllersimulatornew.tasks.camera.TakePhotoInIntervalSimulator;
import eyesatop.controllersimulatornew.tasks.camera.TakePhotoSimulator;
import eyesatop.controllersimulatornew.tasks.camera.ZoomInSimulator;
import eyesatop.controllersimulatornew.tasks.camera.ZoomOutSimulator;
import eyesatop.util.RemovableCollection;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 29/08/2017.
 */

public class DroneCameraSimulator extends AbstractDroneCamera {

    private final ControllerSimulator controller;
    private ExecutorService fakeVideoExecutor;

    private ExecutorService recordTimeExecutor;

    private final RemovableCollection streamerBindings = new RemovableCollection();
    private final Property<RTMPLiveStreamer> liveStreamer = new Property<>();
    private final Property<SimulatorZoomSpec> zoomSpec = new Property<>();

    public DroneCameraSimulator(ControllerSimulator controller) {
        this.controller = controller;
        startSimulators();
    }

    private void startSimulators(){

        liveStreamer.observe(new Observer<RTMPLiveStreamer>() {
            @Override
            public void observe(RTMPLiveStreamer oldValue, RTMPLiveStreamer newValue, Observation<RTMPLiveStreamer> observation) {
                streamerBindings.remove();
                if(newValue != null){
                    streamerBindings.add(streamState().bind(newValue.streamState()));
                }
            }
        });

        recording().observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                if(newValue != null && newValue){
                    if(recordTimeExecutor != null){
                        recordTimeExecutor.shutdownNow();
                        recordTimeExecutor = null;
                    }
                    recordTimeExecutor = Executors.newSingleThreadExecutor();
                    recordTimeExecutor.execute(new Runnable() {
                        @Override
                        public void run() {

                            Boolean isRecording = recording().value();
                            while(isRecording != null && isRecording){

                                Integer recordTime = recordTimeInSeconds().value();
                                recordTimeInSeconds().set(recordTime == null ? 0 : (recordTime + 1));

                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    return;
                                }
                                isRecording = recording().value();
                            }
                        }
                    });
                }
                if(newValue == null || !newValue){
                    if(recordTimeExecutor != null) {
                        recordTimeExecutor.shutdownNow();
                        recordTimeExecutor = null;
                    }
                    recordTimeInSeconds().set(0);
                }
            }
        });
    }

    public Property<RTMPLiveStreamer> getLiveStreamer() {
        return liveStreamer;
    }

    public void startVideoSimulator(InputStream inputStream) throws IOException, ClassNotFoundException {

        if(fakeVideoExecutor == null){

            final ObjectInputStream in = new ObjectInputStream(inputStream);
            final List<VideoPacket> packetList = (List<VideoPacket>) in.readObject();
            if(packetList == null || packetList.size() == 0){
                throw new IOException();
            }

            final Property<Integer> index = new Property<>(0);
            connectivity.observe(new Observer<ComponentConnectivityType>() {
                @Override
                public void observe(ComponentConnectivityType oldValue, ComponentConnectivityType newValue, Observation<ComponentConnectivityType> observation) {

                    if(fakeVideoExecutor != null){
                        fakeVideoExecutor.shutdownNow();
                        fakeVideoExecutor = null;
                        index.set(0);
                    }

                    switch (newValue){

                        case NULL:
                            break;
                        case NOT_CONNECTED:
                            break;
                        case CONNECTED:

                            fakeVideoExecutor = Executors.newSingleThreadExecutor();
                            fakeVideoExecutor.execute(new Runnable() {
                                @Override
                                public void run() {

                                    while(true){

                                        VideoPacket currentPacket = packetList.get(index.value());
                                        try {
                                            Thread.sleep(currentPacket.getInterval());
                                        } catch (InterruptedException e) {
                                            return;
                                        }

                                        videoBuffer().set(currentPacket);
                                        index.set(index.value()+1);
                                        if(index.value() >= packetList.size()){
                                            index.set(0);
                                        }
                                    }
                                }
                            });
                            break;
                    }
                }
            }).observeCurrentValue();
        }
    }

    @Override
    protected RunnableDroneTask<CameraTaskType> stubToRunnable(StubDroneTask<CameraTaskType> stubDroneTask) throws DroneTaskException{

        switch (stubDroneTask.taskType()){

            case START_LIVE_STREAM:
                StartLiveStream startLiveStream = (StartLiveStream) stubDroneTask;
                return new StartLiveStreamSimulator(controller,startLiveStream.url());
            case STOP_LIVE_STREAM:
                return new StopLiveStreamSimulator(controller);
            case STOP_SHOOTING_PHOTOS:
                return new StopShootingPhotosSimulator(controller);
            case TAKE_PHOTO_INTERVAL:
                TakePhotoInInterval takePhotoInInterval = (TakePhotoInInterval)stubDroneTask;
                return new TakePhotoInIntervalSimulator(controller,takePhotoInInterval.captureCount(),takePhotoInInterval.interval());
            case START_RECORD:
                return new StartRecordSimulator(controller);
            case STOP_RECORD:
                return new StopRecordSimulator(controller);
            case TAKE_PHOTO:
                return new TakePhotoSimulator(controller);
            case CHANGE_MODE:
                return new ChangeCameraModeSimulator(((SetCameraMode)stubDroneTask).mode(),controller);
            case ZOOM_OUT:
                return new ZoomOutSimulator(controller);
            case ZOOM_IN:
                return new ZoomInSimulator(controller);
            default:
                throw new DroneTaskException("Didn't implement stubToRunnable for : " + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {
    }

    @Override
    public void onComponentConnected() {
        isZoomSupported().set(true);
    }

    @Override
    public void clearData() {
        super.clearData();
        zoomSpec.set(null);
    }

    public Property<SimulatorZoomSpec> getZoomSpec() {
        return zoomSpec;
    }

    @Override
    public void internalFormatSDCard() throws DroneTaskException {
        throw new DroneTaskException("Format is not implemented in simulator");
    }
}
