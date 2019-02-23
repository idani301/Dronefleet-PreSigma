package eyesatop.imageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 * Created by Einav on 05/10/2017.
 */

public class VideoCaptureCamera implements Camera {

    private final int cameraIndex;
    private final Size size;

    private VideoCapture capture;


    public VideoCaptureCamera(int cameraIndex, Size size) {
        this.cameraIndex = cameraIndex;
        this.size = size;
    }

    @Override
    public Mat capture() {
        startCamera();
        Mat mat = new Mat();
        capture.read(mat);
        return mat;
    }

    public void startCamera(){
        if (capture == null || !capture.isOpened()) {

            capture = new VideoCapture(cameraIndex);
            capture.open(cameraIndex);
            capture.set(Videoio.CAP_PROP_FRAME_WIDTH, size.width);
            capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, size.height);
        }
    }

    @Override
    public double getFps() {
        return capture.get(Videoio.CAP_PROP_FPS);
    }

    @Override
    public Size getSize() {
        startCamera();
        if (capture.isOpened())
            return new Size(capture.get(Videoio.CAP_PROP_FRAME_WIDTH),capture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        Mat mat = capture();
        close();
        return mat.size();
    }

    @Override
    public void close() {
        if (capture != null) {
            capture.release();
            capture = null;

        }
    }
}
