package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.mock.MockController;
import eyesatop.util.geo.Location;

public class HomeLocationUpdate extends ControllerUpdate<Location> {

    @JsonCreator
    public HomeLocationUpdate(@JsonProperty(VALUE) Location value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.droneHome().homeLocation().setIfNew(getValue());
    }
}
