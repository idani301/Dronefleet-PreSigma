package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.beans.StreamState;
import eyesatop.controller.livestream.RTMPLiveStreamer;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StopLiveStream;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

public class StopLiveStreamSimulator extends RunnableDroneTask<CameraTaskType> implements StopLiveStream{

    private final ControllerSimulator controller;

    public StopLiveStreamSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        RTMPLiveStreamer streamer = controller.camera().getLiveStreamer().value();
        if(streamer == null){
            throw new DroneTaskException("Has no Streamer");
        }

        streamer.stopStream();
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.STOP_LIVE_STREAM;
    }
}
