package eyesatop.math;

/**
 * Created by Einav on 30/04/2017.
 */

public class HarmonicParameters {

    public enum HarmonicParametersNames{
        Amplitude,
        phase;
    }

    private final int power;
    private final HarmonicParametersNames harmonicParametersNames;
    private final double parameterValue;

    public HarmonicParameters(int power, HarmonicParametersNames harmonicParametersNames, double parameterValue) {
        this.power = power;
        this.harmonicParametersNames = harmonicParametersNames;
        this.parameterValue = parameterValue;
    }

    public int getPower() {
        return power;
    }

    public HarmonicParametersNames getHarmonicParametersNames() {
        return harmonicParametersNames;
    }

    public double getParameterValue() {
        return parameterValue;
    }
}
