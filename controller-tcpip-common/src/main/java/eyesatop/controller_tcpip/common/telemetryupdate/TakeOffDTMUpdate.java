package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class TakeOffDTMUpdate extends ControllerUpdate<Double> {

    @JsonCreator
    public TakeOffDTMUpdate(@JsonProperty(VALUE) Double value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.droneHome().takeOffDTM().setIfNew(getValue());
    }
}
