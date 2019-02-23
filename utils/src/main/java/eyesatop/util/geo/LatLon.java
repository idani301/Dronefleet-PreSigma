package eyesatop.util.geo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LatLon {

    private static final String LAT = "lat";
    private static final String LON = "lon";

    private final double lat;
    private final double lon;

    @JsonCreator
    public LatLon(@JsonProperty(LAT) double lat,@JsonProperty(LON) double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @JsonProperty(LAT)
    public double getLat() {
        return lat;
    }

    @JsonProperty(LON)
    public double getLon() {
        return lon;
    }
}
