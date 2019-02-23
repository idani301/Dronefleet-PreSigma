package eyesatop.unit.ui.models.map.mission;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.beans.GeneralDroneState;
import eyesatop.util.geo.Location;

/**
 * Created by Idan on 28/04/2018.
 */

public class MissionSessionSnapshot {

    private static final String NAME = "name";
    private static final String MISSION_PLAN = "missionPlanSnapshot";
    private static final String INDEX = "index";
    private static final String LOCATION = "lastKnownLocation";
    private static final String LAST_STATE = "lastKnownState";
    private static final String FIRST_LOCATION = "firstLocationBeforeSession";

    private final String name;
    private final MissionPlanSnapshot missionPlanSnapshot;
    private final Integer index;
    private final Location lastKnownLocation;
    private final GeneralDroneState lastKnownState;
    private final Location firstLocationBeforeSession;

    @JsonCreator
    public MissionSessionSnapshot(@JsonProperty(NAME) String name,
                                  @JsonProperty(MISSION_PLAN) MissionPlanSnapshot missionPlanSnapshot,
                                  @JsonProperty(INDEX) Integer index,
                                  @JsonProperty(LOCATION) Location lastKnownLocation,
                                  @JsonProperty(LAST_STATE) GeneralDroneState lastKnownState,
                                  @JsonProperty(FIRST_LOCATION) Location firstLocationBeforeSession) {
        this.name = name;
        this.missionPlanSnapshot = missionPlanSnapshot;
        this.index = index;
        this.lastKnownLocation = lastKnownLocation;
        this.lastKnownState = lastKnownState;
        this.firstLocationBeforeSession = firstLocationBeforeSession;
    }

    @JsonProperty(FIRST_LOCATION)
    public Location getFirstLocationBeforeSession() {
        return firstLocationBeforeSession;
    }

    @JsonProperty(LAST_STATE)
    public GeneralDroneState getLastKnownState() {
        return lastKnownState;
    }

    @JsonProperty(NAME)
    public String getName() {
        return name;
    }

    @JsonProperty(MISSION_PLAN)
    public MissionPlanSnapshot getMissionPlanSnapshot() {
        return missionPlanSnapshot;
    }

    @JsonProperty(INDEX)
    public Integer getIndex() {
        return index;
    }

    @JsonProperty(LOCATION)
    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }
}
