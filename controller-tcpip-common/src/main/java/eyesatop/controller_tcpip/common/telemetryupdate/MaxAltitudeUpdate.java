package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class MaxAltitudeUpdate extends ControllerUpdate<Double> {

    @JsonCreator
    public MaxAltitudeUpdate(@JsonProperty(VALUE) Double value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.droneHome().maxAltitudeFromTakeOffLocation().setIfNew(getValue());
    }
}
