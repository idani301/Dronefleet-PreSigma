package eyesatop.math.camera;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//import javax.imageio.ImageIO;

import eyesatop.eyesatop.camera.cameracalibration.GeneralCameraModuleIn;
import eyesatop.eyesatop.util.readfromtxtfile.ReadHeaders;
import eyesatop.imageprocessing.Color;
import eyesatop.imageprocessing.LoadOpenCVDLL;
import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.Time;

/**
 * Created by Einav on 10/07/2017.
 */

public class Image {

    static {
        new LoadOpenCVDLL();
    }

    private final ImageInfo imageInfo;
    private final Mat image;

    public Image(ImageInfo imageInfo, Mat image) {
        this.imageInfo = imageInfo;
        this.image = image;
    }

    public static Image ReadRawImageFromFile(File imageFile, double focalLength){
        Mat image = Imgcodecs.imread(imageFile.getAbsolutePath());
        ImageInfo imageInfo = new ImageInfo(
                new PinHoleCameraModule(new Frame(image.width(),image.height()),focalLength),
                RotationMatrix3D.Body3DNauticalAngles(Math.toRadians(0),0,Math.toRadians(0)),
                null,
                null
        );
        return new Image(imageInfo,image);
    }

    public static Image ReadImageFromCameraSavedImage(File imageFile, RotationMatrix3D rotationMatrix3D) throws Exception {

        Mat mat = Imgcodecs.imread(imageFile.getAbsolutePath());
        String cameraSN = Long.toString(ReadHeaders.ReadCameraSN(imageFile));
        File cameraModuleFile = new File("C:\\eyeatop\\cameraModule\\" + cameraSN + ".txt");
        CameraModule cameraModule = GeneralCameraModuleIn.ReadGeneralCameraModule(cameraModuleFile);
        Location location = ReadHeaders.ReadCameraLocation(imageFile);
        Time time = ReadHeaders.ReadCameraTime(imageFile);
        return new Image(new ImageInfo(cameraModule,rotationMatrix3D,location,time),mat);
    }



    public BufferedImage getBufferedImage(){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( image.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = image.channels()*image.cols()*image.rows();
        byte [] b = new byte[bufferSize];
        image.get(0,0,b); // get all the pixels
        BufferedImage bufferedImage = new BufferedImage(image.cols(),image.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return bufferedImage;
    }

    public Color getPixelValue(Pixel pixel){
        double[] doubles = image.get((int)pixel.getU(),(int)pixel.getV());
        return new Color(doubles);
    }

    public boolean saveRawImage(File file){
        return Imgcodecs.imwrite(file.getAbsolutePath(),image);
    }

    public void saveImage(File file) throws IOException {
//        ImageIO.write(getBufferedImage(), "jpg", file);
        System.out.println("ImageInfo was saved: " + file.getAbsolutePath());
    }

    public ArrayList<Color> getArrayOfPixelsColor(ArrayList<Pixel> pixels){
        ArrayList<Color> colors = new ArrayList<>();
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( image.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = image.channels()*image.cols()*image.rows();
        byte [] b = new byte[bufferSize];
        image.get(0,0,b); // get all the pixels
        for (int i = 0; i < pixels.size(); i++) {
            Pixel pixel = pixels.get(i);
            double red = unsignedByte(b[place(pixel)*3]);
            double green = unsignedByte(b[place(pixel)*3 + 1]);
            double blue = unsignedByte(b[place(pixel)*3 + 2]);
            colors.add(new Color(red,green,blue));
        }

        return colors;
    }

    private double unsignedByte(byte b){
        return b&0xFF;
    }

    private int place(Pixel pixel){
        return (int) ((int)(pixel.getV())*imageInfo.getCameraModule().getFrame().getWidth() + (int)(pixel.getU()));
    }

    public Mat getImage() {
        return image;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageInfo=" + imageInfo +
                ", image={" + image.size().width + "*" + image.size().height + "}" +
                '}';
    }
}
