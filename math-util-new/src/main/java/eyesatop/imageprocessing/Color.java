package eyesatop.imageprocessing;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 21/06/2017.
 */

public class Color {

    private final Point3D color;

    public Color(double red, double green, double blue) {
        red = limitOfColor(red);
        green = limitOfColor(green);
        blue = limitOfColor(blue);

        color = Point3D.cartesianPoint(red, green, blue);
    }

    public Color(double[] doubles){
        double red = limitOfColor(doubles[0]);
        double green = limitOfColor(doubles[1]);
        double blue = limitOfColor(doubles[2]);

        color = Point3D.cartesianPoint(red, green, blue);
    }

    private double limitOfColor(double color){
        if (color > 255)
            return 255;
        if (color < 0)
            return 0;
        return color;
    }

    public double getRed(){
        return color.getX();
    }

    public double getGreen(){
        return color.getY();
    }

    public double getBlue(){
        return color.getZ();
    }

    public double getColor(ColorName colorName){
        switch (colorName){
            case Blue:
                return getBlue();
            case Green:
                return getGreen();
            case Red:
                return getRed();
        }

        return -1;
    }
    @Override
    public String toString() {
        return "Color{" +
                "red=" + getRed() +
                ",green=" + getGreen() +
                ",blue=" + getBlue() +
                '}';
    }
}
