package eyesatop.controller.mock.tasks.home;

import java.util.UUID;

import eyesatop.controller.mock.tasks.MockDroneTask;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;

public class MockSetMaxDistanceFromHome extends MockDroneTask<HomeTaskType> implements SetMaxDistanceFromHome {

    private final double distance;

    public MockSetMaxDistanceFromHome(UUID uuid, double distance) {
        super(uuid, HomeTaskType.SET_MAX_DISTANCE_FROM_HOME);
        this.distance = distance;
    }

    @Override
    public double maxDistanceFromHome() {
        return distance;
    }
}
