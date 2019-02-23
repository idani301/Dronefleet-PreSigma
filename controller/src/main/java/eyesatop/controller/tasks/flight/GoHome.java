package eyesatop.controller.tasks.flight;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 14/05/2017.
 */

public interface GoHome extends DroneTask<FlightTaskType> {

    public class GoHomeStub extends StubDroneTask<FlightTaskType> implements GoHome {

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.GO_HOME;
        }
    }
}
