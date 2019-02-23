package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import eyesatop.controller.mock.MockController;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;

public class HomeBlockersUpdate extends ControllerUpdate<List<HomeTaskBlockerType>> {

    @JsonCreator
    public HomeBlockersUpdate(@JsonProperty(VALUE) List<HomeTaskBlockerType> value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        List<HomeTaskBlockerType> blockersToAdd = new ArrayList<>();
        List<HomeTaskBlockerType> blockersToRemove = new ArrayList<>();

        for(HomeTaskBlockerType blocker : getValue()){
            if(!controller.droneHome().taskBlockers().contains(blocker)){
                blockersToAdd.add(blocker);
            }
        }

        for(HomeTaskBlockerType blocker : controller.droneHome().taskBlockers()){
            if(!getValue().contains(blocker)){
                blockersToRemove.add(blocker);
            }
        }

        for(HomeTaskBlockerType blockerToAdd : blockersToAdd){
            controller.droneHome().taskBlockers().add(blockerToAdd);
        }

        for(HomeTaskBlockerType blockerToRemove : blockersToRemove){
            controller.droneHome().taskBlockers().remove(blockerToRemove);
        }
    }
}
