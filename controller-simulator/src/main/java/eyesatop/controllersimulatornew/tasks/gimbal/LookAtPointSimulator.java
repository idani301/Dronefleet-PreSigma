package eyesatop.controllersimulatornew.tasks.gimbal;

import eyesatop.controller.GimbalRequest;
import eyesatop.util.geo.GimbalState;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by einav on 17/05/2017.
 */

public class LookAtPointSimulator extends RunnableDroneTask<GimbalTaskType> implements LookAtPoint {

    private final ControllerSimulator controller;
    private final Location location;

    public LookAtPointSimulator(ControllerSimulator controller, Location location) {
        this.controller = controller;
        this.location = location;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Thread.sleep(1000);

        if(controller.telemetry().isNull()){
            throw new DroneTaskException("Unknown currnet location");
        }

        Location currentLocation = controller.telemetry().value().location();

        double distanceFromTarget = currentLocation.distance(location);
        double azFromTarget = currentLocation.az(location);

        controller.telemetry().set(controller.telemetry().value().heading(azFromTarget));

        double altDistance = Math.max(currentLocation.getAltitude() - location.getAltitude(),0);

        double requiredPitch = Math.toDegrees(Math.atan(altDistance/distanceFromTarget));
        controller.gimbal().addGimbalRequest(new GimbalRequest(new GimbalState(0,requiredPitch,azFromTarget),true,false,true));
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    public GimbalTaskType taskType() {
        return GimbalTaskType.LOOK_AT_POINT;
    }
}
