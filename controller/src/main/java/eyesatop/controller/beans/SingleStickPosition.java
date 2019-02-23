package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SingleStickPosition {

    private static final String VALUE_IN_PERCENT = "valueInPercent";

    private final double valueInPercent;

    @JsonCreator
    public SingleStickPosition(@JsonProperty(VALUE_IN_PERCENT) double valueInPercent) {
        this.valueInPercent = valueInPercent;
    }

    @JsonProperty(VALUE_IN_PERCENT)
    public double getValueInPercent() {
        return valueInPercent;
    }

    @Override
    public String toString() {
        return valueInPercent + "";
    }
}
