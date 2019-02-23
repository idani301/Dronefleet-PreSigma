package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Idan on 24/12/2017.
 */

public class AltitudeInfo {

    private static final String ALTITUDE_TYPE = "altitudeType";
    private static final String VALUE = "valueInMeters";

    private final AltitudeType altitudeType;
    private final Double valueInMeters;

    @JsonCreator
    public AltitudeInfo(
            @JsonProperty(ALTITUDE_TYPE) AltitudeType altitudeType,
            @JsonProperty(VALUE) Double value) {
        this.altitudeType = altitudeType;
        this.valueInMeters = value;
    }

    public AltitudeInfo altitudeType(AltitudeType altitudeType){
        return new AltitudeInfo(altitudeType,this.valueInMeters);
    }

    public AltitudeInfo valueInMeters(Double valueInMeters){
        return new AltitudeInfo(this.altitudeType,valueInMeters);
    }

    @JsonProperty(ALTITUDE_TYPE)
    public AltitudeType getAltitudeType() {
        return altitudeType;
    }

    @JsonProperty(VALUE)
    public Double getValueInMeters() {
        return valueInMeters;
    }

    @JsonIgnore
    public boolean isNull(){
        return altitudeType == null || valueInMeters == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AltitudeInfo that = (AltitudeInfo) o;

        if (altitudeType != that.altitudeType) return false;
        return valueInMeters != null ? valueInMeters.equals(that.valueInMeters) : that.valueInMeters == null;
    }

    @Override
    public String toString() {

        String valueInMetersString = valueInMeters == null ? "N/A" : (valueInMeters + "");
        String altitudeTypeString = altitudeType == null ? "N/A" : altitudeType.toString();

        return valueInMetersString + "(" + altitudeTypeString + ")";
    }
}
