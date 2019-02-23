package eyesatop.controller.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ZoomInfo {

    private static final String OPTICAL_ZOOM_FACTOR = "opticalZoomFactor";
    private static final String DIGITAL_ZOOM_FACTOR = "digitalZoomFactor";

    private final double opticalZoomFactor;
    private final double digitalZoomFactor;

    @JsonCreator
    public ZoomInfo(@JsonProperty(OPTICAL_ZOOM_FACTOR) double opticalZoomFactor,
                    @JsonProperty(DIGITAL_ZOOM_FACTOR) double digitalZoomFactor) {
        this.opticalZoomFactor = opticalZoomFactor;
        this.digitalZoomFactor = digitalZoomFactor;
    }

    @JsonProperty(OPTICAL_ZOOM_FACTOR)
    public double getOpticalZoomFactor() {
        return opticalZoomFactor;
    }

    @JsonProperty(DIGITAL_ZOOM_FACTOR)
    public double getDigitalZoomFactor() {
        return digitalZoomFactor;
    }
}
