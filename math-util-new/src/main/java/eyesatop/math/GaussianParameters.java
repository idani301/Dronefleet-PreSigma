package eyesatop.math;

/**
 * Created by Einav on 03/05/2017.
 */

public class GaussianParameters {

    enum GaussianParametersName{
        amplitude,
        bias,
        gaussianWidth;
    }

    private final double valueOfParameter;
    private final int gaussianNumber;
    private final GaussianParametersName gaussianParametersName;

    public GaussianParameters(double valueOfParameter, int numberOfGaussian, GaussianParametersName gaussianParametersName) {
        this.valueOfParameter = valueOfParameter;
        this.gaussianNumber = numberOfGaussian;
        this.gaussianParametersName = gaussianParametersName;
    }

    public GaussianParameters(double valueOfParameter, GaussianParametersName gaussianParametersName) {
        this.valueOfParameter = valueOfParameter;
        this.gaussianParametersName = gaussianParametersName;
        gaussianNumber = 1;
    }

    public double getValueOfParameter() {
        return valueOfParameter;
    }

    public int getGaussianNumber() {
        return gaussianNumber;
    }

    public GaussianParametersName getGaussianParametersName() {
        return gaussianParametersName;
    }
}
