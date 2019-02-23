package eyesatop.controller.tasks.takeoff;

/**
 * Created by einav on 23/01/2017.
 */
public class DroneNotReadyException extends Exception {

    private final String errorString;

    public DroneNotReadyException(String errorString) {
        this.errorString = errorString;
    }

    public String getErrorString() {
        return errorString;
    }
}
