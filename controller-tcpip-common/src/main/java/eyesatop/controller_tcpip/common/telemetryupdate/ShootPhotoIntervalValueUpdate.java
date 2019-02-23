package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;

public class ShootPhotoIntervalValueUpdate extends ControllerUpdate<Integer> {

    @JsonCreator
    public ShootPhotoIntervalValueUpdate(@JsonProperty(VALUE) Integer value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.camera().shootPhotoIntervalValue().setIfNew(getValue());
    }
}