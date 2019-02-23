package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 22/09/2017.
 */

public interface SetZoomLevel extends DroneTask<CameraTaskType> {
    double zoomLevel();

    abstract class SetZoomLevelStub extends StubDroneTask<CameraTaskType> implements SetZoomLevel {
        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.SET_ZOOM_LEVEL;
        }
    }
}
