package eyesatop.math.Geometry;

import java.util.ArrayList;

import Jama.Matrix;

/**
 * Created by Einav on 09/07/2017.
 */

public class ClosestPoint {

    private final Point3D point3D;
    private final Point3D point3DErrors;
    private final ArrayList<Point3D> pointsOnLines = new ArrayList<>();
    private final ArrayList<Double> pointsOnLinesErrors = new ArrayList<>();

    private final Matrix residualVector;

    public ClosestPoint(ArrayList<Line3D> line3Ds, double sigmaLocation, double sigmaDirection) {

        Matrix A = new Matrix(0,0);
        Matrix B = new Matrix(0,0);
        Matrix Er = new Matrix(0,0);

        for (int i = 0; i < line3Ds.size(); i++) {
            A = addLineToMatrixA(A, line3Ds.get(i));
            B = addLineToMatrixB(B, line3Ds.get(i));
        }

        Matrix X = calcClosestPointMatrix(A,B);
        residualVector = calcResidualVector(A,X,B);

        for (int i = 0; i < line3Ds.size(); i++) {
            Er = addLineToMatrixErrors(
                    Er,
                    Point3D.cartesianPoint(sigmaLocation,sigmaLocation,sigmaLocation),
                    Point3D.cartesianPoint(sigmaDirection,sigmaDirection,sigmaDirection),
                    -X.get(3+i,0)
            );
        }
        Matrix I = Matrix.identity(6,6);
        prinMatrix(A.transpose().times(A).inverse());
//        prinMatrix(X);
        Er = Er.plus(residualVector);
        Matrix Errors = calcClosestPointMatrix(A,Er);
        point3DErrors = Point3D.cartesianPoint(Errors.get(0,0),Errors.get(1,0),Errors.get(2,0));
        point3D = Point3D.cartesianPoint(X.get(0,0),X.get(1,0),X.get(2,0));
        for (int i = 0; i < line3Ds.size(); i++) {
            pointsOnLines.add(line3Ds.get(i).getPointOnLine(-X.get(3+i,0)));
            pointsOnLinesErrors.add(-Errors.get(3+i,0));
        }

    }

    public ClosestPoint(ArrayList<Line3D> line3Ds, ArrayList<Line3D> line3DsErrors) {

        Matrix A = new Matrix(0,0);
        Matrix B = new Matrix(0,0);
        Matrix Er = new Matrix(0,0);

        for (int i = 0; i < line3Ds.size(); i++) {
            A = addLineToMatrixA(A, line3Ds.get(i));
            B = addLineToMatrixB(B, line3Ds.get(i));
        }

        Matrix X = calcClosestPointMatrix(A,B);

        for (int i = 0; i < line3Ds.size(); i++) {
            Er = addLineToMatrixErrors(Er,line3DsErrors.get(i).getPoint3D(),line3DsErrors.get(i).getDirection(),-X.get(3+i,0));
        }

        Matrix Errors = calcClosestPointMatrix(A,Er);
        point3DErrors = Point3D.cartesianPoint(Errors.get(0,0),Errors.get(1,0),Errors.get(2,0));

        point3D = Point3D.cartesianPoint(X.get(0,0),X.get(1,0),X.get(2,0));
        for (int i = 0; i < line3Ds.size(); i++) {
            pointsOnLines.add(line3Ds.get(i).getPointOnLine(-X.get(3+i,0)));
        }

        residualVector = calcResidualVector(A,X,B);
        prinMatrix(A);
    }

    public Point3D getPoint3D() {
        return point3D;
    }

    public Point3D getPoint3DErrors() {
        return point3DErrors;
    }

    public ArrayList<Double> getPointsOnLinesErrors() {
        return pointsOnLinesErrors;
    }

    public ArrayList<Point3D> getPointsOnLines() {
        return pointsOnLines;
    }

    private Matrix calcClosestPointMatrix(Matrix A, Matrix B){
        Matrix temp = A.transpose().times(A);
        temp = temp.inverse();
        temp = temp.times(A.transpose());
        return temp.times(B);
    }

    public void printResidal(){
        double[][] a = residualVector.getArray();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + " ,");
            }
            System.out.println("");
        }
    }

    public void prinMatrix(Matrix A){
        double[][] a = A.getArray();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.print(a[i][j] + " ,");
            }
            System.out.println("");
        }
    }


    public Point3D getPointErrorEstimation(){
        return Point3D.cartesianPoint(residualVector.get(0,0),residualVector.get(1,0),residualVector.get(2,0));
    }

    private ArrayList<Double> getErrorsEstimationOnLines(){
        ArrayList<Double> er = new ArrayList<>();
        for (int i = 0; i < pointsOnLines.size(); i++) {
            er.add(residualVector.get(3 + i,0));
        }
        return er;
    }

    public Matrix calcResidualVector(Matrix A, Matrix X, Matrix B){
        return A.times(X).minus(B);
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
}
