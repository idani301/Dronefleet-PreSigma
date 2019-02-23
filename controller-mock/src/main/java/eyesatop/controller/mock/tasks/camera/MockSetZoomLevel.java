package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetZoomLevel;

/**
 * Created by Idan on 22/09/2017.
 */

public class MockSetZoomLevel extends MockDroneTask<CameraTaskType> implements SetZoomLevel {

    private final double zoomLevel;

    public MockSetZoomLevel(UUID uuid, double zoomLevel) {
        super(uuid, CameraTaskType.SET_ZOOM_LEVEL);
        this.zoomLevel = zoomLevel;
    }

    @Override
    public double zoomLevel() {
        return zoomLevel;
    }
}
