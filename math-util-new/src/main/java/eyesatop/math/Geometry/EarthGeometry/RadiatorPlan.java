package eyesatop.math.Geometry.EarthGeometry;

import java.util.ArrayList;

/**
 * Created by Einav on 08/08/2017.
 */

public class RadiatorPlan {

    private final ArrayList<Location> locations = new ArrayList<>();

    private final Location centerLocations;
    private final double lengthFlightInMeters;
    private final double widthFlightInMeters;
    private final double gapInMeters;
    private final double heightInMeters;
    private double distanceCovered = 0;
    private final int size;

    public RadiatorPlan(Location centerLocations, double lengthFlightInMeters, double widthFlightInMeters, double gapInMeters, double heightInMeters, double azimuthInDegree) {
        this.centerLocations = centerLocations;
        this.lengthFlightInMeters = lengthFlightInMeters;
        this.widthFlightInMeters = widthFlightInMeters;
        this.gapInMeters = gapInMeters;
        this.heightInMeters = heightInMeters;

        locations.add(centerLocations.findPosition(Math.sqrt(Math.pow(lengthFlightInMeters,2) + Math.pow(widthFlightInMeters,2))/2,
                Math.toDegrees(Math.atan(-widthFlightInMeters / lengthFlightInMeters)) + azimuthInDegree, heightInMeters));
        int numberOfRotations = (int) (widthFlightInMeters / gapInMeters);
        int index = 1;

        for (int i = 0; i < numberOfRotations/2; i++) {
            locations.add(locations.get(index-1).findPosition(lengthFlightInMeters,180 + azimuthInDegree, heightInMeters));
            distanceCovered += locations.get(index-1).distance(locations.get(index)).get2dRadius();
            index++;
            locations.add(locations.get(index-1).findPosition(gapInMeters,90 + azimuthInDegree, heightInMeters));
            distanceCovered += locations.get(index-1).distance(locations.get(index)).get2dRadius();
            index++;
            locations.add(locations.get(index-1).findPosition(lengthFlightInMeters,0 + azimuthInDegree, heightInMeters));
            distanceCovered += locations.get(index-1).distance(locations.get(index)).get2dRadius();
            index++;
            locations.add(locations.get(index-1).findPosition(gapInMeters,90 + azimuthInDegree, heightInMeters));
            distanceCovered += locations.get(index-1).distance(locations.get(index)).get2dRadius();
            index++;
        }
        locations.add(locations.get(index-1).findPosition(lengthFlightInMeters,180 + azimuthInDegree, heightInMeters));
        distanceCovered += locations.get(index-1).distance(locations.get(index)).get2dRadius();
        index++;
        locations.add(locations.get(index-1).findPosition(gapInMeters,90 + azimuthInDegree, heightInMeters));
        distanceCovered += locations.get(index-1).distance(locations.get(index)).get2dRadius();
        index++;
        locations.add(locations.get(index-1).findPosition(lengthFlightInMeters,0 + azimuthInDegree, heightInMeters));
        distanceCovered += locations.get(index-1).distance(locations.get(index)).get2dRadius();
        index++;

        size = index;
    }

    public double getDistanceCovered() {
        return distanceCovered;
    }

    public int getSize() {
        return size;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public Location getCenterLocationsFromArray(){
        double x = 0;
        double y = 0;
        for (int i = 0; i < locations.size(); i++) {
            x += locations.get(i).getLocation().getX();
            y += locations.get(i).getLocation().getY();
        }

        return new Location(x/locations.size(),y/locations.size(), heightInMeters);
    }

    public Location getCenterLocations() {
        return centerLocations;
    }

    public int size() {
        return size;
    }

    public double getLengthFlightInMeters() {
        return lengthFlightInMeters;
    }

    public double getWidthFlightInMeters() {
        return widthFlightInMeters;
    }

    public double getGapInMeters() {
        return gapInMeters;
    }

    public double getHeightInMeters() {
        return heightInMeters;
    }

    public double getRadiatorLength(){
        double distance = 0;
        for (int i = 0; i < locations.size()-1; i++) {
            distance += locations.get(i).distance(locations.get(i+1)).get2dRadius();
        }
        return distance;
    }

    public int getNumberOfLags(){
        return (locations.size() -2)/2;
    }

    @Override
    public String toString() {
        String s;
        s = "Radiator Plan= {";
        for (int i = 0; i < locations.size(); i++) {
            s += "\n";
            s += locations.get(i).toString();
        }
        s += "}";
        return s;
    }
}
