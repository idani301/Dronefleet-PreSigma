package eyesatop.controller.mock.tasks.camera;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.FormatSDCard;

public class MockFormatSDCard extends MockDroneTask<CameraTaskType> implements FormatSDCard {

    public MockFormatSDCard(UUID uuid) {
        super(uuid, CameraTaskType.FORMAT_SD_CARD);
    }
}
