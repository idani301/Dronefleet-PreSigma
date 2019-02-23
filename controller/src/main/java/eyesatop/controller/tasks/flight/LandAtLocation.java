package eyesatop.controller.tasks.flight;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

/**
 * Created by einav on 14/05/2017.
 */

public interface LandAtLocation extends DroneTask<FlightTaskType> {
    Location location();

    abstract class LandAtLocationStub extends StubDroneTask<FlightTaskType> implements LandAtLocation{
        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.LAND_AT_LOCATION;
        }
    }
}
