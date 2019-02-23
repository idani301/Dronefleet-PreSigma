package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.GpsState;
import eyesatop.controller.mock.MockController;

public class GpsStateUpdate extends ControllerUpdate<GpsState>{

    @JsonCreator
    public GpsStateUpdate(@JsonProperty(VALUE) GpsState value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.gps().setIfNew(getValue());
    }
}
