package eyesatop.math.Geometry.EarthGeometry;

/**
 * Created by Einav on 31/10/2017.
 */

public class UtmType {

    public static UtmType ITM = new UtmType(0.55386965463774187, 0.61443473225468932968779302834737, 1.0000067, 219529.584 + 72.68952200683998, 2885516.9488 - 47.586694441735744);
    public static UtmType ICS = new UtmType(0.55386447682762762, 0.6145667421719, 1.00000, 170251.555, 2385259.0);

    private final double latitudeCenter;
    private final double longitudeCenter;
    private final double k0;
    private final double falseEasting;
    private final double falseNorthing;

    public UtmType(double latitudeCenter, double longitudeCenter, double k0, double falseEasting, double falseNorthing) {
        this.latitudeCenter = latitudeCenter;
        this.longitudeCenter = longitudeCenter;
        this.k0 = k0;
        this.falseEasting = falseEasting;
        this.falseNorthing = falseNorthing;
    }

    public static UtmType CustomWGS84Utm(double latitudeCenter, double longitudeCenter, double falseEasting, double falseNorthing){
        return new UtmType(latitudeCenter,longitudeCenter,0.9996,falseEasting,falseNorthing);
    }

    public double getLatitudeCenter() {
        return latitudeCenter;
    }

    public double getLongitudeCenter() {
        return longitudeCenter;
    }

    public double getK0() {
        return k0;
    }

    public double getFalseEasting() {
        return falseEasting;
    }

    public double getFalseNorthing() {
        return falseNorthing;
    }
}
