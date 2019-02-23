package eyesatop.math;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;

import java.util.ArrayList;

/**
 * Created by Einav on 03/05/2017.
 */

public class ParametricGaussianFunction implements ParametricUnivariateFunction {

    private final int numberOfConstParameters;
    private final int numberOfGaussiansInSeries;
    private final Double constParameter;

    private final int numberOfFreeParameters;
    private ArrayList<GaussianParameters> generalParameters;

    public ParametricGaussianFunction(ArrayList<GaussianParameters> constParameters, int numberOfGaussiansInSeries, Double constParameter) {
        this.numberOfGaussiansInSeries = numberOfGaussiansInSeries;
        this.constParameter = constParameter;
        if (constParameters == null)
            constParameters = new ArrayList<>();

        if (constParameter == null){
            this.numberOfConstParameters = constParameters.size();
        }
        else
            this.numberOfConstParameters = constParameters.size() + 1;

        numberOfFreeParameters = numberOfGaussiansInSeries*3 + 1 - numberOfConstParameters;
        createGeneralParametersArrayList(constParameters);
    }

    @Override
    public double value(double x, double... parameters) {
        if(!checkIfNumberOfParametersCorrect(parameters.length)){
            System.err.println("Wrong number of parameters!");
            return 1/0;
        }
        double value = 0;
        ArrayList<GaussianParameters> fullFunctionParameters = getFullFunction(parameters);
        if (constParameter != null){
            value += constParameter;
        }
        else {
            value += parameters[0];
        }
        for (int i = 0; i < fullFunctionParameters.size(); i+=3){
            value += fullFunctionParameters.get(i).getValueOfParameter()*valueOfExponent(x,fullFunctionParameters.get(i+1).getValueOfParameter(),fullFunctionParameters.get(i+2).getValueOfParameter());
        }
        return value;
    }


    @Override
    public double[] gradient(double x, double... parameters) {
        if(!checkIfNumberOfParametersCorrect(parameters.length)){
            System.err.println("Wrong number of parameters!");
            return null;
        }
        double[] gradient = new double[parameters.length];
        int i = 0;
        ArrayList<GaussianParameters> fullFunctionParameters = getFullFunction(parameters);
        if (constParameter == null){
            gradient[i] = 1;
            i++;
        }

        for (int j = 0; j < numberOfGaussiansInSeries; j++){
            double amplitude = fullFunctionParameters.get(3*j).getValueOfParameter();
            double bias = fullFunctionParameters.get(3*j + 1).getValueOfParameter();
            double sigma = fullFunctionParameters.get(3*j + 2).getValueOfParameter();

            if(generalParameters.get(3*j) == null){
                gradient[i] = valueOfExponent(x,bias,sigma);
                i++;
            }
            if(generalParameters.get(3*j + 1) == null){
                gradient[i] = amplitude*(x-bias)/(sigma*sigma)*valueOfExponent(x,bias,sigma);
                i++;
            }
            if (generalParameters.get(3*j + 2) == null){
                gradient[i] = amplitude*Math.pow(x-bias,2)/(sigma*sigma*sigma)*valueOfExponent(x,bias,sigma);
                i++;
            }
        }

        return gradient;
    }

    private void createGeneralParametersArrayList(ArrayList<GaussianParameters> constParameters){
        generalParameters = new ArrayList<>();
        for (int gaussianNumber = 1; gaussianNumber <= numberOfGaussiansInSeries; gaussianNumber++){
            int place = isParameterIsConstAndHisPlace(constParameters,gaussianNumber, GaussianParameters.GaussianParametersName.amplitude);
            if(place == -1){
                generalParameters.add(null);
            }
            else{
                generalParameters.add(constParameters.get(place));
            }
            place = isParameterIsConstAndHisPlace(constParameters,gaussianNumber, GaussianParameters.GaussianParametersName.bias);
            if(place == -1){
                generalParameters.add(null);
            }
            else{
                generalParameters.add(constParameters.get(place));
            }
            place = isParameterIsConstAndHisPlace(constParameters,gaussianNumber, GaussianParameters.GaussianParametersName.gaussianWidth);
            if(place == -1){
                generalParameters.add(null);
            }
            else{
                generalParameters.add(constParameters.get(place));
            }
        }
    }

    public ArrayList<GaussianParameters> getFullFunction(double... parameters){
        ArrayList<GaussianParameters> copyOfGeneralParameters = new ArrayList<>(generalParameters);

        int i = 0;
        if(constParameter == null) {
            i++;
        }
        for (int j = 0; j < copyOfGeneralParameters.size(); j++){
            if(copyOfGeneralParameters.get(j) == null){
                GaussianParameters.GaussianParametersName gaussianParametersName = null;
                switch (j%3){
                    case 0:
                        gaussianParametersName = GaussianParameters.GaussianParametersName.amplitude;
                        break;
                    case 1:
                        gaussianParametersName = GaussianParameters.GaussianParametersName.bias;
                        break;
                    case 2:
                        gaussianParametersName = GaussianParameters.GaussianParametersName.gaussianWidth;
                        break;
                }

                copyOfGeneralParameters.set(j,new GaussianParameters(parameters[i],j/3 + 1,gaussianParametersName));
                i++;
            }
        }

        return copyOfGeneralParameters;
    }

    private double valueOfExponent(double x, double bais, double sigma){
        return Math.exp(-Math.pow(x - bais,2)/(2*sigma*sigma));
    }

    private int isParameterIsConstAndHisPlace(ArrayList<GaussianParameters> constParameters, int numberOfGaussian, GaussianParameters.GaussianParametersName gaussianParametersName){

        for (int j = 0; j < constParameters.size(); j++){
            if(constParameters.get(j).getGaussianNumber() == numberOfGaussian && constParameters.get(j).getGaussianParametersName() == gaussianParametersName){
                return j;
            }
        }
        return -1;
    }


    private boolean checkIfNumberOfParametersCorrect(int length) {
        return (length + numberOfConstParameters == numberOfGaussiansInSeries*3 + 1);
    }

    public LeastSquaresOptimizer.Optimum getCalculatedParametersWithStartingParameters(WeightedObservedPoints points, double... start) throws Exception {

        if(!checkIfNumberOfParametersCorrect(start.length)){
            throw new Exception("Wrong Parameters for this function");
        }

        fitCurveFunction fitter = fitCurveFunction.create(this, start);
        return fitter.fitParameters(points.toList());
    }

    public int getNumberOfFreeParameters() {
        return numberOfFreeParameters;
    }
}
