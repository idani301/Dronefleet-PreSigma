package eyesatop.controller.mock.tasks.home;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import eyesatop.util.geo.Location;

public class MockSetHomeLocation extends MockDroneTask<HomeTaskType> implements SetHomeLocation {

    private final Location location;

    public MockSetHomeLocation(
            UUID uuid,
            Location location) {
        super(uuid, HomeTaskType.SET_HOME_LOCATION);
        this.location = location;
    }

    @Override
    public Location location() {
        return location;
    }
}
