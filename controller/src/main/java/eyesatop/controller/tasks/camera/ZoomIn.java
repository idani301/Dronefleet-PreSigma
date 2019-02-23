package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

public interface ZoomIn extends DroneTask<CameraTaskType> {
    public class ZoomInStub extends StubDroneTask<CameraTaskType> implements ZoomIn {

        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.ZOOM_IN;
        }
    }
}
