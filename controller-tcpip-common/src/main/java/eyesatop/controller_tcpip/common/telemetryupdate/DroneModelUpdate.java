package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.util.drone.DroneModel;
import eyesatop.controller.mock.MockController;

public class DroneModelUpdate extends ControllerUpdate<DroneModel> {

    @JsonCreator
    public DroneModelUpdate(@JsonProperty(VALUE) DroneModel value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.model().setIfNew(getValue());
    }
}
