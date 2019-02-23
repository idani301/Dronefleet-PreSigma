package eyesatop.util.geo;

import java.util.ArrayList;

import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.Circle;
import eyesatop.math.Geometry.CollectionPoints;
import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.Line2D;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Polygon;
import eyesatop.math.Geometry.Square;
import eyesatop.math.MathException;

/**
 * Created by Einav on 07/11/2017.
 */

public class RadiatorPlan {

    private final ArrayList<CollectionPoints> collectionPointses;
    private final double gap;
    private final double velocity;
    private final double altitude;
    private final Angle rotation;


    public static ArrayList<RadiatorInfo> CreateRadiatorFromPolygon(LocationGroup locationGroup, double gap, double velocity, double altitude, Angle rotation) throws Exception {
        ArrayList<RadiatorInfo> radiatorInfos = new ArrayList<>();
        Polygon polygon = locationGroup.getPolygon();
        eyesatop.math.Geometry.EarthGeometry.Location referenceLine = Location.CreateReferencedLineUTM(locationGroup.getReferenceLocation().getLatitude(), locationGroup.getReferenceLocation().getLongitude());
        RadiatorPlan radiatorPlan = new RadiatorPlan(polygon, gap, velocity, altitude, rotation);
        for (int i = 0; i < radiatorPlan.collectionPointses.size(); i++) {
            ArrayList<eyesatop.util.geo.Location> locations = new ArrayList<>();
            for (int j = 0; j < radiatorPlan.collectionPointses.get(i).size(); j++) {
                Location location = new Location(radiatorPlan.collectionPointses.get(i).getPoint2Ds().get(j), referenceLine);
                locations.add(new eyesatop.util.geo.Location(location.latitude(), location.longitude(), location.Height()));
            }
            radiatorInfos.add(new RadiatorInfo(
                    new LocationGroup(locations),
                    gap,
                    velocity,
                    altitude,
                    rotation
            ));

        }
        return radiatorInfos;
    }

    public static RadiatorInfo CreateRadiatorFromCircle(GeoCircle geoCircle, double gap, double velocity, double altitude, Angle rotation) throws MathException {
        eyesatop.math.Geometry.EarthGeometry.Location referenceLine = Location.CreateReferencedLineUTM(geoCircle.getLocation().getLatitude(),geoCircle.getLocation().getLongitude());
        Circle circle = new Circle(geoCircle.getRadius(),Point2D.zero());
        RadiatorPlan radiatorPlan = new RadiatorPlan(circle, gap, velocity, altitude, rotation);
        ArrayList<eyesatop.util.geo.Location> locations  = new ArrayList<>();
        for (int j = 0; j < radiatorPlan.collectionPointses.get(0).size(); j++) {
            Location location = new Location(radiatorPlan.collectionPointses.get(0).getPoint2Ds().get(j), referenceLine);
            locations.add(new eyesatop.util.geo.Location(location.latitude(), location.longitude(), location.Height()));
        }
        return new RadiatorInfo(
                new LocationGroup(locations),
                gap,
                velocity,
                altitude,
                rotation
        );
    }


    public RadiatorPlan(Polygon polygon, double gap, double velocity, double altitude, Angle rotation) throws Exception {
        this.gap = gap;
        this.velocity = velocity;
        this.altitude = altitude;
        this.rotation = rotation;
        if (!isPolygonSupport(polygon))
            throw new Exception("Polygon is not supported");

        collectionPointses = getOptionalRadiators(polygon);
    }

    private boolean isPolygonSupport(Polygon polygon) {
        if (polygon.numberOfVertexWithMoreThen180degree() > 1)
            return false;
        return true;
    }

    public RadiatorPlan(Circle circle, double gap, double velocity, double altitude, Angle rotation) throws MathException {
        this.gap = gap;
        this.velocity = velocity;
        this.altitude = altitude;
        this.rotation = rotation;
        collectionPointses = new ArrayList<>();
        this.collectionPointses.add(getRadiatorPlan(circle));
    }

    private CollectionPoints getRadiatorPlan(Circle circle) throws MathException {

        CollectionPoints collectionPoints = new CollectionPoints(new ArrayList<Point2D>());
        Square square = circle.getBlockingSquare();
        square = square.setNewLength(square.getLength());
        square = square.setNewRotation(Angle.QuarterCircle().sub(rotation));
        ArrayList<Line2D> line2Ds = square.getLinesFromSquareForRadiator(gap);
        ArrayList<ArrayList<Point2D>> arrayLists = new ArrayList<>();
        for (int i = 0; i < line2Ds.size(); i++) {
            ArrayList<Point2D> point2Ds = circle.crossSectionWithLine(line2Ds.get(i));
            if (point2Ds.size() == 2) {
                arrayLists.add(point2Ds);
            }
        }
        collectionPoints.add(circle.getPointOnCircle(rotation));
        for (int i = 0; i < arrayLists.size(); i++) {
            int subIndex = findSubIndex(collectionPoints.getPoint2Ds().get(collectionPoints.size() - 1),arrayLists.get(i));
            collectionPoints.add(arrayLists.get(i).get(subIndex));
            collectionPoints.add(arrayLists.get(i).get(subIndexForward(subIndex)));
        }
        collectionPoints.add(circle.getPointOnCircle(rotation.add(Angle.halfCircle())));
        return collectionPoints;
    }

    public ArrayList<CollectionPoints> getOptionalRadiators(Polygon polygon) throws MathException {

        ArrayList<CollectionPoints> radiatorInfos = new ArrayList<>();
        Square square = polygon.getBlockingSquare();
        square = square.setNewLength(square.getLength());
        square = square.setNewRotation(rotation.add(Angle.halfCircle()));
        ArrayList<Line2D> line2Ds = square.getLinesFromSquareForRadiator(gap);
        ArrayList<Integer> minDistanceIndexLine = new ArrayList<>();
        for (int i = 0; i < line2Ds.size(); i++) {
            double minDistance = gap * 10;
            for (int j = 0; j < polygon.getVertexes().size(); j++) {
                double distance = line2Ds.get(i).distanceFromPoint(polygon.getVertexes().get(j));
                if (distance < minDistance) {
                    minDistance = distance;
                    minDistanceIndexLine.add(j);
                }
            }
        }

        ArrayList<ArrayList<Point2D>> point2DsOfPoints = new ArrayList<>();
        ArrayList<ArrayList<Point2D>> point2DsOfPointsSplited = new ArrayList<>();

        boolean isHaveSplit = false;
        boolean isStartWithSplit = false;
        boolean is2movedTo4 = false;
        int indexForward = 0;
        for (int j = 0; j < line2Ds.size(); j++) {
            try {
                ArrayList<Point2D> point2Ds1 = polygon.pointsLineCrossSection(line2Ds.get(j));
                if (point2Ds1.size() == 2) {
                    point2DsOfPoints.add(point2Ds1);
                    is2movedTo4 = true;
                }
                if (point2Ds1.size() == 4) {
                    if (is2movedTo4){
                        double distance1 = point2DsOfPoints.get(point2DsOfPoints.size()-1).get(0).distance(point2Ds1.get(0)) + point2DsOfPoints.get(point2DsOfPoints.size()-1).get(1).distance(point2Ds1.get(1));
                        double distance2 = point2DsOfPoints.get(point2DsOfPoints.size()-1).get(0).distance(point2Ds1.get(2)) + point2DsOfPoints.get(point2DsOfPoints.size()-1).get(1).distance(point2Ds1.get(3));
                        if (distance1 > distance2){
                            indexForward = 2;
                        }
                        is2movedTo4 = false;
                    }
                    ArrayList<Point2D> point2Ds = new ArrayList<>();
                    point2Ds.add(point2Ds1.get(indexForward(0 + indexForward, 4)));
                    point2Ds.add(point2Ds1.get(indexForward(1 + indexForward, 4)));
                    point2DsOfPoints.add(point2Ds);
                    ArrayList<Point2D> point2Ds2 = new ArrayList<>();
                    point2Ds2.add(point2Ds1.get(indexForward(2 + indexForward, 4)));
                    point2Ds2.add(point2Ds1.get(indexForward(3 + indexForward, 4)));
                    point2DsOfPointsSplited.add(point2Ds2);
                    isHaveSplit = true;
                    if (j == 0) {
                        isStartWithSplit = true;
                    }
                }
            } catch (MathException e) {
                if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinity) {
                    Line2D line2D = line2Ds.get(j);
                    ArrayList<Point2D> point2Ds1 = polygon.pointsLineCrossSection(new Line2D(line2D.getPoint2D().add(Point2D.cartesianPoint(0.0001, 0.0001)), line2D.getDirection()));
                    if (point2Ds1.size() == 2) {
                        point2DsOfPoints.add(point2Ds1);
                    } else {
                        point2Ds1 = polygon.pointsLineCrossSection(new Line2D(line2D.getPoint2D().add(Point2D.cartesianPoint(-0.0001, -0.0001)), line2D.getDirection()));
                        if (point2Ds1.size() == 2) {
                            point2DsOfPoints.add(point2Ds1);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < polygon.getVertexes().size(); i++) {
            int index = 0;
            int secIndex = 0;
            double minDistance = gap * 10;
            for (int j = 0; j < point2DsOfPoints.size(); j++) {
                for (int k = 0; k < 2; k++) {
                    double distance = point2DsOfPoints.get(j).get(k).distance(polygon.getVertexes().get(i));
                    if (minDistance > distance) {
                        minDistance = distance;
                        index = j;
                        secIndex = k;
                    }
                }
            }

            if (index == 0) {
                ArrayList<Point2D> radiatorPoints = new ArrayList<>();
                radiatorPoints.add(polygon.getVertexes().get(i));
                addToRadiatorPointsFromStart(point2DsOfPoints,radiatorPoints);
                if (isHaveSplit) {
                    radiatorPoints.add(polygon.getVertexes().get(polygon.getVertexesIndexesWithMoreThen180().get(0)));
                    if (isStartWithSplit) {
                        addToRadiatorPointsFromStart(point2DsOfPointsSplited,radiatorPoints);
                    } else {
                        addToRadiatorPointsFromEnd(point2DsOfPointsSplited,radiatorPoints);
                    }
                }

                double minDistanceNew = radiatorPoints.get(radiatorPoints.size() - 1).distance(polygon.getVertexes().get(0));
                int indexNew = 0;
                for (int j = 1; j < polygon.getVertexes().size(); j++) {
                    double dis = radiatorPoints.get(radiatorPoints.size() - 1).distance(polygon.getVertexes().get(j));
                    if (minDistanceNew > dis) {
                        minDistanceNew = dis;
                        indexNew = j;
                    }
                }
                radiatorPoints.add(polygon.getVertexes().get(indexNew));
                radiatorInfos.add(new CollectionPoints(radiatorPoints));
            }

            if (index == point2DsOfPoints.size() - 1) {
                ArrayList<Point2D> radiatorPoints = new ArrayList<>();
                radiatorPoints.add(polygon.getVertexes().get(i));
                addToRadiatorPointsFromEnd(point2DsOfPoints,radiatorPoints);
                if (isHaveSplit) {
                    radiatorPoints.add(polygon.getVertexes().get(polygon.getVertexesIndexesWithMoreThen180().get(0)));
                    if (!isStartWithSplit) {
                        addToRadiatorPointsFromStart(point2DsOfPointsSplited,radiatorPoints);
                    } else {
                        addToRadiatorPointsFromEnd(point2DsOfPointsSplited,radiatorPoints);
                    }
                }

                double minDistanceNew = 10 * gap;
                int indexNew = 0;
                for (int j = 0; j < polygon.getVertexes().size(); j++) {
                    double dis = radiatorPoints.get(radiatorPoints.size() - 1).distance(polygon.getVertexes().get(j));
                    if (minDistanceNew > dis) {
                        minDistanceNew = dis;
                        indexNew = j;
                    }
                }
                radiatorPoints.add(polygon.getVertexes().get(indexNew));
                radiatorInfos.add(new CollectionPoints(radiatorPoints));
            }
        }

        return radiatorInfos;
    }


    private int findSubIndex(Point2D point2D, ArrayList<Point2D> point2Ds) {
        if (point2D.distance(point2Ds.get(0)) < point2D.distance(point2Ds.get(1)))
            return 0;
        return 1;
    }

    private int subIndexForward(int index){
        if (index == 0)
            return 1;
        else
            return 0;
    }

    private int indexForward(int index, int maxIndex){
        return index%maxIndex;
    }

    private void addToRadiatorPointsFromStart(ArrayList<ArrayList<Point2D>> arrayLists, ArrayList<Point2D> radiatorPoints ) {
        int secIndex = 0;
        for (int j = 0; j < arrayLists.size(); j++) {
            Point2D point2D = radiatorPoints.get(radiatorPoints.size() - 1);
            secIndex = findSubIndex(point2D,arrayLists.get(j));
            radiatorPoints.add(arrayLists.get(j).get(secIndex));
            radiatorPoints.add(arrayLists.get(j).get(subIndexForward(secIndex)));
        }
    }

    private void addToRadiatorPointsFromEnd(ArrayList<ArrayList<Point2D>> arrayLists, ArrayList<Point2D> radiatorPoints ) {
        int secIndex = 0;
        for (int j = arrayLists.size() - 1; j > -1; j--) {
            Point2D point2D = radiatorPoints.get(radiatorPoints.size() - 1);
            secIndex = findSubIndex(point2D,arrayLists.get(j));
            radiatorPoints.add(arrayLists.get(j).get(secIndex));
            radiatorPoints.add(arrayLists.get(j).get(subIndexForward(secIndex)));

        }
    }
}
