package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.mock.MockController;

public class FlightModeUpdate extends ControllerUpdate<FlightMode> {

    @JsonCreator
    public FlightModeUpdate(@JsonProperty(VALUE) FlightMode value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.flightMode().setIfNew(getValue());
    }
}
