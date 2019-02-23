package eyesatop.controller.beans;

/**
 * Created by einav on 24/01/2017.
 */
public enum GpsSignalLevel {
    LEVEL0(0),
    LEVEL1(1),
    LEVEL2(2),
    LEVEL3(3),
    LEVEL4(4),
    LEVEL5(5),
    NONE(-1),
    UNKNOWN(-1);

    private final int gpsLevel;

    GpsSignalLevel(int gpsLevel) {
        this.gpsLevel = gpsLevel;
    }

    public int getGpsLevel() {
        return gpsLevel;
    }
}
