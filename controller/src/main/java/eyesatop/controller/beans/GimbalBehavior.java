package eyesatop.controller.beans;

import eyesatop.controller.GimbalBehaviorType;
import eyesatop.controller.GimbalRequest;
import eyesatop.util.geo.Location;

/**
 * Created by einav on 23/03/2017.
 */
public class GimbalBehavior {

    private final GimbalBehaviorType behaviorType;
    private final Location locationLocked;
    private final double degreeFromLocation;
    private final GimbalRequest targetRequest;

    public GimbalBehavior(GimbalBehaviorType behaviorType, Location locationLocked, double degreeFromLocation, GimbalRequest targetRequest) {
        this.behaviorType = behaviorType;
        this.locationLocked = locationLocked;
        this.degreeFromLocation = degreeFromLocation;
        this.targetRequest = targetRequest;
    }

    public GimbalBehavior behaviorType(GimbalBehaviorType behaviorType){
        return new GimbalBehavior(behaviorType,locationLocked,degreeFromLocation, targetRequest);
    }

    public GimbalBehavior locationLocked(Location locationLocked){
        return new GimbalBehavior(behaviorType,locationLocked,degreeFromLocation, targetRequest);
    }

    public GimbalBehavior degreeFromLocation(double degreeFromLocation){
        return new GimbalBehavior(behaviorType,locationLocked,degreeFromLocation, targetRequest);
    }

    public boolean isYawLocked() {

        switch (behaviorType){

            case ONLY_YAW_LOCKED_AT_LOCATION:
                return true;
            case LOCKED_AT_LOCATION:
                return true;
            case LOCKED_FORWARD:
                return true;
            case DEGREES_LOCKED:
                return targetRequest.isYawEnable();
            case NOTHING:
                return false;

            default:
                return false;
        }
    }

    public boolean isPitchLocked() {

        switch (behaviorType){

            case ONLY_YAW_LOCKED_AT_LOCATION:
                return false;
            case LOCKED_AT_LOCATION:
                return true;
            case LOCKED_FORWARD:
                return true;
            case DEGREES_LOCKED:
                return targetRequest.isPitchEnable();
            case NOTHING:
                return false;
            default:
                return false;
        }
    }

    public GimbalBehaviorType getBehaviorType() {
        return behaviorType;
    }

    public Location getLocationLocked() {
        return locationLocked;
    }

    public double getDegreeFromLocation() {
        return degreeFromLocation;
    }

    public GimbalRequest getTargetRequest() {
        return targetRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GimbalBehavior that = (GimbalBehavior) o;

        if (Double.compare(that.degreeFromLocation, degreeFromLocation) != 0) return false;
        if (behaviorType != that.behaviorType) return false;
        return locationLocked != null ? locationLocked.equals(that.locationLocked) : that.locationLocked == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = behaviorType != null ? behaviorType.hashCode() : 0;
        result = 31 * result + (locationLocked != null ? locationLocked.hashCode() : 0);
        temp = Double.doubleToLongBits(degreeFromLocation);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "GimbalBehavior{" +
                "behaviorType=" + behaviorType +
                ", locationLocked=" + locationLocked +
                ", degreeFromLocation=" + degreeFromLocation +
                '}';
    }
}