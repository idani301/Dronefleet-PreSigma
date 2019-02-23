package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.mock.MockController;

public class ConnectivityUpdate extends ControllerUpdate<DroneConnectivity> {

    @JsonCreator
    public ConnectivityUpdate(@JsonProperty(VALUE) DroneConnectivity value) {
        super(value);
    }

    @Override
    public void updateController(MockController controller) {
        controller.connectivity().setIfNew(getValue());
    }
}
