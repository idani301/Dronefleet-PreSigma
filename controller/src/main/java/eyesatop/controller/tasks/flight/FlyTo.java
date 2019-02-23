package eyesatop.controller.tasks.flight;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

public interface FlyTo extends DroneTask<FlightTaskType> {

    Location location();
    AltitudeInfo altitudeInfo();
    Double az();
    Double maxVelocity();
    Double radiusReached();
    
    public class FlyToStub extends StubDroneTask<FlightTaskType> implements FlyTo {

        private final Location location;
        private final AltitudeInfo altitudeInfo;
        private final Double az;
        private final Double maxVelocity;
        private final Double radius;

        public FlyToStub(Location location, AltitudeInfo altitudeInfo, Double az, Double maxVelocity, Double radius) {
            this.location = location;
            this.altitudeInfo = altitudeInfo;
            this.az = az;
            this.maxVelocity = maxVelocity;
            this.radius = radius;
        }

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.GOTO_POINT;
        }

        @Override
        public Location location() {
            return location;
        }

        @Override
        public AltitudeInfo altitudeInfo() {
            return altitudeInfo;
        }

        @Override
        public Double az() {
            return az;
        }

        @Override
        public Double maxVelocity() {
            return maxVelocity;
        }

        @Override
        public Double radiusReached() {
            return radius;
        }
    }
}
