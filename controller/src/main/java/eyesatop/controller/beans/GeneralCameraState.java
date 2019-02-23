package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Idan on 08/05/2018.
 */

public class GeneralCameraState {

    private final static String IS_RECORDING = "isRecording";
    private final static String IS_SHOOTING_PHOTO = "isShootingPhotoInInterval";
    private final static String SHOOT_PHOTO_INTERVAL = "shootPhotoInteralNumber";

    private final Boolean isRecording;
    private final Boolean isShootingPhotoInInterval;
    private final Integer shootPhotoInteralNumber;

    @JsonCreator
    public GeneralCameraState(@JsonProperty(IS_RECORDING) Boolean isRecording,
                              @JsonProperty(IS_SHOOTING_PHOTO) Boolean isShootingPhotoInInterval,
                              @JsonProperty(SHOOT_PHOTO_INTERVAL) Integer shootPhotoInteralNumber) {
        this.isRecording = isRecording;
        this.isShootingPhotoInInterval = isShootingPhotoInInterval;
        this.shootPhotoInteralNumber = shootPhotoInteralNumber;
    }

    @JsonProperty(IS_RECORDING)
    public Boolean isRecording() {
        return isRecording;
    }

    @JsonProperty(IS_SHOOTING_PHOTO)
    public Boolean isShootingPhotoInInterval() {
        return isShootingPhotoInInterval;
    }

    @JsonProperty(SHOOT_PHOTO_INTERVAL)
    public Integer getShootPhotoInteralNumber() {
        return shootPhotoInteralNumber;
    }
}
