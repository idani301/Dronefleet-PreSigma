package eyesatop.util.geomine;

import eyesatop.math.Geometry.Point2D;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.dtm.DtmObject;

/**
 * Created by Einav on 11/11/2017.
 */

public class SpatialMagneticInfo extends DtmObject{

    protected SpatialMagneticInfo(int width, int length, double densityInDegree, Location startLocation) throws Exception {
        super(width, length, densityInDegree, startLocation);
    }

    public static SpatialMagneticInfo CreateDTM(double lengthNorth, double lengthEast, DensityLevel densityLevel, Location centerDtmLocation) throws Exception {
        double density = 0.01;
        switch (densityLevel){
            case HIGH:
                density = 0.000002; //20 cm
                break;
            case MID:
                density = 0.00001; //1 m
                break;
            case LOW:
                density = 0.0001; //10 m
                break;
        }
        Point2D point2D = Point2D.GeographicPoint(lengthNorth/2,lengthEast/2);

        Location startLocation = centerDtmLocation.getLocationFromAzAndDistance(point2D.getRadius(),-Math.toDegrees(point2D.getAngle()));
        double distanceLengthNorth = -startLocation.getLatitude() + startLocation.getLocationFromAzAndDistance(lengthNorth,0).getLatitude();
        double distanceLengthEast = -startLocation.getLongitude() + startLocation.getLocationFromAzAndDistance(lengthEast,90).getLongitude();
        int lengthLat = (int) (distanceLengthNorth/density);
        int lengthLon = (int) (distanceLengthEast/density);
        return new SpatialMagneticInfo(lengthLat, lengthLon, density, startLocation);
    }

}
