package eyesatop.math.Geometry.EarthGeometry;

import eyesatop.math.Geometry.Point2D;

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by Einav on 31/10/2017.
 */

public class DatumNew {

    public static DatumNew CLARK80 = new DatumNew(6378300.789, 6356566.4116309, 0.003407549767264, 0.006803488139112318, 0.08248325975076590, -235, -85, 264);
    public static DatumNew WGS84 = new DatumNew(6378137.0, 6356752.3142, 0.00335281066474748, 0.006694380004260807, 0.0818191909289062, 0, 0, 0);
    public static DatumNew GRS80 = new DatumNew(6378137.0, 6356752.3141, 0.0033528106811823, 0.00669438002290272, 0.0818191910428276, -48, 55, 52);

    private final double a;
    private final double b;
    private final double f;
    private final double esq;
    private final double e;
    private final double dX;
    private final double dY;
    private final double dZ;

    public DatumNew(double a, double b, double f, double esq, double e, double dX, double dY, double dZ) {
        this.a = a;
        this.b = b;
        this.f = f;
        this.esq = esq;
        this.e = e;
        this.dX = dX;
        this.dY = dY;
        this.dZ = dZ;
    }

    public static Point2D getITMFromWGS84(double latitude, double longitude) throws Exception {
        if (latitude < 20 || latitude > 40 || longitude < 20 || longitude > 40)
            throw new Exception("The coordinates can't be used with ITM");
        Point2D point2D = GRS80.getGeoFromWGS84(latitude,longitude);
        return GRS80.getUtm(point2D.getNorth(),point2D.getEast(), UtmType.ITM);
    }

    public Point2D getGeoFromWGS84(double latitude, double longitude){

        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);

        double dX = WGS84.dX - this.dX;
        double dY = WGS84.dY - this.dY;
        double dZ = WGS84.dZ - this.dZ;

        double slat = sin(latitude);
        double clat = cos(latitude);
        double slon = sin(longitude);
        double clon = cos(longitude);
        double ssqlat = slat*slat;

        double from_f = WGS84.f;
        double df = this.f - from_f;
        double from_a = WGS84.a;
        double da = this.a - from_a;
        double from_esq = WGS84.esq;
        double adb = 1.0 / (1.0 - from_f);
        double rn = from_a / sqrt(1 - from_esq * ssqlat);
        double rm = from_a * (1 - from_esq) / pow((1 - from_esq * ssqlat),1.5);
        double from_h = 0.0; // we're flat!

        double dlat = (-dX*slat*clon - dY*slat*slon + dZ*clat
                + da*rn*from_esq*slat*clat/from_a +
                + df*(rm*adb + rn/adb)*slat*clat) / (rm+from_h);

        double newLatitude = latitude + dlat;

        double dlon = (-dX*slon + dY*clon) / ((rn+from_h)*clat);
        double newLongitude = longitude + dlon;

        return Point2D.GeographicPoint(Math.toDegrees(newLatitude),Math.toDegrees(newLongitude));
    }

    public Point2D getUtm(double latitude, double longitude, UtmType utmType){

        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);

        double slat1 = sin(latitude);
        double clat1 = cos(latitude);
        double clat1sq = clat1 * clat1;
        double tanlat1sq = slat1 * slat1 / clat1sq;
        double e2 = e * e;
        double e4 = e2 * e2;
        double e6 = e4 * e2;
        double eg = (e * a / b);
        double eg2 = eg * eg;

        double l1 = 1 - e2 / 4 - 3 * e4 / 64 - 5 * e6 / 256;
        double l2 = 3 * e2 / 8 + 3 * e4 / 32 + 45 * e6 / 1024;
        double l3 = 15 * e4 / 256 + 45 * e6 / 1024;
        double l4 = 35 * e6 / 3072;
        double M = a * (l1 * latitude - l2 * sin(2 * latitude) + l3 * sin(4 * latitude) - l4 * sin(6 * latitude));

        double nu = a / sqrt(1 - (e * slat1) * (e * slat1));
        double p = longitude - utmType.getLongitudeCenter();
        double k0 = utmType.getK0();

        double K1 = M * k0;
        double K2 = k0 * nu * slat1 * clat1 / 2;
        double K3 = (k0 * nu * slat1 * clat1 * clat1sq / 24) * (5 - tanlat1sq + 9 * eg2 * clat1sq + 4 * eg2 * eg2 * clat1sq * clat1sq);
        // ING north
        double Y = K1 + K2 * p * p + K3 * p * p * p * p - utmType.getFalseNorthing();

        // x = easting = K4p + K5p3, where
        double K4 = k0 * nu * clat1;
        double K5 = (k0 * nu * clat1 * clat1sq / 6) * (1 - tanlat1sq + eg2 * clat1 * clat1);
        // ING east
        double X = K4 * p + K5 * p * p * p + utmType.getFalseEasting();

        return Point2D.GeographicPoint(Y,X);
    }

    public enum DtmName{
        ICS,
        ITM,
    }


}
