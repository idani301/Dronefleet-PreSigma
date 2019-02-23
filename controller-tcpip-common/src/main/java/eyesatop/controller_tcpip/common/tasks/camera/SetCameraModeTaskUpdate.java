package eyesatop.controller_tcpip.common.tasks.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.camera.MockSetCameraMode;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;

public class SetCameraModeTaskUpdate extends CameraTaskUpdate{

    private final static String MODE = "mode";

    private final CameraMode mode;

    @JsonCreator
    public SetCameraModeTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                   @JsonProperty(ERROR) DroneTaskException error,
                                   @JsonProperty(STATUS) TaskStatus status,
                                   @JsonProperty(MODE) CameraMode mode) {
        super(uuid, error, status);
        this.mode = mode;
    }

    @JsonProperty(MODE)
    public CameraMode getMode() {
        return mode;
    }

    @Override
    protected MockDroneTask<CameraTaskType> createNewMockTask() {
        return new MockSetCameraMode(getUuid(),mode);
    }
}
