package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class ReturnHomeAltitudeUpdate extends ControllerUpdate<Double> {

    @JsonCreator
    public ReturnHomeAltitudeUpdate(@JsonProperty(VALUE) Double value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.droneHome().returnHomeAltitude().setIfNew(getValue());
    }
}
