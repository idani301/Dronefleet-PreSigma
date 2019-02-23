package eyesatop.controller.tasks.flight;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

/**
 * Created by Idan on 10/05/2018.
 */

public interface FlyToSafeAndFast extends DroneTask<FlightTaskType> {

    public static double MIN_AGL = 15D;

    Location targetLocation();
    AltitudeInfo altitudeInfo();

    public class FlySafeAndFastToStub extends StubDroneTask<FlightTaskType> implements FlyToSafeAndFast {

        private final Location targetLocation;
        private final AltitudeInfo altitudeInfo;

        public FlySafeAndFastToStub(Location targetLocation, AltitudeInfo altitudeInfo) {
            this.targetLocation = targetLocation;
            this.altitudeInfo = altitudeInfo;
        }

        @Override
        public Location targetLocation() {
            return targetLocation;
        }

        @Override
        public AltitudeInfo altitudeInfo() {
            return altitudeInfo;
        }

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.FLY_SAFE_TO;
        }
    }
}
