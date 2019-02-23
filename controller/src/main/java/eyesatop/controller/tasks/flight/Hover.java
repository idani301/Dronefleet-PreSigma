package eyesatop.controller.tasks.flight;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 22/05/2018.
 */

public interface Hover extends DroneTask<FlightTaskType>{
    int hoverTime();

    public class HoverStub extends StubDroneTask<FlightTaskType> implements Hover {

        private final int hoverTimne;

        public HoverStub(int hoverTimne) {
            this.hoverTimne = hoverTimne;
        }

        @Override
        public int hoverTime() {
            return hoverTimne;
        }

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.HOVER;
        }
    }
}
