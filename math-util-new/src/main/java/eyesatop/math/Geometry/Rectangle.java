package eyesatop.math.Geometry;

import java.util.ArrayList;

import eyesatop.math.MathException;

/**
 * Created by Einav on 07/11/2017.
 */

public class Rectangle extends Polygon{

    protected final double width;
    protected final double length;
    protected final double rotation;

    public Rectangle(ArrayList<Point2D> vertexes) throws MathException {
        super(vertexes);
        if (vertexes.size() != 4)
            throw new MathException(MathException.MathExceptionCause.notRectangle);
        for (int i = 0; i < numberOfVertexs(); i++) {
            if(getVertexAngle(i).degree() != 90){
                throw new MathException(MathException.MathExceptionCause.notRectangle);
            }
        }

        width = vertexes.get(0).distance(vertexes.get(1));
        length = vertexes.get(0).distance(vertexes.get(3));
        rotation = Line2D.getLineOutOfTwoPoints(vertexes.get(0),vertexes.get(3)).getDirection().getAngle();
    }



}
