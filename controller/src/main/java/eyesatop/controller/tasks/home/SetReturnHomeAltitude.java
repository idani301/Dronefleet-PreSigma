package eyesatop.controller.tasks.home;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 21/07/2017.
 */

public interface SetReturnHomeAltitude extends DroneTask<HomeTaskType>{
    double altitude();

    public abstract class StubSetReturnHomeAltitude extends StubDroneTask<HomeTaskType> implements SetReturnHomeAltitude{
        @Override
        public HomeTaskType taskType() {
            return HomeTaskType.SET_RETURN_HOME_ALT;
        }
    }
}
