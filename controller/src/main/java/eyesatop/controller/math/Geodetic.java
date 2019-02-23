package eyesatop.controller.math;

import eyesatop.util.geo.Location;
import static java.lang.Math.*;

/**
 * Created by einav on 28/01/2017.
 */
public class Geodetic {

    public static final double k = Math.PI/180;
    public static final double Rearth = 6371;

    public double GeoDistance(Location p1, Location p2)
    {
        double lat1 = p1.getLatitude()*k;
        double lon1 = p1.getLongitude()*k;
        double lat2 = p2.getLatitude()*k;
        double lon2 = p2.getLongitude()*k;

        double d = sin(lat1)* sin(lat2) + cos(lat1)* cos(lat2)* cos(lon1 - lon2);
        return Rearth * acos(d)*1000;
    }


    public double CalcAzimuth(Location p1, Location p2)
    {
        double result = 0;

        double lat1 = p1.getLatitude();
        double lon1 = p1.getLongitude();
        double lat2 = p2.getLatitude();
        double lon2 = p2.getLongitude();

        long ilat1 = (long)(0.50 + lat1 * 360000.0);
        long ilat2 = (long)(0.50 + lat2 * 360000.0);
        long ilon1 = (long)(0.50 + lon1 * 360000.0);
        long ilon2 = (long)(0.50 + lon2 * 360000.0);

        lat1 *= k;
        lon1 *= k;
        lat2 *= k;
        lon2 *= k;

        if ((ilat1 == ilat2) && (ilon1 == ilon2))
        {
            //if(result<0)
            //result +=360;
            return result;
        }
        else if (ilon1 == ilon2)
        {
            if (ilat1 > ilat2)
                result = 180.0;
        }
        else
        {
            double c = acos(sin(lat2)* sin(lat1) + cos(lat2)* cos(lat1)* cos((lon2-lon1)));
            double A = asin(cos(lat2)* sin((lon2-lon1))/ sin(c));
            result = A/k;
            if ((ilat2 > ilat1) && (ilon2 > ilon1))
            {
            }
            else if ((ilat2 < ilat1) && (ilon2 < ilon1))
            {
                result = 180.0 - result;
            }
            else if ((ilat2 < ilat1) && (ilon2 > ilon1))
            {
                result = 180.0 - result;
            }
            else if ((ilat2 > ilat1) && (ilon2 < ilon1))
            {
                result += 360.0;
            }

        }
        //if(result<0)
        //result +=360;
        return result;
    }

    public Location findPosition(Location p, double distance, double azimuth)
    {
        distance = distance/1000;
        azimuth = azimuth*k;
        double b = distance/Rearth;
        double a = acos(cos(b)* cos(90*k-p.getLatitude()*k) + sin(90*k-p.getLatitude()*k)* sin(b)* cos(azimuth));
        double B = asin(sin(b)* sin(azimuth)/ sin(a));
        a = a/k;
        return new Location(90-a,B/k+p.getLongitude(),p.getAltitude());
    }
}
