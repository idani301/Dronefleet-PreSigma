package eyesatop.unit.ui.models.specialfunctions;

import java.util.ArrayList;

import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by einav on 29/06/2017.
 */
public class ObliInfo {

    private final Property<Location> centerLocation  = new Property<>();
    private final ArrayList<ObliWaypoint> waypoints = new ArrayList<>();
}
