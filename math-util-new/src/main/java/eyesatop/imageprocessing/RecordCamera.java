package eyesatop.imageprocessing;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Einav on 05/10/2017.
 */

public class RecordCamera implements Camera {

    private final Camera camera;
    private final File pathToSave;
    private String fileName;
    private final Size size;
    private final double fps;

    private VideoWriter videoWriter;

    public RecordCamera(Camera camera, File pathToSave, Size size, double fps) throws Exception {
        this.camera = camera;
        this.pathToSave = pathToSave;
        this.size = size;
        this.fps = fps;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyy-kkmmss");
        fileName = format.format(cal.getTime()) + ".avi";

        videoWriter = new VideoWriter(pathToSave.toString() + "\\" + fileName, VideoWriter.fourcc('M', 'J', 'P', 'G'), fps, size);
        if (!videoWriter.isOpened())
            throw new Exception("Video couldn't start recording please check your path");
    }

    @Override
    public Mat capture() throws Exception {
        Mat mat = camera.capture();
        if (mat.size().height == size.height && mat.size().width == size.width){
            videoWriter.write(mat);
            return mat;
        }
        else {
            mat = camera.capture();
            Size newSize = camera.getSize();
            size.set(new double[]{newSize.width, newSize.height});
            if (videoWriter.isOpened()) {
                videoWriter.release();
                File file = new File(pathToSave.toString() + "\\" + fileName);
                file.delete();
            }
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("ddMMyy-kkmmss");
            fileName = format.format(cal.getTime()) + ".avi";
            videoWriter = new VideoWriter(pathToSave.toString() + "\\" + fileName, VideoWriter.fourcc('M', 'J', 'P', 'G'), fps, size);
            if (!videoWriter.isOpened()) {
                System.out.print("Video couldn't start recording please check your path");
                close();
                System.exit(-1);
            }
            videoWriter.write(mat);
            return mat;
        }
    }

    public void startNewRecord(){
        if (videoWriter.isOpened())
            videoWriter.release();
        videoWriter.open(pathToSave.toString(), VideoWriter.fourcc('M','J','P','G'),fps,size);
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    public double getFps() {
        return fps;
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public void close() {
        camera.close();
        videoWriter.release();
    }

    public void release() {

        if(videoWriter != null && videoWriter.isOpened()) {

            try {
                videoWriter.release();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
