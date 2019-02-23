package eyesatop.eyesatop.camera.cameracalibration;

import java.util.ArrayList;

import eyesatop.eyesatop.camera.RawData;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 08/06/2017.
 */

public class FindSeccondAproxTetaVsRadius {

    private ArrayList<Double>[] dteta;
    private double deltaTetaUsed;

    public FindSeccondAproxTetaVsRadius() {



    }

    public ArrayList<RawData> findPartOfTeta(RawData rawData, double delta_Teta, FindFirstAproxTetaVsRadius findFirstAproxTetaVsRadius){

        int maxZone = (int) (Math.PI/delta_Teta);
        ArrayList<RawData> data = new ArrayList<>();

        ArrayList<Pixel>[] pixels = new ArrayList[maxZone];
        ArrayList<Point3D>[] points3d = new ArrayList[maxZone];
        dteta = new ArrayList[maxZone];

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = new ArrayList<>();
            points3d[i] = new ArrayList<>();
            dteta[i] = new ArrayList<>();
        }

        for (int i = 0; i < rawData.size(); i++) {
            int zone = (int) ((rawData.getPoint3DsForCalibration().get(i).getTetaCameraPointOfView())/delta_Teta);
            if(zone < maxZone)
            {
                pixels[zone].add(rawData.getPixelsForCalibration().get(i));
                points3d[zone].add(rawData.getPoint3DsForCalibration().get(i));
                double dt = findFirstAproxTetaVsRadius.getPolynomFunction().value(rawData.getPoint3DsForCalibration().get(i).getTetaCameraPointOfView()) - rawData.getPixelsForCalibration().get(i).getRadius(rawData.getFrame());
                dteta[zone].add(dt);
            }
            else
                System.err.println("problem with angle teta");
        }
        ArrayList<Integer> des = new ArrayList<Integer>();

        for (int i = 0; i < pixels.length; i++) {
            if(pixels[i].size() > 15)
            {
                des.add(i);
                i += 10;
            }
        }
        if(des.size() < 2)
        {
            delta_Teta += 0.025;
            if(delta_Teta > 0.5)
            {
                return null;
            }
            return findPartOfTeta(rawData,delta_Teta,findFirstAproxTetaVsRadius);
        }

        for (int i = 0; i < des.size(); i++) {
            data.add(new RawData(pixels[des.get(i)],points3d[des.get(i)], rawData.getCameraSN(), rawData.getFrame()));
        }
        deltaTetaUsed = delta_Teta;
        return data;
    }


}

