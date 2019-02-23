package eyesatop.math;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;

import java.util.ArrayList;

/**
 * Created by Einav on 27/04/2017.
 */

public class ParametricPolynomFunction implements ParametricUnivariateFunction {

    private final ArrayList<PolynomDegreeParameters> constParameters;
    private final int numberOfConstParameters;

    private final int polynomDegree;

    public ParametricPolynomFunction(ArrayList<PolynomDegreeParameters> constParameters, int polynomDegree) {
        if (constParameters == null) {
            numberOfConstParameters = 0;
        }
        else {
            numberOfConstParameters = constParameters.size();
        }
        this.constParameters = constParameters;
        this.polynomDegree = polynomDegree;
    }

    public static ParametricPolynomFunction noConstPolynom(int polynomDegree){
        ArrayList<PolynomDegreeParameters> polynomDegreeParameterses = new ArrayList<>();
        polynomDegreeParameterses.add(new PolynomDegreeParameters(0,0));
        return new ParametricPolynomFunction(polynomDegreeParameterses,polynomDegree);
    }

    public static ParametricPolynomFunction OddPolynom(int ploynomDegree){
        ArrayList<PolynomDegreeParameters> polynomDegreeParameterses = new ArrayList<>();
        for (int i=0 ; i < ploynomDegree ; i++){
            if(i%2 == 0){
                polynomDegreeParameterses.add(new PolynomDegreeParameters(i,0));
            }
        }
        return new ParametricPolynomFunction(polynomDegreeParameterses,ploynomDegree);
    }

    public static ParametricPolynomFunction DoublePolynom(int ploynomDegree){
        ArrayList<PolynomDegreeParameters> polynomDegreeParameterses = new ArrayList<>();
        for (int i=0 ; i < ploynomDegree ; i++){
            if(i%2 == 1){
                polynomDegreeParameterses.add(new PolynomDegreeParameters(i,0));
            }
        }
        return new ParametricPolynomFunction(polynomDegreeParameterses,ploynomDegree);
    }

    public ArrayList<PolynomDegreeParameters> getConstParameters() {
        return constParameters;
    }

    public int getNumberOfConstParameters() {
        return numberOfConstParameters;
    }

    public int getPolynomDegree() {
        return polynomDegree;
    }

    @Override
    public double value(double x, double... param) {
        double value = 0;
        int length = param.length;
        if(length + numberOfConstParameters - 1 != polynomDegree) {
            System.err.println("Wrong Parameters for this function");
            return 1 / 0;
        }
        int i = 0;
        int degree = 0;
        while(degree <= polynomDegree){
            int j;
            for (j = 0; j < numberOfConstParameters ; j++){
                    if(constParameters.get(j).getX_power() == degree){
                        value += constParameters.get(j).getParameter()*Math.pow(x,degree);
                        break;
                    }
            }
            if(j == numberOfConstParameters){
                value += param[i]*Math.pow(x,degree);
                i++;
            }
            degree++;
        }
        return value;
    }

    @Override
    public double[] gradient(double x, double... parameters) {
        int length = parameters.length;
        if(length + numberOfConstParameters - 1 != polynomDegree) {
            System.err.println("Wrong Parameters for this function");
            return null;
        }
        double[] grad = new double[parameters.length];
        int degree = 0;
        int i = 0;
        while (degree <= polynomDegree){
            int j;
            for(j = 0; j < numberOfConstParameters; j++){
                if(constParameters.get(j).getX_power() == degree){
                    break;
                }
            }
            if(j == numberOfConstParameters) {
                grad[i] = Math.pow(x, degree);
                i++;
            }
            degree++;
        }
        return grad;
    }

    public LeastSquaresOptimizer.Optimum getCalculatedParameters(WeightedObservedPoints points){

        double[] start = new double[polynomDegree - numberOfConstParameters + 1];
        for (int i = 0; i < start.length; i++){
            start[i] = 1;
        }

        fitCurveFunction fitter = fitCurveFunction.create(this, start);
        return fitter.fitParameters(points.toList());

    }






}
