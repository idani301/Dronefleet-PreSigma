package eyesatop.imageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Size;

/**
 * Created by Einav on 05/10/2017.
 */

public interface Camera {

    Mat capture() throws Exception;
    double getFps();
    Size getSize();
    void close();

}
