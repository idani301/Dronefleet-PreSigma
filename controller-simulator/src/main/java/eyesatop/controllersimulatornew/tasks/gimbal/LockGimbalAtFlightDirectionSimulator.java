package eyesatop.controllersimulatornew.tasks.gimbal;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtFlightDirection;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 06/09/2017.
 */

public class LockGimbalAtFlightDirectionSimulator extends RunnableDroneTask<GimbalTaskType> implements LockGimbalAtFlightDirection {

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOCK_TO_FLIGHT_DIRECTION;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        countDownLatch.await();
    }
}
