package eyesatop.controller.beans;

/**
 * Created by Idan on 08/05/2018.
 */

public enum CameraActionType {
//    NO_CARE("Don't Care"),
    NONE("Nothing"),
    VIDEO("Video Record"),
    STILLS("Shoot Photo In Interval");

    private final String name;

    CameraActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return name;
    }
}
