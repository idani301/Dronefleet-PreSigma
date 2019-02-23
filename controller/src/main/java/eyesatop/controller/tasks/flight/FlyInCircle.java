package eyesatop.controller.tasks.flight;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

/**
 * Created by Idan on 09/10/2017.
 */

public interface FlyInCircle extends DroneTask<FlightTaskType> {

    Location center();
    double radius();
    RotationType rotationType();
    double degreesToCover();
    double startingDegree();
    AltitudeInfo altitudeInfo();
    double velocity();

    public class FlyInCircleStub extends StubDroneTask<FlightTaskType> implements FlyInCircle {

        private final Location center;
        private final double radius;
        private final RotationType rotationType;
        private final double degreesToCover;
        private final double startingDegree;
        private final AltitudeInfo altitudeInfo;
        private final double velocity;

        public FlyInCircleStub(Location center, double radius, RotationType rotationType, double degreesToCover, double startingDegree, AltitudeInfo altitudeInfo, double velocity) {
            this.center = center;
            this.radius = radius;
            this.rotationType = rotationType;
            this.degreesToCover = degreesToCover;
            this.startingDegree = startingDegree;
            this.altitudeInfo = altitudeInfo;
            this.velocity = velocity;
        }

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.FLY_IN_CIRCLE;
        }

        @Override
        public Location center() {
            return center;
        }

        @Override
        public double radius() {
            return radius;
        }

        @Override
        public RotationType rotationType() {
            return rotationType;
        }

        @Override
        public double degreesToCover() {
            return degreesToCover;
        }

        @Override
        public double startingDegree() {
            return startingDegree;
        }

        @Override
        public AltitudeInfo altitudeInfo() {
            return altitudeInfo;
        }

        @Override
        public double velocity() {
            return velocity;
        }
    }
}
