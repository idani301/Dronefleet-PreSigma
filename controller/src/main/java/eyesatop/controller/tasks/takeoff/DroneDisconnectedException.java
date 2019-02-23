package eyesatop.controller.tasks.takeoff;

import eyesatop.controller.tasks.exceptions.DroneTaskException;

/**
 * Created by einav on 24/01/2017.
 */
public class DroneDisconnectedException extends DroneTaskException {

    public DroneDisconnectedException(String errorString) {
        super(errorString);
    }
}
