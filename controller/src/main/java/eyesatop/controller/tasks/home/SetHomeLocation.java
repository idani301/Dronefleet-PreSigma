package eyesatop.controller.tasks.home;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;

/**
 * Created by einav on 30/04/2017.
 */
public interface SetHomeLocation extends DroneTask<HomeTaskType> {

    Location location();

    public abstract class StubSetHomeLocation extends StubDroneTask<HomeTaskType> implements SetHomeLocation{
        @Override
        public HomeTaskType taskType() {
            return HomeTaskType.SET_HOME_LOCATION;
        }
    }
}
