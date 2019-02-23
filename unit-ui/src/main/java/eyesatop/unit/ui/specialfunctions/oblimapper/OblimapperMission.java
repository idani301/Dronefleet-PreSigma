package eyesatop.unit.ui.specialfunctions.oblimapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 08/10/2017.
 */

public class OblimapperMission {

    private final Property<Location> center = new Property<>();
    private final List<ObliCircle> circles;

    public OblimapperMission(List<ObliCircle> circles) {
        this.circles = circles;
    }

    public Property<Location> getCenter() {
        return center;
    }

    public List<ObliCircle> getCircles() {
        return circles;
    }

    public void clear(){
        center.set(null);
        circles.clear();
    }


    enum ObliFieldType{
        STRING,
        BOOLEAN,
        NUMBER;
    }

    enum ObliHeaderType {
        LATITUDE("latitude", ObliFieldType.NUMBER),
        LONGITUDE("longitude", ObliFieldType.NUMBER),
        ALTITUDE("altitude", ObliFieldType.NUMBER),
        HEADING("heading", ObliFieldType.NUMBER),
        MAX_REACH_TIME("maxReachTime", ObliFieldType.NUMBER),
        SPEED("speed", ObliFieldType.NUMBER),
        STAY_TIME("stayTime", ObliFieldType.NUMBER),
        TURN_MODE("turnMode", ObliFieldType.STRING),
        ACTIONS("actions", ObliFieldType.STRING),
        DAMPING_DISTANCE("dampingDistance", ObliFieldType.NUMBER),
        OPERATION("Operation", ObliFieldType.STRING),
        OPERATION_PARAMETERS("operationParameters", ObliFieldType.STRING),
        RESTART_VIDEO("restartVideo", ObliFieldType.BOOLEAN),
        FOCUS_POINT("focusPoint", ObliFieldType.NUMBER),
        REPEAT_COUNT("repeatCount", ObliFieldType.NUMBER),
        MOVING_MODE("movingMode", ObliFieldType.NUMBER),
        PATH_MODE("pathMode", ObliFieldType.NUMBER),
        FINISH_ACTION("finishAction", ObliFieldType.NUMBER),
        FLYING_SPEED("flyingSpeed", ObliFieldType.NUMBER),
        REMOTE_CONTROL_SPEED("remoteControlSpeed", ObliFieldType.NUMBER),
        OVERLAY("overlay", ObliFieldType.STRING),
        START_ACTION("startAction", ObliFieldType.STRING),
        MISSION_TYPE("missionType", ObliFieldType.STRING),
        PHOTO_COUNT("PhotoCount", ObliFieldType.NUMBER),
        PHOTO_TIME("PhotoTime", ObliFieldType.NUMBER),
        GIMBAL_PITCH("GimbalPitch", ObliFieldType.NUMBER),
        ALTITUDE_PRIORITY("AltitudePriority", ObliFieldType.BOOLEAN),
        REFERENCE_LATITUDE("referenceLatitude", ObliFieldType.NUMBER),
        REFERENCE_LONGITUDE("referenceLongitude", ObliFieldType.NUMBER),
        REFERENCE_ALTITUDE("referenceAltitude", ObliFieldType.NUMBER),
        USE_AGL("UseAGL", ObliFieldType.BOOLEAN);

        private final String nameInXML;
        private final ObliFieldType fieldType;

        ObliHeaderType(String nameInXML, ObliFieldType fieldType) {
            this.nameInXML = nameInXML;
            this.fieldType = fieldType;
        }

        public String getNameInXML() {
            return nameInXML;
        }

        public ObliFieldType getFieldType() {
            return fieldType;
        }
    }


    private static ObliHeaderType getFromString(String input){
        for(ObliHeaderType headerType : ObliHeaderType.values()){

            if(input.matches(headerType.getNameInXML())){
                return headerType;
            }
        }
        return null;
    }


    private static class ObliWaypoint {
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


    public static boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static OblimapperMission getFromFile(File file) throws IOException {

        BufferedReader br = null;

        HashMap<ObliHeaderType, Integer> headersMap = null;
        ArrayList<ObliWaypoint> waypoints = new ArrayList<>();
        Location middleLocation = null;

        String line;
        br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        while ((line = br.readLine()) != null) {
            boolean isHeaderLine = true;

            String[] lineApart = line.split(",");
            for (String item : lineApart) {
                if (item != null && !item.equals("") && isNumeric(item)) {
                    isHeaderLine = false;
                    break;
                }
            }

            if (isHeaderLine){

                headersMap = new HashMap<>();

                for (int i = 0; i < lineApart.length; i++) {
                    ObliHeaderType tempHeader = getFromString(lineApart[i]);
                    if (tempHeader != null) {
                        headersMap.put(tempHeader, i);
                    }
                }
            } else {
                if (headersMap != null && headersMap.containsKey(ObliHeaderType.LATITUDE) && headersMap.containsKey(ObliHeaderType.LONGITUDE)) {
                    double latitude = Double.parseDouble(lineApart[headersMap.get(ObliHeaderType.LATITUDE)]);
                    double longitude = Double.parseDouble(lineApart[headersMap.get(ObliHeaderType.LONGITUDE)]);
                    double altitude = Double.parseDouble(lineApart[headersMap.get(ObliHeaderType.ALTITUDE)]);
                    double heading = Double.parseDouble(lineApart[headersMap.get(ObliHeaderType.HEADING)]);
                    waypoints.add(new ObliWaypoint(latitude, longitude, altitude, heading));
                } else if (headersMap != null && headersMap.containsKey(ObliHeaderType.REFERENCE_LATITUDE) && headersMap.containsKey(ObliHeaderType.REFERENCE_LONGITUDE)) {

                    double latitudeSum = 0;
                    double longitudeSum = 0;

                    for (ObliWaypoint waypoint : waypoints) {
                        latitudeSum += waypoint.getLatitude();
                        longitudeSum += waypoint.getLongitude();
                    }

                    double latAverage = latitudeSum / waypoints.size();
                    double lonAverage = longitudeSum / waypoints.size();
                    middleLocation = new Location(latAverage, lonAverage);

                    double gimbalRotation = Double.parseDouble(lineApart[headersMap.get(ObliHeaderType.GIMBAL_PITCH)]);

                    for (ObliWaypoint waypoint : waypoints) {
                        waypoint.setGimbalRotation(gimbalRotation);
                    }
                }
            }
        }

        ArrayList<ObliCircle> obliCircles = new ArrayList<>();

        if(waypoints.size() > 0 && middleLocation != null){

            double counter = 0;
            double radiusSum = 0;
            double altitudeSum = 0;
            double gimbalSum = 0;
            int isLookAtMidSum =0;

            ObliWaypoint lastWaypoint = waypoints.get(0);
            Location lastWaypointLocation = new Location(lastWaypoint.getLatitude(),lastWaypoint.getLongitude());
            double lastRadius = lastWaypointLocation.distance(middleLocation);

            double lastAzToMid = lastWaypointLocation.az(middleLocation);
            double lastAzFromMid = middleLocation.az(lastWaypointLocation);
            boolean lastIsLookToMid = Location.angularDistance(lastAzToMid,lastWaypoint.getHeading()) < Location.angularDistance(lastAzFromMid ,lastWaypoint.getHeading());

            isLookAtMidSum += lastIsLookToMid ? 1 : -1;
            gimbalSum += lastWaypoint.getGimbalRotation();
            radiusSum += lastRadius;
            altitudeSum += lastWaypoint.getAltitude();
            counter+=1;

            for(int i=1; i<waypoints.size();i++){
                ObliWaypoint currentWaypoint = waypoints.get(i);
                Location currentLocation = new Location(currentWaypoint.getLatitude(),currentWaypoint.getLongitude());
                double currnetRadius = currentLocation.distance(middleLocation);

                double azToMid = currentLocation.az(middleLocation);
                double azFromMid = middleLocation.az(currentLocation);
                boolean isLookToMid = Location.angularDistance(azToMid,currentWaypoint.getHeading()) < Location.angularDistance(azFromMid ,currentWaypoint.getHeading());

                if(currnetRadius <=5){
                    currnetRadius = 5;
                    isLookToMid = false;
                }

                if(lastRadius != 5 && currnetRadius == 5 || lastRadius == 5 && currnetRadius != 5){
                    // New Circle
                    double averageRadius = radiusSum/counter;
                    double averageAltitude = altitudeSum/counter;
                    boolean averageIsLookAtMid = isLookAtMidSum > 0;
                    double averageGimbal = -Math.abs(gimbalSum)/counter;

                    obliCircles.add(new ObliCircle(averageAltitude,averageRadius,averageIsLookAtMid,averageGimbal));

                    isLookAtMidSum = 0;
                    gimbalSum = 0;
                    counter = 0;
                    radiusSum = 0;
                    altitudeSum = 0;
                }
                else if(currnetRadius != 5 && (Location.angularDistance(azFromMid,lastAzFromMid) < 5)){
                    // New Circle
                    double averageRadius = radiusSum/counter;
                    double averageAltitude = altitudeSum/counter;
                    boolean averageIsLookAtMid = isLookAtMidSum > 0;
                    double averageGimbal = -Math.abs(gimbalSum)/counter;

                    obliCircles.add(new ObliCircle(averageAltitude,averageRadius,averageIsLookAtMid,averageGimbal));

                    isLookAtMidSum = 0;
                    gimbalSum = 0;
                    counter = 0;
                    radiusSum = 0;
                    altitudeSum = 0;
                }

                counter += 1;
                isLookAtMidSum += isLookToMid ? 1 : -1;
                gimbalSum += currentWaypoint.getGimbalRotation();
                altitudeSum += currentWaypoint.getAltitude();
                radiusSum += currnetRadius;

                lastAzFromMid = azFromMid;
                lastRadius = currnetRadius;
            }

            // New Circle
            double averageRadius = radiusSum/counter;
            double averageAltitude = altitudeSum/counter;
            boolean averageIsLookAtMid = isLookAtMidSum > 0;
            double averageGimbal = -Math.abs(gimbalSum)/counter;

            obliCircles.add(new ObliCircle(averageAltitude,averageRadius,averageIsLookAtMid,averageGimbal));

        }

        return new OblimapperMission(obliCircles);
    }

}
