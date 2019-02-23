package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.mock.MockController;

public class CameraModeUpdate extends ControllerUpdate<CameraMode> {

    @JsonCreator
    public CameraModeUpdate(@JsonProperty(VALUE) CameraMode value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.camera().mode().setIfNew(getValue());
    }
}
