package eyesatop.math.Geometry.EarthGeometry;

import java.util.ArrayList;

import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Polygon;
import eyesatop.math.MathException;

/**
 * Created by Einav on 10/07/2017.
 */

public class GeographicPolygon {

    private final ArrayList<Location> vertexes;

    public GeographicPolygon(ArrayList<Location> vertexes) {
        this.vertexes = vertexes;
    }

    public ArrayList<Location> getVertexes() {
        return vertexes;
    }

    public int polygonNumber(){return vertexes.size();}


    public Location getCenter(){
        double x = 0;
        double y = 0;
        double z = 0;
        double size = vertexes.size();
        for (int i = 0; i < size; i++) {
            x += vertexes.get(i).getLocation().getX();
            y += vertexes.get(i).getLocation().getY();
            z += vertexes.get(i).getLocation().getZ();
        }
        return new Location(x/size,y/size,z/size);
    }

    public Location getCenterAsReferencePoint(){
        double x = 0;
        double y = 0;
        double size = vertexes.size();
        for (int i = 0; i < size; i++) {
            x += vertexes.get(i).getLocation().getX();
            y += vertexes.get(i).getLocation().getY();
        }
        return Location.CreateReferencedLineUTM(x/size,y/size);
    }

    public ArrayList<Double> sizeOfSides(){
        ArrayList<Double> doubles = new ArrayList<>();
        for (int i = 0; i < polygonNumber()-1; i++) {
            doubles.add(vertexes.get(i).distance(vertexes.get(i+1)).get2dRadius());
        }
        doubles.add(vertexes.get(polygonNumber()-1).distance(vertexes.get(0)).get2dRadius());
        return doubles;
    }

    public void setReferencePointAsCenter(){
        Location refPoint = getCenterAsReferencePoint();

        for (int i = 0; i < vertexes.size(); i++) {
            vertexes.get(i).setUtmReferenceLine(refPoint);
        }
    }

    public void setReferencePoint(Location referencePoint){

        for (int i = 0; i < vertexes.size(); i++) {
            vertexes.get(i).setUtmReferenceLine(referencePoint);
        }
    }

    public Polygon getUtm2dPolygon() throws MathException {
        ArrayList<Point2D> point2Ds = new ArrayList<>();

        for (int i = 0; i < vertexes.size(); i++) {
            point2Ds.add(vertexes.get(i).getUtmLocationAsRefPointAsZeroPoint());
        }

        return new Polygon(point2Ds);
    }

}
