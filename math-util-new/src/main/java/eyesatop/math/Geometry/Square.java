package eyesatop.math.Geometry;

import java.util.ArrayList;

import eyesatop.math.MathException;

/**
 * Created by Einav on 07/11/2017.
 */

public class Square extends Polygon{

    protected final double length;
    protected final Angle rotation;
    protected final Point2D centerPoint;

    public Square(ArrayList<Point2D> vertexes) throws MathException {
        super(vertexes);
        if (vertexes.size() != 4)
            throw new MathException(MathException.MathExceptionCause.notSquare);
        for (int i = 0; i < numberOfVertexs(); i++) {
            if(getVertexAngle(i).degree() != 90){
                throw new MathException(MathException.MathExceptionCause.notSquare);
            }
        }

        double width = vertexes.get(0).distance(vertexes.get(1));
        length = vertexes.get(0).distance(vertexes.get(3));
        rotation = Angle.angleRadian(Line2D.getLineOutOfTwoPoints(vertexes.get(0),vertexes.get(3)).getDirection().getAngle());
        centerPoint = centerPoint();
        if (length != width)
            throw new MathException(MathException.MathExceptionCause.notSquare);
    }

    public Square(double length, Angle rotation, Point2D centerPoint) throws MathException {
        if (length <= 0){
            throw new MathException(MathException.MathExceptionCause.notSquare);
        }
        if (centerPoint == null){
            centerPoint = Point2D.cartesianPoint(0,0);
        }
        this.length = length;
        this.rotation = rotation;
        this.centerPoint = centerPoint;

        vertexes.add(Point2D.cartesianPoint(length/2,length/2).rotate(rotation.radian()).add(centerPoint));
        vertexes.add(Point2D.cartesianPoint(length/2,-length/2).rotate(rotation.radian()).add(centerPoint));
        vertexes.add(Point2D.cartesianPoint(-length/2,-length/2).rotate(rotation.radian()).add(centerPoint));
        vertexes.add(Point2D.cartesianPoint(-length/2,length/2).rotate(rotation.radian()).add(centerPoint));
    }

    public Square setNewCenterPoint(Point2D newCenterPoint) throws MathException {
        return new Square(length,rotation,newCenterPoint);
    }

    public Square setNewRotation(Angle newRotation) throws MathException {
        return new Square(length,newRotation,centerPoint);
    }

    public Square setNewLength(double newLength) throws MathException {
        return new Square(newLength,rotation,centerPoint);
    }

    public ArrayList<Line2D> getLinesFromSquareForRadiator(double gap) throws MathException {

        ArrayList<Line2D> line2Ds = new ArrayList<>();

        SectionLine2D sectionLine2D1 = new SectionLine2D(vertexes.get(1),vertexes.get(3));
        SectionLine2D sectionLine2D2 = new SectionLine2D(vertexes.get(0),vertexes.get(2));
        int i = 0;

        while (true) {
            Point2D point2D = sectionLine2D1.getPointOnLine(gap*i + gap/2);
            if (point2D == null)
                break;
            line2Ds.add(Line2D.getLineOutOfTwoPoints(point2D,sectionLine2D2.getPointOnLine(gap*i + gap/2)));
            i++;
        }

        return line2Ds;

    }

    public double getLength() {
        return length;
    }

    public Angle getRotation() {
        return rotation;
    }

    public Point2D getCenterPoint() {
        return centerPoint;
    }

    @Override
    public Point2D centerPoint() {
        return centerPoint;
    }
}
