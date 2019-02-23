package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.StreamState;
import eyesatop.controller.mock.MockController;

public class StreamStateUpdate extends ControllerUpdate<StreamState> {

    @JsonCreator
    public StreamStateUpdate(@JsonProperty(VALUE) StreamState value) {
        super(value);
    }

    @Override
    public void updateController(MockController controller) {
        controller.camera().streamState().set(getValue());
    }
}
