package eyesatop.imageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;

/**
 * Created by Einav on 19/10/2017.
 */

public class FileCamera implements Camera {

    private final File videoFile;
    private final VideoCapture videoCapture;
    private int sleep;

    public FileCamera(File videoFile) throws Exception {
        this.videoFile = videoFile;
        videoCapture = new VideoCapture(videoFile.getAbsolutePath());
        if (!videoCapture.isOpened())
            throw new Exception("cant open this video file");
        normalSpeed();
    }

    public void faster(){
        sleep = sleep/2;
    }

    public void slower(){
        sleep = sleep*2;
    }

    public void normalSpeed(){
        sleep = (int) Math.round((1/videoCapture.get(Videoio.CAP_PROP_FPS))*1000);
    }

    @Override
    public Mat capture() throws Exception {
        Mat mat = new Mat();
        if (videoCapture.isOpened()){
            videoCapture.read(mat);
            Thread.sleep(sleep);
        }
        else {
            videoCapture.open(videoFile.getAbsolutePath());
            if (!videoCapture.isOpened())
                throw new Exception("cant open this video file");
            videoCapture.read(mat);
        }
        if (mat.width() == 0)
            throw new ImageProcessingException(ImageProcessingException.ImageProcessingExceptionType.END_OF_VIDEO_FILE);
        return mat;
    }

    @Override
    public double getFps() {
        return videoCapture.get(Videoio.CAP_PROP_FPS);
    }

    @Override
    public Size getSize() {
        return new Size(videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
    }

    @Override
    public void close() {
        videoCapture.release();
    }
}
