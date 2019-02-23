package eyesatop.imageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 * Created by Einav on 22/10/2017.
 */

public class ImageProcessingCamera implements Camera {

    private final Camera camera;
    private final ImageProcessing imageProcessing;

    public ImageProcessingCamera(Camera camera, ImageProcessing imageProcessing) {
        this.camera = camera;
        this.imageProcessing = imageProcessing;
    }


    @Override
    public Mat capture() throws Exception {
        return null;
    }

    @Override
    public double getFps() {
        return 0;
    }

    @Override
    public Size getSize() {
        return null;
    }

    @Override
    public void close() {

    }
}
