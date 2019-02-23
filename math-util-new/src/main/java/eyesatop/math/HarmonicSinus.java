package eyesatop.math;

import java.util.ArrayList;

/**
 * Created by Einav on 06/06/2017.
 */

public class HarmonicSinus implements MathFunction{

    private final double angularFrequency;
    private final double constParameter;

    private final ArrayList<Double> Amplitudes;
    private final ArrayList<Double> Phases;

    public HarmonicSinus(double constParameter, double angularFrequency, ArrayList<Double> amplitudes, ArrayList<Double> phases) throws Exception {
        this.angularFrequency = angularFrequency;
        this.constParameter = constParameter;
        Amplitudes = amplitudes;
        Phases = phases;
        if(Amplitudes.size() != Phases.size())
            throw new Exception("length of Amplitudes and Phases must be equal");

    }

    @Override
    public double value(double x) {

        double value = constParameter;

        for (int i = 0; i < Amplitudes.size(); i++) {
            double power = i+1;
            value += Amplitudes.get(i) * Math.sin(power*angularFrequency*x + Phases.get(i));
        }

        return value;
    }

    @Override
    public double derivative(double x) {

        double derivative = 0;
        for (int i = 0; i < Amplitudes.size(); i++) {
            double power = i+1;
            derivative += Amplitudes.get(i)*angularFrequency*power * Math.cos(power*angularFrequency*x + Phases.get(i));
        }
        return derivative;
    }

    @Override
    public String toString() {
        String result = constParameter + "";
        for (int i = 0; i < Amplitudes.size(); i++) {
            result += " + " + Amplitudes.get(i) + "*sin(x*" + angularFrequency + " + " + Phases.get(i) + ")";
        }
        return result;
    }
}
