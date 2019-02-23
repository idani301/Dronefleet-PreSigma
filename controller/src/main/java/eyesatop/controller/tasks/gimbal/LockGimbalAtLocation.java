package eyesatop.controller.tasks.gimbal;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

/**
 * Created by einav on 14/05/2017.
 */
public interface LockGimbalAtLocation extends DroneTask<GimbalTaskType> {
    Location location();

    public class LockGimbalAtLocationStub extends StubDroneTask<GimbalTaskType> implements LockGimbalAtLocation{

        private final Location location;

        public LockGimbalAtLocationStub(Location location) {
            this.location = location;
        }

        @Override
        public GimbalTaskType taskType() {
            return GimbalTaskType.LOCK_LOOK_AT_LOCATION;
        }

        @Override
        public Location location() {
            return location;
        }
    }
}
