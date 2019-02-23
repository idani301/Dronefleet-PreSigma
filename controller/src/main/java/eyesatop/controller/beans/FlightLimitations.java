package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by einav on 21/03/2017.
 */
public class FlightLimitations {

    private static final String ACTIVE = "active";
    private static final String MAX_DISTANCE = "maxDistance";
    private static final String MAX_ALTITUDE = "maxAltitude";

    public static final double UNKNOWN = -1;

    private final boolean limitationActive;
    private final double maxDistanceFromHome;
    private final double maxAltitudeFromTakeOffLocation;

    @JsonCreator
    public FlightLimitations(
            @JsonProperty(ACTIVE)
            boolean limitationActive,

            @JsonProperty(MAX_DISTANCE)
            double maxDistanceFromHome,

            @JsonProperty(MAX_ALTITUDE)
            double maxAltitudeFromTakeOffLocation) {
        this.limitationActive = limitationActive;
        this.maxDistanceFromHome = maxDistanceFromHome;
        this.maxAltitudeFromTakeOffLocation = maxAltitudeFromTakeOffLocation;
    }

    public FlightLimitations limitationActive(boolean limitationActive){
        return new FlightLimitations(limitationActive,maxDistanceFromHome,maxAltitudeFromTakeOffLocation);
    }

    public FlightLimitations maxDistanceFromHome(double maxDistanceFromHome){
        return new FlightLimitations(limitationActive,maxDistanceFromHome,maxAltitudeFromTakeOffLocation);
    }

    public FlightLimitations maxAltitudeFromTakeOffLocation(double maxAltitudeFromTakeOffLocation){
        return new FlightLimitations(limitationActive,maxDistanceFromHome,maxAltitudeFromTakeOffLocation);
    }

    @JsonProperty(ACTIVE)
    public boolean isLimitationActive() {
        return limitationActive;
    }

    @JsonProperty(MAX_DISTANCE)
    public double getMaxDistanceFromHome() {
        return maxDistanceFromHome;
    }

    @JsonProperty(MAX_ALTITUDE)
    public double getMaxAltitudeFromTakeOffLocation() {
        return maxAltitudeFromTakeOffLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlightLimitations that = (FlightLimitations) o;

        if (limitationActive != that.limitationActive) return false;
        if (Double.compare(that.maxDistanceFromHome, maxDistanceFromHome) != 0) return false;
        return Double.compare(that.maxAltitudeFromTakeOffLocation, maxAltitudeFromTakeOffLocation) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (limitationActive ? 1 : 0);
        temp = Double.doubleToLongBits(maxDistanceFromHome);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxAltitudeFromTakeOffLocation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "FlightLimitations{" +
                "limitationActive=" + limitationActive +
                ", maxDistanceFromHome=" + maxDistanceFromHome +
                ", maxAltitudeFromTakeOffLocation=" + maxAltitudeFromTakeOffLocation +
                '}';
    }
}
