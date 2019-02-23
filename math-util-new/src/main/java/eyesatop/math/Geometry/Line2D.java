package eyesatop.math.Geometry;

import eyesatop.math.MathException;

/**
 * Created by Einav on 11/07/2017.
 */

public class Line2D {

    protected final Point2D point2D;
    protected final Point2D direction;

    public Line2D(Point2D point2D, Point2D direction) {
        this.point2D = point2D;
        this.direction = direction;
    }

    public static Line2D getLineOutOfTwoPoints(Point2D point2D1, Point2D point2D2) {
        Point2D direction = point2D2.vectorToPoint(point2D1).normal();
        return new Line2D(point2D1,direction);
    }

    public static Line2D getLineFromSlopeAndPoint(Point2D point2D, double slope){
        double angle = Math.atan(slope);
        Point2D direction = Point2D.cartesianPoint(Math.cos(angle),Math.sin(angle));
        return new Line2D(point2D,direction);
    }

    public Line3D getLine3D(){
        return new Line3D(point2D.getPoint3D(),direction.getPoint3D());
    }


    public Point2D getPointOnLine(double distanceFromStartingPoint){
        return direction.multiple(distanceFromStartingPoint).add(point2D); // p = t*v + v0
    }

    public Point2D getPointOnLineX(double x) throws MathException {
        if (direction.getX() == 0){
            if (x == point2D.getX())
                return point2D;
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        }

        return getPointOnLine((x-point2D.getX())/direction.getX());
    }

    public Point2D getPointOnLineY(double y) throws MathException {
        if (direction.getY() == 0){
            if (y == point2D.getY())
                return point2D;
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        }

        return getPointOnLine((y-point2D.getY())/direction.getY());
    }

    public Point2D crossLine(Line2D line2D) throws MathException {

        return getPointOnLine(getLineDistanceFromStartingPointToCrossLinePoint(line2D));
    }

    protected double getLineDistanceFromStartingPointToCrossLinePoint(Line2D line2D) throws MathException {
        double gama = (direction.getX()*line2D.direction.getY() - direction.getY()*line2D.direction.getX());
        double alpha = line2D.getPoint2D().getX() - point2D.getX();
        double beta = line2D.getPoint2D().getY() - point2D.getY();

        if (gama == 0) {
            if (alpha == 0 && beta == 0)
                throw new MathException(MathException.MathExceptionCause.infinity);
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        }

        return (-beta*line2D.direction.getX() + alpha*line2D.direction.getY())/gama;
    }

    protected Double pointOnLine(Point2D point2D){
        double t = 0;
        if (direction.getX() == 0){
            t = (point2D.getY() - this.point2D.getY()) / direction.getY();
        } else {
            t = (point2D.getX() - this.point2D.getX()) / direction.getX();
        }
        Point2D point2D1 = getPointOnLine(t);
        if (point2D1 == null)
            return null;
        if (point2D.distance(point2D1) > 1e-12)
            return null;
        return t;
    }

    public boolean isPointOnLine(Point2D point2D){
        if (pointOnLine(point2D) != null)
            return true;
        return false;
    }

    public double getSlope() throws MathException {
        return Math.atan(direction.getAngle());
    }

    public double getConst() throws MathException {
        double m = 0;
        try{
            m = getSlope();
        } catch (MathException e){
            if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinity){
                if (point2D.getX() != 0)
                    throw new MathException(MathException.MathExceptionCause.zeroResult);
                return 0;
            }
        }
        if (m == 0){
            return getPointOnLine(0).getY();
        }
        Point2D p0 = getPointOnLine(0);
        return p0.getY() - m*p0.getX();
    }

    public double getPerpendicularSlope() throws MathException {
        try {
            double m = getSlope();
        } catch (MathException e){
            if(e.getMathExceptionCause() == MathException.MathExceptionCause.infinity){
                return 0;
            }
        }
        if (getSlope() == 0)
            throw new MathException(MathException.MathExceptionCause.infinity);
        return -1/getSlope();
    }

    public double distanceFromPoint(Point2D point2D){
        Line2D line2D = null;
        try {
            line2D = Line2D.getLineFromSlopeAndPoint(point2D,getPerpendicularSlope());
        } catch (MathException e) {
            if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinity){
                return Math.abs(point2D.getY() - this.point2D.getY());
            }
        }
        Point2D point2D1 = null;
        try {
            point2D1 = crossLine(line2D);
        } catch (MathException e1) {
            if (e1.getMathExceptionCause() == MathException.MathExceptionCause.infinity)
                return 0;
        }
        return point2D.distance(point2D1);
    }

    public double getScaleFromX(double x) throws MathException {
        if (direction.getX() == 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        return (x - point2D.getX())/direction.getX();
    }

    public double getScaleFromY(double y) throws MathException {
        if (direction.getY() == 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        return (y - point2D.getY())/direction.getY();
    }


    public Point2D getPoint2D() {
        return point2D;
    }

    public Point2D getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "Line2D{" +
                "point2D=" + point2D +
                ", direction=" + direction +
                '}';
    }
}
