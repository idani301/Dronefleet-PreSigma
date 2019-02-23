package eyesatop.controller_tcpip.common.tasksrequests.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class SetZoomLevelRequest implements CameraTaskRequest {

    private static final String ZOOM_LEVEL = "zoomLevel";

    private final double zoomLevel;

    @JsonCreator
    public SetZoomLevelRequest(@JsonProperty(ZOOM_LEVEL) double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    @JsonProperty(ZOOM_LEVEL)
    public double getZoomLevel() {
        return zoomLevel;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {

        try {
            DroneTask newTask = controller.camera().setZoomLevel(zoomLevel);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
