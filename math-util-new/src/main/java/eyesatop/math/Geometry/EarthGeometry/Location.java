package eyesatop.math.Geometry.EarthGeometry;

import eyesatop.math.Geometry.Ellipsoid;
import eyesatop.math.Geometry.Point2D;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.MathException;

import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Einav on 20/06/2017.
 */

public class Location{

    public static final EarthGeometry EARTH_GEOMETRY = new EarthGeometry();

    private final double toRad = Math.PI/180;
    private final double toDegree = 180/Math.PI;

    private static final double EARTHRADIUS = 6371; // in KM
    private DatumName datumName = DatumName.WGS84;

    private final Point3D location;
    private Point2D utmReferenceLine = Point2D.cartesianPoint(0,33);

    private double NorthBias = 0;

    public Location(Location location){
        this.location = location.location;
        utmReferenceLine = location.utmReferenceLine;
        this.NorthBias = location.NorthBias;
    }

    public Location(Location location, DatumName datumName){
        this.location = location.location;
        utmReferenceLine = location.utmReferenceLine;
        this.NorthBias = location.NorthBias;
        this.datumName = datumName;
    }

    public Location(Point3D locationRad) {
        this.location = locationRad;
    }

    public Location(double latitude, double longitude){
        location = Point3D.cartesianPoint(latitude*toRad,longitude*toRad,0);
    }

    public Location(double latitude, double longitude, double height){
        location = Point3D.cartesianPoint(latitude*toRad,longitude*toRad,height);
    }

    public Location(Point3D location, Location utmReferenceLine) {
        this.location = location;
        this.utmReferenceLine = Point2D.cartesianPoint(utmReferenceLine.latitude(),utmReferenceLine.longitude());
        NorthBias = utmReferenceLine.NorthBias;
    }

    public Location(double latitude, double longitude, Location utmReferenceLine){
        location = Point3D.cartesianPoint(latitude*toRad,longitude*toRad,0);
        this.utmReferenceLine = Point2D.cartesianPoint(utmReferenceLine.latitude(),utmReferenceLine.longitude());
        NorthBias = utmReferenceLine.NorthBias;
    }

    public Location(double latitude, double longitude, double height, Location utmReferenceLine){
        location = Point3D.cartesianPoint(latitude*toRad,longitude*toRad,height);
        this.utmReferenceLine = Point2D.cartesianPoint(utmReferenceLine.latitude(),utmReferenceLine.longitude());
        NorthBias = utmReferenceLine.NorthBias;
    }

    public Location(Point2D utmLocation) {
        double height = 0;
        this.location = getGeoFromUtm(utmLocation,height,EARTH_GEOMETRY.getFalsePoint(datumName),datumName);
    }

    public Location(Point2D utmLocation, double height) {
        this.location = getGeoFromUtm(utmLocation,height,EARTH_GEOMETRY.getFalsePoint(datumName),datumName);
    }

//    public Location(Point2D utmLocation, double height, double bias) {
//        this.location = getGeoFromWgs84Utm(utmLocation,height,Point2D.EARTH_GEOMETRY.getFalsePoint(datumName).getX() - bias);
//    }

    public Location(Point2D utmLocation, Location utmReferenceLine) {
        double height = 0;
        this.utmReferenceLine = Point2D.cartesianPoint(utmReferenceLine.latitude(),utmReferenceLine.longitude());
        this.NorthBias = utmReferenceLine.NorthBias;
        this.location = getGeoFromUtm(utmLocation,height,Point2D.zero(),datumName);
    }

    public Location(Point2D utmLocation, double height, Location utmReferenceLine) {
        this.utmReferenceLine = Point2D.cartesianPoint(utmReferenceLine.latitude(),utmReferenceLine.longitude());
        this.location = getGeoFromUtm(utmLocation,height,Point2D.zero(),datumName);
    }

    public static Location CreateReferencedLineUTM(double latitude, double longitude){
        Location location = new Location(latitude,longitude,new Location(latitude,longitude));
        location.NorthBias = location.getXutm() + 500000;
        return new Location(latitude,longitude,location);
    }

    public static Location CreateReferencedLineUTM(Location referenceLocation){
        Location location = new Location(referenceLocation.latitude(),referenceLocation.longitude(),new Location(referenceLocation.latitude(),referenceLocation.longitude()));
        location.NorthBias = location.getXutm() + 500000;
        return new Location(referenceLocation.latitude(),referenceLocation.longitude(),location);
    }

    public void setUtmReferenceLine(Location utmReferenceLine) {
        this.utmReferenceLine = Point2D.cartesianPoint(utmReferenceLine.latitude(),utmReferenceLine.longitude());
        NorthBias = utmReferenceLine.NorthBias;
    }

    public double latitude(){
        return location.getX()*toDegree;
    }

    private double Latitude() {return location.getX();}

    public double longitude(){
        return location.getY()*toDegree;
    }

    private double Longitude() {return location.getY();}

    public double Height(){
        return location.getZ();
    }

    public double getEast(){ return getUTMLocationFromGeo(EARTH_GEOMETRY.getFalsePoint(datumName),datumName).getX(); }

    public double getNorth(){ return getUTMLocationFromGeo(EARTH_GEOMETRY.getFalsePoint(datumName),datumName).getY(); }

    public double getXutm(){ return getUTMLocationFromGeo(EARTH_GEOMETRY.getFalsePoint(datumName),datumName).getX() - EARTH_GEOMETRY.getFalsePoint(datumName).getX(); }

    public double getYutm() { return getUTMLocationFromGeo(EARTH_GEOMETRY.getFalsePoint(datumName),datumName).getY() - NorthBias; }

    public Point2D getUtmLocationNoBias(){
        Point2D point2D = getUTMLocationFromGeo(Point2D.zero(),datumName);
        return Point2D.cartesianPoint(point2D.getX(),point2D.getY());
    }

    public Point3D getLocation() {
        return Point3D.cartesianPoint(latitude(), longitude(),Height());
    }

    public Point3D getLocationInRadians(){
        return location;
    }

    public Point2D getUtmLocation() {
        return getUTMLocationFromGeo(EARTH_GEOMETRY.getFalsePoint(datumName),datumName);
    }

    public Point2D getUtmLocationAsRefPointAsZeroPoint() {
        return getUTMLocationFromGeo(Point2D.cartesianPoint(0,-getNorthBias()),datumName);
    }

    public Point3D getUtmLocationAsRefPointAsZeroPointWithHeight() {
        return Point3D.cartesianPoint(getUTMLocationFromGeo(Point2D.cartesianPoint(0,-getNorthBias()),datumName),Height());
    }

    public Location getUtmReferenceLine() {
        return new Location(utmReferenceLine.getX(),utmReferenceLine.getY());
    }


    public double getNorthBias() {
        return NorthBias;
    }

    private Point2D getUTMLocationFromGeo(Point2D bias, DatumName datumName){

        double lati = Latitude();
        double long1 = Longitude();
        double long0 = utmReferenceLine.getY()*toRad;
        double k0 = EARTH_GEOMETRY.getK0(datumName);
        double a = EARTH_GEOMETRY.getEllipsoidDatum(datumName).getA();
        double b = EARTH_GEOMETRY.getEllipsoidDatum(datumName).getB();

        double e = Math.sqrt(1-(Math.pow(b,2)/Math.pow(a,2)));
        double etag = e*a/b;
        double e2 = Math.pow(etag,2);
        double e4 = Math.pow(etag,4);
        double n = (a-b)/(a+b);
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
        double y = K1 + K2*Math.pow(p,2) + K3*Math.pow(p,4) + bias.getY(); //north

        double K4 = k0*nu*Math.cos(lati);
        double K5 = (k0*nu*Math.pow((Math.cos(lati)),3)/6)*(1 - Math.pow((Math.tan(lati)),2) + e2*Math.pow((Math.cos(lati)),2));

        double x = (K4*p + K5*Math.pow(p,3)) + bias.getX(); //east

        return Point2D.GeographicPoint(y,x);

    }


    private Point3D getGeoFromUtm(Point2D utmLocation, double height, Point2D bias, DatumName datumName){

        double x = utmLocation.getEast() - bias.getX();
        double long0 = utmReferenceLine.getY()*toRad;
        double a = EARTH_GEOMETRY.getEllipsoidDatum(datumName).getA();
        double b = EARTH_GEOMETRY.getEllipsoidDatum(datumName).getB();
        double e = Math.sqrt(1-(Math.pow(b, 2)/Math.pow(a, 2)));
        double k0 = EARTH_GEOMETRY.getK0(datumName);
        double M = (utmLocation.getNorth() - bias.getY() + NorthBias)/k0;
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

        return Point3D.cartesianPoint(lati,long1,height);
    }

    public Point3D distance(Location location){
        double hdistance = distanceHorizontal(location);
        double aDistance = location.Height() - Height();
        double azimuth = azimuth(location);

        return Point3D.pointFromHorizontalDistanceAltitudeDistanceAzimuth(hdistance,aDistance,azimuth);
    }

    private double distanceHorizontal(Location location){
        double lat1 = Latitude();
        double lon1 = Longitude();
        double lat2 = location.Latitude();
        double lon2 = location.Longitude();

        double d = Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon1 - lon2);
        return EARTHRADIUS * Math.acos(d)*1000;
    }

    public double azimuth(Location location){
        double result = 0;

        double lat1 = latitude();
        double lon1 = longitude();
        double lat2 = location.latitude();
        double lon2 = location.longitude();

        if (Math.abs(lat1 - lat2) < 1e-10)
            lat2 = lat1;
        if (Math.abs(lon1 - lon2) < 1e-10)
            lon2 = lon1;

        long ilat1 = (long)(0.50 + lat1 * 360000.0);
        long ilat2 = (long)(0.50 + lat2 * 360000.0);
        long ilon1 = (long)(0.50 + lon1 * 360000.0);
        long ilon2 = (long)(0.50 + lon2 * 360000.0);

        lat1 *= toRad;
        lon1 *= toRad;
        lat2 *= toRad;
        lon2 *= toRad;

        if ((ilat1 == ilat2) && (ilon1 == ilon2))
        {
            return result;
        }
        else if (ilon1 == ilon2)
        {
            if (ilat1 > ilat2)
                result = 180.0;
        }
        else if (ilat1 == ilat2)
        {
            if (ilon1 > ilon2)
                result = 270.0;
            else
                result = 90.0;
        }
        else
        {
            double c = acos(sin(lat2)* sin(lat1) + cos(lat2)* cos(lat1)* cos((lon2-lon1)));
            double A = asin(cos(lat2)* sin((lon2-lon1))/ sin(c));
            result = A*toDegree;
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

        return result*toRad;
    }

    public Location findPosition(double distanceInMeters, Point3D direction)
    {
        distanceInMeters /= 1000;
        double azimuth = direction.getAzimuth();
        double elevation = direction.getElevation();
        double horizontalDistance = distanceInMeters*Math.cos(elevation);
        double height = distanceInMeters*Math.sin(elevation)*1000;

        double b = horizontalDistance/EARTHRADIUS;
        double a = Math.acos(Math.cos(b)*Math.cos(90*toRad-Latitude()) + Math.sin(90*toRad-Latitude())*Math.sin(b)*Math.cos(azimuth));
        double B = Math.asin(Math.sin(b)*Math.sin(azimuth)/Math.sin(a));
        a = a*toDegree;

        return new Location(90-a,B*toDegree+longitude(),Height() + height);
    }

    public Location findPositionWithHorizontalDistance(double horizontalDistanceInMeters, Point3D direction) throws MathException {
        horizontalDistanceInMeters /= 1000;
        double azimuth = direction.getAzimuth();
        double elevation = direction.getElevation();
        if (elevation == Math.PI/2)
            throw new MathException(MathException.MathExceptionCause.infinity);
        if (elevation == -Math.PI/2)
            throw new MathException(MathException.MathExceptionCause.infinityMinus);
        double horizontalDistance = horizontalDistanceInMeters;
        double height = horizontalDistance*Math.tan(elevation)*1000;

        double b = horizontalDistance/EARTHRADIUS;
        double a = Math.acos(Math.cos(b)*Math.cos(90*toRad-Latitude()) + Math.sin(90*toRad-Latitude())*Math.sin(b)*Math.cos(azimuth));
        double B = Math.asin(Math.sin(b)*Math.sin(azimuth)/Math.sin(a));
        a = a*toDegree;

        return new Location(90-a,B*toDegree+longitude(),Height() + height);
    }


    public Location findPosition(Point3D direction, double height) throws MathException {
        double deltaHeight = height - location.getZ();
        double alpha = direction.getElevation();
        if (deltaHeight*alpha <= 0)
            throw new MathException(MathException.MathExceptionCause.zeroResult);
        if (Math.abs(alpha) == Math.PI/2)
            return new Location(latitude(),longitude(),height);
        double distance = deltaHeight/Math.tan(alpha);
        return findPosition(distance,direction.getAzimuthDegree(),height);
    }


    public Location findPosition(double horizontalDistance, double azimuthInDegree, double height){

        double azimuth = azimuthInDegree*toRad;
        horizontalDistance /= 1000;
        double b = horizontalDistance/EARTHRADIUS;
        double a = Math.acos(Math.cos(b)*Math.cos(90*toRad-Latitude()) + Math.sin(90*toRad-Latitude())*Math.sin(b)*Math.cos(azimuth));
        double B = Math.asin(Math.sin(b)*Math.sin(azimuth)/Math.sin(a));
        a = a*toDegree;
        return new Location(90-a,B*toDegree+longitude(),height);
    }

    public Location findPosition(double horizontalDistance, double azimuthInDegree){

        double azimuth = azimuthInDegree*toRad;
        horizontalDistance /= 1000;
        double b = horizontalDistance/EARTHRADIUS;
        double a = Math.acos(Math.cos(b)*Math.cos(90*toRad-Latitude()) + Math.sin(90*toRad-Latitude())*Math.sin(b)*Math.cos(azimuth));
        double B = Math.asin(Math.sin(b)*Math.sin(azimuth)/Math.sin(a));
        a = a*toDegree;
        return new Location(90-a,B*toDegree+longitude(),Height());
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude() +
                " ,longitude=" + longitude() +
                " ,Height=" + Height() +
                '}';
    }

    public static double convertDMStoDegree(double degree, double minute, double second) throws Exception {
        if (minute < 0)
            throw new Exception("minute must be positive number");
        if (second < 0)
            throw new Exception("second must be positive number");
        if (degree > 0)
            return (second/60 + minute)/60 + degree;
        return degree - (second/60 + minute)/60;
    }

    public static double convertHMStoDegree(double hour, double minute, double second) throws Exception {
        if (minute < 0)
            throw new Exception("minute must be positive number");
        if (second < 0)
            throw new Exception("second must be positive number");
        if (hour > 0)
            return ((second/60 + minute)/60 + hour)*360/24;
        return (hour - (second/60 + minute)/60)*360/24;
    }

    public Location getLocation(DatumName datumName){
        Ellipsoid ellipsoidFrom = EARTH_GEOMETRY.getEllipsoidDatum(this.datumName);
        Ellipsoid ellipsoidTo = EARTH_GEOMETRY.getEllipsoidDatum(datumName);

        double h = 0;

        double dx = ellipsoidTo.getCenter().getX() - ellipsoidFrom.getCenter().getX();
        double dy = ellipsoidTo.getCenter().getY() - ellipsoidFrom.getCenter().getY();
        double dz = ellipsoidTo.getCenter().getZ() - ellipsoidFrom.getCenter().getZ();

        double da = ellipsoidTo.getA() - ellipsoidFrom.getA();
        double df = ellipsoidTo.getFlattening() - ellipsoidFrom.getFlattening();

        double Rn = ellipsoidFrom.getA()/Math.sqrt(1- ellipsoidFrom.getFirstEccentricitySquared()*Math.pow(Math.sin(Latitude()),2));

        double Rm = ellipsoidFrom.getA()*(1- ellipsoidFrom.getFirstEccentricitySquared())/Math.pow(1 - ellipsoidFrom.getFirstEccentricitySquared()*Math.pow(Math.sin(Latitude()),2),1.5);

        double dphi1 = -dx*Math.sin(Latitude())*Math.cos(Longitude());
        double dphi2 = -dy*Math.sin(Latitude())*Math.sin(Longitude());
        double dphi3 = dz*Math.cos(Latitude());
        double dphi4 = ellipsoidFrom.getFlattening()*da*Math.sin(2*Latitude());
        double dphi5 = ellipsoidFrom.getA()*df*Math.sin(2*Latitude());
        double dphi6 = 1/Rm;
        double dlat = dphi6*(dphi1 + dphi2 + dphi3 + dphi4 + dphi5);

        double lat = Latitude() + dlat;

        double dlon1 = -dx*Math.sin(Longitude());
        double dlon2 =  dy*Math.cos(Longitude());
        double dlon3 = 1/((Rn + h)*Math.cos(Latitude()));
        double dlon = dlon3*(dlon1 + dlon2);

        double lon = Longitude() + dlon;

        double h1 = dx*Math.cos(Latitude())*Math.cos(Longitude());
        double h2 = dy*Math.cos(Latitude())*Math.sin(Longitude());
        double h3 = dz*Math.sin(Latitude());
        double h4 = - da;
        double h5 = (ellipsoidFrom.getFlattening()*da + ellipsoidFrom.getA()*df)* Math.pow(Math.sin(Latitude()),2);
        double dh = h1 + h2 + h3 + h4 + h5;

        double height = Height() + dh;

        Location location = new Location(lat*toDegree,lon*toDegree,height);
        return new Location(location,datumName);
    }

    public void setDatumName(DatumName datumName) {
        this.datumName = datumName;
    }
}
