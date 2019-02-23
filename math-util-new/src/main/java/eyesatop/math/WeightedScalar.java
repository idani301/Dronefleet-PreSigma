package eyesatop.math;

import Jama.Matrix;

/**
 * Created by Einav on 19/11/2017.
 */

public class WeightedScalar {

    private final double scalar;
    private final Matrix errors;

    protected WeightedScalar(double scalar, Matrix errors) {
        this.scalar = scalar;
        this.errors = errors;
    }

    public WeightedScalar(double scalar, double delta){
        this.scalar = scalar;
        errors = new Matrix(1,2);
        errors.set(0,0,delta);
        errors.set(0,1,delta);
    }

    public double value() {
        return scalar;
    }

    public Matrix getErrors() {
        return errors;
    }

    public double getAverageError(){
        return (errors.get(0,0) + errors.get(0,1))/2;
    }

    public WeightedScalar add(WeightedScalar weightedScalar){
        return new WeightedScalar(scalar + weightedScalar.scalar, Pol(getAverageError(),weightedScalar.getAverageError()));
    }

    public WeightedScalar minus(WeightedScalar weightedScalar){
        return new WeightedScalar(scalar - weightedScalar.scalar, Pol(getAverageError(),weightedScalar.getAverageError()));
    }

    public WeightedScalar multiple(WeightedScalar weightedScalar){
        return new WeightedScalar(scalar * weightedScalar.scalar, Pol(scalar * weightedScalar.getAverageError(), weightedScalar.scalar * getAverageError()));
    }

    public WeightedScalar divide(WeightedScalar weightedScalar){
        return new WeightedScalar(scalar / weightedScalar.scalar, Pol(getAverageError()/weightedScalar.scalar , -weightedScalar.getAverageError() * scalar/(weightedScalar.scalar*weightedScalar.scalar)));
    }

    private double Pol(double num1, double num2){
        return Math.sqrt(Math.pow(num1,2) + Math.pow(num2,2));
    }

    @Override
    public String toString() {
        return "" + scalar +
                "\u00B1" + getAverageError();
    }
}
