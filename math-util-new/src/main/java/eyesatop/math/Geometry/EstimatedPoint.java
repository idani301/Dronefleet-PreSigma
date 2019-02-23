package eyesatop.math.Geometry;

import java.util.ArrayList;

import Jama.Matrix;
import eyesatop.math.GeneralStaticMethods;

/**
 * Created by Einav on 10/07/2017.
 */

public class EstimatedPoint {

    private final Point3D point3D;
    private final Matrix errors;

    public EstimatedPoint(Point3D point3D, Matrix errors) {
        this.point3D = point3D;
        this.errors = errors;
    }

    public EstimatedPoint(ArrayList<Line3D> line3Ds, double sigmaLocation, double sigmaDirection){
        Matrix A = new Matrix(0,0);
        Matrix B = new Matrix(0,0);

        for (int i = 0; i < line3Ds.size(); i++) {
            A = addLineToMatrixA(A, line3Ds.get(i));
            B = addLineToMatrixB(B, line3Ds.get(i));
        }

        Matrix X = calcClosestPointMatrix(A,B);

        ArrayList<Point3D> locationErrors = new ArrayList<>();
        ArrayList<Point3D> directionErrors = new ArrayList<>();
        ArrayList<Double> linesLength = new ArrayList<>();

        for (int i = 0; i < line3Ds.size(); i++) {
            locationErrors.add(Point3D.cartesianPoint(sigmaLocation,sigmaLocation,sigmaLocation));
            directionErrors.add(Point3D.cartesianPoint(sigmaDirection,sigmaDirection,sigmaDirection));
            linesLength.add(-X.get(3+i,0));
        }

        point3D = Point3D.cartesianPoint(X.get(0,0),X.get(1,0),X.get(2,0));

        Matrix W = calcWMatrix(locationErrors, directionErrors, linesLength);

        Matrix A2 = A.transpose().times(A).inverse();
        Matrix varianceCovariance = A2.times(A.transpose()).times(W).times(A).times(A2);
        errors = new Matrix(3,3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                errors.set(i,j,varianceCovariance.get(i,j));
            }
        }
    }

    private Matrix calcWMatrix(ArrayList<Point3D> locationErrors, ArrayList<Point3D> directionErrors, ArrayList<Double> linesLength) {
        int matrixSize = linesLength.size()*3;
        Matrix matrix = new Matrix(matrixSize,matrixSize);
        int matrixPosition = 0;
        for (int i = 0; i < linesLength.size(); i++) {
            matrix.set(matrixPosition,matrixPosition,locationErrors.get(i).getX() + Math.abs(linesLength.get(i)*directionErrors.get(i).getX()));
            matrixPosition++;
            matrix.set(matrixPosition,matrixPosition,locationErrors.get(i).getY() + Math.abs(linesLength.get(i)*directionErrors.get(i).getY()));
            matrixPosition++;
            matrix.set(matrixPosition,matrixPosition,locationErrors.get(i).getZ() + Math.abs(linesLength.get(i)*directionErrors.get(i).getZ()));
            matrixPosition++;
        }
        matrix.times(matrix);
        return matrix;
    }

    public Point3D getPoint3D() {
        return point3D;
    }

    public Matrix getErrors() {
        return errors;
    }

    private Matrix addLineToMatrixErrors(Matrix Er, Point3D dx, Point3D dv, double t){
        double[][] tempB = Er.getArray();
        int lengthB = tempB.length;

        double[][] b = new double[lengthB+3][1];
        for (int i = 0; i < lengthB; i++) {
            b[i][0] = tempB[i][0];
        }
        b[lengthB][0] = Math.abs(dx.getX()) + Math.abs(dv.getX()*t);
        b[lengthB+1][0] = Math.abs(dx.getY()) + Math.abs(dv.getY()*t);
        b[lengthB+2][0] = Math.abs(dx.getZ()) + Math.abs(dv.getZ()*t);

        return new Matrix(b);
    }

    private Matrix addLineToMatrixB(Matrix B, Line3D line3D){
        double[][] tempB = B.getArray();
        int lengthB = tempB.length;

        double[][] b = new double[lengthB+3][1];
        for (int i = 0; i < lengthB; i++) {
            b[i][0] = tempB[i][0];
        }
        b[lengthB][0] = line3D.getPoint3D().getX();
        b[lengthB+1][0] = line3D.getPoint3D().getY();
        b[lengthB+2][0] = line3D.getPoint3D().getZ();

        return new Matrix(b);
    }

    private Matrix addLineToMatrixA(Matrix A, Line3D line3D){

        double[][] temp = A.getArray();
        double[][] temp1 =
                {{1,0,0},
                        {0,1,0},
                        {0,0,1}};

        int lengthA = temp.length;
        int widthA;
        double[][] a;
        if(lengthA ==0) {
            widthA = 0;
            a = new double[3][4];
        }
        else {
            widthA = temp[0].length;
            a = new double[lengthA+3][widthA+1];
        }

        for (int i = 0; i < lengthA; i++) {
            for (int j = 0; j < widthA; j++) {
                a[i][j] = temp[i][j];
            }
        }

        for (int i = lengthA; i < lengthA+3; i++) {
            for (int j = widthA; j < widthA+1; j++) {
                a[i][j] = 0;
            }
        }

        for (int i = lengthA; i < lengthA+3; i++) {
            for (int j = 0; j < 3; j++) {
                a[i][j] = temp1[i-lengthA][j];
            }
        }
        if(lengthA ==0)
            widthA = 3;
        a[lengthA][widthA] =   line3D.getDirection().getX();
        a[lengthA+1][widthA] = line3D.getDirection().getY();
        a[lengthA+2][widthA] = line3D.getDirection().getZ();

        return new Matrix(a);
    }

    private Matrix calcClosestPointMatrix(Matrix A, Matrix B){
        Matrix temp = A.transpose().times(A);
        temp = temp.inverse();
        temp = temp.times(A.transpose());
        return temp.times(B);
    }

    @Override
    public String toString() {
        return "EstimatedPoint{" +
                "\npoint3D=" + point3D.toStringCartesian() +
                ",\nerrors={\n" + GeneralStaticMethods.matrixToString(errors) +
                '}';
    }
}
