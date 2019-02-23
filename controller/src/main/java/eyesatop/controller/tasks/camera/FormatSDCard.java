package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 18/12/2017.
 */

public interface FormatSDCard extends DroneTask<CameraTaskType> {

    class FormatSDCardStub extends StubDroneTask<CameraTaskType> implements FormatSDCard{
        @Override
        public CameraTaskType taskType() {
            return CameraTaskType.FORMAT_SD_CARD;
        }
    }
}
