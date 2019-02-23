package eyesatop.controller.tasks.gimbal;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 14/05/2017.
 */
public interface LockGimbalAtFlightDirection extends DroneTask<GimbalTaskType> {

    public abstract class LockGimbalAtFlightDirectionStub extends StubDroneTask<GimbalTaskType> implements LockGimbalAtFlightDirection{
        @Override
        public GimbalTaskType taskType() {
            return GimbalTaskType.LOCK_TO_FLIGHT_DIRECTION;
        }
    }
}
