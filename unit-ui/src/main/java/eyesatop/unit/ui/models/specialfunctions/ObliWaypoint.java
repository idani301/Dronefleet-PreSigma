package eyesatop.unit.ui.models.specialfunctions;

/**
 * Created by einav on 23/05/2017.
 */

public class ObliWaypoint {
    private double latitude;
    private double longitude;
    private final double altitude;
    private final double heading;
    private double gimbalRotation;

    public void adjustWaypoint(double adjustLatitude,double adjustLongitude){
        latitude += adjustLatitude;
        longitude += adjustLongitude;
    }

    public ObliWaypoint(double latitude, double longitude, double altitude, double heading) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.heading = heading;
    }

    public void setGimbalRotation(double gimbalRotation) {
        this.gimbalRotation = gimbalRotation;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getHeading() {
        return heading;
    }

    public double getGimbalRotation() {
        return gimbalRotation;
    }
}
