package eyesatop.unit.ui.models.missionplans.components;

import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 14/11/2017.
 */

public class Waypoint extends Location{
    private final Property<String> waypointName = new Property<>();

    public Waypoint(Location location){
        super(location.getLatitude(),location.getLongitude(),location.getAltitude());
    }

    public Property<String> getWaypointName() {
        return waypointName;
    }
}
