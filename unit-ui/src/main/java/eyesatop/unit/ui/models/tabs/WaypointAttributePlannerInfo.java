//package eyesatop.unit.ui.models.tabs;
//
//import java.util.List;
//
//import eyesatop.math.Geometry.Angle;
//import eyesatop.unit.ui.models.missionexecution.MissionExecution;
//import eyesatop.unit.ui.models.missionexecution.components.AttributeData;
//import eyesatop.unit.ui.models.missionexecution.components.Waypoint;
//import eyesatop.util.Removable;
//import eyesatop.util.geo.Location;
//import eyesatop.util.geo.LocationGroup;
//import eyesatop.util.geo.RadiatorInfo;
//import eyesatop.util.geo.RadiatorPlan;
//import eyesatop.util.model.BooleanProperty;
//import eyesatop.util.model.CollectionObserver;
//import eyesatop.util.model.ObservableList;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//
///**
// * Created by Idan on 14/11/2017.
// */
//
//public class WaypointAttributePlannerInfo {
//
//    private static final String ALTITUDE = "Altitude";
//    private static final String AGL = "AGL";
//    private static final String VELOCITY = "Velocity";
//    private static final String ROTATION = "Rotation";
//    private static final String GAP = "Gap";
//
//    private static final int ALTITUDE_DEFAULT_VALUE = 50;
//    private static final int VELOCITY_DEFAULT_VALUE = 5;
//    private static final int AGL_DEFAULT_VALUE = 30;
//    private static final int GAP_DEFAULT_VALUE = 5;
//    private static final int ROTATION_DEFAULT_VALUE = 0;
//
//
//    public enum PlaningMode {
//        START_MISSION_EXECUTION("Start Mission Plan"),
//        FINISH_MISSION_EXECUTION("Finish Mission Plan"),
//        RADIATOR_PLAN("Radiator Plan");
//
//        private final String description;
//
//        PlaningMode(String description) {
//            this.description = description;
//        }
//
//        public String getDescription() {
//            return description;
//        }
//    }
//
//    private final Removable removable;
//    private final Property<PlaningMode> currnetPlanningMode = new Property<>();
//    private final BooleanProperty isMenuOpened = new BooleanProperty(false);
//    private final ObservableList<Waypoint> waypoints = new ObservableList<>();
//    private final ObservableList<AttributeData> attributes = new ObservableList<>();
//
//    private final ObservableList<RadiatorInfo> radiatorPlans = new ObservableList<>();
//    private final Property<RadiatorInfo> choosenRadiator = new Property<>();
//    private final Property<Waypoint> choosenRadiatorStartingWaypoint = new Property<>();
//
//    public ObservableList<Waypoint> getWaypoints() {
//        return waypoints;
//    }
//
//    public ObservableList<AttributeData> getAttributes() {
//        return attributes;
//    }
//
//    public BooleanProperty getIsMenuOpened() {
//        return isMenuOpened;
//    }
//
//    public Property<PlaningMode> getCurrnetPlanningMode() {
//        return currnetPlanningMode;
//    }
//
//    public boolean syncIntoMission(MissionExecution missionExecution){
//
//        ObservableList<Location> missionWaypointsList = null;
//        List<Location> pathPlannerWaypointList = (List<Location>)(List<?>)waypoints;
//
//        switch (currnetPlanningMode.value()){
//
//            case START_MISSION_EXECUTION:
//                missionWaypointsList = missionExecution.getGoToMissionWaypoints();
//
//                for(AttributeData attributeData : attributes){
//                    switch (attributeData.getAttributeName().value()){
//                        case ALTITUDE :
//                            Integer altitude = attributeData.getValue().value();
//                            missionExecution.getGotoMissionAltitude().set(altitude);
//                            break;
//                    }
//                }
//                break;
//            case FINISH_MISSION_EXECUTION:
//                missionWaypointsList = missionExecution.getEndMissionWaypoints();
//
//                for(AttributeData attributeData : attributes){
//                    switch (attributeData.getAttributeName().value()){
//                        case ALTITUDE :
//                            Integer altitude = attributeData.getValue().value();
//                            missionExecution.getEndMissionAltitude().set(altitude);
//                            break;
//                    }
//                }
//                break;
//            case RADIATOR_PLAN:
//                RadiatorInfo selectedRadiator = choosenRadiator.value();
//
//                if(selectedRadiator == null && waypoints.size() > 0){
//                    return false;
//                }
//                missionExecution.getRadiator().set(selectedRadiator);
//                missionWaypointsList = missionExecution.getRadiatorAnchorWaypoints();
//
//                for(AttributeData attributeData : attributes){
//                    switch (attributeData.getAttributeName().value()){
//                        case AGL :
//                            Integer altitude = attributeData.getValue().value();
//                            missionExecution.getRadiatorAGL().set(altitude);
//                            break;
//                        case VELOCITY :
//                            Integer velocity = attributeData.getValue().value();
//                            missionExecution.getRadiatorVelocity().set(velocity);
//                            break;
//                        case ROTATION :
//                            Integer rotation = attributeData.getValue().value();
//                            missionExecution.getRadiatorRotation().set(rotation);
//                            break;
//                        case GAP :
//                            Integer gap = attributeData.getValue().value();
//                            missionExecution.getRadiatorGap().set(gap);
//                            break;
//                    }
//                }
//                break;
//        }
//
//        missionWaypointsList.clear();
//        for(Location location : pathPlannerWaypointList){
//            missionWaypointsList.add(location);
//        }
//
//        return true;
//    }
//
//    public WaypointAttributePlannerInfo(){
//        removable = waypoints.observe(new CollectionObserver<Waypoint>(){
//            @Override
//            public void added(Waypoint value, Observation<Waypoint> observation) {
//                value.getWaypointName().set("Point " + waypoints.size());
//            }
//
//            @Override
//            public void removed(Waypoint value, Observation<Waypoint> observation) {
//                try {
//                    for (int i = 0; i < waypoints.size(); i++) {
//                        waypoints.get(i).getWaypointName().set("Point " + (i + 1));
//                    }
//                }
//                catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    private Removable radiatorRotationRemovable = Removable.STUB;
//    private Removable radiatorGapRemovable = Removable.STUB;
//
//    public void openForOperation(final MissionExecution missionExecution,PlaningMode mode){
//
//        currnetPlanningMode.set(mode);
//        isMenuOpened.set(true);
//        waypoints.clear();
//        attributes.clear();
//
//        ObservableList<Location> newWaypoints = null;
//        switch (mode){
//
//            case START_MISSION_EXECUTION:
//                newWaypoints = missionExecution.getGoToMissionWaypoints();
//                attributes.add(new AttributeData(ALTITUDE,5,missionExecution.getGotoMissionAltitude().withDefault(ALTITUDE_DEFAULT_VALUE).value(), delta));
//                break;
//            case FINISH_MISSION_EXECUTION:
//                newWaypoints = missionExecution.getEndMissionWaypoints();
//                attributes.add(new AttributeData(ALTITUDE,5,missionExecution.getEndMissionAltitude().withDefault(ALTITUDE_DEFAULT_VALUE).value(), delta));
//                break;
//            case RADIATOR_PLAN:
//
//                radiatorRotationRemovable.remove();
//                radiatorGapRemovable.remove();
//
//                newWaypoints = missionExecution.getRadiatorAnchorWaypoints();
//
//                AttributeData rotationAttribute = new AttributeData(ROTATION,1,missionExecution.getRadiatorRotation().withDefault(ROTATION_DEFAULT_VALUE).value(), delta);
//                radiatorRotationRemovable = rotationAttribute.getValue().observe(new Observer<Integer>() {
//                    @Override
//                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
//                        calcRadiatorPlans();
//                    }
//                });
//
//                AttributeData gapAttribute = new AttributeData(GAP,1,missionExecution.getRadiatorGap().withDefault(GAP_DEFAULT_VALUE).value(), delta);
//                gapAttribute.getValue().observe(new Observer<Integer>() {
//                    @Override
//                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
//                        calcRadiatorPlans();
//                    }
//                });
//
//                attributes.add(new AttributeData(AGL,1,missionExecution.getRadiatorAGL().withDefault(AGL_DEFAULT_VALUE).value(), delta));
//                attributes.add(new AttributeData(VELOCITY,1,missionExecution.getRadiatorVelocity().withDefault(VELOCITY_DEFAULT_VALUE).value(), delta));
//                attributes.add(rotationAttribute);
//                attributes.add(gapAttribute);
//                break;
//        }
//
//        if(newWaypoints != null){
//            for(Location newWaypoint : newWaypoints){
//                waypoints.add(new Waypoint(newWaypoint));
//            }
//        }
//
//        if(mode == PlaningMode.RADIATOR_PLAN){
//            calcRadiatorPlans();
//            RadiatorInfo missionRadiator = missionExecution.getRadiator().value();
//            choosenRadiator.set(missionRadiator);
//        }
//    }
//
//    public void calcRadiatorPlans(){
//
//        radiatorPlans.clear();
//        choosenRadiator.set(null);
//
//        if(waypoints.size() <3){
//            choosenRadiatorStartingWaypoint.set(null);
//            return;
//        }
//
//        Integer rotation = null;
//        Integer gap = null;
//
//        for(AttributeData attributeData : attributes){
//            switch (attributeData.getAttributeName().value()){
//
//                case ROTATION :
//                    rotation = attributeData.getValue().value();
//                    break;
//                case GAP :
//                    gap = attributeData.getValue().value();
//                    break;
//            }
//        }
//
//        try {
//            List<RadiatorInfo> radiatorInfos = RadiatorPlan.CreateRadiatorFromPolygon(
//                    new LocationGroup((List<Location>)(List<?>)waypoints),
//                    gap == null ? GAP_DEFAULT_VALUE : gap,
//                    5,
//                    5,
//                    Angle.angleDegree(rotation == null ? ROTATION_DEFAULT_VALUE : rotation));
//
//            Waypoint startingWaypoint = choosenRadiatorStartingWaypoint.value();
//
//            for(RadiatorInfo radiatorInfo : radiatorInfos){
//                radiatorPlans.add(radiatorInfo);
//                if(startingWaypoint != null){
//                    Location startingLocation = radiatorInfo.getRadiatorWayPoints().getLocations().get(0);
//                    if(startingLocation.distance(startingWaypoint) < 3){
//                        choosenRadiator.set(radiatorInfo);
//                    }
//                }
//            }
//
//            if(choosenRadiator.value() == null){
//                choosenRadiatorStartingWaypoint.set(null);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public ObservableList<RadiatorInfo> getRadiatorPlans() {
//        return radiatorPlans;
//    }
//
//    public Property<RadiatorInfo> getChoosenRadiator() {
//        return choosenRadiator;
//    }
//
//    public Property<Waypoint> getChoosenRadiatorStartingWaypoint() {
//        return choosenRadiatorStartingWaypoint;
//    }
//
//    public void unbind(){
//        removable.remove();
//    }
//}
