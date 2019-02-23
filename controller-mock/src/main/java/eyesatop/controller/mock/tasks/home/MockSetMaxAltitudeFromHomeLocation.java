package eyesatop.controller.mock.tasks.home;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;

public class MockSetMaxAltitudeFromHomeLocation extends MockDroneTask<HomeTaskType> implements SetMaxAltitudeFromHomeLocation {

    private final double altitude;

    public MockSetMaxAltitudeFromHomeLocation(UUID uuid, double altitude) {
        super(uuid, HomeTaskType.SET_MAX_ALT_FROM_TAKE_OFF_ALT);
        this.altitude = altitude;
    }

    @Override
    public double altitude() {
        return altitude;
    }
}
