package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

public interface ZoomOut extends DroneTask<CameraTaskType> {
    public class ZoomOutStub extends StubDroneTask<CameraTaskType> implements ZoomOut {

        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.ZOOM_OUT;
        }
    }
}
