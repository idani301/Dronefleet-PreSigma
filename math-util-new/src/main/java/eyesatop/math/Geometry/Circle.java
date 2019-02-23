package eyesatop.math.Geometry;

import java.util.ArrayList;

import eyesatop.math.MathException;
import eyesatop.math.Polynom;

/**
 * Created by Einav on 27/09/2017.
 */

public class Circle {

    private final double radius;
    private final Point2D center;

    public Circle(double radius, Point2D center) {
        this.radius = radius;
        this.center = center;
    }

    public static Circle circleOutOfPointAndCenter(Point2D point2D, Point2D center){
        return new Circle(point2D.distance(center),center);
    }

    public static Circle bestCircleOutOfPoints(ArrayList<Point2D> point2Ds) throws MathException {

        ArrayList<Line3D> line3Ds = new ArrayList<>();
        for (int i = 0; i < point2Ds.size(); i++) {
            for (int j = point2Ds.size()-1; j > i; j--) {
                Point2D middlePoint = point2Ds.get(i).middlePoint(point2Ds.get(j));
                Line2D line2D = Line2D.getLineOutOfTwoPoints(point2Ds.get(i),point2Ds.get(j));
                line3Ds.add(Line2D.getLineFromSlopeAndPoint(middlePoint,line2D.getPerpendicularSlope()).getLine3D());
            }
        }

        EstimatedPoint estimatedPoint = new EstimatedPoint(line3Ds,1,1);
        if (Math.abs(estimatedPoint.getPoint3D().getZ()) > 0.001)
            throw new MathException(MathException.MathExceptionCause.general);
        Point2D center = Point2D.cartesianPoint(estimatedPoint.getPoint3D().getX(),estimatedPoint.getPoint3D().getY());
        double radius = 0;
        for (int i = 0; i < point2Ds.size(); i++) {
            radius += center.distance(point2Ds.get(i));
        }
        radius /= point2Ds.size();

        return new Circle(radius,center);
    }

    public Point2D getPointOnCircle(Angle angle){
        return Point2D.polarPoint(radius,angle.radian()).add(center);
    }

    public double getRadius() {
        return radius;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getPerimeter(){
        return radius*2*Math.PI;
    }

    public double getArea(){
        return Math.pow(radius,2)*Math.PI;
    }

    public ArrayList<Double> getY(double x){
        ArrayList<Double> solve = new ArrayList<>();
        double q = x - center.getX();
        if (Math.abs(q) > radius)
            return solve;
        if (Math.abs(q) == radius){
            solve.add(-center.getY());
            return solve;
        }

        solve.add(-center.getY() + Math.sqrt(radius*radius - q*q));
        solve.add(-center.getY() - Math.sqrt(radius*radius - q*q));

        return solve;
    }

    public ArrayList<Double> getX(double y){
        ArrayList<Double> solve = new ArrayList<>();
        double q = y - center.getY();
        if (q > radius)
            return solve;
        if (q == radius){
            solve.add(-center.getX());
            return solve;
        }

        solve.add(-center.getX() + Math.sqrt(radius*radius - q*q));
        solve.add(-center.getX() - Math.sqrt(radius*radius - q*q));

        return solve;
    }

    public ArrayList<Point2D> crossSectionWithLine(Line2D line2D) throws MathException {
        ArrayList<Point2D> solve = new ArrayList<>();
        double m = 0;
        double n = 0;
        try {
            m = line2D.getSlope();
            n = line2D.getConst();
        } catch (MathException e) {
            if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinity){
                double x = line2D.getPointOnLine(0).getX();
                ArrayList<Double> doubles = getY(x);
                for (int i = 0; i < doubles.size(); i++) {
                    solve.add(Point2D.cartesianPoint(x,doubles.get(i)));
                }
                return solve;
            }
        }

        ArrayList<Double> polyParameters = new ArrayList<>();
        polyParameters.add(center.getX()*center.getX() + center.getY()*center.getY() - radius*radius + n*n -2*center.getY()*n);
        polyParameters.add(2*m*n - 2*center.getX() - 2*m*center.getY());
        polyParameters.add(m*m + 1);

        Polynom polynom = new Polynom(polyParameters);

        ArrayList<Double> x = polynom.solve(0);
        for (int i = 0; i < x.size(); i++) {
            solve.add(Point2D.cartesianPoint(x.get(i),m*x.get(i) + n));
        }
        return solve;
    }

    public ArrayList<Point2D> crossSectionWithCircle(Circle circle) throws MathException {
        ArrayList<Point2D> point2Ds = new ArrayList<>();
        double distance = center.distance(circle.center);
        if (distance == 0){
            if (radius != circle.radius)
                return point2Ds;
            else
                throw new MathException(MathException.MathExceptionCause.infinity);
        }
        if (distance > radius + circle.radius)
            return point2Ds;

        double v = (radius + circle.radius - distance)/2;
        Line2D line2D = Line2D.getLineOutOfTwoPoints(center,circle.center); //find the line between two circle's centers
        Point2D point2D = line2D.getPointOnLine(radius - v);
        Line2D line2D1 = null;
        try {
            line2D1 = Line2D.getLineFromSlopeAndPoint(point2D, line2D.getPerpendicularSlope());
        }catch (MathException e){
            if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinity){
                line2D1 = new Line2D(point2D, Point2D.cartesianPoint(0,1));
            }
        }

        return crossSectionWithLine(line2D1);
    }

    public Square getBlockingSquare() throws MathException {
        return new Square(radius*2,Angle.zero(),center);
    }

    @Override
    public String toString() {
        return "Circle{" +
                "radius=" + radius +
                ", center=" + center +
                '}';
    }
}
