package eyesatop.math;

import java.util.ArrayList;

import Jama.Matrix;

/**
 * Created by Einav on 01/12/2017.
 */

public class LinearEquations {

    private final int numberOfVariables;
    private final ArrayList<Double[]> equations = new ArrayList<>();

    public LinearEquations(int numberOfVariables) {
        this.numberOfVariables = numberOfVariables;
    }

    /**
     *
     * @param equation
     *
     * last place at equation array is the free parameter
     *
     * @throws MathException
     */
    public void addEquation(Double[] equation) throws MathException {
        if (equation.length != numberOfVariables + 1)
            throw new MathException(MathException.MathExceptionCause.general);
        equations.add(equation);
    }

    public double[] solve() throws MathException {
        if (equations.size() < numberOfVariables)
            throw new MathException(MathException.MathExceptionCause.infinity);


        double[][] tempMatrix = new double[equations.size()][numberOfVariables];
        double[][] freeParameters = new double[equations.size()][1];

        for (int i = 0; i < equations.size(); i++) {
            for (int j = 0; j < numberOfVariables; j++) {
                tempMatrix[i][j] = equations.get(i)[j];
            }
            freeParameters[i][0] = -equations.get(i)[numberOfVariables];
        }

        Matrix eqMatrix = new Matrix(tempMatrix);
        Matrix freeMatrix = new Matrix(freeParameters);

        Matrix tempSolution = calcClosestPointMatrix(eqMatrix,freeMatrix);

        double[] solution = new double[numberOfVariables];
        for (int i = 0; i < numberOfVariables; i++) {
            solution[i] = tempSolution.get(i,0);
        }
        return solution;
    }

    private Matrix calcClosestPointMatrix(Matrix A, Matrix B){
        Matrix temp = A.transpose().times(A);
        temp = temp.inverse();
        temp = temp.times(A.transpose());
        return temp.times(B);
    }

    public int getNumberOfVariables() {
        return numberOfVariables;
    }
}
