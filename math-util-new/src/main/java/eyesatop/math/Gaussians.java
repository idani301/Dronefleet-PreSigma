package eyesatop.math;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;

import java.util.ArrayList;

/**
 * Created by Einav on 21/06/2017.
 */

public class Gaussians implements MathFunction, UnivariateDifferentiableFunction {

    private final double constParameter;

    private final ArrayList<Double> amplitude;
    private final ArrayList<Double> bias;
    private final ArrayList<Double> gaussianWidth;

    public Gaussians(double constParameter, ArrayList<Double> amplitude, ArrayList<Double> bias, ArrayList<Double> gaussianWidth) {
        this.constParameter = constParameter;
        this.amplitude = amplitude;
        this.bias = bias;
        this.gaussianWidth = gaussianWidth;
    }

    public Gaussians(double... parameters) throws Exception {
        ArrayList<Double> amplitude = new ArrayList<>();
        ArrayList<Double> bias = new ArrayList<>();
        ArrayList<Double> gaussianWidth = new ArrayList<>();
        if(parameters.length%3 != 1)
            throw new Exception("Wrong number of parameters.");
        this.constParameter = parameters[0];
        for (int i = 1; i < parameters.length; i+=3) {
            amplitude.add(parameters[i]);
            bias.add(parameters[i+1]);
            gaussianWidth.add(parameters[i+2]);
        }

        this.amplitude = amplitude;
        this.bias = bias;
        this.gaussianWidth = gaussianWidth;
    }

    @Override
    public double value(double x) {
        double value = 0;
        value += constParameter;

        for (int i = 0; i < amplitude.size(); i++) {
            double amp = amplitude.get(i);
            double sigma = gaussianWidth.get(i);
            double bias = this.bias.get(i);
            value += amp*Math.exp(-Math.pow((x - bias),2)/(2*sigma*sigma));
        }

        return value;
    }

    @Override
    public double derivative(double x) {
        return 0;
    }

    @Override
    public DerivativeStructure value(DerivativeStructure derivativeStructure) throws DimensionMismatchException {
        return null;
    }
}
