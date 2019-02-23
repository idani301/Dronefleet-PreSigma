package eyesatop.util.geo.functions;

import eyesatop.util.geo.Location;
import eyesatop.util.Function;

public class LocationAltitude implements Function<Location, Double> {
    @Override
    public Double apply(Location input) {
        return input == null ? null : input.getAltitude();
    }
}
