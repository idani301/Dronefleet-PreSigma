package eyesatop.controller.tasks.gimbal;

import eyesatop.controller.GimbalRequest;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 20/09/2017.
 */

public interface RotateGimbal extends DroneTask<GimbalTaskType> {

    GimbalRequest rotationRequest();
    Integer timeoutInSeconds();

    public class RotateGimbalStub extends StubDroneTask<GimbalTaskType> implements RotateGimbal{

        private final GimbalRequest gimbalRequest;
        private final Integer timeoutInSeconds;

        public RotateGimbalStub(GimbalRequest gimbalRequest, Integer timeoutInSeconds) {
            this.gimbalRequest = gimbalRequest;
            this.timeoutInSeconds = timeoutInSeconds;
        }

        @Override
        public GimbalTaskType taskType() {
            return GimbalTaskType.ROTATE_GIMBAL;
        }

        @Override
        public GimbalRequest rotationRequest() {
            return gimbalRequest;
        }

        @Override
        public Integer timeoutInSeconds() {
            return timeoutInSeconds;
        }
    }
}
