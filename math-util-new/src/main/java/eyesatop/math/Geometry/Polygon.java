package eyesatop.math.Geometry;

import java.util.ArrayList;

import eyesatop.math.MathException;

/**
 * Created by Einav on 10/07/2017.
 */

public class Polygon {

    protected final ArrayList<Point2D> vertexes;

    public Polygon(ArrayList<Point2D> vertexes) throws MathException {

        this.vertexes = vertexes;
        ArrayList<SectionLine2D> sectionLine2Ds = new ArrayList<>();
        for (int i = 0; i < vertexes.size(); i++) {
            SectionLine2D ba = new SectionLine2D(vertexes.get(i),vertexes.get(i+1 == vertexes.size() ? 0 : i+1));
            sectionLine2Ds.add(ba);
            Point2D vectorBA = ba.getDirection();
            SectionLine2D bc = new SectionLine2D(vertexes.get(i),vertexes.get(i-1 == -1 ? vertexes.size()-1 : i-1));
            Point2D vectorBC = bc.getDirection();
            if (Math.abs(vectorBA.getAngle(vectorBC)) == Math.toRadians(180))
                throw new MathException(MathException.MathExceptionCause.notPolygon);
        }
        for (int i = 0; i < sectionLine2Ds.size(); i++) {
            for (int j = i+1; j < sectionLine2Ds.size(); j++) {
                try {
                    if (sectionLine2Ds.get(i).crossLineWithoutEndingPoints(sectionLine2Ds.get(j)) != null)
                        throw new MathException(MathException.MathExceptionCause.notPolygon);
                } catch (MathException e){
                if(e.getMathExceptionCause() == MathException.MathExceptionCause.infinity)
                    throw new MathException(MathException.MathExceptionCause.notPolygon);
                }
            }
        }

        for (int i = 0; i < vertexes.size(); i++) {
            int counter = 0;
            for (int j = 0; j < sectionLine2Ds.size(); j++) {
                if (sectionLine2Ds.get(j).isPointOnLine(vertexes.get(i)))
                    counter++;
            }
            if (counter != 2) {
                System.out.println("hiii");
                throw new MathException(MathException.MathExceptionCause.notPolygon);
            }
        }
    }

    protected Polygon(){
        vertexes = new ArrayList<>();
    }

    public ArrayList<Point2D> getVertexes() {
        return vertexes;
    }

    public Point2D centerPoint(){
        double meanX = 0;
        double meanY = 0;
        for (int i = 0; i < vertexes.size(); i++) {
            meanX += vertexes.get(i).getX();
            meanY += vertexes.get(i).getY();
        }

        return Point2D.cartesianPoint(meanX/ vertexes.size(),meanY/ vertexes.size());
    }

    public Angle getVertexAngle(int vertexNumber){
        Point2D vectorBA = Line2D.getLineOutOfTwoPoints(vertexes.get(vertexNumber),vertexes.get(vertexNumber+1 == vertexes.size() ? 0 : vertexNumber+1)).getDirection();
        Point2D vectorBC = Line2D.getLineOutOfTwoPoints(vertexes.get(vertexNumber),vertexes.get(vertexNumber-1 == -1 ? vertexes.size()-1 : vertexNumber-1)).getDirection();

        Angle angle = Angle.angleRadian(vectorBA.getAngle(vectorBC));
        if (isVertexHaveMoreThan180Degree(vertexNumber)){
            return angle.negetive();
        }
        return angle;
    }



    private boolean isVertexHaveMoreThan180Degree(int vertexNumber){
        Point2D vertexB = vertexes.get(vertexNumber);
        int counter = 0;
        double delta = 0.000000001;

        Point2D vertexA = vertexes.get(vertexNumber + 1 == vertexes.size() ? 0 : vertexNumber + 1);
        Point2D vertexC = vertexes.get(vertexNumber - 1 == -1 ? vertexes.size() - 1 : vertexNumber - 1);

        Point2D vectorBA = Line2D.getLineOutOfTwoPoints(vertexB,vertexA).getDirection();
        Point2D vectorBC = Line2D.getLineOutOfTwoPoints(vertexB,vertexC).getDirection();

        double angle = vectorBA.getAngle() + vectorBA.getAngle(vectorBC)/2;


        for (int i = -1; i < 2; i+=2) {
            Point2D checkPoint = vertexB.add(Point2D.cartesianPoint(i*delta,0).rotate(angle));
            if (isPointInsidePolygon(checkPoint))
                counter++;
            checkPoint = vertexB.add(Point2D.cartesianPoint(0,i*delta).rotate(angle));
            if (isPointInsidePolygon(checkPoint))
                counter++;
        }
        if (counter > 2)
            return true;
        return false;
    }

    public boolean isPolygonHaveVertexWithMoreTHan180Degree(){
        for (int i = 0; i < vertexes.size(); i++) {
            if (isVertexHaveMoreThan180Degree(i))
                return true;
        }
        return false;
    }

    public int numberOfVertexWithMoreThen180degree(){
        int counter = 0;
        for (int i = 0; i < vertexes.size(); i++) {
            if (isVertexHaveMoreThan180Degree(i))
                counter++;
        }
        return counter;
    }

    public ArrayList<Integer> getVertexesIndexesWithMoreThen180(){
        ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 0; i < vertexes.size(); i++) {
            if (isVertexHaveMoreThan180Degree(i))
                integers.add(i);
        }
        return integers;
    }

    public boolean isPointInsidePolygon(Point2D point2D){
        ArrayList<SectionLine2D> sectionLine2Ds = getPointsAsSetOfSectionLines();
        double x = 0;
        if(point2D.getX() == 0) {
            x = 1;
        }
        Line2D line2D = Line2D.getLineOutOfTwoPoints(point2D,Point2D.cartesianPoint(x,point2D.getY()));
        int counter = 0;
        for (int i = 0; i < sectionLine2Ds.size(); i++) {
            Point2D tempPoint2D = null;
            try {
                tempPoint2D = sectionLine2Ds.get(i).crossLine(line2D);
            } catch (MathException e) {
                if(e.getMathExceptionCause() == MathException.MathExceptionCause.infinity){
                    if (sectionLine2Ds.get(i).isPointOnLine(point2D))
                        return true;
                }
            }
            if (tempPoint2D != null){
                if (tempPoint2D.getX() <= point2D.getX()){
                    counter++;
                }
            }
        }
        if (counter%2 == 1)
            return true;
        return false;
    }

    private ArrayList<SectionLine2D> getPointsAsSetOfSectionLines() {
        ArrayList<SectionLine2D> sectionLine2Ds = new ArrayList<>();

        for (int i = 0; i < vertexes.size()-1; i++) {
            sectionLine2Ds.add(new SectionLine2D(vertexes.get(i), vertexes.get(i+1)));
        }
        sectionLine2Ds.add(new SectionLine2D(vertexes.get(vertexes.size()-1), vertexes.get(0)));

        return sectionLine2Ds;
    }

    public int numberOfVertexs(){
        return vertexes.size();
    }

    public Square getBlockingSquare() throws MathException {
        Point2D centerPoint = centerPoint();
        double maxLength = vertexes.get(0).distance(centerPoint);
        for (int i = 1; i < vertexes.size(); i++) {
            double distance = vertexes.get(i).distance(centerPoint);
            if (distance > maxLength){
                maxLength = distance;
            }
        }
        return new Square(maxLength*2,Angle.angleDegree(0),centerPoint);
    }

    public ArrayList<Point2D> pointsLineCrossSection(Line2D line2D) throws MathException {
        ArrayList<Point2D> point2Ds = new ArrayList<>();

        if (Angle.angleRadian(line2D.getDirection().getAngle()).degree() >= 180){
            line2D = new Line2D(line2D.getPoint2D(),line2D.getDirection().rotate(Math.toRadians(180)));
        }

        for (int i = 0; i < vertexes.size(); i++) {
            SectionLine2D section = new SectionLine2D(vertexes.get(i),vertexes.get(i+1 == vertexes.size() ? 0 : i + 1));
            Point2D crossPoint = section.crossLine(line2D);
            if (crossPoint != null)
                point2Ds.add(crossPoint);
        }
        if (point2Ds.size() == 0 )
            return point2Ds;
        if (point2Ds.size() == 4){
            System.out.print("4");
        }
        ArrayList<Point2D> fixedPoints = new ArrayList<>();
        fixedPoints.add(point2Ds.get(0));
        for (int i = 1; i < point2Ds.size(); i++) {
            double t = 0;
            if (Math.abs(line2D.getDirection().getX()) > 0.5) {
                t = line2D.getScaleFromX(point2Ds.get(i).getX());
            }
            else {
                t = line2D.getScaleFromY(point2Ds.get(i).getY());
            }
            for (int j = 0; j < fixedPoints.size(); j++) {
                double s = 0;
                try {
                    s = line2D.getScaleFromX(fixedPoints.get(j).getX());
                } catch (MathException e){
                    if (e.getMathExceptionCause() == MathException.MathExceptionCause.zeroResult){
                        s = line2D.getScaleFromY(fixedPoints.get(j).getY());
                    }
                }
                if (t > s){
                    fixedPoints.add(j,point2Ds.get(i));
                    break;
                }
                if (j == fixedPoints.size()-1){
                    fixedPoints.add(point2Ds.get(i));
                    break;
                }
            }
        }

        return fixedPoints;
    }

    private ArrayList<SectionLine2D> getPolygonSections(){
        ArrayList<SectionLine2D> sectionLine2Ds = new ArrayList<>();
        for (int i = 0; i < vertexes.size()-1; i++) {
            sectionLine2Ds.add(new SectionLine2D(vertexes.get(i),vertexes.get(i+1)));
        }
        sectionLine2Ds.add(new SectionLine2D(vertexes.get(vertexes.size()-1),vertexes.get(0)));
        return sectionLine2Ds;
    }

    public boolean isSectionInsidePolygon(SectionLine2D sectionLine2D) throws MathException {

        ArrayList<SectionLine2D> polygonSections = getPolygonSections();
        int numberOfCrosses = 0;
        for (int i = 0; i < polygonSections.size(); i++) {
            try {
                if (sectionLine2D.crossLineWithoutEndingPoints(polygonSections.get(i)) != null) {
                    return false;
                }
            } catch (MathException e) {
                if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinity) {
                    return isSectionInsidePolygonFinish(sectionLine2D);
                } else {
                    throw new MathException(e.getMathExceptionCause());
                }
            }

        }
        return isSectionInsidePolygonFinish(sectionLine2D);
    }

    private boolean isSectionInsidePolygonFinish(SectionLine2D sectionLine2D){
        if(isPointInsidePolygon(sectionLine2D.getPoint2D()))
            return true;

        int numberOfVertexWithMoreThen180degree = numberOfVertexWithMoreThen180degree();
        double length = sectionLine2D.getLength();
        ArrayList<Point2D> point2Ds = sectionLine2D.getVertexes();
        int counter = 0;
        for (int i = 0; i < numberOfVertexWithMoreThen180degree; i++) {
            point2Ds.add(sectionLine2D.getPointOnLine(length*(i+1)/(numberOfVertexWithMoreThen180degree+1)));
        }
        for (int i = 0; i < point2Ds.size(); i++) {
            if (isPointOnVertex(point2Ds.get(i)) || isPointInsidePolygon(point2Ds.get(i))){
                counter++;
            }
        }
            if (counter == point2Ds.size())
                return true;
        return false;
    }

    public boolean isPointOnVertex(Point2D point2D){
        for (int i = 0; i < vertexes.size(); i++) {
            if (vertexes.get(i).equals(point2D))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Polygon\n{" +
                "vertexes=" + vertexes +
                '}';
    }
}
