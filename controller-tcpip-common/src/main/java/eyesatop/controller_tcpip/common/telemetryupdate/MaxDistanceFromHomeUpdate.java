package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class MaxDistanceFromHomeUpdate extends ControllerUpdate<Double> {

    @JsonCreator
    public MaxDistanceFromHomeUpdate(@JsonProperty(VALUE) Double value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.droneHome().maxDistanceFromHome().setIfNew(getValue());
    }
}
