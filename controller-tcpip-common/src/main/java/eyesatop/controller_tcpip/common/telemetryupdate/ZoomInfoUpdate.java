package eyesatop.controller_tcpip.common.telemetryupdate;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.mock.MockController;

public class ZoomInfoUpdate extends ControllerUpdate<ZoomInfo> {

    @JsonCreator
    public ZoomInfoUpdate(@JsonProperty(VALUE) ZoomInfo value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.camera().zoomInfo().setIfNew(getValue());
    }
}
