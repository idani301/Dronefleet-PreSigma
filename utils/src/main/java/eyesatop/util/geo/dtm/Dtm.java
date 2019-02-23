package eyesatop.util.geo.dtm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Polygon;
import eyesatop.math.MathException;
import eyesatop.util.geo.Location;

/**
 * Created by Einav on 09/04/2018.
 */

public class Dtm {

    private final double cellSize;
    private final float[][] dtm; // [lat][lon]
    private final Location corner;
    private final float noDataValue;

    public Dtm(double cellSize, float[][] dtm, Location corner, float noDataValue) throws MathException {
        this.cellSize = cellSize;
        this.dtm = dtm;
        this.corner = corner;
        this.noDataValue = noDataValue;
    }

    public static Dtm ReadASCFileDTM(File file) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        String numberNPattern = "(\\d?\\d*)";
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
                    throw new Exception("wrong file");
                }
            }
            if (st.contains("nrows")) {
                pattern = Pattern.compile(numberNPattern);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    length = Integer.parseInt(m.group(0));
                }else {
                    throw new Exception("wrong file");
                }
            }
            if (st.contains("xllcorner")) {
                pattern = Pattern.compile(doubleNumber);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    lon = Double.parseDouble(m.group(0));
                }else {
                    throw new Exception("wrong file");
                }
            }
            if (st.contains("yllcorner")) {
                pattern = Pattern.compile(doubleNumber);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    lat = Double.parseDouble(m.group(0));
                }else {
                    throw new Exception("wrong file");
                }
            }
            if (st.contains("cellsize")) {
                pattern = Pattern.compile(doubleNumber);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    cellSize = Double.parseDouble(m.group(0));
                }else {
                    throw new Exception("wrong file");
                }
            }
            if (st.contains("nodata_value")) {
                pattern = Pattern.compile(doubleNumber);
                Matcher m = pattern.matcher(st);
                if (m.find( )) {
                    noDataValue = Float.parseFloat(m.group(0));
                }else {
                    throw new Exception("wrong file");
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

        return new Dtm(cellSize,floats,location,noDataValue);
    }


    public Double getAltitude(Location location){


        int width = dtm[0].length;
        int length = dtm.length;

        double latI = length + (corner.getLatitude() - location.getLatitude())/cellSize;
        double lonI = -(corner.getLongitude() - location.getLongitude())/cellSize;

        int i = (int) (latI);
        int j = (int) (lonI);

        if (j < 0 || i < 0 || i > length || j > width){
            return null;
        }

        double z2 = dtm[i+1][j];
        double z3 = dtm[i][j+1];
        if (z2 == noDataValue || z3 == noDataValue)
            return null;
        latI -= i;
        lonI -= j;

        if(latI+lonI <= 1){
            double z1 = dtm[i][j];
            if (z1 == noDataValue)
                return null;
            System.out.println(z1 + "," + z2 + "," + z3);
            return (z2 - z1)*latI + (z3 - z1)*lonI + z1;
        }
        double z1 = dtm[i+1][j+1];
        if (z1 == noDataValue)
            return null;
        System.out.println(z1 + "," + z2 + "," + z3);
        latI = 1 - latI;
        lonI = 1 - lonI;
        return (z3 - z1)*latI + (z2 - z1)*lonI + z1;
    }

    private double distance(double x, double y, Point2D point2D){
        return Math.sqrt(Math.pow(x - point2D.getX(),2) + Math.pow(y - point2D.getY(),2));
    }



    public class Index {
        private final int i;
        private final int j;

        public Index(int i, int j) {
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
}
