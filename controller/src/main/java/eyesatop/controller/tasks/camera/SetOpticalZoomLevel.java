package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

public interface SetOpticalZoomLevel extends DroneTask<CameraTaskType> {
    double zoomLevel();

    public class SetOpticalZoomLevelStub extends StubDroneTask<CameraTaskType> implements SetOpticalZoomLevel {
        private final double zoomLevel;

        public SetOpticalZoomLevelStub(double zoomLevel) {
            this.zoomLevel = zoomLevel;
        }

        @Override
        public double zoomLevel() {
            return zoomLevel;
        }

        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.SET_OPTICAL_ZOOM_LEVEL;
        }
    }
}
