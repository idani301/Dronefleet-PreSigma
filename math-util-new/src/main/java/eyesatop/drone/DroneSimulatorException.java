package eyesatop.drone;

/**
 * Created by Einav on 17/08/2017.
 */

public class DroneSimulatorException extends Exception{

    public enum DroneSimulatorExceptionReason{
        ChangeVelocityTooBig
    }

    private final DroneSimulatorExceptionReason droneSimulatorExceptionReason;

    public DroneSimulatorException(DroneSimulatorExceptionReason droneSimulatorExceptionReason) {
        super();
        this.droneSimulatorExceptionReason = droneSimulatorExceptionReason;
        System.err.println(droneSimulatorExceptionReason);
    }

    public DroneSimulatorExceptionReason getDroneSimulatorExceptionReason() {
        return droneSimulatorExceptionReason;
    }
}
