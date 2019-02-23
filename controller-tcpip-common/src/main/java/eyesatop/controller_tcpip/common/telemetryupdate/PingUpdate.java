package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class PingUpdate extends ControllerUpdate {

    @JsonCreator
    public PingUpdate(@JsonProperty(VALUE) Object value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
    }
}
