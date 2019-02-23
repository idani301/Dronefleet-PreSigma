package eyesatop.util.geo;

import java.util.ArrayList;
import java.util.List;

import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Polygon;
import eyesatop.math.MathException;

/**
 * Created by Einav on 08/11/2017.
 */

public class LocationGroup {

    private final ArrayList<eyesatop.math.Geometry.EarthGeometry.Location> locations = new ArrayList<>();
    private final Location referenceLocation;

    public LocationGroup(List<Location> locations) {
        referenceLocation = getReferencePoint(locations);
        eyesatop.math.Geometry.EarthGeometry.Location referenceLine = eyesatop.math.Geometry.EarthGeometry.Location.CreateReferencedLineUTM(new eyesatop.math.Geometry.EarthGeometry.Location(referenceLocation.getLatitude(),referenceLocation.getLongitude()));
        for (int i = 0; i < locations.size(); i++) {
            eyesatop.math.Geometry.EarthGeometry.Location temp = new eyesatop.math.Geometry.EarthGeometry.Location(locations.get(i).getLatitude(),locations.get(i).getLongitude(),locations.get(i).getAltitude(),referenceLine);
            this.locations.add(new eyesatop.math.Geometry.EarthGeometry.Location(temp));
        }
    }

    public Polygon getPolygon() throws MathException {
        ArrayList<Point2D> point2Ds = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            point2Ds.add(locations.get(i).getUtmLocationAsRefPointAsZeroPoint());
        }
        return new Polygon(point2Ds);
    }

    private Location getReferencePoint(List<Location> locations){
        double lat = 0;
        double lon = 0;
        for (int i = 0; i < locations.size(); i++) {
            lat += locations.get(i).getLatitude();
            lon += locations.get(i).getLongitude();
        }
        return new Location(lat/locations.size(),lon/locations.size());
    }

    public Location getReferenceLocation() {
        return referenceLocation;
    }

    public ArrayList<Location> getLocations(){
        ArrayList<Location> locationsNew = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            locationsNew.add(new Location(locations.get(i).latitude(),locations.get(i).longitude(),locations.get(i).Height()));
        }
        return locationsNew;
    }
}
