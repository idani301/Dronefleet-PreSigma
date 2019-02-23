package eyesatop.util.geo;

/**
 * Created by Einav on 12/11/2017.
 */

public class GeoCircle {

    private final eyesatop.math.Geometry.EarthGeometry.Location location;
    private final double radius;

    public GeoCircle(Location location, double radius) {
        this.location = new eyesatop.math.Geometry.EarthGeometry.Location(location.getLatitude(),location.getLongitude(),location.getAltitude());
        this.radius = radius;
    }


    public Location getLocation() {
        Location locationTemp = new Location(location.latitude(),location.longitude(),location.Height());
        return locationTemp;
    }

    public double getRadius() {
        return radius;
    }
}
