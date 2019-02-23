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
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by einav on 15/05/2017.
 */
public class DjiLockYawAtLocation extends RunnableDroneTask<GimbalTaskType> implements LockYawAtLocation {

    private final ControllerDjiNew droneController;
    private final Location location;
    private final double degreeShiftFromLocation;

    public DjiLockYawAtLocation(ControllerDjiNew droneController, Location location, double degreeShiftFromLocation) {
        this.droneController = droneController;
        this.location = location;
        this.degreeShiftFromLocation = degreeShiftFromLocation;
    }

    @Override
    public double degreeShiftFromLocation() {
        return degreeShiftFromLocation;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        droneController.gimbal().gimbalBehavior().set(new GimbalBehavior(GimbalBehaviorType.ONLY_YAW_LOCKED_AT_LOCATION,location,degreeShiftFromLocation, null));

        countDownLatch.await();
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOCK_YAW_AT_LOCATION;
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        droneController.gimbal().gimbalBehavior().set(new GimbalBehavior(GimbalBehaviorType.NOTHING,null,0,null));
    }
}
