package eyesatop.util.geometry;

/**
 * Created by Einav on 24/02/2017.
 */

public class Point2D {
    private final double x;
    private final double y;

    public Point2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Point2D rotate(double angle) {
        return rotate(angle, new Point2D(0, 0));
    }

    public Point2D rotate(double angle, Point2D refPoint){
        double x = this.x * Math.cos(angle) - this.y * Math.sin(angle);
        double y = this.x * Math.sin(angle) + this.y * Math.cos(angle);
        return new Point2D(refPoint.x + x, refPoint.y + y);
    }
}
