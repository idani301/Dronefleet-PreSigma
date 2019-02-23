package eyesatop.eyesatop.camera.cameracalibration;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;

import java.util.ArrayList;

import eyesatop.eyesatop.camera.RawData;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.ParametricPolynomFunction;
import eyesatop.math.Polynom;
import eyesatop.math.WeightedObservedPointsWithMathExtension;
import eyesatop.math.camera.Frame;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 09/05/2017.
 */

public class FindFirstAproxTetaVsRadius {

    private double deltaPhiUsed;

    private final LeastSquaresOptimizer.Optimum optimum;
    private final Polynom polynomFunction;
    private final RawData rawData;

    public FindFirstAproxTetaVsRadius(RawData rawData){
        ArrayList<RawData> rawDatas = null;
        try {
            rawDatas = findPartOfPhi(rawData,0.0005);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<LeastSquaresOptimizer.Optimum> calculatedParameters = new ArrayList<>();
        WeightedObservedPointsWithMathExtension points;

        ParametricPolynomFunction parametricPolynomFunction = ParametricPolynomFunction.noConstPolynom(5);

        for (int i = 0; i < rawDatas.size(); i++) {
            points = new WeightedObservedPointsWithMathExtension();
            RawData dataToFit = rawDatas.get(i);
            for (int j = 0; j < dataToFit.size(); j++) {
                points.add(dataToFit.getPoint3DsForCalibration().get(j).getTetaCameraPointOfView(),dataToFit.getPixelsForCalibration().get(j).getRadius(dataToFit.getFrame()));
            }
            calculatedParameters.add(parametricPolynomFunction.getCalculatedParameters(points));
        }

        double minRMS = calculatedParameters.get(0).getRMS();
        int indexMinRMS = 0;

        for (int i = 1; i < calculatedParameters.size(); i++) {

            double temp = calculatedParameters.get(i).getRMS();
            if(temp < minRMS){
                minRMS = temp;
                indexMinRMS = i;
            }
        }

        optimum = calculatedParameters.get(indexMinRMS);

        ArrayList<Double> polynomParameterses = new ArrayList<>();
        double[] coeff = optimum.getPoint().toArray();
        polynomParameterses.add(0.0);
        for (int i = 1; i <= parametricPolynomFunction.getPolynomDegree(); i++) {
                polynomParameterses.add(coeff[i-1]);
        }
        polynomFunction = new Polynom(polynomParameterses);
        this.rawData = rawDatas.get(indexMinRMS);
    }

    public FindFirstAproxTetaVsRadius(RawData rawData,int stam){
        ArrayList<RawData> rawDatas = new ArrayList<>();
        rawDatas.add(rawData);
        ArrayList<LeastSquaresOptimizer.Optimum> calculatedParameters = new ArrayList<>();
        WeightedObservedPointsWithMathExtension points;

        ParametricPolynomFunction parametricPolynomFunction = ParametricPolynomFunction.noConstPolynom(5);

        for (int i = 0; i < rawDatas.size(); i++) {
            points = new WeightedObservedPointsWithMathExtension();
            RawData dataToFit = rawDatas.get(i);
            for (int j = 0; j < dataToFit.size(); j++) {
                points.add(dataToFit.getPoint3DsForCalibration().get(j).getTetaCameraPointOfView(),dataToFit.getPixelsForCalibration().get(j).getRadius(dataToFit.getFrame()));
            }
            calculatedParameters.add(parametricPolynomFunction.getCalculatedParameters(points));
        }

        double minRMS = calculatedParameters.get(0).getRMS();
        int indexMinRMS = 0;

        for (int i = 1; i < calculatedParameters.size(); i++) {

            double temp = calculatedParameters.get(i).getRMS();
            if(temp < minRMS){
                minRMS = temp;
                indexMinRMS = i;
            }
        }

        optimum = calculatedParameters.get(indexMinRMS);

        ArrayList<Double> polynomParameterses = new ArrayList<>();
        double[] coeff = optimum.getPoint().toArray();
        polynomParameterses.add(0.0);
        for (int i = 1; i <= parametricPolynomFunction.getPolynomDegree(); i++) {
            polynomParameterses.add(coeff[i-1]);
        }
        polynomFunction = new Polynom(polynomParameterses);
        this.rawData = rawDatas.get(indexMinRMS);
    }

    public ArrayList<RawData> findPartOfPhi(RawData rawData, double delta_phi) throws Exception {

        int maxZone = (int) (6*Math.PI/delta_phi);
        ArrayList<RawData> data = new ArrayList<>();

        ArrayList<Pixel>[] pixels = new ArrayList[maxZone];
        ArrayList<Point3D>[] points3d = new ArrayList[maxZone];

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = new ArrayList<>();
            points3d[i] = new ArrayList<>();
        }

        for (int i = 0; i < rawData.size(); i++) {
            int zone = (int) ((rawData.getPixelsForCalibration().get(i).getAngle(rawData.getFrame()) + 2*Math.PI)/delta_phi);
            if(zone < maxZone)
            {
                pixels[zone].add(rawData.getPixelsForCalibration().get(i));
                points3d[zone].add(rawData.getPoint3DsForCalibration().get(i));
            }
            else
                System.err.println("problem with angle teta");
        }
        ArrayList<Integer> des = new ArrayList<>();

        for (int i = 0; i < pixels.length; i++) {
            if(pixels[i].size() > 15 && getMinRadiusFromPixels(pixels[i], rawData.getFrame()) < 300 && getMaxRadiusFromPixels(pixels[i], rawData.getFrame()) > 1600)
            {
                des.add(i);
                i += 10;
            }
        }
        if(des.size() < 2) {
            delta_phi += 0.005;
            if(delta_phi > 0.5) {
                throw new Exception("Problem with the data...");
            }
            return findPartOfPhi(rawData,delta_phi);
        }

        for (int i = 0; i < des.size(); i++) {
            data.add(new RawData(pixels[des.get(i)],points3d[des.get(i)], rawData.getCameraSN(), rawData.getFrame()));
        }
        deltaPhiUsed = delta_phi;
        return data;
    }

    public double RMS(){
        double RMS = 0;
        for (int i = 0; i < rawData.size(); i++) {
            RMS += Math.pow(polynomFunction.value(rawData.getPoint3DsForCalibration().get(i).getTetaCameraPointOfView()) - rawData.getPixelsForCalibration().get(i).getRadius(rawData.getFrame()),2);
        }

        return Math.sqrt(RMS/ rawData.size());

    }


    public double getMinRadiusFromPixels(ArrayList<Pixel> pixels, Frame frame){
        double minRadius = pixels.get(0).getRadius(frame);
        for (int i = 1; i < pixels.size(); i++) {
            double temp = pixels.get(i).getRadius(frame);
            if(temp < minRadius)
                minRadius = temp;
        }
        return minRadius;
    }

    public double getMaxRadiusFromPixels(ArrayList<Pixel> pixels, Frame frame){
        double maxRadius = pixels.get(0).getRadius(frame);
        for (int i = 1; i < pixels.size(); i++) {
            double temp = pixels.get(i).getRadius(frame);
            if(temp > maxRadius)
                maxRadius = temp;
        }
        return maxRadius;
    }

    public double getDeltaPhiUsed() {
        return deltaPhiUsed;
    }

    public double getRMS() {
        return optimum.getRMS();
    }

    public Polynom getPolynomFunction() {
        return polynomFunction;
    }

    public LeastSquaresOptimizer.Optimum getOptimum() {
        return optimum;
    }
}
