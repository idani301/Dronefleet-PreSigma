package eyesatop.controller.tasks.home;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 30/04/2017.
 */
public interface SetMaxDistanceFromHome extends DroneTask<HomeTaskType> {
    double maxDistanceFromHome();

    public abstract class StubSetMaxDistanceFromHome extends StubDroneTask<HomeTaskType> implements SetMaxDistanceFromHome{
        @Override
        public HomeTaskType taskType() {
            return HomeTaskType.SET_MAX_DISTANCE_FROM_HOME;
        }
    }
}
