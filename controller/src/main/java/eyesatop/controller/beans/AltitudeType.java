package eyesatop.controller.beans;

/**
 * Created by Idan on 24/12/2017.
 */
public enum AltitudeType {
    ABOVE_GROUND_LEVEL("AGL"),
    ABOVE_SEA_LEVEL("ASL"),
    FROM_TAKE_OFF_LOCATION("ATL");

    private final String name;

    AltitudeType(String name) {
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
