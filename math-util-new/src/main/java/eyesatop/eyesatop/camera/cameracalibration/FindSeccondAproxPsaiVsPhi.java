package eyesatop.eyesatop.camera.cameracalibration;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;

import java.util.ArrayList;

import eyesatop.eyesatop.camera.RawData;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.HarmonicParameters;
import eyesatop.math.HarmonicSinus;
import eyesatop.math.ParametricHarmonicFunction;
import eyesatop.math.ParametricPolynomFunction;
import eyesatop.math.Polynom;
import eyesatop.math.WeightedObservedPointsWithMathExtension;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 10/06/2017.
 */

public class FindSeccondAproxPsaiVsPhi {

    private ArrayList<Double>[] dpsai;
    private double deltaTetaUsed;
    private ArrayList<Double> RMS = new ArrayList<>();

    private ArrayList<LeastSquaresOptimizer.Optimum> calculatedParameters = new ArrayList<>();
    private ArrayList<HarmonicSinus> harmonicSinus = new ArrayList<>();
    private ArrayList<RawData> data;
    private ArrayList<Double> zoneValue = new ArrayList<>();

    private LeastSquaresOptimizer.Optimum optimumPhase;
    private LeastSquaresOptimizer.Optimum optimumAmplitude;

    private Polynom phaseFunction;
    private Polynom AmplitudeFunction;
    private double constParameter;

    public FindSeccondAproxPsaiVsPhi(RawData rawData) {

        data = findPartOfTeta(rawData,0.0005);
        if (data == null)
            return;
        ParametricHarmonicFunction parametricHarmonicFunction = new ParametricHarmonicFunction(null,1,null,1.0);

        calculatedParameters = calcHarmonicParameters(parametricHarmonicFunction,3);

        constParameter = 0;
        for (int i = 0; i < calculatedParameters.size(); i++) {
            constParameter += calculatedParameters.get(i).getPoint().toArray()[0]/calculatedParameters.size();
        }

        parametricHarmonicFunction = new ParametricHarmonicFunction(null,1,constParameter,1.0);
        calculatedParameters = calcHarmonicParameters(parametricHarmonicFunction,2);

        ParametricPolynomFunction parametricPolynomFunction = new ParametricPolynomFunction(null,3);


        WeightedObservedPointsWithMathExtension phasePoints = new WeightedObservedPointsWithMathExtension();
        for (int i = 0; i < calculatedParameters.size(); i++) {
            phasePoints.add(zoneValue.get(i),calculatedParameters.get(i).getPoint().toArray()[1]);
        }
        optimumPhase = parametricPolynomFunction.getCalculatedParameters(phasePoints);
        //System.out.println("Phase Function RMS: " + optimumPhase.getRMS());

        phaseFunction = new Polynom(optimumPhase.getPoint().toArray());

        calculatedParameters = calcHarmonicParametersWithPhase(constParameter,phaseFunction);

        WeightedObservedPointsWithMathExtension amplitudePoints = new WeightedObservedPointsWithMathExtension();
        for (int i = 0; i < calculatedParameters.size(); i++) {
            amplitudePoints.add(zoneValue.get(i),calculatedParameters.get(i).getPoint().toArray()[0]);
        }

        optimumAmplitude = parametricPolynomFunction.getCalculatedParameters(amplitudePoints);
        //System.out.println("AmplitudeFunction RMS: " + optimumAmplitude.getRMS());

        AmplitudeFunction = new Polynom(optimumAmplitude.getPoint().toArray());

    }

    public ArrayList<RawData> findPartOfTeta(RawData rawData, double delta_Teta) {

            int maxZone = (int) (Math.PI/delta_Teta);
            ArrayList<RawData> data = new ArrayList<>();

            ArrayList<Double>[] dpsaiZones = new ArrayList[maxZone];
            ArrayList<Pixel>[] pixels = new ArrayList[maxZone];
            ArrayList<Point3D>[] points3d = new ArrayList[maxZone];

            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = new ArrayList<>();
                points3d[i] = new ArrayList<>();
                dpsaiZones[i] = new ArrayList<>();
            }
            for (int i = 0; i < rawData.size(); i++) {
                int zone = (int) ((rawData.getPoint3DsForCalibration().get(i).getTetaCameraPointOfView())/delta_Teta);
                if(zone < maxZone)
                {
                    pixels[zone].add(rawData.getPixelsForCalibration().get(i));
                    points3d[zone].add(rawData.getPoint3DsForCalibration().get(i));
                    double dangle = rawData.getPsaiFromPoint3d(i) - rawData.getPhiFromPixel(i);
                    if(dangle < 0){
                        dangle += 2*Math.PI;
                    }
                    dpsaiZones[zone].add(dangle);
                }
                else
                    System.err.println("problem with angle teta");
            }

        ArrayList<Integer> des = new ArrayList<>();

            for (int i = 0; i < pixels.length; i++) {
                if(pixels[i].size() > 15)
                {
                    des.add(i);
                    i += 5;
                }
            }
            if(des.size() < 6 || !isMinZone(des,delta_Teta,0.4) || !isMaxZone(des,delta_Teta,0.8))
            {
                delta_Teta += 0.0025;
                if(delta_Teta > 0.5)
                {
                    deltaTetaUsed = -1;
                    return null;
                }
                return findPartOfTeta(rawData,delta_Teta);
            }

            dpsai = new ArrayList[des.size()];
            for (int i = 0; i < des.size(); i++) {
                data.add(new RawData(pixels[des.get(i)],points3d[des.get(i)], rawData.getCameraSN(), rawData.getFrame()));
                dpsai[i] = dpsaiZones[des.get(i)];
                zoneValue.add((double)(des.get(i))*delta_Teta + delta_Teta/2);
            }

            deltaTetaUsed = delta_Teta;
            return data;

    }

    private boolean isMinZone(ArrayList<Integer> integers, double deltaTeta, double minTeta){

        for (int i = 0; i < integers.size(); i++) {
            double zoneValue = (double)(integers.get(i))*deltaTeta;
            if (minTeta > zoneValue)
                return true;
        }
        return false;
    }

    private boolean isMaxZone(ArrayList<Integer> integers, double deltaTeta, double maxTeta){

        for (int i = 0; i < integers.size(); i++) {
            double zoneValue = (double)(integers.get(i))*deltaTeta;
            if (maxTeta < zoneValue)
                return true;
        }
        return false;
    }

    private ArrayList<LeastSquaresOptimizer.Optimum> calcHarmonicParameters(ParametricHarmonicFunction parametricHarmonicFunction, int numOfParameters){
        ArrayList<LeastSquaresOptimizer.Optimum> optima = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            optima.add(calcHarmonicParametersOneFunction(parametricHarmonicFunction,numOfParameters,i));
        }
        return optima;
    }

//    private ArrayList<LeastSquaresOptimizer.Optimum> calcHarmonicParametersWithAmplitude(double constParameter, Polynom AmplitudeFunction){
//        ArrayList<LeastSquaresOptimizer.Optimum> optima = new ArrayList<>();
//        for (int i = 0; i < data.size(); i++) {
//            ArrayList<HarmonicParameters> harmonicParameterses = new ArrayList<>();
//            harmonicParameterses.add(new HarmonicParameters(1, HarmonicParameters.HarmonicParametersNames.Amplitude, AmplitudeFunction.value(zoneValue.get(i))));
//            ParametricHarmonicFunction parametricHarmonicFunction = new ParametricHarmonicFunction(harmonicParameterses,1,constParameter,1.0);
//            optima.add(calcHarmonicParametersOneFunction(parametricHarmonicFunction,1,i));
//        }
//        return optima;
//    }

    private ArrayList<LeastSquaresOptimizer.Optimum> calcHarmonicParametersWithPhase(double constParameter, Polynom PhaseFunction){
        ArrayList<LeastSquaresOptimizer.Optimum> optima = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            ArrayList<HarmonicParameters> harmonicParameterses = new ArrayList<>();
            harmonicParameterses.add(new HarmonicParameters(1, HarmonicParameters.HarmonicParametersNames.phase, PhaseFunction.value(zoneValue.get(i))));
            ParametricHarmonicFunction parametricHarmonicFunction = new ParametricHarmonicFunction(harmonicParameterses,1,constParameter,1.0);
            optima.add(calcHarmonicParametersOneFunction(parametricHarmonicFunction,1,i));
        }
        return optima;
    }

    private LeastSquaresOptimizer.Optimum calcHarmonicParametersOneFunction(ParametricHarmonicFunction parametricHarmonicFunction, int numOfParameters, int i){
        WeightedObservedPointsWithMathExtension points;

        points = new WeightedObservedPointsWithMathExtension();
        RawData dataToFit = data.get(i);
        for (int j = 0; j < dataToFit.size(); j++) {
            points.add(dataToFit.getPsaiFromPoint3d(j),dpsai[i].get(j));
        }
        double start[] = new double[numOfParameters];
        switch (numOfParameters){
            case 3:
                start[0] = points.getmeanMaxMinY();
                start[1] = points.getMaxDifferenceInY();
                start[2] = 0;
                break;
            case 2:
                start[0] = points.getMaxDifferenceInY();
                start[1] = 0;
                break;
            case 1:
                start[0] = points.getMaxDifferenceInY();
        }

        return parametricHarmonicFunction.getCalculatedParametersWithStartingParameters(points,start);
    }

    public ArrayList<Double>[] getDpsai() {
        return dpsai;
    }

    public double getDeltaTetaUsed() {
        return deltaTetaUsed;
    }

    public ArrayList<Double> getRMS() {
        return RMS;
    }

    public ArrayList<LeastSquaresOptimizer.Optimum> getCalculatedParameters() {
        return calculatedParameters;
    }

    public ArrayList<HarmonicSinus> getHarmonicSinus() {
        return harmonicSinus;
    }

    public ArrayList<RawData> getData() {
        return data;
    }

    public ArrayList<Double> getZoneValue() {
        return zoneValue;
    }

    public LeastSquaresOptimizer.Optimum getOptimumPhase() {
        return optimumPhase;
    }

    public LeastSquaresOptimizer.Optimum getOptimumAmplitude() {
        return optimumAmplitude;
    }

    public Polynom getPhaseFunction() {
        return phaseFunction;
    }

    public Polynom getAmplitudeFunction() {
        return AmplitudeFunction;
    }

    public double getConstParameter() {
        return constParameter;
    }
}
