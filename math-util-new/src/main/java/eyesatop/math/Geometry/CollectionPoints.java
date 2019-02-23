package eyesatop.math.Geometry;

import java.util.ArrayList;

/**
 * Created by Einav on 09/11/2017.
 */

public class CollectionPoints {

    private final ArrayList<Point2D> point2Ds;

    public CollectionPoints(ArrayList<Point2D> point2Ds) {
        this.point2Ds = point2Ds;
    }

    public void add(Point2D point2D){
        point2Ds.add(point2D);
    }

    public ArrayList<Point2D> getPoint2Ds() {
        return point2Ds;
    }

    public int size(){
        return point2Ds.size();
    }

    public ArrayList<Point2D> sortPointsByNorth(){
        ArrayList<Point2D> fixedPoints = new ArrayList<>();
        if (size() != 0) {
            fixedPoints.add(point2Ds.get(0));
            for (int i = 1; i < point2Ds.size(); i++) {
                int j = 0;
                while (point2Ds.get(i).getNorth() < fixedPoints.get(j).getNorth()) {
                    if (j == fixedPoints.size() - 1) {
                        j++;
                        break;
                    }
                    j++;
                }
                fixedPoints.add(j, point2Ds.get(i));
            }
        }
        return fixedPoints;
    }

    public ArrayList<Point2D> sortPointsByEast(){
        ArrayList<Point2D> fixedPoints = new ArrayList<>();
        if (size() != 0) {
            fixedPoints.add(point2Ds.get(0));
            for (int i = 1; i < point2Ds.size(); i++) {
                int j = 0;
                while (point2Ds.get(i).getEast() < fixedPoints.get(j).getEast()) {
                    if (j == fixedPoints.size() - 1) {
                        j++;
                        break;
                    }
                    j++;
                }
                fixedPoints.add(j, point2Ds.get(i));
            }
        }
        return fixedPoints;
    }

    @Override
    public String toString() {
        return "CollectionPoints{" +
                "point2Ds=" + point2Ds +
                '}';
    }
}
