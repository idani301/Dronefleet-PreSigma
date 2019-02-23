package eyesatop.imageprocessing;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Einav on 20/11/2017.
 */

public class PrintToImage {

    static {
        new LoadOpenCVDLL();
    }

    public static boolean printToImageFile(ArrayList<ArrayList<Integer[]>> matrix, File file){
        Mat mat = new Mat(matrix.size(), matrix.get(0).size(), CvType.CV_32FC3, new Scalar(255, 255, 255));
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                double[] ints = new double[]{matrix.get(i).get(j)[0],matrix.get(i).get(j)[1],matrix.get(i).get(j)[2]};
                mat.put(i,j,ints);
            }
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyy-kkmmss");
        String fileName = format.format(cal.getTime()) + ".jpg";
        if(Imgcodecs.imwrite("E:\\mapping\\" + fileName,mat)){
            System.out.println("image was saved!");
        }
        else {
            System.out.println("error! no image was saved...");
        }
        return true;
    }

}
