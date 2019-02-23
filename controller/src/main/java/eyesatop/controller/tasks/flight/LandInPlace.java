package eyesatop.controller.tasks.flight;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 14/05/2017.
 */

public interface LandInPlace extends DroneTask<FlightTaskType> {

    abstract class LandInPlaceStub extends StubDroneTask<FlightTaskType> implements LandInPlace{
        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.LAND_IN_PLACE;
        }
    }
}
