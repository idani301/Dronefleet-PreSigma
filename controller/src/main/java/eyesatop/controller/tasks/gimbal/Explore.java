package eyesatop.controller.tasks.gimbal;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

public interface Explore extends DroneTask<GimbalTaskType> {

    public class ExploreStub extends StubDroneTask<GimbalTaskType> implements Explore {

        @Override
        public GimbalTaskType taskType() {
            return GimbalTaskType.EXPLORE;
        }
    }
}
