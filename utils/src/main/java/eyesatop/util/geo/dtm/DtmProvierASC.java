
package eyesatop.util.geo.dtm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 10/04/2018.
 */

public class DtmProvierASC implements DtmProvider {

    private final Property<Double> raiseValue = new Property<>(0D);

    private final double cellSize;
    private final float[][] dtm; // [lat][lon]
    private final Location corner;
    private final float noDataValue;

    private final List<Location> corners = new ArrayList<>();
    
    public DtmProvierASC(File dtmFileSource) throws IOException,IllegalArgumentException{

        if(!dtmFileSource.exists()){
            throw new IllegalArgumentException("Not found");
        }

        BufferedReader br = new BufferedReader(new FileReader(dtmFileSource));
        String st;
        String numberNPattern = "(\\d\\d*)";
        String doubleNumber = "(-?\\d?\\d*\\.?\\d\\d*)";
        Pattern pattern;
        int width = 0;
        int length = 0;
        double lat = 0;
        double lon = 0;
        double cellSize = 0;
        Location location;
        float noDataValue = -9999;
        float[][] floats;
        while ((st = br.readLine()) != null) {
            if (st.contains("ncols")) {
                pattern = Pattern.compile(numberNPattern);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    width = Integer.parseInt(m.group(0));
                }else {
                    throw new IllegalArgumentException("wrong file");
                }
            }
            if (st.contains("nrows")) {
                pattern = Pattern.compile(numberNPattern);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    length = Integer.parseInt(m.group(0));
                }else {
                    throw new IllegalArgumentException("wrong file");
                }
            }
            if (st.contains("xllcorner")) {
                pattern = Pattern.compile(doubleNumber);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    lon = Double.parseDouble(m.group(0));
                }else {
                    throw new IllegalArgumentException("wrong file");
                }
            }
            if (st.contains("yllcorner")) {
                pattern = Pattern.compile(doubleNumber);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    lat = Double.parseDouble(m.group(0));
                }else {
                    throw new IllegalArgumentException("wrong file");
                }
            }
            if (st.contains("cellsize")) {
                pattern = Pattern.compile(doubleNumber);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    cellSize = Double.parseDouble(m.group(0));
                }else {
                    throw new IllegalArgumentException("wrong file");
                }
            }
            if (st.contains("nodata_value")) {
                pattern = Pattern.compile(doubleNumber);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    noDataValue = Float.parseFloat(m.group(0));
                }else {
                    throw new IllegalArgumentException("wrong file");
                }
                break;
            }
        }
        location = new Location(lat,lon);
        floats = new float[length][width];
        int i = 0;
        int j = 0;
        while ((st = br.readLine()) != null) {
            pattern = Pattern.compile(doubleNumber);
            Matcher m = pattern.matcher(st);

            while (m.find()) {
                floats[i][j] = Float.parseFloat(m.group(0));
                j++;
                if (j >= width){
                    j = 0;
                    i++;
                }
            }
        }

        this.cellSize = cellSize;
        this.corner = location;
        this.dtm = floats;
        this.noDataValue = noDataValue;

        boolean isFound = false;
        for(int index1=0; index1 < length; index1++){
            for(int index2=0; index2 < width;index2++){
                if(floats[index1][index2] != noDataValue){
                    corners.add(new Location(corner.getLatitude() + cellSize * index1,corner.getLongitude() + cellSize*index2));
                     isFound = true;
                    break;
                }
            }
            if(isFound){
                break;
            }
        }

        isFound = false;
        for(int index2=0; index2 < width; index2++){
            for(int index1=0; index1 < length;index1++){
                if(floats[index1][index2] != noDataValue){
                    corners.add(new Location(corner.getLatitude() + cellSize * index1,corner.getLongitude() + cellSize*index2));
                    isFound = true;
                    break;
                }
            }
            if(isFound){
                break;
            }
        }

        isFound = false;
        for(int index2=width -1; index2 > 0; index2--){
            for(int index1=0; index1 < length;index1++){
                if(floats[index1][index2] != noDataValue){
                    corners.add(new Location(corner.getLatitude() + cellSize * index1,corner.getLongitude() + cellSize*index2));
                    isFound = true;
                    break;
                }
            }
            if(isFound){
                break;
            }
        }

        isFound = false;
        for(int index1=0; index1 < length; index1++){
            for(int index2=width-1; index2 > 0;index2--){
                if(floats[index1][index2] != noDataValue){
                    corners.add(new Location(corner.getLatitude() + cellSize * index1,corner.getLongitude() + cellSize*index2));
                    isFound = true;
                    break;
                }
            }
            if(isFound){
                break;
            }
        }

        isFound = false;
        for(int index1=length-1; index1 > 0; index1--){
            for(int index2=width-1; index2 > 0;index2--){
                if(floats[index1][index2] != noDataValue){
                    corners.add(new Location(corner.getLatitude() + cellSize * index1,corner.getLongitude() + cellSize*index2));
                    isFound = true;
                    break;
                }
            }
            if(isFound){
                break;
            }
        }


        isFound = false;
        for(int index2=width-1; index2 > 0; index2--){
            for(int index1=length-1; index1 > 0;index1--){
                if(floats[index1][index2] != noDataValue){
                    corners.add(new Location(corner.getLatitude() + cellSize * index1,corner.getLongitude() + cellSize*index2));
                    isFound = true;
                    break;
                }
            }
            if(isFound){
                break;
            }
        }

        isFound = false;
        for(int index2=0; index2 < width; index2++){
            for(int index1=length-1; index1 > 0;index1--){
                if(floats[index1][index2] != noDataValue){
                    corners.add(new Location(corner.getLatitude() + cellSize * index1,corner.getLongitude() + cellSize*index2));
                    isFound = true;
                    break;
                }
            }
            if(isFound){
                break;
            }
        }

        isFound = false;
        for(int index1=length-1; index1 > 0; index1--){
            for(int index2=0; index2 < width;index2++){
                if(floats[index1][index2] != noDataValue){
                    corners.add(new Location(corner.getLatitude() + cellSize * index1,corner.getLongitude() + cellSize*index2));
                    isFound = true;
                    break;
                }
            }
            if(isFound){
                break;
            }
        }
    }

    @Override
    public ObservableValue<Double> dtmRaiseValue() {
        return raiseValue;
    }

    @Override
    public void raiseDTM(double value) {
        raiseValue.set(raiseValue.value() + value);
    }

    @Override
    public void lowerDTM(double value) {
        raiseValue.set(raiseValue.value() - value);
    }

    @Override
    public void clearRaiseValue() {
        raiseValue.set(0D);
    }

    @Override
    public DtmProvider duplicate() {
        return this;
    }

    @Override
    public double density() {
        return corner.distance(corner.latitude(corner.getLatitude()+cellSize));
    }

    @Override
    public double terrainAltitude(Location location) throws TerrainNotFoundException {

        try {

            int width = dtm[0].length;
            int length = dtm.length;

            double latI = length + (corner.getLatitude() - location.getLatitude()) / cellSize;
            double lonI = -(corner.getLongitude() - location.getLongitude()) / cellSize;

            int i = (int) (latI);
            int j = (int) (lonI);

            if (j < 0 || i < 0 || i >= length - 1 || j >= width - 1) {
                throw new TerrainNotFoundException();
            }

            double z2 = dtm[i + 1][j];
            double z3 = dtm[i][j + 1];
            if (z2 == noDataValue || z3 == noDataValue)
                throw new TerrainNotFoundException();
            latI -= i;
            lonI -= j;

            if (latI + lonI <= 1) {
                double z1 = dtm[i][j];
                if (z1 == noDataValue)
                    throw new TerrainNotFoundException();

                return (z2 - z1) * latI + (z3 - z1) * lonI + z1;
            }
            double z1 = dtm[i + 1][j + 1];
            if (z1 == noDataValue)
                throw new TerrainNotFoundException();

            latI = 1 - latI;
            lonI = 1 - lonI;
            return (z3 - z1) * latI + (z2 - z1) * lonI + z1;
        }
        catch (Exception e){
            throw new TerrainNotFoundException();
        }
    }

    @Override
    public double terrainAltitude(double lat, double lon) throws TerrainNotFoundException {
        return terrainAltitude(new Location(lat,lon));
    }

    @Override
    public double maxTerrainAltitudeInArea(Location location, double areaSquareSide) throws TerrainNotFoundException {

        if(areaSquareSide < density()){
            return terrainAltitude(location);
        }

        int width = dtm[0].length;
        int length = dtm.length;

        double latI = length + (corner.getLatitude() - location.getLatitude()) / cellSize;
        double lonI = -(corner.getLongitude() - location.getLongitude()) / cellSize;

        int i = (int) (latI);
        int j = (int) (lonI);

        if (j < 0 || i < 0 || i >= length - 1 || j >= width - 1) {
            throw new TerrainNotFoundException();
        }

        List<IJ> moreIJ = new ArrayList<>();

        int sampleNumber = (int) (Math.round(areaSquareSide / density()));

        for(int index = -sampleNumber/2; index <= sampleNumber/2; index++){
            moreIJ.add(new IJ(i + index,j + index));
        }

        if(moreIJ.size() <=1){
            return terrainAltitude(location);
        }

        if(!isIndexesInRange(moreIJ.get(0).getI(),moreIJ.get(0).getJ())){
            throw new TerrainNotFoundException();
        }

        double maxTerrainAltitude = dtm[moreIJ.get(0).getI()][moreIJ.get(0).getJ()];

        if(maxTerrainAltitude == noDataValue){
            throw new TerrainNotFoundException();
        }

        for(int index=1; index < moreIJ.size(); index++){

            if(!isIndexesInRange(moreIJ.get(index).getI(),moreIJ.get(index).getJ())){
                throw new TerrainNotFoundException();
            }

            double indexASL = dtm[moreIJ.get(index).getI()][moreIJ.get(index).getJ()];

            if(indexASL == noDataValue || moreIJ.get(index).getI() >= length || moreIJ.get(index).getJ() >= width){
                throw new TerrainNotFoundException();
            }

            maxTerrainAltitude = Math.max(maxTerrainAltitude,indexASL);
        }

        return maxTerrainAltitude;
    }

    private boolean isIndexesInRange(int i, int j){

        if(i < 0 || j < 0 || i >= dtm.length || j >= dtm[0].length){
            return false;
        }

        return true;
    }

    private class IJ {
        private final int i;
        private final int j;

        private IJ(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }
    }

    @Override
    public List<Location> corners() {
        return corners;
    }

    @Override
    public double maxSteps() {
        return 400;
    }

    @Override
    public double stepDistanceInMeters() {
        return density()/2;
    }

    public Location getCorner() {
        return corner;
    }
}
