package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class ConfirmLandRequireUpdate extends ControllerUpdate<Boolean> {

    @JsonCreator
    public ConfirmLandRequireUpdate(@JsonProperty(VALUE) Boolean value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.flightTasks().confirmLandRequire().setIfNew(getValue());
    }
}
