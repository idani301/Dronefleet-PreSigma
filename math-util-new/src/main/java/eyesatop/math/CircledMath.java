package eyesatop.math;

/**
 * Created by Einav on 06/09/2017.
 */

public class CircledMath {

    private final double x;
    private final double maxValue;

    public CircledMath(double x, double maxValue) {
        double temp = x%maxValue;
        if (temp < 0) {
            this.x = temp + maxValue;
        } else {
            this.x = x % maxValue;
        }
        this.maxValue = maxValue;
    }

    public CircledMath add(double var){
        double temp = (x + var)%maxValue;
        if (temp < 0)
            return new CircledMath(temp + maxValue , maxValue);
        return new CircledMath(temp , maxValue);
    }

    public CircledMath multiple(double var){
        double temp = (x * var)%maxValue;
        if (temp < 0)
            return new CircledMath(temp + maxValue , maxValue);
        return new CircledMath(temp , maxValue);    }

    public CircledMath sub(double var){
        double temp = (x - var)%maxValue;
        if (temp < 0)
            return new CircledMath(temp + maxValue , maxValue);
        return new CircledMath(temp , maxValue);    }

    public CircledMath divide(double var){
        double temp = (x / var)%maxValue;
        if (temp < 0)
            return new CircledMath(temp + maxValue , maxValue);
        return new CircledMath(temp , maxValue);    }

    public double getX() {
        return x;
    }

    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public String toString() {
        return "CircledMath{" +
                "x=" + x +
                ", maxValue=" + maxValue +
                '}';
    }
}
