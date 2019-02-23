package eyesatop.unit.ui.models.map.mission;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import eyesatop.controller.mission.flightplans.FlightPlanInfo;

/**
 * Created by Idan on 25/04/2018.
 */

public class MissionPlanSnapshot {

    private static final String NAME = "name";
    private static final String COMPONENTS = "componentsInfo";

    private final String name;
    private final List<FlightPlanInfo> componentsInfo;

    @JsonCreator
    public MissionPlanSnapshot(@JsonProperty(NAME) String name,@JsonProperty(COMPONENTS) List<FlightPlanInfo> componentsInfo) {
        this.name = name;
        this.componentsInfo = componentsInfo;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(COMPONENTS)
    public List<FlightPlanInfo> getComponentsInfo() {
        return componentsInfo;
    }
}
