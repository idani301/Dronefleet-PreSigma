package eyesatop.eyesatop.util.readfromtxtfile;

import java.io.File;
import java.util.ArrayList;

import eyesatop.math.Geometry.Point3D;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 07/05/2017.
 */

public class ReadObjectToPixel {
    private ArrayList<ArrayList<GroupOfRegexFindings>> arrayListsOfMatchers = new ArrayList<>();
    private final long cameraSN;

    public ReadObjectToPixel(File file) throws Exception {
        cameraSN = ReadHeaders.ReadCameraSN(file);
        ReadFromTXTFile readFromTXTFile = new ReadFromTXTFile(file);
        ArrayList<String> patterns = new ArrayList<>();
        patterns.add("\\((\\d*.\\d*)\\,(\\d*.\\d*)\\)");
        patterns.add("\\{(-?\\d*.\\d*E?-?\\d*),(-?\\d*.\\d*E?-?\\d*)\\}");
        arrayListsOfMatchers = readFromTXTFile.getAllRegexFromTxtFile(patterns);
    }

    public ArrayList<ArrayList<GroupOfRegexFindings>> getArrayListsOfMatchers() {
        return arrayListsOfMatchers;
    }

    public ArrayList<Pixel> getPixelsForCalibration(){
        ArrayList<Pixel> pixels = new ArrayList<>();
        for (int i = 0; i < arrayListsOfMatchers.get(0).size(); i++){
            double x = Double.parseDouble(arrayListsOfMatchers.get(0).get(i).getStrings().get(0));
            double y = Double.parseDouble(arrayListsOfMatchers.get(0).get(i).getStrings().get(1));
            pixels.add(new Pixel(x,y,1.6e-6));
        }
        return pixels;
    }

    public ArrayList<Point3D> getStarVectorsForCalibration(){
        ArrayList<Point3D> point3Ds = new ArrayList<>();
        for (int i = 0; i < arrayListsOfMatchers.get(1).size(); i++){
            double teta = 0;
            try {
                teta = Double.parseDouble(arrayListsOfMatchers.get(1).get(i).getStrings().get(0));
            }catch (Exception e){
                System.out.print(e.fillInStackTrace());
            }
            double psai = Double.parseDouble(arrayListsOfMatchers.get(1).get(i).getStrings().get(1));
            point3Ds.add(Point3D.cameraSpherePointOfView(teta,psai));
        }
        return point3Ds;
    }

    public ArrayList<Point3D> getStarVectors(){
        ArrayList<Point3D> point3Ds = new ArrayList<>();
        for (int i = 0; i < arrayListsOfMatchers.get(1).size(); i++){
            double elevation = 0;
            try {
                elevation = Double.parseDouble(arrayListsOfMatchers.get(1).get(i).getStrings().get(0));
            }catch (Exception e){
                System.out.print(e.fillInStackTrace());
            }
            double azimuth = Double.parseDouble(arrayListsOfMatchers.get(1).get(i).getStrings().get(1));
            point3Ds.add(Point3D.ElevationAzimuthPointOfView(elevation,azimuth));
        }
        return point3Ds;
    }

    public long getCameraSN() {
        return cameraSN;
    }
}
