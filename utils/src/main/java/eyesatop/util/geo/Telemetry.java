package eyesatop.util.geo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Telemetry {

    private static final String LOCATION = "location";
    private static final String VELOCITIES = "velocities";
    private static final String HEADING = "heading";

    private final Location location;
    private final Velocities velocities;
    private final double heading;

    @JsonCreator
    public Telemetry(
            @JsonProperty(LOCATION)
            Location location,

            @JsonProperty(VELOCITIES)
            Velocities velocities,

            @JsonProperty(HEADING)
            double heading) {
        this.location = location;
        this.velocities = velocities;
        this.heading = heading;
    }

    @JsonIgnore
    public static Location telemetryToLocation(Telemetry telemetry){
        if(telemetry == null){
            return null;
        }
        return telemetry.location();
    }

    @JsonProperty(LOCATION)
    public Location location() {
        return location;
    }

    @JsonProperty(VELOCITIES)
    public Velocities velocities() {
        return velocities;
    }

    @JsonProperty(HEADING)
    public double heading() {
        return heading;
    }

    public Telemetry location(Location location) {
        return new Telemetry(location,velocities, heading);
    }

    @JsonIgnore
    public Telemetry heading(double heading) {
        return new Telemetry(location,velocities,heading);
    }

    @JsonIgnore
    public Telemetry velocities(Velocities velocities) {
        return new Telemetry(location,velocities,heading);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Telemetry telemetry = (Telemetry) o;

        if (Double.compare(telemetry.heading, heading) != 0) return false;
        if (location != null ? !location.equals(telemetry.location) : telemetry.location != null)
            return false;
        return velocities != null ? velocities.equals(telemetry.velocities) : telemetry.velocities == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = location != null ? location.hashCode() : 0;
        result = 31 * result + (velocities != null ? velocities.hashCode() : 0);
        temp = Double.doubleToLongBits(heading);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Telemetry{" +
                "location=" + (location == null ? "NULL" : location.toString()) +
                ", velocities=" + (velocities == null ? "NULL" : velocities.toString()) +
                ", heading=" + heading +
                '}';
    }
}
