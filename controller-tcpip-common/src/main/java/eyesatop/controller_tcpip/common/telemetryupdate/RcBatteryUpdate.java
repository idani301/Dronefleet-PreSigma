package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.mock.MockController;

public class RcBatteryUpdate extends ControllerUpdate<BatteryState> {

    @JsonCreator
    public RcBatteryUpdate(@JsonProperty(VALUE) BatteryState value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        controller.rcBattery().setIfNew(getValue());
    }
}
