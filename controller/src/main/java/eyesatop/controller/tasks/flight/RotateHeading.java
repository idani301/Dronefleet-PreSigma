package eyesatop.controller.tasks.flight;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 09/05/2018.
 */

public interface RotateHeading extends DroneTask<FlightTaskType> {
    double angle();

    public class RotateHeadnigStub extends StubDroneTask<FlightTaskType> implements RotateHeading {

        private final double angle;

        public RotateHeadnigStub(double angle) {
            this.angle = angle;
        }


        @Override
        public double angle() {
            return angle;
        }

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.ROTATE_HEADING;
        }
    }
}
