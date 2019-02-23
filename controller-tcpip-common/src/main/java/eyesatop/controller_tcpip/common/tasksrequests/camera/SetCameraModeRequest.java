package eyesatop.controller_tcpip.common.tasksrequests.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;

public class SetCameraModeRequest implements CameraTaskRequest {

    private static final String CAMERA_MODE = "cameraMode";

    private final CameraMode cameraMode;

    @JsonCreator
    public SetCameraModeRequest(@JsonProperty(CAMERA_MODE) CameraMode cameraMode) {
        this.cameraMode = cameraMode;
    }

    @JsonProperty(CAMERA_MODE)
    public CameraMode getCameraMode() {
        return cameraMode;
    }

    @Override
    @JsonIgnore
    public TaskResponse perform(DroneController controller) {
        try {
            DroneTask newTask = controller.camera().setMode(cameraMode);
            return new TaskResponse(newTask.uuid(),null);
        } catch (DroneTaskException e) {
            return new TaskResponse(null,e.getErrorString());
        }
    }
}
