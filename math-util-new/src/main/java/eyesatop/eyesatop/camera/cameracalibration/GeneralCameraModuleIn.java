package eyesatop.eyesatop.camera.cameracalibration;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;

import java.io.File;
import java.util.ArrayList;

import eyesatop.eyesatop.util.readfromtxtfile.GroupOfRegexFindings;
import eyesatop.eyesatop.util.readfromtxtfile.ReadFromTXTFile;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Polynom;
import eyesatop.math.camera.CameraModule;
import eyesatop.math.camera.Frame;
import eyesatop.math.camera.Pixel;

/**
 * Created by Einav on 06/06/2017.
 */

public class GeneralCameraModuleIn implements CameraModule{

    private final Polynom polynomTetaToRadius;
    private final Frame frame;
    private final double pixelSize;

    private final Polynom amplitudeFunction;
    private final Polynom phaseFunction;
    private final double constParameter;
    private final double angularFrequency = 1;
    private final long cameraSN;
    private double moduleRMS;

    public GeneralCameraModuleIn(Polynom polynomTetaToRadius, Frame frame, double pixelSize, Polynom amplitudeFunction, Polynom phaseFunction, double constParameter, long cameraSN, double moduleRMS) {
        this.polynomTetaToRadius = polynomTetaToRadius;
        this.frame = frame;
        this.pixelSize = pixelSize;
        this.amplitudeFunction = amplitudeFunction;
        this.phaseFunction = phaseFunction;
        this.constParameter = constParameter;
        this.cameraSN = cameraSN;
        this.moduleRMS = moduleRMS;
    }

    public GeneralCameraModuleIn(Polynom polynomTetaToRadius, Frame frame, double pixelSize, Polynom amplitudeFunction, Polynom phaseFunction, double constParameter, long cameraSN) {
        this.polynomTetaToRadius = polynomTetaToRadius;
        this.frame = frame;
        this.pixelSize = pixelSize;
        this.amplitudeFunction = amplitudeFunction;
        this.phaseFunction = phaseFunction;
        this.constParameter = constParameter;
        this.cameraSN = cameraSN;
        this.moduleRMS = 0;
    }

    public static GeneralCameraModuleIn ReadGeneralCameraModule(File file) throws Exception {
        if (!file.getName().contains(".txt")) {
            throw new Exception("must have a txt file");
        }
        ReadFromTXTFile readFromTXTFile = new ReadFromTXTFile(file);
        ArrayList<String> pattern = new ArrayList<>();
        pattern.add("cameraSN=(\\d{10})");  //0
        pattern.add("polynomTetaToRadius=Polynom\\{parameters=\\[" + GroupOfRegexFindings.getPatternForNumberOfNumber(',',6));  //1
        pattern.add("Pixel\\{" + GroupOfRegexFindings.getPatternForNumberOfNumber(',',2)); //2
        pattern.add("width=(\\d*)"); //3
        pattern.add("height=(\\d*)"); //4
        pattern.add("pixelSize=(" + GroupOfRegexFindings.DOUBLEPATTERN + ")"); //5
        pattern.add("amplitudeFunction=Polynom\\{parameters=\\[" + GroupOfRegexFindings.getPatternForNumberOfNumber(',',4)); //6
        pattern.add("phaseFunction=Polynom\\{parameters=\\[" + GroupOfRegexFindings.getPatternForNumberOfNumber(',',4)); //7
        pattern.add("constParameter=(" + GroupOfRegexFindings.DOUBLEPATTERN + ")"); //8
        pattern.add("ModuleRMS= (" + GroupOfRegexFindings.DOUBLEPATTERN + ")");

        ArrayList<ArrayList<GroupOfRegexFindings>> arrayLists = readFromTXTFile.getAllRegexFromTxtFile(pattern);
        double width = arrayLists.get(3).get(0).getTheFirstElementAsDouble();
        double height = arrayLists.get(4).get(0).getTheFirstElementAsDouble();
        double pixelSize = arrayLists.get(5).get(0).getTheFirstElementAsDouble();
        Pixel pixel = new Pixel(arrayLists.get(2).get(0).getTheFirstElementAsDouble(), arrayLists.get(2).get(0).getElementAsDouble(1),pixelSize);

        return new GeneralCameraModuleIn(Polynom.createPolynomFromStrings(arrayLists.get(1).get(0).getStrings()),
                new Frame(pixel,width,height),
                pixelSize,
                Polynom.createPolynomFromStrings(arrayLists.get(6).get(0).getStrings()),
                Polynom.createPolynomFromStrings(arrayLists.get(7).get(0).getStrings()),
                arrayLists.get(8).get(0).getTheFirstElementAsDouble(),
                (long) arrayLists.get(0).get(0).getTheFirstElementAsDouble(),
                arrayLists.get(9).get(0).getTheFirstElementAsDouble()) ;
    }

    public void setModuleRMS(double moduleRMS) {
        this.moduleRMS = moduleRMS;
    }

    public Polynom getPolynomTetaToRadius() {
        return polynomTetaToRadius;
    }

    private Pixel getPixelFirstCorrection(Point3D point3D){

        double teta = point3D.getTetaCameraPointOfView();
        double radius = polynomTetaToRadius.value(teta);
        double psai = point3D.getPsaiCameraPointOfView();

        return Pixel.RadialPixel(radius,psai,pixelSize,frame);
    }

    public double getRadiusFirstCorrection(Point3D point3D){
        return polynomTetaToRadius.value(point3D.getTetaCameraPointOfView());
    }

    public double getRadiusFirstCorrection(double teta){
        return polynomTetaToRadius.value(teta);
    }

    private double getAngleSeccondCorrection(Point3D point3D){
        double teta = point3D.getTetaCameraPointOfView();

        double psai = point3D.getPsaiCameraPointOfView();
        double phase = phaseFunction.value(teta);
//        if (teta > 0.6)
//            teta = 0.6;
        double amplitude = amplitudeFunction.value(teta);
        return psai - (constParameter + amplitude*Math.sin(angularFrequency*psai + phase)) ;

    }

    public Pixel getPixelFromPoint3D(Point3D point3D){
        return Pixel.RadialPixel(getRadiusFirstCorrection(point3D),getAngleSeccondCorrection(point3D),pixelSize,frame);
    }

    private double getTetaFromPixel(Pixel pixel){
        double[] doubles = getPolynomTetaToRadius().getPrametersAsArray();
        doubles[0] = -pixel.getRadius(frame);
        PolynomialFunction polynomialFunction = new PolynomialFunction(doubles);
        NewtonRaphsonSolver newtonRaphsonSolver = new NewtonRaphsonSolver();
        return newtonRaphsonSolver.solve(10,polynomialFunction,0,1);
    }

    private double getPsaiFromPixel(Pixel pixel, double teta){
        if (amplitudeFunction == null)
            return pixel.getAngle(frame);
        double amplitude = amplitudeFunction.value(teta);
        double phase = phaseFunction.value(teta);
        double angle = pixel.getAngle(frame);
//        if (angle < 0)
//            angle = angle + 2*Math.PI;
        PsaiFunction psaiFunction = new PsaiFunction(amplitude,constParameter,phase,angle);

        NewtonRaphsonSolver newtonRaphsonSolver = new NewtonRaphsonSolver();


        double x = newtonRaphsonSolver.solve(10,psaiFunction,0,1);

        return Math.PI/2 - (x - phase);

    }

    public Point3D getPoint3DFromPixel(Pixel pixel){

        double teta = getTetaFromPixel(pixel);
        return Point3D.cameraSpherePointOfView(teta,getPsaiFromPixel(pixel,teta));

    }

    @Override
    public Point3D getPoint3DFromPixelOpticalZoom(Pixel pixel, double opticalZoomFactor) {
        return getPoint3DFromPixel(pixel);
    }

    @Override
    public double EstimatedError(Pixel pixel) {
        return 0;
    }

    public ArrayList<Point3D> getArrayPoint3DFromArrayPixels(ArrayList<Pixel> pixels){
        ArrayList<Point3D> point3Ds = new ArrayList<>();
        for (int i = 0; i < pixels.size(); i++) {
            point3Ds.add(getPoint3DFromPixel(pixels.get(i)));
        }
        return point3Ds;
    }

    public ArrayList<Pixel> getArrayListOfPixelsFromPoint3D(ArrayList<Point3D> point3Ds){
        ArrayList<Pixel> pixels = new ArrayList<>();
        for (int i = 0; i < point3Ds.size(); i++) {
            pixels.add(getPixelFromPoint3D(point3Ds.get(i)));
        }
        return pixels;
    }

    public double getModuleRMS() {
        return moduleRMS;
    }

    public long getCameraSN() {
        return cameraSN;
    }

    public Frame getFrame() {
        return frame;
    }

    @Override
    public String toString() {
        return "GeneralCameraModuleIn{" +
                "\ncameraSN=" + cameraSN +
                "\npolynomTetaToRadius=" + polynomTetaToRadius +
                "\nframe=" + frame +
                "\npixelSize=" + pixelSize +
                "\namplitudeFunction=" + amplitudeFunction +
                "\nphaseFunction=" + phaseFunction +
                "\nconstParameter=" + constParameter +
                "\nangularFrequency=" + angularFrequency +
                "\nModuleRMS= " + moduleRMS +
                "\n}";
    }
}
