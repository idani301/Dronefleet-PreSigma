package eyesatop.controllersimulatornew.tasks.gimbal;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.GimbalRequest;
import eyesatop.util.geo.GimbalState;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 29/08/2017.
 */
public class LockYawAtLocationSimulator extends RunnableDroneTask<GimbalTaskType> implements LockYawAtLocation {

    private final ControllerSimulator controller;
    private final Location location;
    private final double degreeFromLocation;

    private Removable telemetryObserver;

    public LockYawAtLocationSimulator(ControllerSimulator controller, Location location, double degreeFromLocation) {
        this.controller = controller;
        this.location = location;
        this.degreeFromLocation = degreeFromLocation;
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

                double azFromTarget = currentLocation.az(location);

                double newAzFromTarget = azFromTarget + degreeFromLocation;
                if(newAzFromTarget >=360){
                    newAzFromTarget -= 360;
                }

                double newYaw = Location.degreeBetween0To360(newAzFromTarget);

                try {
                    controller.gimbal().addGimbalRequest(new GimbalRequest(new GimbalState(0,0,newYaw),false,false,true));
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
    public double degreeShiftFromLocation() {
        return degreeFromLocation;
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOCK_YAW_AT_LOCATION;
    }
}

