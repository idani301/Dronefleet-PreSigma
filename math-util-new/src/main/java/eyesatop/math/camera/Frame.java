package eyesatop.math.camera;

import java.util.ArrayList;

/**
 * Created by Einav on 05/05/2017.
 */

public class Frame {

    private final Pixel center;
    private final double width;
    private final double height;

    public Frame(Pixel center, double width, double height) {
        this.center = center;
        this.width = width;
        this.height = height;
    }

    public Frame(double width, double height) {
        this.width = width;
        this.height = height;

        center = new Pixel(width/2,height/2,-1);

    }

    public boolean isInFrame(Pixel pixel){
        if (pixel.getU() < 0 || pixel.getU() > width || pixel.getV() < 0 || pixel.getV() > height)
            return false;
        return true;

    }

    public ArrayList<Pixel> getFrameBorderPixels(){
        ArrayList<Pixel> pixels = new ArrayList<>();
        pixels.add(new Pixel(1,1,center.getSize()));
        pixels.add(new Pixel(width,1,center.getSize()));
        pixels.add(new Pixel(width,height,center.getSize()));
        pixels.add(new Pixel(1,height,center.getSize()));

        return pixels;
    }

    public Pixel getCenter() {
        return center;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "center=" + center +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
