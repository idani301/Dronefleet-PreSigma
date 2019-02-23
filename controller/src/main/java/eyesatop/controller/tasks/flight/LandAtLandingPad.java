package eyesatop.controller.tasks.flight;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.landingpad.LandingPad;

/**
 * Created by einav on 24/01/2017.
 */
public interface LandAtLandingPad extends DroneTask<FlightTaskType> {

    LandingPad landingPad();

    abstract class LandAtLandingPadStub extends StubDroneTask<FlightTaskType> implements LandAtLandingPad {
        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.LAND_IN_LANDING_PAD;
        }
    }
}
