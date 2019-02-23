package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.MediaStorage;
import eyesatop.controller.mock.MockController;

public class MediaStorageUpdate extends ControllerUpdate<MediaStorage> {

    @JsonCreator
    public MediaStorageUpdate(@JsonProperty(VALUE) MediaStorage value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.camera().mediaStorage().set(getValue());
    }
}
