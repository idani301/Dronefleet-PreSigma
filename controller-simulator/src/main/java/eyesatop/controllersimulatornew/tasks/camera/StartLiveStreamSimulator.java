package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.beans.StreamState;
import eyesatop.controller.livestream.RTMPLiveStreamer;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StartLiveStream;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

public class StartLiveStreamSimulator extends RunnableDroneTask<CameraTaskType> implements StartLiveStream {

    private final ControllerSimulator controller;
    private final String url;

    public StartLiveStreamSimulator(ControllerSimulator controller, String url) {
        this.controller = controller;
        this.url = url;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        RTMPLiveStreamer streamer = controller.camera().getLiveStreamer().value();
        if(streamer == null){
            throw new DroneTaskException("Has no Streamer");
        }

        streamer.startStream(url);
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.START_LIVE_STREAM;
    }
}
