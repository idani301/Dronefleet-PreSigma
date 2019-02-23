package eyesatop.math.Geometry;

import java.util.ArrayList;

import eyesatop.math.MathException;

/**
 * Created by Einav on 11/07/2017.
 */

public class SectionLine2D extends Line2D{

    private final double length;

    public SectionLine2D(Point2D startingPoint, Point2D finishPoint) {
        super(startingPoint,finishPoint.vectorToPoint(startingPoint).normal());
        length = startingPoint.distance(finishPoint);
    }

    public SectionLine2D updateLastPoint(Point2D point2D){
        return new SectionLine2D(this.point2D,point2D);
    }

    public ArrayList<Point2D> getVertexes(){

        ArrayList<Point2D> point2Ds = new ArrayList<>();
        point2Ds.add(getPoint2D());
        point2Ds.add(getPointOnLine(length));

        return point2Ds;
    }


    public Point2D crossLine(Line2D line2D) throws MathException {
        double distance = 0;
        try {
            distance = getLineDistanceFromStartingPointToCrossLinePoint(line2D);
        } catch (MathException e) {
            if (e.getMathExceptionCause() == MathException.MathExceptionCause.zeroResult)
                return null;
            throw new MathException(MathException.MathExceptionCause.infinity);
        }
        if (distance > length)
            return null;
        if(distance < 0)
            return null;
        return getPointOnLine(distance);
    }

    public Point2D crossLine(SectionLine2D sectionLine2D) throws MathException {
        double distance1 = 0;
        double distance2 = 0;
        try {
            distance1 = getLineDistanceFromStartingPointToCrossLinePoint(sectionLine2D);
            distance2 = sectionLine2D.getLineDistanceFromStartingPointToCrossLinePoint(this);
        } catch (MathException e) {
            if (e.getMathExceptionCause() == MathException.MathExceptionCause.zeroResult)
                return null;
            throw new MathException(MathException.MathExceptionCause.infinity);
        }
        if (distance1 > length || distance2 > sectionLine2D.length)
            return null;
        if(distance1 < 0 || distance2 < 0)
            return null;
        return getPointOnLine(distance1);
    }

    public Point2D crossLineWithoutEndingPoints(SectionLine2D sectionLine2D) throws MathException {
        double distance1 = 0;
        double distance2 = 0;
        try {
            distance1 = getLineDistanceFromStartingPointToCrossLinePoint(sectionLine2D);
            distance2 = sectionLine2D.getLineDistanceFromStartingPointToCrossLinePoint(this);
        } catch (MathException e) {
            if (e.getMathExceptionCause() == MathException.MathExceptionCause.zeroResult)
                return null;
            throw new MathException(MathException.MathExceptionCause.infinity);
        }
        if (distance1 >= length || distance2 >= sectionLine2D.length)
            return null;
        if(distance1 <= 0 || distance2 <= 0)
            return null;
        return getPointOnLine(distance1);
    }

    @Override
    public Point2D getPointOnLine(double distanceFromStartingPoint) {
        if (distanceFromStartingPoint > length || distanceFromStartingPoint < 0)
            return null;
        return super.getPointOnLine(distanceFromStartingPoint);
    }

    @Override
    public boolean isPointOnLine(Point2D point2D) {
        Double aDouble = pointOnLine(point2D);
        if (aDouble == null) {
            return false;
        }
        if(getPoint2D().distance(point2D) <= aDouble){
            return true;
        }
        return false;
    }



    public double getLength() {
        return length;
    }
}
