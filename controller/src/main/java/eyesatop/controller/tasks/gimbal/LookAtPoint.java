package eyesatop.controller.tasks.gimbal;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

/**
 * Created by einav on 14/05/2017.
 */
public interface LookAtPoint extends DroneTask<GimbalTaskType> {
    Location location();

    abstract class LookAtPointStub extends StubDroneTask<GimbalTaskType> implements LookAtPoint {
        @Override
        public GimbalTaskType taskType() {
            return GimbalTaskType.LOOK_AT_POINT;
        }
    }
}
