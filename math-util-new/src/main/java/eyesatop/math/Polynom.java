package eyesatop.math;

import java.util.ArrayList;

import Jama.Matrix;
import eyesatop.math.Geometry.Point2D;

/**
 * Created by Einav on 15/05/2017.
 */

public class Polynom implements MathFunction {

    private final ArrayList<Double> parameters;

    public Polynom(ArrayList<Double> parameters) {
        this.parameters = parameters;
    }

    public static Polynom createPolynomFromStrings(ArrayList<String> parameters){
        ArrayList<Double> doubles = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            try {
                doubles.add(Double.parseDouble(parameters.get(i)));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }
        return new Polynom(doubles);
    }

    public Polynom(double[] parameters){
        this.parameters = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            this.parameters.add(parameters[i]);
        }
    }


    public static Polynom calcPolynomWithPoints(ArrayList<Point2D> point2Ds){

        int numberOfPoints = point2Ds.size();
        double[][] arrayX = new double[numberOfPoints][numberOfPoints];
        double[][] arrayY = new double[numberOfPoints][1];
        for (int i = 0; i < numberOfPoints; i++) {
            for (int j = 0; j < numberOfPoints; j++) {
                arrayX[i][j] = Math.pow(point2Ds.get(i).getX(),j);
                arrayY[i][0] = point2Ds.get(i).getY();
            }
        }

        Matrix A = new Matrix(arrayX);
        Matrix b = new Matrix(arrayY);

        Matrix x = A.solve(b);
        ArrayList<Double> parameters = new ArrayList<>();
        for (int i = 0; i < numberOfPoints; i++) {
            parameters.add(x.get(i,0));
        }

        return new Polynom(parameters);
    }

    public double[] getPrametersAsArray(){
        double[] doubles = new double[parameters.size()];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = parameters.get(i);
        }
        return doubles;
    }

    @Override
    public double value(double x) {
        double value = 0;
        for (int i = 0; i < parameters.size(); i++) {
            value += parameters.get(i)*Math.pow(x,i);
        }
        return value;
    }

    @Override
    public double derivative(double x) {
        double derivative = 0;
        for (int i = 1; i < parameters.size(); i++) {
            derivative += i*parameters.get(i)*Math.pow(x,i-1);
        }
        return derivative;
    }

    public int getPolynomDegree(){
        return parameters.size() - 1;
    }

    public ArrayList<Double> solve(double functionValue){
        ArrayList<Double> doubles = new ArrayList<>();
        switch (getPolynomDegree()){
            case 1:
                doubles.add((functionValue - parameters.get(0))/parameters.get(1));
                return doubles;
            case 2:
                return quadraticEquationSolver();
            case 3:
                return thirdDegreeEquationSolver();

        }
        return doubles;
    }

    private ArrayList<Double> quadraticEquationSolver(){
        ArrayList<Double> solve = new ArrayList<>();
        double a = parameters.get(2);
        double b = parameters.get(1);
        double c = parameters.get(0);

        double det = Math.pow(b,2) - 4*a*c;
        if (det < 0){
            return solve;
        }
        if (det == 0){
            solve.add(-b/(2*a));
            return solve;
        }
        det = Math.sqrt(det);
        solve.add((-b + det)/(2*a));
        solve.add((-b - det)/(2*a));

        return solve;
    }

    private ArrayList<Double> thirdDegreeEquationSolver(){
        ArrayList<Double> solve = new ArrayList<>();
        double a = parameters.get(3);
        double b = parameters.get(2);
        double c = parameters.get(1);
        double d = parameters.get(0);

        double q = (3*a*c - b*b)/(9*a*a);
        double r = (9*a*b*c - 27*a*a - 2*b*b*b);
        double det = q*q*q + r*r;

        return solve;
    }

    @Override
    public String toString() {
        return "Polynom{" +
                "parameters=" + parameters +
                '}';
    }
}
