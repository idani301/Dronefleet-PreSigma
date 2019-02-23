package eyesatop.controller.tasks.camera;

import eyesatop.controller.tasks.EnumWithName;

/**
 * Created by einav on 14/05/2017.
 */
public enum CameraTaskType implements EnumWithName {
    START_RECORD("Start Record"),
    STOP_RECORD("Stop Record"),
    STOP_SHOOTING_PHOTOS("Stop Shooting Photos"),
    TAKE_PHOTO("Take Photo"),
    SET_ZOOM_LEVEL("Set Zoom Level"),
    SET_OPTICAL_ZOOM_LEVEL("Set Optical Zoom Level"),
    TAKE_PHOTO_INTERVAL("Take Photo in Interval"),
    FORMAT_SD_CARD("Format SD Card"),
    ZOOM_IN("Zoom In"),
    ZOOM_OUT("Zoom Out"),
    START_LIVE_STREAM("Start Live Stream"),
    STOP_LIVE_STREAM("Stop Live Stream"),
    CHANGE_MODE("Change Camera Mode");

    private final String name;

    CameraTaskType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
