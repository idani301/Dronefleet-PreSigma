package eyesatop.controller.djinew.tasks.gimbal;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.GimbalBehaviorType;
import eyesatop.controller.beans.GimbalBehavior;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by einav on 15/05/2017.
 */
public class DjiLockGimbalAtLocation extends RunnableDroneTask<GimbalTaskType> implements LockGimbalAtLocation {

    private final ControllerDjiNew droneController;
    private final Location location;
    private Removable removable = Removable.STUB;

    public DjiLockGimbalAtLocation(ControllerDjiNew droneController, Location location) {
        this.droneController = droneController;
        this.location = location;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        droneController.gimbal().gimbalBehavior().set(new GimbalBehavior(GimbalBehaviorType.LOCKED_AT_LOCATION,location,0, null));

        removable = droneController.flying().observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                if(newValue == null || !newValue){
                    countDownLatch.countDown();
                    removable.remove();
                    removable = Removable.STUB;
                }
            }
        });

        countDownLatch.await();
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOCK_LOOK_AT_LOCATION;
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        removable.remove();
        droneController.gimbal().gimbalBehavior().set(new GimbalBehavior(GimbalBehaviorType.NOTHING,null,0, null));
    }
}
