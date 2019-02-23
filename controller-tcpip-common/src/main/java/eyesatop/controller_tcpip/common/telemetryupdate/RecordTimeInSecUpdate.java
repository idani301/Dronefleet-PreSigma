package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class RecordTimeInSecUpdate extends ControllerUpdate<Integer> {

    @JsonCreator
    public RecordTimeInSecUpdate(@JsonProperty(VALUE) Integer value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.camera().recordTimeInSeconds().setIfNew(getValue());
    }
}
