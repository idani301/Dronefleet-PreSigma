package eyesatop.util.geo.functions;

import eyesatop.util.Function;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Valued;

public class LocationDistance implements Function<Location, Double> {

    private final Valued<Location> other;

    public LocationDistance(Valued<Location> other) {
        this.other = other;
    }

    @Override
    public Double apply(Location input) {
        if (other.isNull() || input == null) {
            return null;
        }
        return input.distance(other.value());
    }
}
