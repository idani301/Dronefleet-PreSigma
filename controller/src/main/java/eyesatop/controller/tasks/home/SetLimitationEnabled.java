package eyesatop.controller.tasks.home;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 30/04/2017.
 */
public interface SetLimitationEnabled extends DroneTask<HomeTaskType> {
    boolean enabled();

    public abstract class StubSetLimitationEnabled extends StubDroneTask<HomeTaskType> implements SetLimitationEnabled{
        @Override
        public HomeTaskType taskType() {
            return HomeTaskType.SET_LIMITATION_ENABLED;
        }
    }
}
