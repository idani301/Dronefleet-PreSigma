package eyesatop.controller.functions;

import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.Function;

public class TelemetryLocation implements Function<Telemetry,Location> {
    @Override
    public Location apply(Telemetry input) {
        if (input == null) {
            return null;
        }
        return input.location();
    }
}
