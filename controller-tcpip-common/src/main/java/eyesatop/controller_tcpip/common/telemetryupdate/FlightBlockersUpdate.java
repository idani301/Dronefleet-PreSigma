package eyesatop.controller_tcpip.common.telemetryupdate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import eyesatop.controller.mock.MockController;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;

public class FlightBlockersUpdate extends ControllerUpdate<List<FlightTaskBlockerType>> {

    @JsonCreator
    public FlightBlockersUpdate(@JsonProperty(VALUE) List<FlightTaskBlockerType> value) {
        super(value);
    }

    @Override
    @JsonIgnore
    public void updateController(MockController controller) {
        List<FlightTaskBlockerType> blockersToAdd = new ArrayList<>();
        List<FlightTaskBlockerType> blockersToRemove = new ArrayList<>();

        for(FlightTaskBlockerType flightTaskBlockerType : getValue()){
            if(!controller.flightTasks().tasksBlockers().contains(flightTaskBlockerType)){
                blockersToAdd.add(flightTaskBlockerType);
            }
        }

        for(FlightTaskBlockerType flightTaskBlockerType : controller.flightTasks().tasksBlockers()){
            if(!getValue().contains(flightTaskBlockerType)){
                blockersToRemove.add(flightTaskBlockerType);
            }
        }

        for(FlightTaskBlockerType blockerToAdd : blockersToAdd){
            controller.flightTasks().tasksBlockers().add(blockerToAdd);
        }

        for(FlightTaskBlockerType blockerToRemove : blockersToRemove){
            controller.flightTasks().tasksBlockers().remove(blockerToRemove);
        }
    }
}
