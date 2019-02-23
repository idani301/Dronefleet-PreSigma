package eyesatop.math;

/**
 * Created by Einav on 28/04/2017.
 */

public class PolynomDegreeParameters {

    private final int x_power;
    private final double parameter;

    public PolynomDegreeParameters(int x_power, double parameter) {
        this.x_power = x_power;
        this.parameter = parameter;
    }

    public int getX_power() {
        return x_power;
    }

    public double getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        return "PolynomDegreeParameters{" +
                "x_power=" + x_power +
                ", parameter=" + parameter +
                '}';
    }
}
