package eyesatop.controller_tcpip.common.tasksrequests.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class StartLiveStreamRequest implements CameraTaskRequest {

    private static final String URL = "url";

    private final String url;

    @JsonCreator
    public StartLiveStreamRequest(@JsonProperty(URL) String url) {
        this.url = url;
    }

    @JsonProperty(URL)
    public String getUrl() {
        return url;
    }

    @Override
    public TaskResponse perform(DroneController controller) {
        try {
            return new TaskResponse(controller.camera().startLiveStream(getUrl()).uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
