package eyesatop.imageprocess;

/**
 * Created by Idan on 20/05/2018.
 */

public class DetectionData {

    public DetectionData(long captureTime, int xPixel, int yPixel, DetectionType detectionType) {
        this.captureTime = captureTime;
        this.xPixel = xPixel;
        this.yPixel = yPixel;
        this.detectionType = detectionType;
    }

    public long getCaptureTime() {
        return captureTime;
    }

    public int getxPixel() {
        return xPixel;
    }

    public int getyPixel() {
        return yPixel;
    }

    public DetectionType getDetectionType() {
        return detectionType;
    }

    public enum DetectionType {
        PERSON;
    }

    private final long captureTime;
    private final int xPixel;
    private final int yPixel;
    private final DetectionType detectionType;

    @Override
    public String toString() {
        return "DetectionData{" +
                "captureTime=" + captureTime +
                ", xPixel=" + xPixel +
                ", yPixel=" + yPixel +
                ", detectionType=" + detectionType +
                '}';
    }
}
