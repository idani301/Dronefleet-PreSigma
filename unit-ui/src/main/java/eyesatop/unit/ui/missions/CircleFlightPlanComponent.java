//package eyesatop.unit.ui.missions;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import eyesatop.controller.GimbalRequest;
//import eyesatop.controller.beans.RotationType;
//import eyesatop.controller.mission.MissionRow;
//import eyesatop.controller.mission.MissionTaskInfo;
//import eyesatop.controller.tasks.TaskCategory;
//import eyesatop.controller.tasks.exceptions.DroneTaskException;
//import eyesatop.controller.tasks.flight.FlyInCircle;
//import eyesatop.controller.tasks.flight.FlyToUsingDTM;
//import eyesatop.controller.tasks.gimbal.GimbalTaskType;
//import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
//import eyesatop.controller.tasks.gimbal.RotateGimbal;
//import eyesatop.controller.tasks.stabs.StubDroneTask;
//import eyesatop.unit.ui.models.map.MapCircle;
//import eyesatop.unit.ui.models.map.MapViewModel;
//import eyesatop.util.Removable;
//import eyesatop.util.RemovableCollection;
//import eyesatop.util.geo.GimbalState;
//import eyesatop.util.geo.Location;
//import eyesatop.util.model.BooleanProperty;
//import eyesatop.util.model.ObservableValue;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//
///**
// * Created by Idan on 05/12/2017.
// */
//
//public class CircleFlightPlanComponent implements FlightPlanComponent {
//
//    private final Property<String> name = new Property<>();
//
//    private final Property<CircleFlightPlanSnapshot> snapshot = new Property<>();
//
//    private final Property<Double> radiusReached = new Property<>();
//    private final Property<Location> centerLocation = new Property<>();
//    private final BooleanProperty visibility = new BooleanProperty(true);
//
//    private final Property<Double> agl = new Property<>();
//    private final Property<Double> velocity = new Property<>();
//
//    private final Property<RotationType> rotationType = new Property<>();
//
//    private final Property<Double> gotoCircleAGL = new Property<>();
//
//    private final Property<Double> gimbalPitch = new Property<>();
//    private final BooleanProperty lookToMid = new BooleanProperty();
//
//    private final Property<Location> lastLocationBeforeCircle = new Property<>();
//
//    private final Property<Location> startLocation = new Property<>();
//    private final Property<Location> endLocation = new Property<>();
//
//    public Removable justBindForTest(){
//
//        ArrayList<Removable> removableList = new ArrayList<>();
//
//        removableList.add(
//                centerLocation.observe(new Observer<Location>() {
//                    @Override
//                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                        startLocation.set(calcStartLocation(lastLocationBeforeCircle.value(),centerLocation.value(),radiusReached.value()));
//                        endLocation.set(calcStartLocation(lastLocationBeforeCircle.value(),centerLocation.value(),radiusReached.value()));
//                    }
//                })
//        );
//
//        removableList.add(
//                lastLocationBeforeCircle.observe(new Observer<Location>() {
//                    @Override
//                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                        startLocation.set(calcStartLocation(lastLocationBeforeCircle.value(),centerLocation.value(),radiusReached.value()));
//                        endLocation.set(calcStartLocation(lastLocationBeforeCircle.value(),centerLocation.value(),radiusReached.value()));
//                    }
//                })
//        );
//
//        removableList.add(
//                radiusReached.observe(new Observer<Double>() {
//                    @Override
//                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
//                        startLocation.set(calcStartLocation(lastLocationBeforeCircle.value(),centerLocation.value(),radiusReached.value()));
//                        endLocation.set(calcStartLocation(lastLocationBeforeCircle.value(),centerLocation.value(),radiusReached.value()));
//                        endLocation.set(calcStartLocation(lastLocationBeforeCircle.value(),centerLocation.value(),radiusReached.value()));
//                    }
//                }).observeCurrentValue()
//        );
//
//        return new RemovableCollection(removableList);
//    }
//
//    @Override
//    public ObservableValue<String> componentName() {
//        return name;
//    }
//
//    @Override
//    public Removable addToMap(final MapViewModel mapViewModel) {
//
//        ArrayList<Removable> removableList = new ArrayList<>();
//
//        final MapCircle mapCircle = new MapCircle();
//        removableList.add(mapCircle.center().bind(centerLocation));
//        removableList.add(mapCircle.radiusReached().bind(radiusReached));
//        removableList.add(mapCircle.visible().bind(visibility));
//
//        mapViewModel.addMapItem(mapCircle);
//        removableList.add(new Removable() {
//            @Override
//            public void remove() {
//                mapViewModel.removeMapItem(mapCircle);
//            }
//        });
//        return new RemovableCollection(removableList);
//    }
//
//    @Override
//    public BooleanProperty visibleInMap() {
//        return visibility;
//    }
//
//    @Override
//    public Property<Location> center() {
//        return centerLocation;
//    }
//
//    @Override
//    public void relocate(Location newCenter)throws DroneTaskException {
//        centerLocation.set(newCenter);
//    }
//
//    @Override
//    public ObservableValue<Location> startLocation() {
//        return startLocation;
//    }
//
//    @Override
//    public ObservableValue<Location> endLocation() {
//        return endLocation;
//    }
//
//    @Override
//    public List<MissionRow.MissionRowStub> getMissionRows() throws DroneTaskException {
//
//        ArrayList<MissionRow.MissionRowStub> missionRows = new ArrayList<>();
//
//        MissionRow.MissionRowStub rotatePitchMissionRow = new MissionRow.MissionRowStub();
//        MissionRow.MissionRowStub flyToCircleMissionRow = new MissionRow.MissionRowStub();
//        MissionRow.MissionRowStub rotateGimbalInPlaceMissionRow = new MissionRow.MissionRowStub();
//        MissionRow.MissionRowStub flyInCircleMissionRow = new MissionRow.MissionRowStub();
//
//        Double gimbalPitchValue = gimbalPitch.value();
//        Boolean isLookToMidValue = lookToMid.value();
//        Location centerLocationValue = centerLocation.value();
//        Double radiusValue = radiusReached.value();
//
//        if(centerLocationValue == null){
//            throw new DroneTaskException("Unknown Center, Can't create fly in circle mission");
//        }
//
//        if(radiusValue == null){
//            throw new DroneTaskException("Unknown radiusReached, unable to create fly in circle mission");
//        }
//
//        if(gimbalPitchValue != null) {
//            rotatePitchMissionRow.addPreCleanupCategory(TaskCategory.GIMBAL);
//
//            RotateGimbal.RotateGimbalStub rotatePitchTask = new RotateGimbal.RotateGimbalStub(
//                    new GimbalRequest(
//                            new GimbalState(0, gimbalPitchValue, 0), true, false, false
//                    )
//            );
//            rotatePitchMissionRow.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(rotatePitchTask,true,false,null));
//            missionRows.add(rotatePitchMissionRow);
//        }
//
//        // FlyToCircle Mission row Creation
//        Location firstPoint = startLocation.value();
//
//        if(firstPoint == null){
//            throw new DroneTaskException("Unable to know where we start");
//        }
//
//        flyToCircleMissionRow.addTask(TaskCategory.FLIGHT,
//                new MissionTaskInfo(
//                        new FlyToUsingDTM.FlyToUsingDTMStub(
//                                firstPoint, gotoCircleAGL.withDefault(agl.withDefault(50D).value()).value(),20,20,velocity.value()
//                        ),true,false,null));
//
//        StubDroneTask<GimbalTaskType> flyToCircleLockGimbalTask = getLockGimbalTask(isLookToMidValue,centerLocationValue);
//        StubDroneTask<GimbalTaskType> flyInCircleLockGimbalTask = getLockGimbalTask(isLookToMidValue,centerLocationValue);
//
//        if(flyToCircleLockGimbalTask != null){
//            flyToCircleMissionRow.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(flyToCircleLockGimbalTask,false,false,null));
//            flyToCircleMissionRow.addPostCleanupCategory(TaskCategory.GIMBAL);
//        }
//
//        missionRows.add(flyToCircleMissionRow);
//
//        if(isLookToMidValue != null){
//            rotateGimbalInPlaceMissionRow.addPreCleanupCategory(TaskCategory.GIMBAL);
//            rotateGimbalInPlaceMissionRow.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(
//                    new RotateGimbal.RotateGimbalStub(
//                            new GimbalRequest(
//                                    new GimbalState(0,0,isLookToMidValue ?  firstPoint.az(centerLocationValue) : centerLocation.value().az(firstPoint)),false,false,true
//                            )
//                    ),true,false,null
//            ));
//            missionRows.add(rotateGimbalInPlaceMissionRow);
//        }
//
//        MissionTaskInfo flyInCircleTaskInfo = new MissionTaskInfo(new FlyInCircle.FlyInCircleStub(
//                center().value(),radiusReached.value(),rotationType.withDefault(RotationType.CLOCKWISE).value(),360,-1,agl.withDefault(50D).value(),velocity.withDefault(5D).value()),true,false,null);
//        flyInCircleMissionRow.addTask(TaskCategory.FLIGHT,flyInCircleTaskInfo);
//
//        if(flyInCircleLockGimbalTask != null){
//            flyInCircleMissionRow.addPreCleanupCategory(TaskCategory.GIMBAL);
//            flyInCircleMissionRow.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(flyInCircleLockGimbalTask,false,false,null));
//            flyInCircleMissionRow.addPostCleanupCategory(TaskCategory.GIMBAL);
//        }
//
//        missionRows.add(flyInCircleMissionRow);
//
//        return missionRows;
//    }
//
//    @Override
//    public void startEdit() {
//        snapshot.set(new CircleFlightPlanSnapshot(
//                name.value(),
//                radiusReached.value(),
//                centerLocation.value(),
//                visibility.value(),
//                agl.value(),
//                velocity.value(),
//                rotationType.value(),
//                gotoCircleAGL.value(),
//                gimbalPitch.value(),
//                lookToMid.value()
//        ));
//    }
//
//    @Override
//    public void restoreToLastSnapshot() throws DroneTaskException{
//        CircleFlightPlanSnapshot lastData = snapshot.value();
//        if(lastData == null){
//            throw new DroneTaskException("Unable to restoreToLastSnapshot data");
//        }
//
//        name.set(lastData.getName());
//
//        radiusReached.set(lastData.getRadius());
//        centerLocation.set(lastData.getCenterLocation());
//        visibility.set(lastData.getVisibility());
//
//        agl.set(lastData.getAgl());
//        velocity.set(lastData.getVelocity());
//
//        rotationType.set(lastData.getRotationType());
//
//        gotoCircleAGL.set(lastData.getGotoCircleAGL());
//
//        gimbalPitch.set(lastData.getGimbalPitch());
//        lookToMid.set(lastData.getLookToMid());
//
//    }
//
//    public Property<Double> getRadius() {
//        return radiusReached;
//    }
//
//    private Location calcStartLocation(Location lastKnownLocation,Location centerLocation,Double radiusReached){
//        if(lastKnownLocation == null || centerLocation == null || radiusReached == null){
//            return null;
//        }
//
//        double startingDegree = centerLocation.az(lastKnownLocation);
//        return centerLocation.getLocationFromAzAndDistance(radiusReached,startingDegree);
//    }
//
//    private StubDroneTask<GimbalTaskType> getLockGimbalTask(Boolean lockToMidValue,Location centerLocationValue) throws DroneTaskException {
//
//        if(lockToMidValue == null){
//            return null;
//        }
//
//        if(centerLocationValue == null){
//            throw new DroneTaskException("Can't generate lock gimbal task, has no center");
//        }
//
//        return new LockYawAtLocation.LockYawAtLocationStub(centerLocationValue,lockToMidValue ? 0 : 180);
//    }
//
//    public Property<Location> getCenterLocation() {
//        return centerLocation;
//    }
//
//    public BooleanProperty getVisibility() {
//        return visibility;
//    }
//
//    public Property<Double> getAgl() {
//        return agl;
//    }
//
//    public Property<Double> getVelocity() {
//        return velocity;
//    }
//
//    public Property<RotationType> getRotationType() {
//        return rotationType;
//    }
//
//    public Property<Double> getGotoCircleAGL() {
//        return gotoCircleAGL;
//    }
//
//    public Property<Double> getGimbalPitch() {
//        return gimbalPitch;
//    }
//
//    public BooleanProperty getLookToMid() {
//        return lookToMid;
//    }
//
//    public Property<Location> getLastLocationBeforeCircle() {
//        return lastLocationBeforeCircle;
//    }
//
//    public ObservableValue<Location> getStartLocation() {
//        return startLocation;
//    }
//
//    public ObservableValue<Location> getEndLocation() {
//        return endLocation;
//    }
//
//}
