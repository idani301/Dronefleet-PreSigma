package eyesatop.imageprocessing;

/**
 * Created by Einav on 05/10/2017.
 */


import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class HardwareCamera implements Camera {
    private final int cameraIndex;
    private final double frameWidth;
    private final double frameHeight;

    private final double focus;
    private final double exposure;

    private VideoCapture capture;

    public HardwareCamera(
            int cameraIndex,
            double frameWidth,
            double frameHeight,
            double focus,
            double exposure) {
        this.cameraIndex = cameraIndex;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        this.focus = focus;
        this.exposure = exposure;
    }

    @Override
    public Mat capture() {
        if (capture == null) {
            capture = new VideoCapture(cameraIndex);
            capture.open(cameraIndex);

            capture.set(Videoio.CAP_PROP_FRAME_WIDTH, frameWidth);
            capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, frameHeight);
            if (focus != -1) {
                capture.set(Videoio.CAP_PROP_FOCUS, focus);
                capture.set(Videoio.CAP_PROP_AUTOFOCUS, 0);
            } else {
                capture.set(Videoio.CAP_PROP_AUTOFOCUS, 1);
            }

            if (exposure != -1) {
                capture.set(Videoio.CAP_PROP_EXPOSURE, exposure);
                capture.set(Videoio.CAP_PROP_AUTO_EXPOSURE, 0);
            } else {
                capture.set(Videoio.CAP_PROP_AUTO_EXPOSURE, 1);
            }
        }

        Mat mat = new Mat();
        capture.read(mat);
        return mat;
    }

    @Override
    public double getFps() {
        return capture.get(Videoio.CAP_PROP_FPS);
    }

    @Override
    public Size getSize() {
        return new Size(frameWidth,frameHeight);
    }

    @Override
    public void close() {
        if (capture != null) {
            capture.release();
            capture = null;
        }
    }
}

