package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class MotorsOnUpdate extends ControllerUpdate<Boolean> {

    @JsonCreator
    public MotorsOnUpdate(@JsonProperty(VALUE) Boolean value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.motorsOn().setIfNew(getValue());
    }
}
