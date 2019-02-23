package eyesatop.eyesatop.camera.sun;

import java.util.ArrayList;

import eyesatop.imageprocessing.Color;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.camera.CameraModule;
import eyesatop.math.camera.Image;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 20/06/2017.
 */

public class SunTrace {

    private final ArrayList<Color> colors;
    private final ArrayList<Double> azimuths;


    public SunTrace(ArrayList<Color> colors, ArrayList<Double> azimuth){
        this.colors = colors;
        this.azimuths = azimuth;
    }

    public SunTrace(Color color,double azimuth){
        this.colors = new ArrayList<>();
        this.azimuths = new ArrayList<>();
        colors.add(color);
        azimuths.add(azimuth);
    }

    public static SunTrace getArrayOfPixelsFromLineInTheRealWorld(Image image, double elevation, double deltaAngle){
        ArrayList<Pixel> pixels = new ArrayList<>();
        ArrayList<Double> azimuths = new ArrayList<>();
        CameraModule cameraModule = image.getImageInfo().getCameraModule();
        RotationMatrix3D rotationMatrix3D = image.getImageInfo().getRotationMatrix3D();
        int numberOfPixel = (int) (360/deltaAngle);
        int count = 0;
        for (int i = 0; i < numberOfPixel; i++) {
            double azimuth = Math.toRadians(i*deltaAngle);
            Pixel pixel = cameraModule.getPixelFromPoint3D(Point3D.ElevationAzimuthPointOfView(elevation,azimuth).rotate(rotationMatrix3D));
            if (cameraModule.getFrame().isInFrame(pixel)) {
                if(pixels.size() == 0){
                    pixels.add(pixel);
                    azimuths.add(azimuth);
                }
                else if (pixels.get(pixels.size()-1).distance(pixel) > 1){
                    pixels.add(pixel);
                    azimuths.add(azimuth);
                }
            }
            else{
                System.out.print("");
            }
        }
        return new SunTrace(image.getArrayOfPixelsColor(pixels),azimuths);
    }

    public ArrayList<Color> getColors() {
        return colors;
    }

    public ArrayList<Double> getAzimuths() {
        return azimuths;
    }

    public Color getOneColor(int i){
        return colors.get(i);
    }
    public double getOneAzimuth(int i){
        return azimuths.get(i);
    }

    public Color getOneColor(){
        return colors.get(0);
    }
    public double getOneAzimuth(){
        return azimuths.get(0);
    }

    public SunTrace getSunTrace(int i){
        return new SunTrace(colors.get(i), azimuths.get(i));
    }

    public double size() {
        return colors.size();
    }
}
