package eyesatop.util.geo;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.math.Geometry.EarthGeometry.DatumName;
import eyesatop.math.Geometry.EarthGeometry.DatumNew;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;

import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by einav on 23/01/2017.
 */

public class Location {

    public static final int MIN_CHANGE = 1;
    public static final double UNSET_VALUE = -99999;

    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String ALTITUDE = "altitude";
    private static final String TYPE = "type";

    private final double latitude;
    private final double longitude;
    private final double altitude;
    private final LocationType type;

    @JsonIgnore
    public Location(double latitude, double longitude) {
        this(latitude, longitude, UNSET_VALUE);
    }

    @JsonIgnore
    public Location(double latitude, double longitude, double altitude) {
        this (latitude, longitude, altitude, LocationType.GEO);
    }

    @JsonCreator
    public Location(
            @JsonProperty(LATITUDE) double latitude,
            @JsonProperty(LONGITUDE) double longitude,
            @JsonProperty(ALTITUDE) double altitude,
            @JsonProperty(TYPE) LocationType type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.type = type;
    }

    @JsonProperty(LATITUDE)
    public double getLatitude() {
        return latitude;
    }

    @JsonProperty(LONGITUDE)
    public double getLongitude() {
        return longitude;
    }

    @JsonProperty(ALTITUDE)
    public double getAltitude() {
        return altitude;
    }

    @JsonProperty(TYPE)
    public LocationType getType() {
        return type;
    }

    @JsonIgnore
    public boolean isValid(){

        return !Double.isNaN(latitude) && !Double.isNaN(longitude);
    }

    @JsonIgnore
    public Location latitude(double latitude){
        return new Location(latitude,longitude,altitude, type);
    }

    @JsonIgnore
    public Location longitude(double longitude){
        return new Location(latitude,longitude,altitude, type);
    }

    @JsonIgnore
    public Location altitude(double altitude){
        return new Location(latitude,longitude,altitude, type);
    }

    @JsonIgnore
    public Point3D toEd50(){
        eyesatop.math.Geometry.EarthGeometry.Location einavLocation =
                new eyesatop.math.Geometry.EarthGeometry.Location(latitude,longitude,altitude);
        einavLocation.setDatumName(DatumName.ED50I);
        return Point3D.cartesianPoint(einavLocation.getUtmLocation(),altitude);
    }

    @JsonIgnore
    public double distance(Location other) {
        if (!LocationType.GEO.equals(type)) {
            return toGeo().distance(other);
        }

        if (!LocationType.GEO.equals(other.type)) {
            return distance(other.toGeo());
        }

        double k = Math.PI/180;
        double Rearth = 6371;

        double lat1 = getLatitude()*k;
        double lon1 = getLongitude()*k;
        double lat2 = other.getLatitude()*k;
        double lon2 = other.getLongitude()*k;

        double d = sin(lat1)* sin(lat2) + cos(lat1)* cos(lat2)* cos(lon1 - lon2);

        if(d >= 1){
            d = 1;
        }

        return Rearth * acos(d)*1000;
    }

    @JsonIgnore
    public double distance3D(Location other){
        double altDistance = Math.abs(other.altitude -altitude);
        double xyDistance = distance(other);
        return Math.sqrt(Math.pow(altDistance,2) + Math.pow(xyDistance,2));
    }

    @JsonIgnore
    public Point3D distance3DPoint(Location other){
        double hdistance = distance(other);
        double aDistance = other.altitude - altitude;
        double azimuth = az(other);
        return Point3D.pointFromHorizontalDistanceAltitudeDistanceAzimuth(hdistance,aDistance,Math.toRadians(azimuth));
    }

    @JsonIgnore
    public static double degreesCovered(double oldDegree,double newDegree) throws IllegalArgumentException{

        oldDegree = degreeBetween0To360(oldDegree);
        newDegree = degreeBetween0To360(newDegree);

        double angularDistance = angularDistance(oldDegree,newDegree);

        if(angularDistance <= 90){

            if(oldDegree <90 && newDegree > 360-90){
                oldDegree += 360;
            }
            if(newDegree < 90 && oldDegree > 360-90){
                newDegree += 360;
            }
            double sign = Math.signum(newDegree - oldDegree);
            return sign*angularDistance;
        }

        throw new IllegalArgumentException("Gap is too big to calc");
    }

    @JsonIgnore
    public static double degreeBetweenMinus180To180(double degree){
        double degreeBetween0To360 = degreeBetween0To360(degree);
        if(degreeBetween0To360 <= 180){
            return degreeBetween0To360;
        }
        else{
            return degreeBetween0To360 - 360;
        }
    }

    @JsonIgnore
    public static double shortestDegreeToAdd(double currentDegree,double targetDegree){
        currentDegree = degreeBetween0To360(currentDegree);
        targetDegree = degreeBetween0To360(targetDegree);

        double firstDistance = targetDegree - currentDegree;
        double secondDistance = targetDegree - currentDegree - 360;

        if(Math.abs(firstDistance) < Math.abs(secondDistance)){
            return firstDistance;
        }
        else{
            return secondDistance;
        }
    }

    @JsonIgnore
    public static double longestDegreeToAdd(double currentDegree,double targetDegree){
        currentDegree = degreeBetween0To360(currentDegree);
        targetDegree = degreeBetween0To360(targetDegree);

        double firstDistance = targetDegree - currentDegree;
        double secondDistance = targetDegree - currentDegree - 360;

        if(Math.abs(firstDistance) < Math.abs(secondDistance)){
            return secondDistance;
        }
        else{
            return firstDistance;
        }
    }

    @JsonIgnore
    public static double angularDistance(double degree1,double degree2){
        degree1 = degreeBetween0To360(degree1);
        degree2 = degreeBetween0To360(degree2);

        double angularDistance = Math.abs(degree1 - degree2);

        if(angularDistance > 180){
            return 360-angularDistance;
        }

        return angularDistance;
    }

    @JsonIgnore
    public static double degreeBetween180ToMinus180(double degree){
        double newDegree = degreeBetween0To360(degree);

        if(newDegree >= 180){
            return newDegree-360;
        }
        return newDegree;
    }

    @JsonIgnore
    public static double degreeBetween0To360(double degree){

        double newDegree = degree;

        while(Math.abs(newDegree) >= 360){
            newDegree -= Math.signum(newDegree)*360;
        }

        if(newDegree < 0){
            newDegree+= 360;
        }
        return newDegree;
    }

    @JsonIgnore
    public double az(Location other){


        double k = Math.PI/180;

        if (!LocationType.GEO.equals(type)) {
            return toGeo().az(other);
        }

        if (!LocationType.GEO.equals(other.type)) {
            return az(other.toGeo());
        }

        Double result = 0D;

        double lat1 = getLatitude();
        double lon1 = getLongitude();
        double lat2 = other.getLatitude();
        double lon2 = other.getLongitude();

        if (Math.abs(lat1 - lat2) < 1e-10)
            lat2 = lat1;
        if (Math.abs(lon1 - lon2) < 1e-10)
            lon2 = lon1;

        long ilat1 = (long)(0.50 + lat1 * 36000000000.0);
        long ilat2 = (long)(0.50 + lat2 * 36000000000.0);
        long ilon1 = (long)(0.50 + lon1 * 36000000000.0);
        long ilon2 = (long)(0.50 + lon2 * 36000000000.0);

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
            double var = cos(lat2)* sin((lon2-lon1))/ sin(c);
            if (var > 1)
                return 90;
            if (var < -1)
                return 270;
            double A = asin(var);
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
        return result;
    }


    @JsonIgnore
    public Location getLocationFromAzAndDistance(double RangeInMeter, double Az, double height){
        double toRad = Math.PI/180;
        double toDegree = 180/Math.PI;
        double EARTHRADIUS = 6371;

        double azimuth = Az*toRad;
        RangeInMeter /= 1000;
        double b = RangeInMeter/EARTHRADIUS;
        double v = Math.cos(b)*Math.cos(90*toRad-latitude*toRad) + Math.sin(90*toRad-latitude*toRad)*Math.sin(b)*Math.cos(azimuth);
        if (Math.abs(v) > 1) {
            v = Math.signum(v);
        }
        double a = Math.acos(v);
        v = Math.sin(b)*Math.sin(azimuth)/Math.sin(a);
        if (Math.abs(v) > 1) {
            v = Math.signum(v);
        }
        double B = Math.asin(v);
        if (a == 0){
            B = b;
        }
        a = a*toDegree;
        return new Location(90-a,B*toDegree+longitude,this.getAltitude() + height);
    }

    // Az In degree
    @JsonIgnore
    public Location getLocationFromAzAndDistance(double RangeInMeter, double Az){
        double toRad = Math.PI/180;
        double toDegree = 180/Math.PI;
        double EARTHRADIUS = 6371;

        double azimuth = Az*toRad;
        RangeInMeter /= 1000;
        double b = RangeInMeter/EARTHRADIUS;
        double v = Math.cos(b)*Math.cos(90*toRad-latitude*toRad) + Math.sin(90*toRad-latitude*toRad)*Math.sin(b)*Math.cos(azimuth);
        if (Math.abs(v) > 1) {
            v = Math.signum(v);
        }
        double a = Math.acos(v);
        v = Math.sin(b)*Math.sin(azimuth)/Math.sin(a);
        if (Math.abs(v) > 1) {
            v = Math.signum(v);
        }
        double B = Math.asin(v);
        if (a == 0){
            B = b;
        }
        a = a*toDegree;
        return new Location(90-a,B*toDegree+longitude,this.getAltitude());
    }

    // Az in Degree
    @JsonIgnore
    public Location getLocationFromAzAndDistance1(double RangeInMeter, double Az) {
        double L_Lat1, L_Lon1;
        L_Lat1 = latitude;
        L_Lon1 = longitude;
    /*
                L_Lat1 = DegToFULLNumber(Lat);
                L_Lon1 = DegToFULLNumber(Lon);
                */
        // double SRange = NMRange * 1852;
        double SRange = RangeInMeter;
        double aDatum = 6378137D;
        double F = 1 / 298.257223563f;

        double e2 = F * (2 - F);

        double et2 = e2 / (1 - e2);

        double R = Math.PI / 180;

        double sn = Math.sqrt(et2) * Math.cos(L_Lat1 * R);

        double t = Math.tan(L_Lat1 * R);

        double V = Math.sqrt(1 + (sn * sn));

        double N = aDatum / (Math.sqrt(1 - (e2 * Math.sin(L_Lat1 * R) * Math.sin(L_Lat1 * R))));

        double nn = (SRange / N) * Math.sin(Az * R);

        double mm = (SRange / N) * Math.cos(Az * R);

        double a = mm - (0.5 * nn * nn * t) - (1.5 * mm * mm * sn * sn * t) - ((nn * nn * mm / 6) * (1 + (3 * t * t + sn * sn) - (9 * sn * sn * t * t)));

        double b = (mm * mm * mm * 0.5 * sn * sn * (1 - (t * t))) - ((nn * nn * nn * nn * t / 24) * (1 + (3 * t * t) + (sn * sn) - (9 * sn * sn * t * t)));

        double C = ((nn * nn * mm * mm * t / 12) * (4 + (6 * t * t) - (13 * sn * sn) - (9 * sn * sn * t * t))) - (mm * mm * mm * mm * 0.5 * sn * sn * t);

        double D = ((nn * nn * nn * nn * mm / 120) * (1 + (30 * t * t) + (45 * t * t * t * t))) - ((nn * nn * mm * mm * mm / 30) * (2 + (15 * t * t) + (15 * t * t * t * t)));

        double dL_Lat1 = (a - b - C + D) * V * V;

        double ayy = (L_Lat1 * R + dL_Lat1) / R;

        double E = nn + (nn * mm * t) - (nn * nn * nn * t * t / 3) + ((nn * mm * mm / 3) * (1 + (3 * t * t) + (sn * sn)));

        double FF = ((nn * nn * nn * mm * t / 3) * (1 + (3 * t * t) + (sn * sn))) - ((nn * mm * mm * mm * t / 3) * (2 + (3 * t * t) + (sn * sn)));

        double G = ((nn * nn * nn * nn * nn * t * t / 15) * (1 + (3 * t * t))) + ((nn * mm * mm * mm * mm / 15) * (2 + (15 * t * t) + (15 * t * t * t * t))) - ((nn * nn * nn * mm * mm / 15) * (1 + (20 * t * t) + (30 * t * t * t * t)));

        double dL_Lon1 = (E - FF + G) / Math.cos(L_Lat1 * R);

        double axx = ((L_Lon1 * R) + dL_Lon1) / R;
    /*
    NewLon = FULLNumberToDeg(axx);
    NewLat = FULLNumberToDeg(ayy);
    */
        return new Location(ayy, axx);
    }

    @JsonIgnore
    public Location toGeo() {
        if (LocationType.GEO.equals(type)) {
            return this;
        }

        double x = longitude-500000;
        double long0 = 33*Math.PI/180;
        double a = 6378137; //(m)
        double b = 6356752.3142; //(m)
        double e = Math.sqrt(1-(Math.pow(b, 2)/Math.pow(a, 2)));
        double k0 = 0.9996;
        double M = latitude/k0;
        double et = e*a/b;
        double mu = M/(a*(1 - Math.pow(e, 2)/4 - 3*Math.pow(e, 4)/64 - 5*Math.pow(e, 6)/256));
        double e1 = (a-b)/(a+b);
        double J1 = (3*e1/2 - 27*Math.pow(e1, 3)/32);
        double J2 = (21*Math.pow(e1, 2)/16 - 55*Math.pow(e1, 4)/32);
        double J3 = (151*Math.pow(e1, 3)/96);
        double J4 = (1097*Math.pow(e1, 4)/512);
        double fp = mu + J1*Math.sin(2*mu) + J2*Math.sin(4*mu) + J3*Math.sin(6*mu) + J4*Math.sin(8*mu);


        double C1 = Math.pow(et, 2)*Math.pow(Math.cos(fp), 2);
        double T1 = Math.pow(Math.tan(fp),2);
        double R1 = a*(1-Math.pow(e,2))/Math.sqrt(Math.pow(1-Math.pow(e,2)*Math.pow(Math.sin(fp),2),3));
        double N1 = a/Math.sqrt(1-Math.pow(e,2)*Math.pow(Math.sin(fp),2));
        double D = x/(N1*k0);


        double Q1 = N1*Math.tan(fp)/R1;
        double Q2 = Math.pow(D,2)/2;
        double Q3 = (5 + 3*T1 + 10*C1 - 4*Math.pow(C1,2) -9*Math.pow(et,2))*Math.pow(D,4)/24;
        double Q4 = (61 + 90*T1 + 298*C1 +45*Math.pow(T1,2)  - 3*Math.pow(C1,2) -252*Math.pow(et,2))*Math.pow(D,6)/720;

        double lati = fp - Q1*(Q2 - Q3 + Q4);

        double Q5 = D;
        double Q6 = (1 + 2*T1 + C1)*Math.pow(D,3)/6;
        double Q7 = (5 - 2*C1 + 28*T1 - 3*Math.pow(C1,2) + 8*Math.pow(et,2) + 24*Math.pow(T1,2))*Math.pow(D,5)/120;

        double long1 = long0 + (Q5 - Q6 + Q7)/Math.cos(fp);

        double lon = long1 * 180/Math.PI;
        double lat = lati * 180/Math.PI;

        return new Location(lat,lon,altitude, LocationType.GEO);
    }

    @JsonIgnore
    public Location toUtm() {
        if (LocationType.UTM.equals(type)) {
            return this;
        }

        double lati = latitude*Math.PI/180;
        double long1 = longitude*Math.PI/180;
        double long0 = 33*Math.PI/180;
        double k0 = 0.9996;
        double a = 6378137; //(m)
        double b = 6356752.3142; //(m)

        double e = Math.sqrt(1-(Math.pow(b,2)/Math.pow(a,2)));
        double etag = e*a/b;
        double e2 = Math.pow(etag,2);
        double e4 = Math.pow(etag,4);
        double nu = a/Math.sqrt(1-Math.pow(e,2)*(Math.pow(Math.sin(lati),2)));
        double p = long1 - long0;

        double N = (1-Math.sqrt(1-Math.pow(e,2)))/(1+Math.sqrt(1-Math.pow(e,2)));

        double A1 = 1+(9.0/4.0)*Math.pow(N,2)+(225.0/64.0)*Math.pow(N,4);
        double A2 = (3.0/2.0)*(N+(15/8)*Math.pow(N,3)+(175.0/64.0)*Math.pow(N,5));
        double A3 = (15.0/16.0)*(Math.pow(N,2)+(7.0/4.0)*Math.pow(N,4));
        double A4 = (35.0/48.0)*(Math.pow(N,3)+(27.0/16.0)*Math.pow(N,5));

        double S = a*Math.pow((1-N),2)*(1+N)*(A1*lati-A2*Math.sin(2*lati)+A3*Math.sin(4*lati)-A4*Math.sin(6*lati));

        double K1 = S*k0;
        double K2 = k0*nu*Math.sin(2*lati)/4;
        double K3 = (k0*nu*Math.sin(lati)*Math.pow(Math.cos(lati),3)/24)*((5 - Math.pow((Math.tan(lati)),2) + 9*e2*Math.pow((Math.cos(lati)),2) + 4*e4*Math.pow((Math.cos(lati)),4)));
        double y = K1 + K2*Math.pow(p,2) + K3*Math.pow(p,4); //north

        double K4 = k0*nu*Math.cos(lati);
        double K5 = (k0*nu*Math.pow((Math.cos(lati)),3)/6)*(1 - Math.pow((Math.tan(lati)),2) + e2*Math.pow((Math.cos(lati)),2));

        double x = (K4*p + K5*Math.pow(p,3)) + 500000; //east

        return new Location(x,y,altitude,LocationType.UTM);
    }

    @JsonIgnore
    public Point2D getItmCoordinate() throws Exception {
        return DatumNew.getITMFromWGS84(latitude,longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Double.compare(location.latitude, latitude) != 0) return false;
        if (Double.compare(location.longitude, longitude) != 0) return false;
        if (Double.compare(location.altitude, altitude) != 0) return false;
        return type == location.type;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", type=" + type +
                '}';
    }
}
