package eyesatop.unit.ui.models.map.mission;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.CameraActionType;
import eyesatop.controller.mission.IteratorCommandInfo;
import eyesatop.controller.mission.MissionIteratorType;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.MissionTaskInfo;
import eyesatop.controller.mission.flightplans.CircleFlightPlanInfo;
import eyesatop.controller.mission.flightplans.FlightPlanInfo;
import eyesatop.controller.mission.flightplans.RadiatorFlightPlanInfo;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.camera.StartRecording;
import eyesatop.controller.tasks.camera.StopRecording;
import eyesatop.controller.tasks.camera.StopShootingPhotos;
import eyesatop.controller.tasks.camera.TakePhotoInInterval;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.controller.tasks.flight.Hover;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.unit.ui.models.map.MapLine;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.observablelistidan.ListObserver;
import eyesatop.util.observablelistidan.ObservableListIdan;

/**
 * Created by Idan on 03/04/2018.
 */

public class MissionPlan {

    private final ObservableListIdan<FlightPlanComponent> flightPlanComponents = new ObservableListIdan<>();
    private final Property<String> name = new Property<>();
    private final Property<Location> missionCenter = new Property<>();
    private final BooleanProperty visibleOnMap = new BooleanProperty(true);

    private final Property<Location> lastLocationBeforeMission = new Property<>();

    private final RemovableCollection missionBindings = new RemovableCollection();

    private final BooleanProperty showFirstLine = new BooleanProperty(true);

    private double componentsLatSum = 0;
    private double componentsLonSum = 0;
    private double componentsNotNullCenterNumber = 0;

    private MapLine firstLine = null;

    public ObservableListIdan<FlightPlanComponent> getFlightPlanComponents() {
        return flightPlanComponents;
    }

    public MissionPlan(){

        missionBindings.add(flightPlanComponents.observe(new ListObserver<FlightPlanComponent>() {

            private HashMap<FlightPlanComponent,Removable> flightPlanComponentRemovableHashMap = new HashMap<FlightPlanComponent, Removable>();

            @Override
            public void added(FlightPlanComponent value, int index) {

                Location componentCenter = (Location) value.centerLocation().value();
                if(componentCenter != null){
                    componentsLatSum += componentCenter.getLatitude();
                    componentsLonSum += componentCenter.getLongitude();
                    componentsNotNullCenterNumber +=1;
                }
                updateStartEndLocations();
                updateCenter();

                flightPlanComponentRemovableHashMap.put(value,
                    value.centerLocation().observe(new Observer<Location>(){
                        @Override
                        public void observe(Location oldValue, Location newValue, Observation<Location> observation) {

                            if(oldValue == null && newValue == null){
                                return;
                            }

                            if(oldValue != null){
                                componentsLatSum -= oldValue.getLatitude();
                                componentsLonSum -= oldValue.getLongitude();
                                componentsNotNullCenterNumber -=1;
                            }

                            if(newValue != null){
                                componentsLatSum += newValue.getLatitude();
                                componentsLonSum += newValue.getLongitude();
                                componentsNotNullCenterNumber +=1;
                            }
                            updateCenter();
                        }
                    })
                );
            }

            @Override
            public void removed(FlightPlanComponent value, int oldIndex) {
                flightPlanComponentRemovableHashMap.remove(value);

                Location componentCenter = (Location) value.centerLocation().value();
                if(componentCenter != null){
                    componentsLatSum -= componentCenter.getLatitude();
                    componentsLonSum -= componentCenter.getLongitude();
                    componentsNotNullCenterNumber -=1;
                }
                updateCenter();
                updateStartEndLocations();
            }
        }));

        missionBindings.add(visibleOnMap.observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                for(FlightPlanComponent component : flightPlanComponents){
                    component.visibleOnMap().set(newValue);
                }
            }
        }));
        missionBindings.add(lastLocationBeforeMission.observe(new Observer<Location>() {
            @Override
            public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                updateStartEndLocations();
            }
        }));
    }

    public Property<String> getName() {
        return name;
    }

    public MissionPlanSnapshot createSnapshot(){

        ArrayList<FlightPlanInfo> planInfos = new ArrayList<>();
        for(FlightPlanComponent component : flightPlanComponents){
            planInfos.add(component.createSnapshot());
        }

        return new MissionPlanSnapshot(name.value(),planInfos);
    }

    public boolean containCameraCommand(){

        for(FlightPlanComponent component : flightPlanComponents){
            if(!component.cameraActionType().isNull() && component.cameraActionType().value() != CameraActionType.NONE){
                return true;
            }
        }
        return false;
    }

    public boolean containGimbalCommand(){

        for(FlightPlanComponent component : flightPlanComponents){
            if(!component.gimbalPitch().isNull()){
                return true;
            }
        }
        return false;
    }

    public void restoreFromSnapshot(MissionPlanSnapshot snapshot){
        name.set(snapshot.getName());
        flightPlanComponents.clear();
        List<FlightPlanInfo> snapshotComponents = snapshot.getComponentsInfo();

        for(FlightPlanInfo snapshotInfo : snapshotComponents){
            switch (snapshotInfo.componentType()){

                case CIRCLE:
                    CircleFlightPlanComponent newCircle = new CircleFlightPlanComponent();
                    newCircle.restoreFromSnapshot((CircleFlightPlanInfo) snapshotInfo);
                    flightPlanComponents.add(newCircle);
                    break;
                case WAYPOINTS:
                    break;
                case RADIATOR:
                    RadiatorFlightPlanComponent newRadiator = new RadiatorFlightPlanComponent();
                    newRadiator.restoreFromSnapshot((RadiatorFlightPlanInfo)snapshotInfo);
                    flightPlanComponents.add(newRadiator);
                    break;
            }
        }
    }

    private RemovableCollection startEndLocationRemovable = new RemovableCollection();

    private void updateStartEndLocations(){

        startEndLocationRemovable.remove();

        if(flightPlanComponents.size() > 0){
            startEndLocationRemovable.add(flightPlanComponents.get(0).lastLocationBeforeComponent().bind(lastLocationBeforeMission));

            for (int i = 1; i < flightPlanComponents.size(); i++) {
                startEndLocationRemovable.add(flightPlanComponents.get(i).lastLocationBeforeComponent().bind(flightPlanComponents.get(i-1).endLocation()));
            }
        }
    }

    private void updateCenter(){
        if(componentsNotNullCenterNumber == 0){
            missionCenter.setIfNew(null);
        }
        else{
            missionCenter.setIfNew(new Location(componentsLatSum/componentsNotNullCenterNumber,componentsLonSum/componentsNotNullCenterNumber));
        }
    }

    public void relocate(Location newCenter){
        Location currentCenter = missionCenter.value();

        if(currentCenter == null || newCenter == null){
            return;
        }

        double latDifference = newCenter.getLatitude() - currentCenter.getLatitude();
        double lonDifference = newCenter.getLongitude() - currentCenter.getLongitude();

        for(FlightPlanComponent component : flightPlanComponents){
            Location componentCenter = (Location) component.centerLocation().value();
            if(componentCenter != null){
                Location componentNewCenter = new Location(componentCenter.getLatitude() + latDifference,componentCenter.getLongitude() + lonDifference);
                component.relocate(componentNewCenter);
            }
        }
    }

    public Removable addToMap(final MapViewModel mapViewModel){

        final RemovableCollection mapBindings = new RemovableCollection();
        final HashMap<FlightPlanComponent,Removable> flightPlanComponentRemovableHashMap = new HashMap<>();
        final RemovableCollection lineBindings = new RemovableCollection();

        ListObserver<FlightPlanComponent> flightComponentsObserver = new ListObserver<FlightPlanComponent>() {
            @Override
            public void added(FlightPlanComponent value, int index) {
                Removable componentAddToMapRemovable = value.addToMap(mapViewModel);
                mapBindings.add(componentAddToMapRemovable);
                flightPlanComponentRemovableHashMap.put(value,componentAddToMapRemovable);
                arrangeLines(mapViewModel,lineBindings);
            }

            @Override
            public void removed(FlightPlanComponent value, int oldIndex) {
                Removable flightPlanComponentRemovable = flightPlanComponentRemovableHashMap.remove(value);
                mapBindings.removeRemovable(flightPlanComponentRemovable);
                flightPlanComponentRemovable.remove();
                arrangeLines(mapViewModel,lineBindings);
            }

            @Override
            public void swapped(FlightPlanComponent firstValue, FlightPlanComponent secondValue, int firstValueOldIndex, int secondValueOldIndex) {
                arrangeLines(mapViewModel,lineBindings);
            }
        };

        mapBindings.add(flightPlanComponents.observe(flightComponentsObserver));
        flightPlanComponents.observeCurrentValue(flightComponentsObserver);

        mapBindings.add(new Removable() {
            @Override
            public void remove() {
                lineBindings.remove();
            }
        });
        return mapBindings;
    }

    private void arrangeLines(final MapViewModel mapViewModel, RemovableCollection lineBindings){

        lineBindings.remove();

        if(flightPlanComponents.size() > 0){

            final MapLine mapLine = new MapLine();
            this.firstLine = mapLine;
            mapLine.getDash().set(20D);
            mapLine.getGap().set(20D);
            mapLine.getColor().set(Color.YELLOW);

            lineBindings.add(mapLine.getVisibility().bind(showFirstLine.and(visibleOnMap)));
            lineBindings.add(mapLine.getStartPoint().bind(lastLocationBeforeMission));
            lineBindings.add(mapLine.getEndPoint().bind(flightPlanComponents.get(0).startLocation()));
            mapViewModel.addMapItem(mapLine);
            lineBindings.add(new Removable() {
                @Override
                public void remove() {
                    mapViewModel.removeMapItem(mapLine);
                }
            });
        }

        for(int i=1; i<flightPlanComponents.size();i++){
            addLine(mapViewModel,lineBindings,flightPlanComponents.get(i-1).endLocation(),flightPlanComponents.get(i).startLocation());
        }
    }

    private void addLine(final MapViewModel mapViewModel, RemovableCollection lineBindings, ObservableValue<Location> startLocation, ObservableValue<Location> endLocation){
        final MapLine mapLine = new MapLine();
        mapLine.getDash().set(20D);
        mapLine.getGap().set(20D);
        mapLine.getColor().set(Color.YELLOW);

        lineBindings.add(mapLine.getVisibility().bind(visibleOnMap));
        lineBindings.add(mapLine.getStartPoint().bind(startLocation));
        lineBindings.add(mapLine.getEndPoint().bind(endLocation));
        mapViewModel.addMapItem(mapLine);
        lineBindings.add(new Removable() {
            @Override
            public void remove() {
                mapViewModel.removeMapItem(mapLine);
            }
        });
    }

    public List<MissionRow.MissionRowStub> getMissionRows() throws DroneTaskException{

        ArrayList<MissionRow.MissionRowStub> missionPlanRows = new ArrayList<>();

//        Property<CameraActionType> currentCameraActionType = new Property<>(CameraActionType.NO_CARE);


//        Integer firstComponentHeading = (Integer) flightPlanComponents.get(0).heading().value();
//        if(firstComponentHeading != null){
//            MissionRow.MissionRowStub rotateHeadingRow = new MissionRow.MissionRowStub();
//            rotateHeadingRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(new RotateHeading.RotateHeadnigStub(firstComponentHeading),true,false,null));
//            missionPlanRows.add(rotateHeadingRow);
//        }

//        // add hover row.
//        Integer firstHover = (Integer) flightPlanComponents.get(0).hoverTime().value();
//        if(firstHover != null && firstHover > 0){
//            MissionRow.MissionRowStub hoverRow = new MissionRow.MissionRowStub();
//            hoverRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(new Hover.HoverStub(firstHover),true,false,null));
//            missionPlanRows.add(hoverRow);
//        }

//        missionPlanRows.addAll(getCameraRows(flightPlanComponents.get(0)));
//        missionPlanRows.addAll(flightPlanComponents.get(0).getMissionRows());

        for(int i=0; i<flightPlanComponents.size();i++){

            FlightPlanComponent component = flightPlanComponents.get(i);

//            missionPlanRows.addAll(clearCamera());

            final Location componentStartLocation = (Location) component.startLocation().value();
            AltitudeInfo componentAltitudeInfo = (AltitudeInfo) component.altitudeInfo().value();

            MissionRow.MissionRowStub flyToSafeRow = new MissionRow.MissionRowStub();
            FlyToSafeAndFast.FlySafeAndFastToStub flyToSafeTask = new FlyToSafeAndFast.FlySafeAndFastToStub(componentStartLocation,componentAltitudeInfo);
            flyToSafeRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyToSafeTask,true,false,null));
            missionPlanRows.add(flyToSafeRow);

//            Integer componentHeading = (Integer) component.heading().value();
//            if(componentHeading != null){
//                MissionRow.MissionRowStub rotateHeadingRow = new MissionRow.MissionRowStub();
//                rotateHeadingRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(new RotateHeading.RotateHeadnigStub(componentHeading),true,false,null));
//                missionPlanRows.add(rotateHeadingRow);
//            }

            missionPlanRows.addAll(component.getMissionRows());
        }

//        missionPlanRows.addAll(clearCamera());

//        MissionRow.MissionRowStub lastRow = new MissionRow.MissionRowStub();
//        lastRow.setIteratorCommand(new IteratorCommandInfo(MissionIteratorType.LOOP,1));

        return missionPlanRows;
    }

    private List<MissionRow.MissionRowStub> clearCamera(){
        ArrayList<MissionRow.MissionRowStub> missionPlanRows = new ArrayList<>();

        MissionRow.MissionRowStub stopShootingPhotoRow = new MissionRow.MissionRowStub();
        stopShootingPhotoRow.addTask(TaskCategory.CAMERA,new MissionTaskInfo(new StopShootingPhotos.StubStopShootingPhotos(),true,true,null));
        missionPlanRows.add(stopShootingPhotoRow);

        MissionRow.MissionRowStub stopRecordRow = new MissionRow.MissionRowStub();
        stopRecordRow.addTask(TaskCategory.CAMERA,new MissionTaskInfo(new StopRecording.StubStopRecording(), true, true, null));
        missionPlanRows.add(stopRecordRow);

        return missionPlanRows;
    }

    private List<MissionRow.MissionRowStub> getCameraRows(FlightPlanComponent nextComponent){

        ArrayList<MissionRow.MissionRowStub> missionPlanRows = new ArrayList<>();

        CameraActionType tempCameraActionType = (CameraActionType) nextComponent.cameraActionType().value();

        switch (tempCameraActionType){

            case NONE:
                break;
            case VIDEO:
                MissionRow.MissionRowStub cameraRow = new MissionRow.MissionRowStub();
                cameraRow.addTask(TaskCategory.CAMERA,new MissionTaskInfo(new StartRecording.StubStartRecording(), true, false, null));
                missionPlanRows.add(cameraRow);
                break;
            case STILLS:

                MissionRow.MissionRowStub cameraRowForStills = new MissionRow.MissionRowStub();
                cameraRowForStills.addTask(TaskCategory.CAMERA,new MissionTaskInfo(new TakePhotoInInterval.StubTakePhotoInInterval(255, (Integer) nextComponent.shootPhotoInIntervalNumber().value()), true, false, null));
                missionPlanRows.add(cameraRowForStills);
                break;
        }

        return missionPlanRows;
    }

    public BooleanProperty getShowFirstLine() {
        return showFirstLine;
    }

    public void destroy(){

        missionBindings.remove();
        startEndLocationRemovable.remove();
    }

    public Property<Location> getLastLocationBeforeMission() {
        return lastLocationBeforeMission;
    }

    public BooleanProperty getVisibleOnMap() {
        return visibleOnMap;
    }

    public void clear(){
        name.set(null);
        flightPlanComponents.clear();
    }

    public MapLine getFirstLine() {
        return firstLine;
    }

    public String toString() {

        String infoString = "";

        for (FlightPlanComponent flightPlanComponent : flightPlanComponents) {

            Double componentVelocity = (Double) flightPlanComponent.velocity().value();
            AltitudeInfo componentAltitudeInfo = (AltitudeInfo) flightPlanComponent.altitudeInfo().value();

            infoString += flightPlanComponent.componentType().getName() + " :" + flightPlanComponent.getName().value() + "\n";
            infoString += "    velocity : " + (componentVelocity == null ? "N/A" : componentVelocity);
            infoString += "    Altitude : " + (componentAltitudeInfo == null ? "N/A" : componentAltitudeInfo.toString());
        }
        return infoString;
    }

    public double highestASLOnPlan(DtmProvider provider,Location homeLocation) throws TerrainNotFoundException {

        if (flightPlanComponents.size() == 0) {
            throw new TerrainNotFoundException("Not Enough flight plan components");
        }

        double highestASL = flightPlanComponents.get(0).highestASLOnComponent(provider,homeLocation);

        for (int i = 1; i < flightPlanComponents.size(); i++) {

            Location lastComponentEndLocation = (Location) flightPlanComponents.get(i - 1).endLocation().value();
            Location thisComponentStartLocation = (Location) flightPlanComponents.get(i).startLocation().value();

            if (lastComponentEndLocation == null || thisComponentStartLocation == null) {
                throw new TerrainNotFoundException("Unknown flight plan start location or end location at index : " + i);
            }

            double highestDTMOnLine = DtmProvider.DtmTools.highestDTMBetweenPoints(provider, lastComponentEndLocation, thisComponentStartLocation) + FlyToSafeAndFast.MIN_AGL;
            double thisComponentHighestDTM = flightPlanComponents.get(i).highestASLOnComponent(provider,homeLocation);

            highestASL = Math.max(highestASL, highestDTMOnLine);
            highestASL = Math.max(highestASL, thisComponentHighestDTM);
        }

        return highestASL;
    }
}
