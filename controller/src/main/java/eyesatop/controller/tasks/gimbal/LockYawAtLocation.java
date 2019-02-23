package eyesatop.controller.tasks.gimbal;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

/**
 * Created by einav on 14/05/2017.
 */
public interface LockYawAtLocation extends DroneTask<GimbalTaskType> {
    Location location();
    double degreeShiftFromLocation();

    public class LockYawAtLocationStub extends StubDroneTask<GimbalTaskType> implements LockYawAtLocation{

        private final Location location;
        private final double degreeShiftFromLocation;

        public LockYawAtLocationStub(Location location, double degreeShiftFromLocation) {
            this.location = location;
            this.degreeShiftFromLocation = degreeShiftFromLocation;
        }

        @Override
        public Location location() {
            return location;
        }

        @Override
        public double degreeShiftFromLocation() {
            return degreeShiftFromLocation;
        }

        @Override
        public GimbalTaskType taskType() {
            return GimbalTaskType.LOCK_YAW_AT_LOCATION;
        }
    }
}
