package eyesatop.util.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shape2D {
    private final List<Point2D> points;
    private final Point2D centerOfMass;

    public Shape2D(List<Point2D> points, Point2D centerOfMass) {
        this.points = Collections.unmodifiableList(points);
        this.centerOfMass = centerOfMass;
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public Point2D getCenterOfMass() {
        return centerOfMass;
    }

    public Shape2D rotate(double angle) {
        return rotate(angle, centerOfMass);
    }

    public Shape2D rotate(double angle, Point2D refPoint) {
        List<Point2D> newPoints = new ArrayList<>();
        for (Point2D point : points) {
            newPoints.add(point.rotate(angle, refPoint));
        }
        return new Shape2D(newPoints, centerOfMass.rotate(angle, refPoint));
    }
}
