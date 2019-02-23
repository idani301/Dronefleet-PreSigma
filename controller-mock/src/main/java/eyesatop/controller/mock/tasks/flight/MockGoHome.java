package eyesatop.controller.mock.tasks.flight;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.GoHome;

public class MockGoHome extends MockDroneTask<FlightTaskType> implements GoHome {
    public MockGoHome(UUID uuid) {
        super(uuid, FlightTaskType.GO_HOME);
    }
}
