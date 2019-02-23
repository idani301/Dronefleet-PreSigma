package eyesatop.util.geo.dtm.mapping;

import java.io.File;
import java.util.ArrayList;

import eyesatop.imageprocessing.PrintToImage;
import eyesatop.math.Geometry.Angle;
import eyesatop.math.Geometry.EarthGeometry.GeographicPolygon;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Geometry.Polygon;
import eyesatop.math.MathException;
import eyesatop.math.camera.ImageInfo;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmObject;
import eyesatop.util.geo.dtm.Indexes;

/**
 * Created by Einav on 16/11/2017.
 */

public class ImageSpatialInfo {

    private final DtmObject dtmObject;
    private final ArrayList<Point3D>[][] imageSpatialInfo;
    private int numberOfImages = 0;
    private final int width;
    private final int length;
    private final int density;

    public ImageSpatialInfo(DtmObject dtmObject, int density) throws Exception {
        if (density < 1){
            throw new Exception("density must be bigger then 1");
        }
        this.dtmObject = dtmObject;
        this.density = density;
        width = (dtmObject.getWidth()/density) + 1;
        length = (dtmObject.getLength()/density) + 1;
        imageSpatialInfo = new ArrayList[width][length];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                imageSpatialInfo[i][j] = new ArrayList<>();
            }
        }
    }

    public void addImageInfoToMapping(ImageInfo imageInfo) throws MathException, TerrainNotFoundException {
        numberOfImages++;
        Indexes indexes = dtmObject.getIndexFromLocation(new Location(31,31));
        ArrayList<Point2D> point2Ds = new ArrayList<>();
        GeographicPolygon geographicPolygon = null;
        if (imageInfo != null) {
            geographicPolygon = imageInfo.getEstimateFramePolygonOnPlato(0);

            for (int i = 0; i < geographicPolygon.getVertexes().size(); i++) {
                point2Ds.add(Point2D.cartesianPoint(geographicPolygon.getVertexes().get(i).latitude(), geographicPolygon.getVertexes().get(i).longitude()));
            }
            Polygon polygon = null;
            try {
                polygon = new Polygon(point2Ds);
            } catch (MathException e){
                if (e.getMathExceptionCause() == MathException.MathExceptionCause.notPolygon)
                    return;
            }

            for (int i = 0; i < width - 1; i++) {
                for (int j = 0; j < length - 1; j++) {
                    Location location = dtmObject.getLocationFromIJ(i * density, j * density);
                    Point2D point2D = Point2D.cartesianPoint(location.getLatitude(), location.getLongitude());
                    if (polygon.isPointInsidePolygon(point2D)) {
                        Location cameraLocation = new Location(imageInfo.getCameraLocation().latitude(), imageInfo.getCameraLocation().longitude(), imageInfo.getCameraLocation().Height());
                        double azimuth = Math.toRadians(location.az(cameraLocation));
                        double horizontalDistance = location.distance(cameraLocation);
                        double verticalDistance = dtmObject.terrainAltitude(location) - cameraLocation.getAltitude();
                        double elevation = Math.atan(Math.abs(horizontalDistance) / Math.abs(verticalDistance));
                        double distance = Math.sqrt(Math.pow(horizontalDistance, 2) + Math.pow(verticalDistance, 2));
                        imageSpatialInfo[i][j].add(Point3D.spherePoint(distance, azimuth, elevation));
                    }
                }
            }
        }

    }

    public ArrayList<Point3D> getGeometricInfo(Location location){
        Indexes indexes = dtmObject.getIndexFromLocation(location);
        return imageSpatialInfo[indexes.getI()/density][indexes.getJ()/density];
    }

    public int getNumberOfImages() {
        return numberOfImages;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getDensity() {
        return density;
    }

    public int getNumberOfImagesWithAzimuthDifference(Angle angle, Indexes indexes){
        int counter = 0;
        ArrayList<Angle> angles = new ArrayList<>();
        ArrayList<Point3D> point3Ds = imageSpatialInfo[indexes.getI()][indexes.getJ()];
        if (point3Ds.size() == 0)
            return 0;
        counter++;
        angles.add(Angle.angleRadian(point3Ds.get(0).getAzimuth()));
        for (int i = 1; i < point3Ds.size(); i++) {
            Angle tempAngle  = Angle.angleRadian(point3Ds.get(i).getAzimuth());
            int j;
            for (j = 0; j < angles.size(); j++) {
                if (tempAngle.distanceFromAngle(angles.get(j)).degree() < angle.degree()){
                    break;
                }
            }
            if (j == angles.size()){
                counter++;
                angles.add(tempAngle);
            }
        }
        return counter;
    }

    public boolean printToImageSpatialImage() {

        ArrayList<ArrayList<Integer[]>> arrayLists = new ArrayList<>();
        for (int i = 0; i < getWidth(); i++) {
            arrayLists.add(new ArrayList<Integer[]>());
            for (int j = 0; j < getLength(); j++) {
                int i1 = getNumberOfImagesWithAzimuthDifference(Angle.angleDegree(20),new Indexes(i,j))*50;
                Integer[] integers = new Integer[]{0,0,i1 > 255 ? 255 : i1};
                if(i1 == 0){
                    integers = new Integer[]{255,255,255};
                }
                arrayLists.get(i).add(integers);
            }
        }
        PrintToImage.printToImageFile(arrayLists,new File("E:\\mapping\\"));
        return true;
    }
}
