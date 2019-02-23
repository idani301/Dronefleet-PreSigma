package eyesatop.controllersimulatornew.tasks.gimbal;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.GimbalRequest;
import eyesatop.util.geo.GimbalState;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;


/**
 * Created by einav on 21/05/2017.
 */
public class LockGimbalAtLocationSimulator extends RunnableDroneTask<GimbalTaskType> implements LockGimbalAtLocation{

    private final ControllerSimulator controller;
    private final Location location;

    private Removable telemetryObserver;

    public LockGimbalAtLocationSimulator(ControllerSimulator controller, Location location) {
        this.controller = controller;
        this.location = location;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        telemetryObserver = controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {

                Location oldLocation = null;
                Location newLocation = null;

                if(oldValue != null){
                    oldLocation = oldValue.location();
                }

                if(newValue != null){
                    newLocation = newValue.location();
                }

                if(oldLocation != null && newLocation != null && newLocation.equals(oldLocation)){
                    return;
                }

                if(oldLocation == null && newLocation == null){
                    return;
                }

                Location currentLocation = newValue.location();

                double distanceFromTarget = currentLocation.distance(location);
                double azFromTarget = currentLocation.az(location);

                double newYaw = Location.degreeBetween0To360(azFromTarget);

                double altDistance = Math.max(currentLocation.getAltitude() - location.getAltitude(),0);

                double requiredPitch = Math.toDegrees(Math.atan(altDistance/distanceFromTarget));

                try {
                    controller.gimbal().addGimbalRequest(new GimbalRequest(new GimbalState(0,requiredPitch,newYaw),true,false,true));
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                }
            }
        }).observeCurrentValue();

        countDownLatch.await();

        telemetryObserver.remove();
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        if(telemetryObserver != null){
            telemetryObserver.remove();
        }
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOCK_LOOK_AT_LOCATION;
    }
}
