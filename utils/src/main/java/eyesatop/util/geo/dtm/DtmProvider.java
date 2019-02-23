package eyesatop.util.geo.dtm;

import java.util.List;

import eyesatop.math.Geometry.Point3D;
import eyesatop.math.MathException;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by einav on 17/07/2017.
 */

public interface DtmProvider {

    public static class Stub implements DtmProvider {

        private final Property<Double> raiseValue = new Property<>(0D);

        @Override
        public ObservableValue<Double> dtmRaiseValue() {
            return raiseValue;
        }

        @Override
        public void raiseDTM(double value) {
            raiseValue.set(raiseValue.value() + value);
        }

        @Override
        public void lowerDTM(double value) {
            raiseValue.set(raiseValue.value() - value);
        }

        @Override
        public void clearRaiseValue() {
            raiseValue.set(0D);
        }

        @Override
        public DtmProvider duplicate() {
            return this;
        }

        @Override
        public double density() {
            return 1;
        }

        @Override
        public double terrainAltitude(Location location) throws TerrainNotFoundException {
            return 0;
        }

        @Override
        public double terrainAltitude(double lat, double lon) throws TerrainNotFoundException {
            return 0;
        }

        @Override
        public double maxTerrainAltitudeInArea(Location location, double areaSquareSide) throws TerrainNotFoundException {
            return 0;
        }

        @Override
        public List<Location> corners() {
            return null;
        }

        @Override
        public double maxSteps() {
            return 500;
        }

        @Override
        public double stepDistanceInMeters() {
            return 1;
        }
    }

    ObservableValue<Double> dtmRaiseValue();
    void raiseDTM(double value);
    void lowerDTM(double value);
    void clearRaiseValue();

    DtmProvider duplicate();
    double density();
    double terrainAltitude(Location location) throws TerrainNotFoundException;
    double terrainAltitude(double lat, double lon) throws TerrainNotFoundException;

    double maxTerrainAltitudeInArea(Location location,double areaSquareSide) throws TerrainNotFoundException;

    List<Location> corners();
    double maxSteps();
    double stepDistanceInMeters();

    public class DtmTools {
        public static Location cutWithoutDTM(Location currentLocation, GimbalState lineDirection, DtmProvider provider){
            double pitch = lineDirection.getPitch();

            double maxDistance = provider.maxSteps() * provider.stepDistanceInMeters();
            if(pitch >= 0){
                return currentLocation.getLocationFromAzAndDistance(maxDistance,lineDirection.getYaw()).altitude(0);
            }

            if(pitch == -90){
                return currentLocation.altitude(0);
            }

            double distance = currentLocation.getAltitude()/Math.tan(Math.toRadians(Math.abs(pitch)));
            return currentLocation.getLocationFromAzAndDistance(Math.min(distance,maxDistance),lineDirection.getYaw()).altitude(0);
        }

        public static Location getGroundLocationRelativeToRefPoint(Location currentLocation,Double refPointDTM,DtmProvider dtmProvider){
            try{
                if(refPointDTM == null){
                    throw new TerrainNotFoundException();
                }
                double currentLocationDTM = dtmProvider.terrainAltitude(currentLocation);
                return currentLocation.altitude(currentLocationDTM-refPointDTM);
            }
            catch (TerrainNotFoundException e){
                return currentLocation.altitude(0);
            }
        }

        public static Location cutWithDTM(Location currentLocation,GimbalState lineDirection,Double referenceDTM,DtmProvider dtmProvider) {

            if(currentLocation == null || lineDirection == null || dtmProvider == null){
                return null;
            }

            try {

                if(referenceDTM == null){
                    throw new TerrainNotFoundException();
                }

                double currentLocationAbsAltitude = currentLocation.getAltitude() + referenceDTM;
                Point3D point3D = Point3D.ElevationAzimuthPointOfView(Math.toRadians(lineDirection.getPitch()),Math.toRadians(lineDirection.getYaw()));
                eyesatop.math.Geometry.EarthGeometry.Location location = new eyesatop.math.Geometry.EarthGeometry.Location(currentLocation.getLatitude(),currentLocation.getLongitude(),currentLocationAbsAltitude);
                eyesatop.math.Geometry.EarthGeometry.Location newLocation = location;
                for (int i = 0; i < dtmProvider.maxSteps(); i++) {
                    try {
                        newLocation = newLocation.findPositionWithHorizontalDistance(dtmProvider.stepDistanceInMeters(), point3D);
                    }
                    catch (MathException e){
                        if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinityMinus){
                            return new Location(currentLocation.getLatitude(),currentLocation.getLongitude(),dtmProvider.terrainAltitude(currentLocation));
                        }
                        if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinity){
                            break;
                        }
                        throw new TerrainNotFoundException();
                    }
                    double height = dtmProvider.terrainAltitude(newLocation.latitude(),newLocation.longitude());
                    if (newLocation.Height() < height){
                        return new Location(newLocation.latitude(),newLocation.longitude(),height);
                    }
                }
                return currentLocation.getLocationFromAzAndDistance(
                        dtmProvider.stepDistanceInMeters()*dtmProvider.maxSteps(),lineDirection.getYaw()).altitude(currentLocationAbsAltitude);
            } catch (TerrainNotFoundException e) {
                return cutWithoutDTM(currentLocation,lineDirection,dtmProvider);
            }
        }

        public static double highestDTMBetweenPoints(DtmProvider provider,Location firstLocation,Location secondLocation) throws TerrainNotFoundException {

            double azToTarget = firstLocation.az(secondLocation);
            double distanceToTarget = firstLocation.distance(secondLocation);

            double highestDTMToTarget = provider.terrainAltitude(firstLocation);

            for(int i=1; i < distanceToTarget/provider.stepDistanceInMeters();i++){
                double tempDTM = provider.terrainAltitude(firstLocation.getLocationFromAzAndDistance(i*provider.stepDistanceInMeters(),azToTarget));
                highestDTMToTarget = Math.max(tempDTM,highestDTMToTarget);
            }

            return highestDTMToTarget;
        }

        public static double highestDTMAroundCircleWithRadius(DtmProvider provider,Location centerLocation,double radius) throws TerrainNotFoundException {

            double currentAz = 0;

            double highestDTM = provider.terrainAltitude(centerLocation.getLocationFromAzAndDistance(radius,currentAz));

            currentAz += Math.toDegrees(2*Math.asin(provider.stepDistanceInMeters()/(2*radius)));

            while(currentAz < 360){

                double tempDTM = provider.terrainAltitude(centerLocation.getLocationFromAzAndDistance(radius,currentAz));
                highestDTM = Math.max(tempDTM,highestDTM);

                currentAz += Math.toDegrees(2*Math.asin(provider.stepDistanceInMeters()/(2*radius)));

            }

            return highestDTM;
        }

        public static Location cutWithDTM(Location currentLocation,GimbalState lineDirection,Location referenceLocation,DtmProvider dtmProvider) {

            if(currentLocation == null || lineDirection == null || dtmProvider == null){
                return null;
            }
            if(referenceLocation == null){
                referenceLocation = currentLocation;
            }

            try {
                double currentLocationAbsAltitude = currentLocation.getAltitude() + dtmProvider.terrainAltitude(referenceLocation);
                Point3D point3D = Point3D.ElevationAzimuthPointOfView(Math.toRadians(lineDirection.getPitch()),Math.toRadians(lineDirection.getYaw()));
                eyesatop.math.Geometry.EarthGeometry.Location location = new eyesatop.math.Geometry.EarthGeometry.Location(currentLocation.getLatitude(),currentLocation.getLongitude(),currentLocationAbsAltitude);
                eyesatop.math.Geometry.EarthGeometry.Location newLocation = location;
                for (int i = 0; i < dtmProvider.maxSteps(); i++) {
                    try {
                        newLocation = newLocation.findPositionWithHorizontalDistance(dtmProvider.stepDistanceInMeters(), point3D);
                    }
                    catch (MathException e){
                        if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinityMinus){
                            return new Location(currentLocation.getLatitude(),currentLocation.getLongitude(),dtmProvider.terrainAltitude(currentLocation));
                        }
                        if (e.getMathExceptionCause() == MathException.MathExceptionCause.infinity){
                            break;
                        }
                        throw new TerrainNotFoundException();
                    }
                    double height = dtmProvider.terrainAltitude(newLocation.latitude(),newLocation.longitude());
                    if (newLocation.Height() < height){
                        return new Location(newLocation.latitude(),newLocation.longitude(),height);
                    }
                }
                return currentLocation.getLocationFromAzAndDistance(
                        dtmProvider.stepDistanceInMeters()*dtmProvider.maxSteps(),lineDirection.getYaw()).altitude(currentLocationAbsAltitude);
            } catch (TerrainNotFoundException e) {
                return cutWithoutDTM(currentLocation,lineDirection,dtmProvider);
            }
        }
    }
}
