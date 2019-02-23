package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.util.Function;

/**
 * Created by einav on 24/01/2017.
 */
public class BatteryState {

    private static final String MAX = "max";
    private static final String CURRENT = "current";
    private static final String TEMPERATURE = "temperature";
    private static final String LIFETIME_PERCENT = "lifePercent";

    public static final int BATTERY_HIGH_PIVOT = 75;
    public static final int BATTERY_MED_PIVOT = 45;


    public static final Function<BatteryState, Double> REMAINING_PERCENT = new Function<BatteryState, Double>() {
        @Override
        public Double apply(BatteryState input) {
            if (input == null || input.max == 0) {
                return null;
            }
            return ((double)input.current / (double) input.max) * 100d;
        }
    };

    public static final Function<BatteryState, String> REMAINING_PERCENT_STRING = new Function<BatteryState, String>() {
        @Override
        public String apply(BatteryState input) {

            Double percent = REMAINING_PERCENT.apply(input);

            if (percent == null) {
                return "N/A";
            }
            return percent.intValue() + "%";
        }
    };

    @JsonIgnore
    public static int getPercent(BatteryState batteryState){
        double percent = 100D * (double)batteryState.getCurrent() / (double)batteryState.getMax();
        return (int) percent;
    }

    private final int max;
    private final int current;
    private final float temperature;
    private final int lifeTimePercent;

    @JsonCreator
    public BatteryState(
            @JsonProperty(MAX)
            int max,

            @JsonProperty(CURRENT)
            int current,

            @JsonProperty(TEMPERATURE)
            float temperature,

            @JsonProperty(LIFETIME_PERCENT)
            int lifeTimePercent) {
        this.max = max;
        this.current = current;
        this.temperature = temperature;
        this.lifeTimePercent = lifeTimePercent;
    }

    @JsonProperty(MAX)
    public int getMax() {
        return max;
    }

    @JsonProperty(CURRENT)
    public int getCurrent() {
        return current;
    }

    @JsonProperty(TEMPERATURE)
    public float getTemperature() {
        return temperature;
    }

    @JsonProperty(LIFETIME_PERCENT)
    public int getLifeTimePercent() {
        return lifeTimePercent;
    }

    @JsonIgnore
    public BatteryState current(int current){
        return new BatteryState(getMax(),current,getTemperature(),getLifeTimePercent());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BatteryState that = (BatteryState) o;

        if (max != that.max) return false;
        if (current != that.current) return false;
        if (Float.compare(that.temperature, temperature) != 0) return false;
        return lifeTimePercent == that.lifeTimePercent;

    }

    @Override
    public int hashCode() {
        int result = max;
        result = 31 * result + current;
        result = 31 * result + (temperature != +0.0f ? Float.floatToIntBits(temperature) : 0);
        result = 31 * result + lifeTimePercent;
        return result;
    }

    @Override
    public String toString() {
        return "BatteryState{" +
                "max=" + max +
                ", current=" + current +
                ", temperature=" + temperature +
                ", lifeTimePercent=" + lifeTimePercent +
                '}';
    }
}
