package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class RcSignalStrengthUpdate extends ControllerUpdate<Integer> {

    @JsonCreator
    public RcSignalStrengthUpdate(@JsonProperty(VALUE) Integer value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.rcSignalStrengthPercent().setIfNew(getValue());
    }
}
