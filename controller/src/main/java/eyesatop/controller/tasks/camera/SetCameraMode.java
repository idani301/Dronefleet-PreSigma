package eyesatop.controller.tasks.camera;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 30/04/2017.
 */
public interface SetCameraMode extends DroneTask<CameraTaskType> {
    CameraMode mode();

    public abstract class StubSetCameraMode extends StubDroneTask<CameraTaskType> implements SetCameraMode{

        private static final String MODE = "mode";

        private final CameraMode mode;

        @JsonCreator
        public StubSetCameraMode(@JsonProperty(MODE) CameraMode mode) {
            this.mode = mode;
        }

        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.CHANGE_MODE;
        }

        @Override
        public CameraMode mode() {
            return mode;
        }
    }
}
