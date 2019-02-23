package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import eyesatop.controller.mock.MockController;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;

public class GimbalBlockerUpdate extends ControllerUpdate<List<GimbalTaskBlockerType>> {

    @JsonCreator
    public GimbalBlockerUpdate(@JsonProperty(VALUE) List<GimbalTaskBlockerType> value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        List<GimbalTaskBlockerType> blockersToAdd = new ArrayList<>();
        List<GimbalTaskBlockerType> blockersToRemove = new ArrayList<>();

        for(GimbalTaskBlockerType gimbalBlocker : getValue()){
            if(!controller.gimbal().tasksBlockers().contains(gimbalBlocker)){
                blockersToAdd.add(gimbalBlocker);
            }
        }

        for(GimbalTaskBlockerType gimbalBlocker : controller.gimbal().tasksBlockers()){
            if(!getValue().contains(gimbalBlocker)){
                blockersToRemove.add(gimbalBlocker);
            }
        }

        for(GimbalTaskBlockerType blockerToAdd : blockersToAdd){
            controller.gimbal().tasksBlockers().add(blockerToAdd);
        }

        for(GimbalTaskBlockerType blockerToRemove : blockersToRemove){
            controller.gimbal().tasksBlockers().remove(blockerToRemove);
        }
    }
}
