package eyesatop.controller.mock.tasks.flight;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.takeoff.TakeOff;

public class MockTakeOff extends MockDroneTask<FlightTaskType> implements TakeOff{

    private final double altitude;

    public MockTakeOff(UUID uuid, double altitude) {
        super(uuid, FlightTaskType.TAKE_OFF);
        this.altitude = altitude;
    }

    @Override
    public double altitude() {
        return altitude;
    }
}
