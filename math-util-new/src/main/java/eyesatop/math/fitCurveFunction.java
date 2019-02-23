package eyesatop.math;

import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;

import java.util.Collection;


public class fitCurveFunction extends AbstractCurveFitter { 

    private final ParametricUnivariateFunction function; 
    private final double[] initialGuess; 
    private final int maxIter; 
  
    private fitCurveFunction(ParametricUnivariateFunction function, 
                              double[] initialGuess, 
                              int maxIter) { 
        this.function = function; 
        this.initialGuess = initialGuess; 
        this.maxIter = maxIter; 
    } 
 
    public static fitCurveFunction create(ParametricUnivariateFunction f, 
            double[] start){
    	return new fitCurveFunction(f, start, Integer.MAX_VALUE);
    }


    public fitCurveFunction withStartPoint(double[] newStart) { 
        return new fitCurveFunction(function, 
                                     newStart.clone(), 
                                     maxIter); 
    } 

    public fitCurveFunction withMaxIterations(int newMaxIter) { 
        return new fitCurveFunction(function, 
                                     initialGuess, 
                                     newMaxIter); 
    } 
 
	@Override
	protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> observations) {
        // Prepare least-squares problem. 
        final int len = observations.size(); 
        final double[] target  = new double[len]; 
        final double[] weights = new double[len]; 
 
        int count = 0; 
        for (WeightedObservedPoint obs : observations) { 
            target[count]  = obs.getY(); 
            weights[count] = obs.getWeight(); 
            ++count; 
        } 
 
        final TheoreticalValuesFunction model
            = new TheoreticalValuesFunction(function,
                                                                observations); 
 
        // Create an optimizer for fitting the curve to the observed points. 
        return new LeastSquaresBuilder(). 
                maxEvaluations(Integer.MAX_VALUE). 
                maxIterations(maxIter). 
                start(initialGuess). 
                target(target). 
                weight(new DiagonalMatrix(weights)). 
                model(model.getModelFunction(), model.getModelFunctionJacobian()). 
                build(); 
	}

    public LeastSquaresOptimizer.Optimum fitParameters(Collection<WeightedObservedPoint> points){
        return this.getOptimizer().optimize(this.getProblem(points));
    }
}