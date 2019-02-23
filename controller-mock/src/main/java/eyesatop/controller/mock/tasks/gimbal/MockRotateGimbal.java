package eyesatop.controller.mock.tasks.gimbal;

import java.util.UUID;

import eyesatop.controller.GimbalRequest;
import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.RotateGimbal;

/**
 * Created by Idan on 20/09/2017.
 */

public class MockRotateGimbal extends MockDroneTask<GimbalTaskType> implements RotateGimbal{

    private final GimbalRequest request;
    private final Integer timeoutInSeconds;

    public MockRotateGimbal(UUID uuid, GimbalRequest request, Integer timeoutInSeconds) {
        super(uuid, GimbalTaskType.ROTATE_GIMBAL);
        this.request = request;
        this.timeoutInSeconds = timeoutInSeconds;
    }

    @Override
    public GimbalRequest rotationRequest() {
        return request;
    }

    @Override
    public Integer timeoutInSeconds() {
        return timeoutInSeconds;
    }
}
