package eyesatop.controller.tasks.home;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by einav on 30/04/2017.
 */
public interface SetMaxAltitudeFromHomeLocation extends DroneTask<HomeTaskType> {
    double altitude();

    public abstract class StubSetMaxAltitudeFromHomeLocation extends StubDroneTask<HomeTaskType> implements SetMaxAltitudeFromHomeLocation{
        @Override
        public HomeTaskType taskType() {
            return HomeTaskType.SET_MAX_ALT_FROM_TAKE_OFF_ALT;
        }
    }
}
