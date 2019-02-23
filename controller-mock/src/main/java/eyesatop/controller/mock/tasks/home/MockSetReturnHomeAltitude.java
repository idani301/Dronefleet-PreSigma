package eyesatop.controller.mock.tasks.home;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;

/**
 * Created by einav on 22/07/2017.
 */

public class MockSetReturnHomeAltitude extends MockDroneTask<HomeTaskType> implements SetReturnHomeAltitude {

    private final double altitude;

    public MockSetReturnHomeAltitude(UUID uuid,double altitude) {
        super(uuid,HomeTaskType.SET_RETURN_HOME_ALT);
        this.altitude = altitude;
    }

    @Override
    public double altitude() {
        return altitude;
    }
}
