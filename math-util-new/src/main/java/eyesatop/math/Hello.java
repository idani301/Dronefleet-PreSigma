package eyesatop.math;

import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 * Created by Einav on 27/04/2017.
 */

public class Hello {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main (String args[]) {
        System.out.println(" Hi there, Einav");
        Mat m = new Mat();

    }
}
