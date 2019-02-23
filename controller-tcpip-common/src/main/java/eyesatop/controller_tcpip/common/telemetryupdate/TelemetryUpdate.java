package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;
import eyesatop.util.geo.Telemetry;

public class TelemetryUpdate extends ControllerUpdate<Telemetry> {

    @JsonCreator
    public TelemetryUpdate(@JsonProperty(VALUE) Telemetry value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.telemetry().set(getValue());
    }
}
