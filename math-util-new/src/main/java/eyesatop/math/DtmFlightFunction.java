package eyesatop.math;

/**
 * Created by Einav on 06/12/2017.
 */

public class DtmFlightFunction implements MathFunction{

    private final double a;
    private final double b;
    private final double c;


    /**
     * f(x) = a/r^2 + b*r^2 + c
     *
     * @param a
     * @param b
     * @param c
     */
    public DtmFlightFunction(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static DtmFlightFunction createDTMFlightFunctionFromGaps(double downGap, double upGap){
        double downGap2 = downGap*downGap;
        double upGap2 = upGap*upGap;
        double gg = 1/downGap2 - 1/upGap2;
        double gama = downGap2 - upGap2;
        double alpha = -gama/gg;
        double beta = alpha/downGap2 + downGap2 + 2*Math.sqrt(alpha);

        double a = alpha/beta;
        double b = 1/beta;
        double c = (2/beta)*Math.sqrt(alpha);

        return new DtmFlightFunction(a,b,c);
    }

    @Override
    public double value(double x) {
        double x2 = x*x;
        return a/x2 + b*x2 + c;
    }

    @Override
    public double derivative(double x) {
        return -2*a/Math.pow(x,3) + 2*b*x;
    }

    @Override
    public String toString() {
        return "DtmFlightFunction{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
