package eyesatop.util.geo.dtm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import eyesatop.math.Geometry.Point2D;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.model.ObservableValue;

/**
 * Created by Einav on 06/11/2017.
 */

public class DtmObject implements DtmProvider{

    public enum DensityLevel{
        HIGH,
        MID,
        LOW
    }

    private final double[][] dtm;
    private final int width;
    private final int length;

    private final double density;
    private final double densityInDegree;
    private final Location startLocation;

    protected DtmObject(int width, int length, double densityInDegree, Location startLocation) throws Exception {

        this.width = width;
        this.length = length;
        double density = startLocation.distance(startLocation.latitude(startLocation.getLatitude() + densityInDegree));
        if (density < 0){
            throw new Exception("density must be positive");
        }
        this.densityInDegree = densityInDegree;
        this.density = density;
        this.startLocation = startLocation;

        dtm = new double[this.width][this.length];
    }

    //all params in meters
    public static DtmObject LeveledDtm(double lengthNorth, double lengthEast, DensityLevel densityLevel, Location centerDtmLocation, double bias) throws Exception {

        DtmObject dtmObject = DtmObject.CreateDTM(lengthNorth,lengthEast,densityLevel,centerDtmLocation);

        for (int i = 0; i < dtmObject.width; i++) {
            for (int j = 0; j < dtmObject.length; j++) {
                dtmObject.setDtmPoint(i,j,bias);
            }
        }
        return dtmObject;
    }

    public static DtmObject CreateDTM(double lengthNorth, double lengthEast, DensityLevel densityLevel, Location centerDtmLocation) throws Exception {
        double density = 0.01;
        switch (densityLevel){
            case HIGH:
                density = 0.000002; //20 cm
                break;
            case MID:
                density = 0.00001; //1 m
                break;
            case LOW:
                density = 0.0001; //10 m
                break;
        }
        Point2D point2D = Point2D.GeographicPoint(lengthNorth/2,lengthEast/2);

        Location startLocation = centerDtmLocation.getLocationFromAzAndDistance(point2D.getRadius(),-Math.toDegrees(point2D.getAngle()));
        double distanceLengthNorth = -startLocation.getLatitude() + startLocation.getLocationFromAzAndDistance(lengthNorth,0).getLatitude();
        double distanceLengthEast = -startLocation.getLongitude() + startLocation.getLocationFromAzAndDistance(lengthEast,90).getLongitude();
        int lengthLat = (int) (distanceLengthNorth/density);
        int lengthLon = (int) (distanceLengthEast/density);
        return new DtmObject(lengthLat, lengthLon, density, startLocation);
    }

    public static DtmObject PlaneDTM(double lengthNorth, double lengthEast, DensityLevel densityLevel, Location centerDtmLocation, double bias, int numberOfObstacle) throws Exception {

        DtmObject dtmObject = CreateDTM(lengthNorth,lengthEast,densityLevel,centerDtmLocation);
        Random random = new Random();
        int luckyNumber = (int) (Math.min(dtmObject.width,dtmObject.length)*random.nextDouble());
        double maxSlope = 0.1;
        dtmObject.setDtmPoint(0,0,bias);
        for (int i = 0; i < dtmObject.width; i++) {
            for (int j = 0; j < dtmObject.length; j++) {
                if (j == 0 && i != 0){
                    dtmObject.setDtmPoint(i,j,dtmObject.getDtmPoint(i-1,j) + random.nextDouble()*maxSlope - random.nextDouble()*maxSlope);
                } else if (i == 0 && j != 0){
                    dtmObject.setDtmPoint(i,j,dtmObject.getDtmPoint(i,j-1) + random.nextDouble()*maxSlope - random.nextDouble()*maxSlope);
                } else if (i != 0){
                    if (i != luckyNumber) {
                        dtmObject.setDtmPoint(i, j, (dtmObject.getDtmPoint(i - 1, j) + dtmObject.getDtmPoint(i, j - 1)) / 2 + random.nextDouble() * maxSlope - random.nextDouble() * maxSlope);
                    }
                    else {
                        dtmObject.setDtmPoint(i, j, (dtmObject.getDtmPoint(i - 1, j) + dtmObject.getDtmPoint(i, j - 1)) / 2 + random.nextDouble() * maxSlope);

                    }
                }
            }
        }

        for (int i = 0; i < numberOfObstacle; i++) {
            int width = (int) (0.0002*random.nextDouble()/dtmObject.densityInDegree);
            int length = (int) (0.0002*random.nextDouble()/dtmObject.densityInDegree);
            int latPosition = (int) (dtmObject.width*random.nextDouble());
            int lonPosition = (int) (dtmObject.length*random.nextDouble());

            double height = 10*random.nextDouble();

            for (int j = 0; j < width; j++) {
                for (int k = 0; k < length; k++) {
                    if (latPosition+j < dtmObject.width && lonPosition+k < dtmObject.length)
                        dtmObject.addToDtmPoint(latPosition+j,lonPosition+k,height + 3*random.nextDouble());
                }
            }
        }

        return dtmObject;
    }

    public void addToDtmPoint(int latI, int lonI, double height){
        dtm[latI][lonI] += height;
    }

    public void setDtmPoint(int latI, int lonI, double height){
        dtm[latI][lonI] = height;
    }

    public double getDtmPoint(int width, int length){
        return dtm[width][length];
    }

    public Location getLocationFromIJ(int latI, int lonI){
        return startLocation.latitude(startLocation.getLatitude() - latI*densityInDegree).longitude(startLocation.getLongitude() + lonI*densityInDegree);
    }

    @Override
    public ObservableValue<Double> dtmRaiseValue() {
        return null;
    }

    @Override
    public void raiseDTM(double value) {

    }

    @Override
    public void lowerDTM(double value) {

    }

    @Override
    public void clearRaiseValue() {

    }

    @Override
    public DtmProvider duplicate() {
        return null;
    }

    @Override
    public double density() {
        return density;
    }

    @Override
    public double terrainAltitude(Location location) throws TerrainNotFoundException {
        return getHeight(location);
    }

    @Override
    public double terrainAltitude(double lat, double lon) throws TerrainNotFoundException {
        return getHeight(new Location(lat,lon));
    }

    @Override
    public double maxTerrainAltitudeInArea(Location location, double areaSquareSide) throws TerrainNotFoundException {
        return 0;
    }

    @Override
    public List<Location> corners() {
        return null;
    }

    @Override
    public double maxSteps() {
        return 500;
    }

    @Override
    public double stepDistanceInMeters() {
        return 2.5;
    }

    public Double getHeight(Location location){

        double latI = (startLocation.getLatitude() - location.getLatitude())/densityInDegree;
        double lonI = -(startLocation.getLongitude() - location.getLongitude())/densityInDegree ;

        int i = (int) (latI);
        int j = (int) (lonI);

        if (j < 0 || i < 0 || i > width || j > length){
            return Double.NaN;
        }

        double z2 = dtm[i+1][j];
        double z3 = dtm[i][j+1];

        latI -= i;
        lonI -= j;

        if(latI+lonI <= 1){
            double z1 = dtm[i][j];
            return (z2 - z1)*latI + (z3 - z1)*lonI + z1;
        }
        double z1 = dtm[i+1][j+1];
        latI = 1 - latI;
        lonI = 1 - lonI;
        return (z3 - z1)*latI + (z2 - z1)*lonI + z1;
    }

    public Indexes getIndexFromLocation(Location location){
        int latI = (int) ((startLocation.getLatitude() - location.getLatitude())/densityInDegree);
        int lonI = (int) (-(startLocation.getLongitude() - location.getLongitude())/densityInDegree);

        return new Indexes(latI,lonI);
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public void print(File file){
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < length; j++) {
                    fileWriter.append(String.format("%1$.2f",dtm[i][j]) + " ");
                }
                fileWriter.append("\r\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
