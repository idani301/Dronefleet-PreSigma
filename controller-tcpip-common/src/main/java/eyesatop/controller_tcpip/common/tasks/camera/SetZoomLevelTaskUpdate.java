package eyesatop.controller_tcpip.common.tasks.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.mock.tasks.camera.MockSetZoomLevel;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;

public class SetZoomLevelTaskUpdate extends CameraTaskUpdate {

    private static final String ZOOM_LEVEL = "zoomLevel";

    private final double zoomLevel;

    @JsonCreator
    public SetZoomLevelTaskUpdate(@JsonProperty(UUID) java.util.UUID uuid,
                                  @JsonProperty(ERROR) DroneTaskException error,
                                  @JsonProperty(STATUS) TaskStatus status,
                                  @JsonProperty(ZOOM_LEVEL) double zoomLevel) {
        super(uuid, error, status);
        this.zoomLevel = zoomLevel;
    }

    @JsonProperty(ZOOM_LEVEL)
    public double getZoomLevel() {
        return zoomLevel;
    }

    @Override
    protected MockDroneTask<CameraTaskType> createNewMockTask() {
        return new MockSetZoomLevel(getUuid(),zoomLevel);
    }
}
