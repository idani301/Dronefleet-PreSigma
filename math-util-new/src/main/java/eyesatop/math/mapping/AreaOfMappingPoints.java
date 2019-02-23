package eyesatop.math.mapping;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import eyesatop.imageprocessing.LoadOpenCVDLL;
import eyesatop.math.Geometry.EarthGeometry.GeographicPolygon;
import eyesatop.math.Geometry.EarthGeometry.Location;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Geometry.Polygon;
import eyesatop.math.MathException;
import eyesatop.math.camera.ImageInfo;

/**
 * Created by Einav on 12/07/2017.
 */

public class AreaOfMappingPoints {

    static {
        new LoadOpenCVDLL();
    }

    private final GeographicPolygon geographicPolygon;
    private final MappingInfo[][] areaOfMappingPoints;

    private final Location referenceLocation;

    private final Point2D firstVertex;

    private final int density = 2;

    private double lowestHeight;
    private double highestHeight;

    final private String folderToSaveMapping = "E:\\mappingInfoImages\\";

    public AreaOfMappingPoints(GeographicPolygon rectangleGeographicPolygon) throws Exception {
        this.geographicPolygon = rectangleGeographicPolygon;
        if (rectangleGeographicPolygon.polygonNumber() != 4)
            throw new Exception("polygon must be rectangle");
        int eastDistance = 0;
        int northDistance = 0;
        ArrayList<Double> distances = rectangleGeographicPolygon.sizeOfSides();
        for (int i = 0; i < distances.size(); i++) {
            if (i%2 == 1){
                if (northDistance < distances.get(i)) {
                    northDistance = distances.get(i).intValue();
                }
            } else {
                if (eastDistance < distances.get(i)) {
                    eastDistance = distances.get(i).intValue();
                }
            }
        }
        areaOfMappingPoints = new MappingInfo[eastDistance/density][northDistance/density];
        referenceLocation = geographicPolygon.getCenterAsReferencePoint();
        geographicPolygon.setReferencePointAsCenter();
        lowestHeight = getLowestHeightFromDTM(); //must have DTM to complete the function
        highestHeight = getLowestHeightFromDTM(); //must have DTM to complete the function
        firstVertex =  geographicPolygon.getVertexes().get(0).getUtmLocationAsRefPointAsZeroPoint();
        resetAreaOfMappingPoints();
    }

    public static AreaOfMappingPoints randomHieghts(GeographicPolygon rectangleGeographicPolygon) throws Exception {
        AreaOfMappingPoints areaOfMappingPoints = new AreaOfMappingPoints(rectangleGeographicPolygon);
        areaOfMappingPoints.highestHeight = 30;
        double lengthI = areaOfMappingPoints.getAreaOfMappingPoints().length;
        double lengthJ = areaOfMappingPoints.getAreaOfMappingPoints()[0].length;
        for (int i = 0; i < lengthI; i++) {
            for (int j = 0; j < lengthJ; j++) {
                if(i > lengthI/2 && i < lengthI/2 + 40 && j > lengthJ/2 && j < lengthJ/2 + 40){
                    areaOfMappingPoints.getAreaOfMappingPoints()[i][j] = new MappingInfo(30);
                }
                else {
                    areaOfMappingPoints.getAreaOfMappingPoints()[i][j] = new MappingInfo(0);
                }
            }
        }
        return areaOfMappingPoints;
    }

    private MappingInfo[][] getAreaOfMappingPoints() {
        return areaOfMappingPoints;
    }

    private void resetAreaOfMappingPoints(){
        for (int i = 0; i < areaOfMappingPoints.length; i++) {
            for (int j = 0; j < areaOfMappingPoints[0].length; j++) {
                areaOfMappingPoints[i][j] = new MappingInfo(0); //must get DTM height
            }
        }
    }

    /*
    this function must get DTM
     */
    private double getLowestHeightFromDTM() {
        return 0;
    }

    public Point3D getUTMLocationOutOfPlaceInArray(int i, int j){
        return Point3D.cartesianPoint(firstVertex.getX() + i*density, firstVertex.getY() - j*density,areaOfMappingPoints[i][j].getHeight());
    }

    public void addNewImage(ImageInfo imageInfo) throws MathException {
        double time = System.currentTimeMillis();
        GeographicPolygon FrameGeographicPolygon = imageInfo.getEstimateFramePolygonOnPlato(lowestHeight);
        Location cameraLocation = imageInfo.getCameraLocation();
        cameraLocation.setUtmReferenceLine(referenceLocation);
        Point2D temp = cameraLocation.getUtmLocationAsRefPointAsZeroPoint();
        Point3D cameraLocationUtm = Point3D.cartesianPoint(temp.getX(),temp.getY(),cameraLocation.Height());
        FrameGeographicPolygon.setReferencePoint(referenceLocation);

        Polygon polygon = FrameGeographicPolygon.getUtm2dPolygon();

//        System.out.println(getUTMLocationOutOfPlaceInArray(0,0).toStringCartesian());
//        System.out.println(getUTMLocationOutOfPlaceInArray(areaOfMappingPoints.length-1,0).toStringCartesian());
//        System.out.println(getUTMLocationOutOfPlaceInArray(areaOfMappingPoints.length-1,areaOfMappingPoints[0].length-1).toStringCartesian());
//        System.out.println(getUTMLocationOutOfPlaceInArray(0,areaOfMappingPoints[0].length-1).toStringCartesian());
//
//        System.out.println(polygon);

        for (int i = 0; i < areaOfMappingPoints.length; i++) {
            for (int j = 0; j < areaOfMappingPoints[0].length; j++) {
                Point3D locationOutOfPlaceInArray = getUTMLocationOutOfPlaceInArray(i,j);
                Point3D point3D = cameraLocationUtm.vectorToPoint(locationOutOfPlaceInArray);
                if (polygon.isPointInsidePolygon(Point2D.cartesianPoint(locationOutOfPlaceInArray.getX(),locationOutOfPlaceInArray.getY())) && isRayGetToCamera(locationOutOfPlaceInArray,point3D)){
                    areaOfMappingPoints[i][j].addPoint(point3D);
                }
            }
        }

        System.out.println("Time to add one Image: " + (System.currentTimeMillis() - time)/1000 + "(s)");
    }

    private boolean isRayGetToCamera(Point3D locationUtm, Point3D point3D) {

        double r = point3D.getSphereRadius();
        double step = 10;
        double delta = 0;
        point3D = point3D.normal();
        while (point3D.getSphereRadius() < r){
            point3D = Point3D.spherePoint(point3D.getSphereRadius() + step,point3D.getTeta(),point3D.getPhi());
            Point3D checkedLocation = locationUtm.add(point3D);
            if(checkedLocation.getZ() > highestHeight)
                return true;
            Double z = getHeightFromUTMLocation(locationUtm.add(point3D).getHorizontalVector());
            if (z == null)
                return true;
            if (z + delta > checkedLocation.getZ()){
                return false;
            }
        }
        return true;
    }

    public double getNumberOfPointsWithMoreThenNumberOfImages(int numberOfImages, double spaceBetweenAnglesDegree){
        int counter = 0;
        for (int i = 0; i < areaOfMappingPoints.length; i++) {
            for (int j = 0; j < areaOfMappingPoints[0].length; j++) {
                if(areaOfMappingPoints[i][j].numberOfImagesNotInTheSameAngle(spaceBetweenAnglesDegree) >= numberOfImages)
                    counter++;
            }
        }
        return counter;
    }

    public double getNumberOfPoints(){
        return areaOfMappingPoints.length*areaOfMappingPoints[0].length;
    }

    public void saveAsImage(int numberOfImages, double angle){
        double[] white = {255,255,255};
        double[] red = {0,0,255};
        double[] green = {0,255,0};
        Mat mat = new Mat(areaOfMappingPoints[0].length,areaOfMappingPoints.length, CvType.CV_32FC3, new Scalar(red));
        for (int i = 0; i < areaOfMappingPoints.length; i++) {
            for (int j = 0; j < areaOfMappingPoints[0].length; j++) {
                if(areaOfMappingPoints[i][j].numberOfImagesNotInTheSameAngle(angle) >= numberOfImages) {
                    mat.put(j, i, green);
                }
            }
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyy-kkmmss");
        String fileName = format.format(cal.getTime()) + ".jpg";
        if(Imgcodecs.imwrite(folderToSaveMapping + fileName,mat))
            System.out.println("Image was saved");
        else
            System.err.println("no saving...");
    }

    public void saveAsImage(double angle){
        double[] white = {255,255,255};
        double[] red = {0,0,255};
        double[] green = {0,255,0};
        double[] blue = {255,0,0};
        Mat mat = new Mat(areaOfMappingPoints[0].length,areaOfMappingPoints.length, CvType.CV_32FC3, new Scalar(white));
        for (int i = 0; i < areaOfMappingPoints.length; i++) {
            for (int j = 0; j < areaOfMappingPoints[0].length; j++) {
                double number = areaOfMappingPoints[i][j].numberOfImagesNotInTheSameAngle(angle);
                if(number >= 9)
                    mat.put(j, i, blue);
                else if (number > 0){
                    if (number <= 5){
                        double[] color = {0,63*(number - 1),255 - 63*(number - 1)};
                        mat.put(j, i, color);
                    }
                    else {
                        double[] color = {42*(number - 5),255 - 42*(number - 5),0};
                        mat.put(j, i, color);
                    }
                }
            }
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyy-kkmmss");
        String fileName = format.format(cal.getTime()) + ".jpg";
        if(Imgcodecs.imwrite(folderToSaveMapping + fileName,mat))
            System.out.println("Image was saved");
        else
            System.err.println("no saving...");
    }

    public void saveAsImageOfResolution(double height){
        Mat mat = new Mat(areaOfMappingPoints[0].length,areaOfMappingPoints.length, CvType.CV_32FC3);
        for (int i = 0; i < areaOfMappingPoints.length; i++) {
            for (int j = 0; j < areaOfMappingPoints[0].length; j++) {

                double blue = 255 - (int)((areaOfMappingPoints[i][j].getShortestDistance() - height));
                if (blue < 0)
                    blue = 0;
                double[] color = {blue,0,0};
                if (areaOfMappingPoints[i][j].getShortestDistance() == -1){
                    color = new double[]{0,0,0};
                }
                mat.put(j,i,color);
            }
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("ddMMyy-kkmmss");
        String fileName = format.format(cal.getTime()) + ".jpg";
        if(Imgcodecs.imwrite(folderToSaveMapping + fileName,mat))
            System.out.println("Image was saved");
        else
            System.err.println("no saving...");
    }

    private Point2D getArrayLocationFromLocation(Location location){

        location.setUtmReferenceLine(referenceLocation);
        Point2D locationUTM = location.getUtmLocationAsRefPointAsZeroPoint();
        return Point2D.cartesianPoint(locationUTM.getX() - firstVertex.getX(),firstVertex.getY() - locationUTM.getY());
    }

    public Point3D addHeightToLocation(Location location){

        location.setUtmReferenceLine(referenceLocation);
        Point2D locationUTM = location.getUtmLocationAsRefPointAsZeroPoint();
        return addHeightToPoint2dUTMLocation(locationUTM);
    }

    private Point3D addHeightToPoint2dUTMLocation(Point2D locationUTM){
        Double z = getHeightFromUTMLocation(locationUTM);
        if (z == null)
            return null;
        return Point3D.cartesianPoint(locationUTM.getX() , locationUTM.getY(),z);
    }

    public Double getHeightFromUTMLocation(Point2D locationUTM){
        double x = locationUTM.getX() - firstVertex.getX();
        double y = firstVertex.getY() - locationUTM.getY();

        int i = (int) x;
        int j = (int) y;

        if (j < 0 || i < 0){
            return null;
        }
        double z2 = areaOfMappingPoints[i+1][j].getHeight();
        double z3 = areaOfMappingPoints[i][j+1].getHeight();

        x -= i;
        y -= j;

        if(x+y <= 1){
            double z1 = areaOfMappingPoints[i][j].getHeight();
            return (z2 - z1)*x/density + (z3 - z1)*y/density + z1;
        }
        double z1 = areaOfMappingPoints[i+1][j+1].getHeight();
        x = 1 - x;
        y = 1 - y;
        return (z3 - z1)*x/density + (z2 - z1)*y/density + z1;
    }

    public int getDensity() {
        return density;
    }
}
