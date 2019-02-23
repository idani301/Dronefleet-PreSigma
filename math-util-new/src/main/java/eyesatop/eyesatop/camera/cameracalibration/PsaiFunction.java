package eyesatop.eyesatop.camera.cameracalibration;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;

/**
 * Created by Einav on 19/06/2017.
 */

public class PsaiFunction implements UnivariateDifferentiableFunction {

    private final double amplitude;
    private final double constant;
    private final double phase;
    private final double angel;
    private final double angularFrequency = 1;

    private final double B;

    public  PsaiFunction(double amplitude, double constant, double phase, double angel) {
        this.amplitude = amplitude;
        this.constant = constant;
        this.phase = phase;
        this.angel = angel;

        this.B = -constant - phase - angel;
    }

    @Override
    public DerivativeStructure value(DerivativeStructure derivativeStructure) throws DimensionMismatchException {
        return new DerivativeStructure(1,derivativeStructure.createConstant(B),-amplitude,derivativeStructure.sin(),+1,derivativeStructure);
    }

    @Override
    public double value(double x) {
        return B - amplitude*Math.sin(x) + x;
    }
}
