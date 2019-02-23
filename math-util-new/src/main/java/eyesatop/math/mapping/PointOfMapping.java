package eyesatop.math.mapping;

import java.util.ArrayList;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 17/11/2017.
 */

public class PointOfMapping {

    private final ArrayList<Point3D> point3Ds;

    protected PointOfMapping(ArrayList<Point3D> point3Ds) {
        this.point3Ds = point3Ds;
    }

    public PointOfMapping(){
        point3Ds = new ArrayList<>();
    }

    public int numberOfImages(){
        return point3Ds.size();
    }

    public ArrayList<Point3D> getPoint3Ds() {
        return point3Ds;
    }
}
