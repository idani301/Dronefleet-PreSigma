package eyesatop.controller.tasks.flight;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

/**
 * Created by Einav on 21/11/2017.
 */

public interface FlyToUsingDTM extends DroneTask<FlightTaskType> {
    Location location();
    Double az();
    double agl();
    double underGapInMeter();
    double upperGapInMeter();
    Double maxVelocity();
    Double radiusReached();

    public class FlyToUsingDTMStub extends StubDroneTask<FlightTaskType> implements FlyToUsingDTM{

        private final Location location;
        private final Double az;
        private final double agl;
        private final double underGrapInMeter;
        private final double upperGapInMeter;
        private final Double maxVelocity;
        private final Double radiusReached;

        public FlyToUsingDTMStub(Location location, Double az, double agl, double underGrapInMeter, double upperGapInMeter, Double maxVelocity, Double radiusReached) {
            this.location = location;
            this.az = az;
            this.agl = agl;
            this.underGrapInMeter = underGrapInMeter;
            this.upperGapInMeter = upperGapInMeter;
            this.maxVelocity = maxVelocity;
            this.radiusReached = radiusReached;
        }

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.FLY_TO_USING_DTM;
        }

        @Override
        public Location location() {
            return location;
        }

        @Override
        public Double az() {
            return az;
        }

        @Override
        public double agl() {
            return agl;
        }

        @Override
        public double underGapInMeter() {
            return underGrapInMeter;
        }

        @Override
        public double upperGapInMeter() {
            return upperGapInMeter;
        }

        @Override
        public Double maxVelocity() {
            return maxVelocity;
        }

        @Override
        public Double radiusReached() {
            return radiusReached;
        }
    }
}
