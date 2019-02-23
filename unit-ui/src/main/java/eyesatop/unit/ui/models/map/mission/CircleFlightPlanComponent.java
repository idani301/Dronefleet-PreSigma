package eyesatop.unit.ui.models.map.mission;

import com.example.abstractcontroller.DroneMicroMovement;

import java.util.ArrayList;
import java.util.List;

import eyesatop.controller.GimbalRequest;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.AltitudeType;
import eyesatop.controller.beans.CameraActionType;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.MissionTaskInfo;
import eyesatop.controller.mission.flightplans.CircleFlightPlanInfo;
import eyesatop.controller.mission.flightplans.FlightPlanComponentType;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.camera.StartRecording;
import eyesatop.controller.tasks.camera.StopRecording;
import eyesatop.controller.tasks.camera.TakePhoto;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.Hover;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.map.MapCircle;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.missionplans.components.AttributeData;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 25/12/2017.
 */

public class CircleFlightPlanComponent extends FlightPlanComponent<CircleFlightPlanInfo> {

    private final Property<Integer> radius = new Property<>();
    private final Property<Location> center = new Property<>();
    private final Property<Integer> photoNumber = new Property<>();

    private final Property<Boolean> isLookToMid = new Property<>();

    private final Property<Location> startLocation = new Property<>();
    private final Property<Location> endLocation = new Property<>();
    private final RemovableCollection bindings = new RemovableCollection();

    public CircleFlightPlanComponent() {


        bindings.add(
                center.observe(new Observer<Location>() {
                    @Override
                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                        startLocation.set(calcStartLocation(lastLocationBeforeComponent().value(),center.value(),radius.value()));
                        endLocation.set(calcStartLocation(lastLocationBeforeComponent().value(),center.value(),radius.value()));
                    }
                })
        );

        bindings.add(
                lastLocationBeforeComponent().observe(new Observer<Location>() {
                    @Override
                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                        startLocation.set(calcStartLocation(lastLocationBeforeComponent().value(),center.value(),radius.value()));
                        endLocation.set(calcStartLocation(lastLocationBeforeComponent().value(),center.value(),radius.value()));
                    }
                })
        );

        bindings.add(
                radius.observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                        startLocation.set(calcStartLocation(lastLocationBeforeComponent().value(),center.value(),radius.value()));
                        endLocation.set(calcStartLocation(lastLocationBeforeComponent().value(),center.value(),radius.value()));
                        endLocation.set(calcStartLocation(lastLocationBeforeComponent().value(),center.value(),radius.value()));
                    }
                }).observeCurrentValue()
        );
    }

    @Override
    public CircleFlightPlanInfo createSnapshot() {

        return new CircleFlightPlanInfo(
                getName().value(),
                altitudeInfo().value(),
                velocity().value(),
                gimbalPitch().value(),
                cameraActionType().value(),
                shootPhotoInIntervalNumber().value(),
                heading().value(),
                hoverTime().value(),center.value(),radius.value(),null,isLookToMid.value(),opticalZoomLevel().value(),
                photoNumber.value());
    }

    @Override
    public void restoreFromSnapshot(CircleFlightPlanInfo snapshot) {
        getName().set(snapshot.getName());
        center.set(snapshot.getCenterLocation());
        radius.set(snapshot.getRadius());
        altitudeInfo().set(snapshot.getAltitudeInfo());
        gimbalPitch().set(snapshot.getGimbalPitchDegree());
        isLookToMid.set(snapshot.getLookToMid());
        velocity().set(snapshot.getVelocity());
        cameraActionType().set(snapshot.getCameraActionType());
        shootPhotoInIntervalNumber().set(snapshot.getShootPhotoInIntervalNumber());
        heading().set(snapshot.getHeading());
        hoverTime().set(snapshot.getHoverTime());
        photoNumber.set(snapshot.getPhotoNumber());
    }


    @Override
    public Removable addToMap(final MapViewModel mapViewModel) {

        RemovableCollection mapBindings = new RemovableCollection();

        final MapCircle mapCircle = new MapCircle();
        mapBindings.add(mapCircle.center().bind(center));
        mapBindings.add(mapCircle.radius().bind(radius.transform(new Function<Integer, Double>() {
            @Override
            public Double apply(Integer input) {
                if(input == null){
                    return null;
                }
                return input.doubleValue();
            }
        })));
        mapBindings.add(mapCircle.visible().bind(visibleOnMap()));

        mapViewModel.addMapItem(mapCircle);
        mapBindings.add(new Removable() {
            @Override
            public void remove() {
                mapViewModel.removeMapItem(mapCircle);
            }
        });
        return mapBindings;
    }


    @Override
    public List<String> illegalFields() {

        ArrayList<String> problemList = new ArrayList<>();

        if(radius.isNull()){
            problemList.add("Radius");
        }

        if(center.isNull()){
            problemList.add("Lat/Lon");
        }

        AltitudeInfo altitudeInfo = altitudeInfo().value();
        if(altitudeInfo == null || altitudeInfo.getValueInMeters() == null || altitudeInfo.getAltitudeType() == null){
            problemList.add("Altitude");
        }

        if(altitudeInfo != null && altitudeInfo.getAltitudeType() == AltitudeType.ABOVE_GROUND_LEVEL){
            if(altitudeInfo.getValueInMeters() != null && altitudeInfo.getValueInMeters() <=0){
                problemList.add("Altitude : AGL too low");
            }
        }

        CameraActionType cameraActionType = cameraActionType().withDefault(CameraActionType.NONE).value();

        Double velocity;
        switch (cameraActionType){

            case NONE:

                velocity = velocity().value();

                if(velocity == null){
                    problemList.add("Velocity is Missing");
                }

                if(velocity <= 0.5D){
                    problemList.add("Velocity is too low, minimal 0.5");
                }
                break;
            case VIDEO:
                velocity = velocity().value();

                if(velocity == null){
                    problemList.add("Velocity is Missing");
                }

                if(velocity <= 0.5D){
                    problemList.add("Velocity is too low, minimal 0.5");
                }
                break;
            case STILLS:
                Integer currentPhotoNumber = photoNumber.value();
                if(currentPhotoNumber == null){
                    problemList.add("Photo Number is empty");
                }
                else if(currentPhotoNumber <= 1){
                    problemList.add("Photo Number is too low");
                }
                break;
        }

        return problemList;
    }

    @Override
    public ObservableValue<Location> startLocation() {
        return startLocation;
    }

    @Override
    public ObservableValue<Location> endLocation() {
        return endLocation;
    }

    @Override
    public Property<Location> centerLocation() {
        return center;
    }

    @Override
    public void relocate(Location newCenter) {
        center.set(newCenter);
    }

    @Override
    public void destroy() {
        bindings.remove();
    }

    @Override
    public double highestTerrainOnComponent(DtmProvider provider) throws TerrainNotFoundException {

        Location centerLocationValue = center.value();
        Integer radiusValue = radius.value();

        if(centerLocationValue == null || radiusValue == null){
            throw new TerrainNotFoundException("Has No Circle");
        }

        return DtmProvider.DtmTools.highestDTMAroundCircleWithRadius(provider,centerLocationValue,radiusValue);
    }

    private Location calcStartLocation(Location lastKnownLocation,Location centerLocation,Integer radius){
        if(lastKnownLocation == null || centerLocation == null || radius == null){
            return null;
        }

        double startingDegree = centerLocation.az(lastKnownLocation);
        return centerLocation.getLocationFromAzAndDistance(radius,startingDegree);
    }

    @Override
    public List<MissionRow.MissionRowStub> getMissionRows() throws DroneTaskException {

        ArrayList<MissionRow.MissionRowStub> missionRows = new ArrayList<>();
        MissionRow.MissionRowStub rotatePitchMissionRow = new MissionRow.MissionRowStub();
        MissionRow.MissionRowStub rotateGimbalInPlaceMissionRow = new MissionRow.MissionRowStub();

        Integer gimbalPitchValue = gimbalPitch().value();
        Boolean isLookToMidValue = isLookToMid.value();
        Location centerLocationValue =  center.value();
        AltitudeInfo altitudeInfoValue = altitudeInfo().value();
        Integer radiusValue = radius.value();

        if(centerLocationValue == null){
            throw new DroneTaskException("Unable to create circle, has no center");
        }

        if(radiusValue == null){
            throw new DroneTaskException("Unable to create circle, has no radiusReached");
        }

        if(altitudeInfoValue == null){
            throw new DroneTaskException("Unable to create circle, has no altitude info");
        }

        if(gimbalPitchValue != null) {
            rotatePitchMissionRow.addPreCleanupCategory(TaskCategory.GIMBAL);

            RotateGimbal.RotateGimbalStub rotatePitchTask = new RotateGimbal.RotateGimbalStub(
                    new GimbalRequest(
                            new GimbalState(0, gimbalPitchValue, 0), true, false, false
                    ),
                    null);
            rotatePitchMissionRow.addTask(TaskCategory.GIMBAL, new MissionTaskInfo(rotatePitchTask, true, false, null));
            missionRows.add(rotatePitchMissionRow);
        }

        final Location startLocation = startLocation().value();

        if(startLocation != null){

            MissionRow.MissionRowStub goToStartLocationRow = new MissionRow.MissionRowStub();

            final AltitudeInfo altitudeInfo = altitudeInfoValue;

            StubDroneTask<FlightTaskType> flyToTask;
            flyToTask = new FlyTo.FlyToStub(startLocation,altitudeInfo,null,null,3D);

            goToStartLocationRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyToTask,true,false,null));

            StubDroneTask<GimbalTaskType> gimbalTask = getLockGimbalTask(isLookToMidValue,center.value());
            if(gimbalTask != null){
                goToStartLocationRow.addPreCleanupCategory(TaskCategory.GIMBAL);
                goToStartLocationRow.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(gimbalTask,false,false,null));
                goToStartLocationRow.addPostCleanupCategory(TaskCategory.GIMBAL);
            }

            missionRows.add(goToStartLocationRow);

            if(isLookToMidValue != null){
                rotateGimbalInPlaceMissionRow.addPreCleanupCategory(TaskCategory.GIMBAL);
                rotateGimbalInPlaceMissionRow.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(
                        new RotateGimbal.RotateGimbalStub(
                                new GimbalRequest(
                                        new GimbalState(0,0,isLookToMidValue ?  startLocation.az(centerLocationValue) : centerLocationValue.az(startLocation)),false,false,true
                                ),
                                null),true,false,null
                ));
                missionRows.add(rotateGimbalInPlaceMissionRow);
            }
        }

        // add hover row.
        Integer hover = (Integer) hoverTime().withDefault(3).value();
        if(hover != null && hover > 0){
            MissionRow.MissionRowStub hoverRow = new MissionRow.MissionRowStub();
            hoverRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(new Hover.HoverStub(hover),true,false,null));
            missionRows.add(hoverRow);
        }

        CameraActionType cameraActionType = this.cameraActionType().withDefault(CameraActionType.NONE).value();

        if(isLookToMidValue != null) {
            MissionRow.MissionRowStub lockGimbalRow = new MissionRow.MissionRowStub();
            lockGimbalRow.addPreCleanupCategory(TaskCategory.GIMBAL);
            lockGimbalRow.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(getLockGimbalTask(isLookToMidValue,centerLocationValue),false,false,null));
            missionRows.add(lockGimbalRow);
        }

        int numberOfRows;
        double degreesToCover;

        switch (cameraActionType){

                case NONE:

                    numberOfRows = numberOfCircles(radiusValue);
                    degreesToCover = 360 / (double)numberOfRows;

                    for (int i = 0; i < numberOfRows; i++) {
                        MissionRow.MissionRowStub tempCircleRow = new MissionRow.MissionRowStub();
                        FlyInCircle.FlyInCircleStub flyInCircleTemp = new FlyInCircle.FlyInCircleStub(centerLocationValue,
                                radiusValue,
                                RotationType.CLOCKWISE,
                                degreesToCover,-1,altitudeInfoValue,velocity().withDefault(5D).value());
                        tempCircleRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyInCircleTemp,true,false,null));
                        missionRows.add(tempCircleRow);
                    }
                    MissionRow.MissionRowStub unlockRow = new MissionRow.MissionRowStub();
                    unlockRow.addPreCleanupCategory(TaskCategory.GIMBAL);
                    missionRows.add(unlockRow);
                    break;
                case VIDEO:

                    MissionRow.MissionRowStub startRecordRow = new MissionRow.MissionRowStub();
                    StartRecording.StubStartRecording startRecordTask = new StartRecording.StubStartRecording();
                    startRecordRow.addTask(TaskCategory.CAMERA,new MissionTaskInfo(startRecordTask,true,false,null));
                    missionRows.add(startRecordRow);

                    numberOfRows = numberOfCircles(radiusValue);
                    degreesToCover = 360 / (double)numberOfRows;

                    for (int i = 0; i < numberOfRows; i++) {
                        MissionRow.MissionRowStub tempCircleRow = new MissionRow.MissionRowStub();
                        FlyInCircle.FlyInCircleStub flyInCircleTemp = new FlyInCircle.FlyInCircleStub(centerLocationValue,
                                radiusValue,
                                RotationType.CLOCKWISE,
                                degreesToCover,-1,altitudeInfoValue,velocity().withDefault(5D).value());
                        tempCircleRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyInCircleTemp,true,false,null));
                        missionRows.add(tempCircleRow);
                    }

                    MissionRow.MissionRowStub stopRecordRow = new MissionRow.MissionRowStub();
                    StopRecording.StubStopRecording stopRecordTask = new StopRecording.StubStopRecording();
                    stopRecordRow.addPreCleanupCategory(TaskCategory.GIMBAL);
                    stopRecordRow.addTask(TaskCategory.CAMERA,new MissionTaskInfo(stopRecordTask,true,false,null));
                    missionRows.add(stopRecordRow);
                    break;
                case STILLS:

                    numberOfRows = photoNumber.value();
                    degreesToCover = 360 / (double)numberOfRows;

                    for (int i = 0; i < numberOfRows-1; i++) {
                        MissionRow.MissionRowStub tempCircleRow = new MissionRow.MissionRowStub();

                        TakePhoto.StubTakePhoto takePhotoTask = new TakePhoto.StubTakePhoto();
                        tempCircleRow.addTask(TaskCategory.CAMERA,new MissionTaskInfo(takePhotoTask,true,false,null));

                        FlyInCircle.FlyInCircleStub flyInCircleTemp = new FlyInCircle.FlyInCircleStub(centerLocationValue,
                                radiusValue,
                                RotationType.CLOCKWISE,
                                degreesToCover,-1,altitudeInfoValue,velocityForEachCircle(radiusValue,numberOfRows));
                        tempCircleRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyInCircleTemp,true,false,null));
                        missionRows.add(tempCircleRow);
                    }

                    MissionRow.MissionRowStub lastStillsRow = new MissionRow.MissionRowStub();
                    TakePhoto.StubTakePhoto takePhotoTask = new TakePhoto.StubTakePhoto();
                    lastStillsRow.addTask(TaskCategory.CAMERA,new MissionTaskInfo(takePhotoTask,true,false,null));
                    lastStillsRow.addPreCleanupCategory(TaskCategory.GIMBAL);
                    missionRows.add(lastStillsRow);
                    break;
        }

        return missionRows;
    }

    @Override
    public FlightPlanComponentType componentType() {
        return FlightPlanComponentType.CIRCLE;
    }

    @Override
    public AttributeDataResult attributeDataList() {

        RemovableCollection attributeBindings = new RemovableCollection();
        ArrayList<AttributeData> attributeDataList = new ArrayList<>();

        final AttributeData latAttribute = AttributeData.createSimpleDoubleValue("Lat",0.0001,null,null);
        final AttributeData lonAttribute = AttributeData.createSimpleDoubleValue("Lon",0.0001,null,null);

        Location currentCenter = center.value();
        if(currentCenter != null){
            latAttribute.getDoubleValue().set(currentCenter.getLatitude());
            lonAttribute.getDoubleValue().set(currentCenter.getLongitude());
        }

        attributeBindings.add(
                latAttribute.getDoubleValue().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        Double lonAttributeValue = lonAttribute.getDoubleValue().value();

                        if(newValue == null && lonAttributeValue != null || (newValue != null && lonAttributeValue == null)){
                            return;
                        }

                        Location oldCenter = center.value();

                        if(newValue == null && lonAttributeValue == null){
                            if(oldCenter != null){
                                center.set(null);
                            }
                            return;
                        }

                        Location newCenter = new Location(newValue,lonAttributeValue);

                        if(!newCenter.equals(oldCenter)){
                            center.set(newCenter);
                        }
                    }
                })
        );
        attributeBindings.add(
                lonAttribute.getDoubleValue().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        Double latAttributeValue = latAttribute.getDoubleValue().value();

                        if(newValue == null && latAttributeValue != null || (newValue != null && latAttributeValue == null)){
                            return;
                        }

                        Location oldCenter = center.value();

                        if(newValue == null && latAttributeValue == null){
                            if(oldCenter != null){
                                center.set(null);
                            }
                            return;
                        }

                        Location newCenter = new Location(latAttributeValue,newValue);

                        if(!newCenter.equals(oldCenter)){
                            center.set(newCenter);
                        }
                    }
                })
        );
        attributeBindings.add(center.observe(new Observer<Location>() {
            @Override
            public void observe(Location oldValue, Location newValue, Observation<Location> observation) {

                if(newValue == null){
                    latAttribute.getDoubleValue().set(null);
                    lonAttribute.getDoubleValue().set(null);
                }
                else{
                    if(!newValue.equals(oldValue)){
                        latAttribute.getDoubleValue().set(newValue.getLatitude());
                        lonAttribute.getDoubleValue().set(newValue.getLongitude());
                    }
                }
            }
        }));

        AttributeData nameAttribute = AttributeData.createSimpleStringValue("Name");
        nameAttribute.getStringValue().set(getName().value());
        attributeBindings.add(getName().bind(nameAttribute.getStringValue()));
        attributeDataList.add(nameAttribute);

        attributeDataList.add(latAttribute);
        attributeDataList.add(lonAttribute);

        AttributeData radiusAttribute = AttributeData.createSimpleIntegerValue("Radius",1,10,null);
        radiusAttribute.getIntValue().set(radius.value());
        attributeBindings.add(radius.bind(radiusAttribute.getIntValue()));
        attributeDataList.add(radiusAttribute);

        AttributeData photoNumberAttribute = AttributeData.createSimpleIntegerValue("Photo Number",1,2,null);
        photoNumberAttribute.getIntValue().set(photoNumber.value());
        attributeBindings.add(photoNumber.bind(photoNumberAttribute.getIntValue()));
        attributeDataList.add(photoNumberAttribute);
        attributeBindings.add(photoNumberAttribute.getVisbility().bind(cameraActionType().equalsTo(CameraActionType.STILLS).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)));

//        AttributeData velocityAttribute = AttributeData.createSimpleDoubleValue("Velocity",0.5);
//        velocityAttribute.getDoubleValue().set(velocity().value());
//        attributeBindings.add(velocity().bind(velocityAttribute.getDoubleValue()));
//        attributeDataList.add(velocityAttribute);

        return new AttributeDataResult(attributeDataList,attributeBindings);
    }

    @Override
    public FlightPlanComponent<CircleFlightPlanInfo> duplicate() {

        CircleFlightPlanComponent circleFlightPlanComponent = new CircleFlightPlanComponent();
        circleFlightPlanComponent.restoreFromSnapshot(createSnapshot());

        return circleFlightPlanComponent;
    }

    private int numberOfCircles(int radius){
        return (int) Math.round((double)radius/10D);
    }

    private double velocityForEachCircle(int radius,int photoNumber){

        final double minPhotoIntervalTime = 2.5;
        return Math.min((2*Math.PI * (double)radius)/(photoNumber*minPhotoIntervalTime), DroneMicroMovement.MAX_ROLL_PITCH_VELOCITY);

    }

    private StubDroneTask<GimbalTaskType> getLockGimbalTask(Boolean lockToMidValue, Location centerLocationValue) throws DroneTaskException {

        if(lockToMidValue == null){
            return null;
        }

        if(centerLocationValue == null){
            throw new DroneTaskException("Can't generate lock gimbal task, has no center");
        }

        return new LockYawAtLocation.LockYawAtLocationStub(centerLocationValue,lockToMidValue ? 0 : 180);
    }

    public Property<Boolean> isLookToMid() {
        return isLookToMid;
    }
}
