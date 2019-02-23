package eyesatop.math;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;

import java.util.ArrayList;

/**
 * Created by Einav on 30/04/2017.
 */

public class ParametricHarmonicFunction implements ParametricUnivariateFunction {

    private final int harmonicDegree;
    private final Double constParameter;
    private final Double AngularFrequency;
    private final int numberOfConstParameters;
    private ArrayList<HarmonicParameters> generalParameters;

    public ParametricHarmonicFunction(ArrayList<HarmonicParameters> constParameters, int harmonicDegree, Double constParameter, Double angularFrequency) {


        generalParameters = new ArrayList<>();
        this.harmonicDegree = harmonicDegree;
        this.constParameter = constParameter;
        AngularFrequency = angularFrequency;
        int degree = 1;
        while (degree <= harmonicDegree){
            int place = isParameterIsConstAndHisPlace(constParameters, degree, HarmonicParameters.HarmonicParametersNames.Amplitude);
            if(place == -1){
                generalParameters.add(null);
            }
            else{
                generalParameters.add(constParameters.get(place));
            }
            place = isParameterIsConstAndHisPlace(constParameters, degree, HarmonicParameters.HarmonicParametersNames.phase);
            if(place == -1){
                generalParameters.add(null);
            }
            else{
                generalParameters.add(constParameters.get(place));
            }
            degree++;
        }
        int size = 0;
        if (constParameters != null)
            size = constParameters.size();
        if(AngularFrequency != null)
            size++;
        if (constParameter != null)
            size++;
        numberOfConstParameters = size;

    }

    public ArrayList<HarmonicParameters> getGeneralParameters() {
        return generalParameters;
    }

    public int getHarmonicDegree() {
        return harmonicDegree;
    }


    @Override
    public double value(double x, double... parameters) {
        int length = 0;
        if(parameters != null){
            length = parameters.length;
        }
        double value = 0;
        double w;
        int i = 0;
        if(!checkIfNumberOfParametersCorrect(length)){
            System.err.println("Wrong number of parameters!");
            return 1/0;
        }
        ArrayList<HarmonicParameters> harmonicParameterses = getFullFunction(parameters);
        if(constParameter != null){
            value += constParameter.doubleValue();
        }
        else{
            value += parameters[i];
            i++;
        }

        if(AngularFrequency != null){
            w = AngularFrequency;
        }
        else {
            w = parameters[i];
            i++;
        }

        for (int degree = 1; degree <= harmonicDegree; degree++){
            double amplitude = harmonicParameterses.get(degree*2 - 2).getParameterValue();
            double phase = harmonicParameterses.get(degree*2 - 1).getParameterValue();

            value += amplitude*valueOfSinusWithParameters(degree,w,x,phase);

        }
        return value;
    }

    @Override
    public double[] gradient(double x, double... parameters) {
        if(!checkIfNumberOfParametersCorrect(parameters.length)){
            System.err.println("Wrong number of parameters!");
            return null;
        }
        int i = 0;
        double[] gradient = new double[parameters.length];
        ArrayList<HarmonicParameters> harmonicParameterses = getFullFunction(parameters);
        if(constParameter == null){
            gradient[i] = 1;
            i++;
        }
        double w;
        if (AngularFrequency != null){
            w = AngularFrequency.doubleValue();
        }
        else{
            w = parameters[i];
            gradient[i] = valueOfDerivativeByAngularFrequency(harmonicParameterses,w,x);
            i++;
        }
        for (int degree = 1; degree <= harmonicDegree; degree++){
            if(generalParameters.get(degree*2 - 2) == null){
                gradient[i] = valueOfSinusWithParameters(degree,w,x,harmonicParameterses.get(degree*2 - 1).getParameterValue());
                i++;
            }
            if (generalParameters.get(degree*2 - 1) == null){
                gradient[i] = harmonicParameterses.get(degree*2 - 2).getParameterValue()*valueOfCosineWithParameters(degree,w,x,harmonicParameterses.get(degree*2 - 1).getParameterValue());
                i++;
            }

        }

        return gradient;
    }

    private int isParameterIsConstAndHisPlace(ArrayList<HarmonicParameters> constParameters, int degree, HarmonicParameters.HarmonicParametersNames harmonicParametersNames){

        if (constParameters == null)
            return -1;

        for (int j = 0; j < constParameters.size(); j++){
            if(constParameters.get(j).getPower() == degree && constParameters.get(j).getHarmonicParametersNames() == harmonicParametersNames){
                return j;
            }
        }
        return -1;
    }

    private boolean checkIfNumberOfParametersCorrect(int length){
        return (length + numberOfConstParameters == harmonicDegree*2 + 2);
    }

    public ArrayList<HarmonicParameters> getFullFunction(double... parameters){
        ArrayList<HarmonicParameters> copyOfGeneralParameters = new ArrayList<>(generalParameters);

        int i = 0;
        if(constParameter == null) {
            i++;
        }
        if(AngularFrequency == null){
            i++;
        }
        for (int j = 0; j < copyOfGeneralParameters.size(); j++){
            if(copyOfGeneralParameters.get(j) == null){
                HarmonicParameters.HarmonicParametersNames harmonicParametersNames = HarmonicParameters.HarmonicParametersNames.phase;
                if(j%2 == 0){
                    harmonicParametersNames = HarmonicParameters.HarmonicParametersNames.Amplitude;
                }
                copyOfGeneralParameters.set(j,new HarmonicParameters(j/2 +1,harmonicParametersNames ,parameters[i]));
                i++;
            }
        }

        return copyOfGeneralParameters;
    }

    private double valueOfDerivativeByAngularFrequency(ArrayList<HarmonicParameters> harmonicFunction, double w, double x){
        double value = 0;
        for (int degree = 1; degree <= harmonicDegree; degree++){
            value += degree*x*harmonicFunction.get(degree/2).getParameterValue()*valueOfCosineWithParameters(degree,w,x,harmonicFunction.get(degree/2+1).getParameterValue());
        }

        return value;
    }

    private double valueOfSinusWithParameters(int degree, double angularFrequency, double x, double phase){
        return Math.sin(degree*angularFrequency*x + phase);
    }

    private double valueOfCosineWithParameters(int degree, double angularFrequency, double x, double phase){
        return Math.cos(degree*angularFrequency*x + phase);
    }

    public LeastSquaresOptimizer.Optimum getCalculatedParameters(WeightedObservedPoints points){

        double[] start = new double[harmonicDegree*2 + 2 - numberOfConstParameters];
        for (int i = 0; i < start.length; i++){
            start[i] = 1;
        }

        fitCurveFunction fitter = fitCurveFunction.create(this, start);
        return fitter.fitParameters(points.toList());

    }

    public LeastSquaresOptimizer.Optimum getCalculatedParametersWithStartingParameters(WeightedObservedPoints points, double... start){

        if(start.length != harmonicDegree*2 + 2 - numberOfConstParameters){
            System.err.println("Wrong Parameters for this function");
            return null;
        }

        fitCurveFunction fitter = fitCurveFunction.create(this, start);
        return fitter.fitParameters(points.toList());
    }
}
