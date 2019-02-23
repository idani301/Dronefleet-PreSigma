package eyesatop.unit.ui.models.map.mission;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import eyesatop.controller.GimbalRequest;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.AltitudeType;
import eyesatop.controller.beans.CameraActionType;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.MissionTaskInfo;
import eyesatop.controller.mission.flightplans.FlightPlanComponentType;
import eyesatop.controller.mission.flightplans.RadiatorFlightPlanInfo;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.unit.ui.models.map.MapLine;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.missionplans.components.AttributeData;
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
 * Created by Idan on 01/05/2018.
 */

public class RadiatorFlightPlanComponent extends FlightPlanComponent<RadiatorFlightPlanInfo> {

    private final Property<Location> centerLocation = new Property<>();
    private final Property<Integer> gap = new Property<>();
    private final Property<Integer> length = new Property<>();
    private final Property<Integer> width = new Property<>();
    private final Property<Integer> degree = new Property<>();

    private Property<List<Location>> points = new Property<>();

    private final Property<Location> startLocation = new Property<>();
    private final Property<Location> endLocation = new Property<>();

    private final RemovableCollection basicBindings = new RemovableCollection();

    public RadiatorFlightPlanComponent(){

        points.set(emptyList);

        basicBindings.add(centerLocation.observe(new Observer<Location>() {
            @Override
            public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                calcRadiator(centerLocation.value(),length.value(),width.value(),gap.value(),degree.value());
            }
        }));

        basicBindings.add(gap.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                calcRadiator(centerLocation.value(),length.value(),width.value(),gap.value(),degree.value());
            }
        }));

        basicBindings.add(length.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                calcRadiator(centerLocation.value(),length.value(),width.value(),gap.value(),degree.value());
            }
        }));

        basicBindings.add(width.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                calcRadiator(centerLocation.value(),length.value(),width.value(),gap.value(),degree.value());
            }
        }));

        basicBindings.add(degree.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                calcRadiator(centerLocation.value(),length.value(),width.value(),gap.value(),degree.value());
            }
        }));

        basicBindings.add(points.observe(new Observer<List<Location>>() {
            @Override
            public void observe(List<Location> oldValue, List<Location> newValue, Observation<List<Location>> observation) {
                if(newValue.size() == 0){
                    startLocation.set(null);
                    endLocation.set(null);
                }
                else{
                    startLocation.set(newValue.get(0));
                    endLocation.set(newValue.get(newValue.size()-1));
                }
            }
        }));
    }

    @Override
    public List<String> illegalFields() {

        ArrayList<String> illegalFields = new ArrayList<>();

        if(centerLocation.isNull()){
            illegalFields.add("Lat/Lon");
        }

        if(gap.isNull()){
            illegalFields.add("Lat/Lon");
        }

        if(length.isNull()){
            illegalFields.add("Length");
        }

        if(width.isNull()){
            illegalFields.add("Width");
        }

        if(velocity().isNull()){
            illegalFields.add("Velocity");
        }

        AltitudeInfo altitudeInfo = altitudeInfo().value();
        if(altitudeInfo == null || altitudeInfo.getValueInMeters() == null || altitudeInfo.getAltitudeType() == null){
            illegalFields.add("Altitude");
        }

        if(altitudeInfo != null && altitudeInfo.getAltitudeType() == AltitudeType.ABOVE_GROUND_LEVEL){
            if(altitudeInfo.getValueInMeters() != null && altitudeInfo.getValueInMeters() <=0){
                illegalFields.add("Altitude : AGL too low");
            }
        }

        CameraActionType cameraActionType = cameraActionType().value();
        if(cameraActionType != null && cameraActionType == CameraActionType.STILLS && shootPhotoInIntervalNumber().isNull()){
            illegalFields.add("Shoot photo in interval : ");
        }

        return illegalFields;
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
        return centerLocation;
    }

    @Override
    public void relocate(Location newCenter) {
        centerLocation.set(newCenter);
    }

    @Override
    protected RadiatorFlightPlanInfo createSnapshot() {
        return new RadiatorFlightPlanInfo(getName().value(),
                centerLocation.value(),
                altitudeInfo().value(),
                gimbalPitch().value(),
                velocity().value(),
                gap.value(),
                length.value(),
                width.value(),
                degree.value(), cameraActionType().value(), shootPhotoInIntervalNumber().value(), heading().value(), hoverTime().value(),opticalZoomLevel().value());
    }

    @Override
    protected void restoreFromSnapshot(RadiatorFlightPlanInfo snapshot) {
        getName().set(snapshot.getName());
        centerLocation.set(snapshot.getCenterLocation());
        altitudeInfo().set(snapshot.getAltitudeInfo());
        gimbalPitch().set(snapshot.getGimbalPitchDegree());
        velocity().set(snapshot.getVelocity());
        gap.set(snapshot.getGap());
        length.set(snapshot.getLength());
        width.set(snapshot.getWidth());
        degree.set(snapshot.getDegree());
        cameraActionType().set(snapshot.getCameraActionType());
        shootPhotoInIntervalNumber().set(snapshot.getShootPhotoInIntervalNumber());
        heading().set(snapshot.getHeading());
        hoverTime().set(snapshot.getHoverTime());
    }

    RemovableCollection mapTempBindings = new RemovableCollection();

    @Override
    public Removable addToMap(final MapViewModel mapViewModel) {

        RemovableCollection mapBindings = new RemovableCollection();

        mapBindings.add(
                points.observe(new Observer<List<Location>>() {
                    @Override
                    public void observe(List<Location> oldValue, List<Location> newValue, Observation<List<Location>> observation) {
                        mapTempBindings.remove();

                        if(newValue.size() == 0){
                            return;
                        }

//                        final MapDrawable firstPoint = new MapDrawable(1F);
//                        firstPoint.drawable().set(ContextCompat.getDrawable(mapViewModel.view().getContext(), R.drawable.waypoint));
//                        firstPoint.location().set(newValue.get(0));
//                        mapTempBindings.add(firstPoint.getVisibility().bind(visibleOnMap()));

//                        mapViewModel.addMapItem(firstPoint);
//                        mapTempBindings.add(new Removable() {
//                            @Override
//                            public void remove() {
//                                mapViewModel.removeMapItem(firstPoint);
//                            }
//                        });

                        if(newValue.size() <= 1){
                            return;
                        }

                        for(int i=1 ; i < newValue.size(); i++){
//                            final MapDrawable mapPoint = new MapDrawable(1F);
//                            mapPoint.drawable().set(ContextCompat.getDrawable(mapViewModel.view().getContext(), R.drawable.waypoint));
//                            mapPoint.location().set(newValue.get(i));
//                            mapTempBindings.add(mapPoint.getVisibility().bind(visibleOnMap()));

                            final MapLine mapLine = new MapLine();
                            mapLine.getColor().set(Color.GREEN);
                            mapLine.getStartPoint().set(newValue.get(i-1));
                            mapLine.getEndPoint().set(newValue.get(i));
                            mapTempBindings.add(mapLine.getVisibility().bind(visibleOnMap()));

//                            mapViewModel.addMapItem(mapPoint);
                            mapViewModel.addMapItem(mapLine);

                            mapTempBindings.add(new Removable() {
                                @Override
                                public void remove() {
//                                    mapViewModel.removeMapItem(mapPoint);
                                    mapViewModel.removeMapItem(mapLine);
                                }
                            });
                        }
                    }
                }).observeCurrentValue()
        );

        mapBindings.add(new Removable() {
            @Override
            public void remove() {
                mapTempBindings.remove();
            }
        });

        return mapBindings;
    }

    @Override
    public List<MissionRow.MissionRowStub> getMissionRows() throws DroneTaskException {

        ArrayList<MissionRow.MissionRowStub> missionRows = new ArrayList<>();

        AltitudeInfo altitudeInfoValue = altitudeInfo().value();
        Integer gimbalPitchValue = gimbalPitch().value();

        if(altitudeInfoValue == null || altitudeInfoValue.isNull()){
            throw new DroneTaskException("No Altitude Info");
        }

        List<Location> pointsValue = points.value();

        if(pointsValue.size() == 0){
            throw new DroneTaskException("No points");
        }

        if(gimbalPitchValue != null) {
            MissionRow.MissionRowStub rotatePitchMissionRow = new MissionRow.MissionRowStub();
            rotatePitchMissionRow.addPreCleanupCategory(TaskCategory.GIMBAL);

            RotateGimbal.RotateGimbalStub rotatePitchTask = new RotateGimbal.RotateGimbalStub(
                    new GimbalRequest(
                            new GimbalState(0, gimbalPitchValue, 0), true, false, false
                    ),
                    null);
            rotatePitchMissionRow.addTask(TaskCategory.GIMBAL, new MissionTaskInfo(rotatePitchTask, true, false, null));
            missionRows.add(rotatePitchMissionRow);
        }

        Integer componentHeading = degree.value();
        if(componentHeading != null){
            MissionRow.MissionRowStub rotateHeadingRow = new MissionRow.MissionRowStub();
            rotateHeadingRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(new RotateHeading.RotateHeadnigStub(componentHeading),true,false,null));
            missionRows.add(rotateHeadingRow);
        }

        switch (altitudeInfoValue.getAltitudeType()){

            case ABOVE_GROUND_LEVEL:

                FlyTo.FlyToStub flyToFirstPoint = new FlyTo.FlyToStub(pointsValue.get(0),altitudeInfoValue,null,null, 5D);
                MissionRow.MissionRowStub flyToFirstRow = new MissionRow.MissionRowStub();
                flyToFirstRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyToFirstPoint,true,false,null));
                missionRows.add(flyToFirstRow);

                for(int i=1; i<pointsValue.size(); i++){

                    Location point = pointsValue.get(i);
                    Location lastPoint = pointsValue.get(i-1);

                    double az = lastPoint.az(point);
                    double radiusReached = 5;
                    if(lastPoint.distance(point) <= 10){
                        radiusReached = Math.max(1D,lastPoint.distance(point)/3);
                    }

                    FlyToUsingDTM.FlyToUsingDTMStub flyToPoint = new FlyToUsingDTM.FlyToUsingDTMStub(point, az, altitudeInfoValue.getValueInMeters(),20,20,velocity().withDefault(5D).value(), radiusReached);
                    MissionRow.MissionRowStub flyToRow = new MissionRow.MissionRowStub();
                    flyToRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyToPoint,true,false,null));
                    missionRows.add(flyToRow);
                }

                break;
            case ABOVE_SEA_LEVEL:
                FlyTo.FlyToStub flyToFirstPointASL = new FlyTo.FlyToStub(pointsValue.get(0),altitudeInfoValue,null,null, 5D);
                MissionRow.MissionRowStub flyToFirstRowASL = new MissionRow.MissionRowStub();
                flyToFirstRowASL.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyToFirstPointASL,true,false,null));
                missionRows.add(flyToFirstRowASL);

                for(int i=1; i<pointsValue.size(); i++){

                    Location point = pointsValue.get(i);
                    Location lastPoint = pointsValue.get(i-1);

                    double az = lastPoint.az(point);
                    double radiusReached = 5;
                    if(lastPoint.distance(point) <= 10){
                        radiusReached = Math.max(1D,lastPoint.distance(point)/3);
                    }


                    FlyTo.FlyToStub flyToPoint = new FlyTo.FlyToStub(point, altitudeInfoValue,az, velocity().value(), radiusReached);
                    MissionRow.MissionRowStub flyToRow = new MissionRow.MissionRowStub();
                    flyToRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyToPoint,true,false,null));
                    missionRows.add(flyToRow);
                }
                break;
            case FROM_TAKE_OFF_LOCATION:
                FlyTo.FlyToStub flyToFirstPointATL = new FlyTo.FlyToStub(pointsValue.get(0),altitudeInfoValue,null,null,5D);
                MissionRow.MissionRowStub flyToFirstRowATL = new MissionRow.MissionRowStub();
                flyToFirstRowATL.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyToFirstPointATL,true,false,null));
                missionRows.add(flyToFirstRowATL);

                for(int i=1; i<pointsValue.size(); i++) {

                    Location point = pointsValue.get(i);
                    Location lastPoint = pointsValue.get(i - 1);

                    double az = lastPoint.az(point);
                    double radiusReached = 5;
                    if(lastPoint.distance(point) <= 10){
                        radiusReached = Math.max(1D,lastPoint.distance(point)/3);
                    }

                    FlyTo.FlyToStub flyToPoint = new FlyTo.FlyToStub(point, altitudeInfoValue, az, velocity().value(), radiusReached);
                    MissionRow.MissionRowStub flyToRow = new MissionRow.MissionRowStub();
                    flyToRow.addTask(TaskCategory.FLIGHT, new MissionTaskInfo(flyToPoint, true, false, null));
                    missionRows.add(flyToRow);
                }
                break;
        }

        return missionRows;
    }

    @Override
    public FlightPlanComponentType componentType() {
        return FlightPlanComponentType.RADIATOR;
    }

    @Override
    public AttributeDataResult attributeDataList() {

        RemovableCollection attributeBindings = new RemovableCollection();
        ArrayList<AttributeData> attributeDataList = new ArrayList<>();

        final AttributeData latAttribute = AttributeData.createSimpleDoubleValue("Lat",0.0001,null,null);
        final AttributeData lonAttribute = AttributeData.createSimpleDoubleValue("Lon",0.0001,null,null);

        Location currentCenter = centerLocation.value();
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

                        Location oldCenter = centerLocation.value();

                        if(newValue == null && lonAttributeValue == null){
                            if(oldCenter != null){
                                centerLocation.set(null);
                            }
                            return;
                        }

                        Location newCenter = new Location(newValue,lonAttributeValue);

                        if(!newCenter.equals(oldCenter)){
                            centerLocation.set(newCenter);
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

                        Location oldCenter = centerLocation.value();

                        if(newValue == null && latAttributeValue == null){
                            if(oldCenter != null){
                                centerLocation.set(null);
                            }
                            return;
                        }

                        Location newCenter = new Location(latAttributeValue,newValue);

                        if(!newCenter.equals(oldCenter)){
                            centerLocation.set(newCenter);
                        }
                    }
                })
        );

        attributeBindings.add(centerLocation.observe(new Observer<Location>() {
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

        AttributeData lengthAttribute = AttributeData.createSimpleIntegerValue("Length",1,10,null);
        lengthAttribute.getIntValue().set(length.value());
        attributeBindings.add(length.bind(lengthAttribute.getIntValue()));
        attributeDataList.add(lengthAttribute);

        AttributeData widthAttribute = AttributeData.createSimpleIntegerValue("Width",1,10,null);
        widthAttribute.getIntValue().set(width.value());
        attributeBindings.add(width.bind(widthAttribute.getIntValue()));
        attributeDataList.add(widthAttribute);

        AttributeData rotationAttribute = AttributeData.createSimpleIntegerValue("Rotation",1,0,359);
        rotationAttribute.getIntValue().set(degree.value());
        attributeBindings.add(degree.bind(rotationAttribute.getIntValue()));
        attributeDataList.add(rotationAttribute);

        AttributeData gapAttribute = AttributeData.createSimpleIntegerValue("Gap",1,2,null);
        gapAttribute.getIntValue().set(gap.value());
        attributeBindings.add(gap.bind(gapAttribute.getIntValue()));
        attributeDataList.add(gapAttribute);

        return new AttributeDataResult(attributeDataList,attributeBindings);
    }

    @Override
    public FlightPlanComponent<RadiatorFlightPlanInfo> duplicate() {

        RadiatorFlightPlanComponent component = new RadiatorFlightPlanComponent();
        component.restoreFromSnapshot(createSnapshot());
        return component;
    }

    @Override
    public void destroy() {
        basicBindings.remove();
    }

    @Override
    public double highestTerrainOnComponent(DtmProvider provider) throws TerrainNotFoundException {

        List<Location> pointsValue = points.value();

        if(pointsValue == null || pointsValue.size() <=1){
            throw new TerrainNotFoundException("Not Enough points in radiator to determine");
        }

        double highestDTM = DtmProvider.DtmTools.highestDTMBetweenPoints(provider,pointsValue.get(0),pointsValue.get(1));
        for(int i=2; i<pointsValue.size(); i++){
            double tempDTM = DtmProvider.DtmTools.highestDTMBetweenPoints(provider,pointsValue.get(i-1),pointsValue.get(i));
            highestDTM = Math.max(tempDTM,highestDTM);
        }

        return highestDTM;
    }

    private final List<Location> emptyList = new ArrayList<>();

    public void calcRadiator(Location center, Integer length, Integer width, Integer gap,Integer degreeBetween0To360) {

        if(center == null || length == null || width == null || gap == null || degreeBetween0To360 == null){
            points.set(emptyList);
            return;
        }

        double angle = -Math.atan2(width,length)*180/Math.PI;
        double distance = 0.5*Math.sqrt(width*width + length*length);
        Location startingLocation = center.getLocationFromAzAndDistance(distance,angle + degreeBetween0To360);
        List<Location> locations = new ArrayList<>();
        locations.add(startingLocation);
        int maxIndexNumber = (int) (width/gap);
        for (int i = 0; i < maxIndexNumber; i++) {
            if (i%2 == 0){
                locations.add(locations.get(locations.size()-1).getLocationFromAzAndDistance(length,180 + degreeBetween0To360));
                locations.add(locations.get(locations.size()-1).getLocationFromAzAndDistance(gap,90 + degreeBetween0To360));
            } else {
                locations.add(locations.get(locations.size()-1).getLocationFromAzAndDistance(length,0 + degreeBetween0To360));
                locations.add(locations.get(locations.size()-1).getLocationFromAzAndDistance(gap,90 + degreeBetween0To360));
            }
        }
        Location endLocation = locations.get(locations.size()-1).getLocationFromAzAndDistance(length,maxIndexNumber%2==0 ? 180 + degreeBetween0To360 : 0 + degreeBetween0To360);
        locations.add(endLocation);

        points.set(locations);
    }
}
