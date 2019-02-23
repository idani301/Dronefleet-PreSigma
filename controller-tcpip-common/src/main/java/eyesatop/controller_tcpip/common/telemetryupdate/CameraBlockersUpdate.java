package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import eyesatop.controller.mock.MockController;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;

public class CameraBlockersUpdate extends ControllerUpdate<List<CameraTaskBlockerType>>{

    @JsonCreator
    public CameraBlockersUpdate(@JsonProperty(VALUE) List<CameraTaskBlockerType> value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        List<CameraTaskBlockerType> blockersToAdd = new ArrayList<>();
        List<CameraTaskBlockerType> blockersToRemove = new ArrayList<>();

        for(CameraTaskBlockerType blocker : getValue()){
            if(!controller.camera().tasksBlockers().contains(blocker)){
                blockersToAdd.add(blocker);
            }
        }

        for(CameraTaskBlockerType blocker : controller.camera().tasksBlockers()){
            if(!getValue().contains(blocker)){
                blockersToRemove.add(blocker);
            }
        }

        for(CameraTaskBlockerType blockerToAdd : blockersToAdd){
            controller.camera().tasksBlockers().add(blockerToAdd);
        }

        for(CameraTaskBlockerType blockerToRemove : blockersToRemove){
            controller.camera().tasksBlockers().remove(blockerToRemove);
        }
    }
}
