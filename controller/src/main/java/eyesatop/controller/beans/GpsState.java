package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by einav on 24/01/2017.
 */
public class GpsState {

    private static final String SATELLITE_COUNT = "satCount";
    private static final String SIGNAL_LEVEL = "signalLevel";

    private final int satelliteCount;
    private final GpsSignalLevel gpsSignalLevel;

    @JsonCreator
    public GpsState(
            @JsonProperty(SATELLITE_COUNT)
            int satelliteCount,

            @JsonProperty(SIGNAL_LEVEL)
            GpsSignalLevel gpsSignalLevel) {
        this.satelliteCount = satelliteCount;
        this.gpsSignalLevel = gpsSignalLevel;
    }


    public GpsState satelliteCount(int satelliteCount){
        return new GpsState(satelliteCount,gpsSignalLevel);
    }

    public GpsState gpsSignalLevel(GpsSignalLevel gpsSignalLevel){
        return new GpsState(satelliteCount,gpsSignalLevel);
    }

    @JsonProperty(SATELLITE_COUNT)
    public int getSatelliteCount() {
        return satelliteCount;
    }

    @JsonProperty(SIGNAL_LEVEL)
    public GpsSignalLevel getGpsSignalLevel() {
        return gpsSignalLevel;
    }

    @Override
    public String toString() {
        return "GpsState{" +
                "satelliteCount=" + satelliteCount +
                ", gpsSignalLevel=" + gpsSignalLevel +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GpsState gpsState = (GpsState) o;

        if (satelliteCount != gpsState.satelliteCount) return false;
        return gpsSignalLevel.equals(gpsState.gpsSignalLevel);
    }

    public boolean hasGpsError(){
        if(satelliteCount == 0){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = satelliteCount;
        result = 31 * result + (gpsSignalLevel != null ? gpsSignalLevel.hashCode() : 0);
        return result;
    }
}
