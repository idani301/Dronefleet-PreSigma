package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;
import eyesatop.util.geo.GimbalState;

public class GimbalStateUpdate extends ControllerUpdate<GimbalState> {

    @JsonCreator
    public GimbalStateUpdate(@JsonProperty(VALUE) GimbalState value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.gimbal().gimbalState().set(getValue());
    }
}
