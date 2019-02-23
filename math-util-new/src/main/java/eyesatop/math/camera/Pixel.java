package eyesatop.math.camera;

import java.io.Serializable;

import eyesatop.math.Geometry.Point2D;

/**
 * Created by Einav on 05/05/2017.
 */

public class Pixel extends Point2D implements Serializable{

    private final double size; // -1 if unknown


    public Pixel(double u, double v, double size) {
        super(u, -v);
        this.size = size;
    }

    public Pixel(double u, double v){
        super(u,-v);
        size = -1;

    }

    public static Pixel RadialPixel(double r, double teta, double size, Frame frame){
        double x = r*Math.cos(teta);
        double y = r*Math.sin(teta);
        Pixel center = frame.getCenter();
        return new Pixel(center.getU() + x, center.getV() - y,size);
    }

    public double getSize() {
        return size;
    }

    public double getRadius(Frame frame){
        return distance(frame.getCenter());
    }

    public double getAngle(Frame frame){
        return angle(frame.getCenter());
    }

    @Override
    public String toString() {
        return "Pixel{" + getU() + " , " + getV() +
                '}';
    }

    @Override
    public Pixel add(Point2D point2D) {
        return new Pixel(getU() + point2D.getU(), getV() + point2D.getV(),size);
    }
}
