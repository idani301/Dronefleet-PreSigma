package eyesatop.unit.ui.models.specialfunctions;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.util.Arrays;

import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.map.mission.CircleFlightPlanComponent;
import eyesatop.unit.ui.models.map.mission.FlightPlanComponent;
import eyesatop.unit.ui.models.map.mission.MissionPlan;
import eyesatop.unit.ui.models.map.mission.RadiatorFlightPlanComponent;
import eyesatop.unit.ui.models.tabs.SpecialFunctionType;
import eyesatop.util.android.FileExplorer.FileExplorer;
import eyesatop.util.android.files.EyesatopAppsFilesUtils;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 13/11/2017.
 */

public class MissionPlannerFunction extends SpecialFunction {

    public static final String MISSION_MAIN_FOLDER = "Eyesatop Mission Plans";

    private final FileExplorer loadMissionFileExplorer;
    private final FileExplorer deletePlanFileExplorer;

    private final BooleanProperty isAddFlightComponentOpened = new BooleanProperty(false);
    private final BooleanProperty isFunctionScreenOpened;
    private final BooleanProperty isMissionPlannerOpened = new BooleanProperty(false);

    private final BooleanProperty isRelocateMarked = new BooleanProperty(false);

    private final Property<MissionState> currentState = new Property<>(MissionState.NONE);

//    private final WaypointAttributePlannerInfo pathPlannerInfo = new WaypointAttributePlannerInfo();
    private final MissionPlan missionPlan = new MissionPlan();

    private final Property<FlightPlanComponent> currentEditedFlightPlanComponent = new Property<>();

    public MissionPlannerFunction(Activity activity, BooleanProperty isFunctionScreenOpened) {
        super(activity);
        this.isFunctionScreenOpened = isFunctionScreenOpened;
        loadMissionFileExplorer = new FileExplorer(Arrays.asList(".mef"), EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.MISSION_PLANS,false),activity, "Choose Mission Plan");
        deletePlanFileExplorer = new FileExplorer(Arrays.asList(".mef"),EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.MISSION_PLANS,false),activity, "Choose Mission Plan To Delete");

        isMissionPlannerOpened.observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                calcCurrentMissionState(currentEditedFlightPlanComponent.value() != null,newValue);
            }
        });

        currentEditedFlightPlanComponent.observe(new Observer<FlightPlanComponent>() {
            @Override
            public void observe(FlightPlanComponent oldValue, FlightPlanComponent newValue, Observation<FlightPlanComponent> observation) {
                calcCurrentMissionState(newValue != null, isMissionPlannerOpened.value());
            }
        });
    }

    private void calcCurrentMissionState(boolean isEditingComponent,boolean isMissionOpened){
        if(!isMissionOpened){
            currentState.set(MissionState.NONE);
            return;
        }

        if(isEditingComponent){
            currentState.set(MissionState.EDIT_COMPONENT);
        }
        else{
            currentState.set(MissionState.EDIT_MISSION);
        }
    }

    public CircleFlightPlanComponent addCircle(){
        CircleFlightPlanComponent newCircle = new CircleFlightPlanComponent();
        missionPlan.getFlightPlanComponents().add(newCircle);
        return newCircle;
    }

    public RadiatorFlightPlanComponent addRadiator(){
        RadiatorFlightPlanComponent newRadiator = new RadiatorFlightPlanComponent();
        missionPlan.getFlightPlanComponents().add(newRadiator);
        return newRadiator;
    }

    public MissionPlan getMissionPlan() {
        return missionPlan;
    }

    @Override
    public Drawable getFunctionDrawable() {
        return ContextCompat.getDrawable(activity, R.drawable.btn_fmode_ortho);
    }

    @Override
    public void actionMenuButtonPressed() {
        isFunctionScreenOpened.set(false);
        isMissionPlannerOpened.set(!isMissionPlannerOpened.value());
    }

    public BooleanProperty getIsAddFlightComponentOpened(){
        return isAddFlightComponentOpened;
    }

    public BooleanProperty getIsMissionPlannerOpened() {
        return isMissionPlannerOpened;
    }

    public Property<MissionState> getCurrentState() {
        return currentState;
    }

    @Override
    public SpecialFunctionType functionType() {
        return SpecialFunctionType.MISSION_PLAN;
    }

    public FileExplorer getLoadMissionFileExplorer() {
        return loadMissionFileExplorer;
    }

//    public WaypointAttributePlannerInfo getPathPlannerInfo() {
//        return pathPlannerInfo;
//    }
//
//    public Removable addMissionToMap(DroneController controller, final MapViewModel mapViewModel){
//
//        final ArrayList<Removable> removableList = new ArrayList<>();
//
//        removableList.add(
//                pathPlannerInfo.getWaypoints().observe(new CollectionObserver<Waypoint>(){
//
//                    final HashMap<Waypoint,Removable> waypointRemovableMap = new HashMap<>();
//
//                    @Override
//                    public void added(Waypoint value, Observation<Waypoint> observation) {
//                        final MapDrawable waypointDrawable = new MapDrawable(2f,pathPlannerInfo.getIsMenuOpened());
//                        waypointDrawable.location().set(value);
//                        Removable bindNameRemovable = waypointDrawable.drawable().bind(value.getWaypointName().transform(new Function<String, Drawable>() {
//                                @Override
//                                public Drawable apply(String input) {
//                                    AddComponentName nameResolver = new AddComponentName(mapViewModel.view().getResources(),input,13);
//                                    return nameResolver.apply(ContextCompat.getDrawable(mapViewModel.view().getContext(),R.drawable.waypoint));
//                                }
//                            }));
//                        mapViewModel.addMapItem(waypointDrawable);
//                        Removable removeFromMapRemovable = new Removable() {
//                                @Override
//                                public void remove() {
//                                    mapViewModel.removeMapItem(waypointDrawable);
//                                }
//                        };
//                        Removable mainRemovable = new RemovableCollection(removeFromMapRemovable,bindNameRemovable);
//                        waypointRemovableMap.put(value,mainRemovable);
//                        removableList.add(mainRemovable);
//                    }
//
//                    @Override
//                    public void removed(Waypoint value, Observation<Waypoint> observation) {
//                        Removable removable = waypointRemovableMap.remove(value);
//                        removableList.remove(removable);
//                        removable.remove();
//                    }
//                })
//        );
//
//        removableList.add(
//
//                pathPlannerInfo.getRadiatorPlans().observe(new CollectionObserver<RadiatorInfo>(){
//
//                    final HashMap<RadiatorInfo,Removable> waypointRemovableMap = new HashMap<>();
//
//                    @Override
//                    public void added(final RadiatorInfo value, Observation<RadiatorInfo> observation) {
//                        final ObservableBoolean visibility = pathPlannerInfo.getIsMenuOpened().and(pathPlannerInfo.getCurrnetPlanningMode().equalsTo(WaypointAttributePlannerInfo.PlaningMode.RADIATOR_PLAN));
//                        final MapDrawable startRadiatorDrawable = new MapDrawable(3f,visibility);
//                        startRadiatorDrawable.drawable().set(ContextCompat.getDrawable(mapViewModel.view().getContext(),R.drawable.start_radiator_waypoint));
//                        Location startLocation = value.getRadiatorWayPoints().getLocations().get(0);
//                        Removable bindNameRemovable = Removable.STUB;
//                        final Waypoint[] bindToWaypoint = new Waypoint[1];
//                        for(Waypoint waypoint : pathPlannerInfo.getWaypoints()){
//                            if(startLocation.distance(waypoint) < 3){
//                                bindToWaypoint[0] = waypoint;
//                                bindNameRemovable = startRadiatorDrawable.drawable().bind(waypoint.getWaypointName().transform(new Function<String, Drawable>() {
//                                    @Override
//                                    public Drawable apply(String input) {
//                                        AddComponentName nameResolver = new AddComponentName(mapViewModel.view().getResources(),input,13);
//                                        return nameResolver.apply(ContextCompat.getDrawable(mapViewModel.view().getContext(),R.drawable.start_radiator_waypoint));
//                                    }
//                                }));
//                                break;
//                            }
//                        }
//
//                        startRadiatorDrawable.location().set(value.getRadiatorWayPoints().getLocations().get(0));
//
//                        Removable listenerObserver = visibility.observe(new Observer<Boolean>() {
//                            @Override
//                            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
//
//                                if(newValue) {
//                                    startRadiatorDrawable.mapListener().set(new MapItem.MapClickListener() {
//                                        @Override
//                                        public void onMapClick() {
//                                            pathPlannerInfo.getChoosenRadiator().set(value);
//                                            pathPlannerInfo.getChoosenRadiatorStartingWaypoint().set(bindToWaypoint[0]);
//                                        }
//                                    });
//                                }
//                                else{
//                                    startRadiatorDrawable.mapListener().set(null);
//                                }
//                            }
//                        }).observeCurrentValue();
//
//                        mapViewModel.addMapItem(startRadiatorDrawable);
//                        Removable removeFromMapRemovable = new Removable() {
//                            @Override
//                            public void remove() {
//                                mapViewModel.removeMapItem(startRadiatorDrawable);
//                            }
//                        };
//                        Removable mainRemovable = new RemovableCollection(removeFromMapRemovable,bindNameRemovable,listenerObserver);
//                        waypointRemovableMap.put(value,mainRemovable);
//                        removableList.add(mainRemovable);
//                    }
//
//                    @Override
//                    public void removed(RadiatorInfo value, Observation<RadiatorInfo> observation) {
//                        Removable removable = waypointRemovableMap.remove(value);
//                        removableList.remove(removable);
//                        removable.remove();
//                    }
//                })
//        );
//
//        final ObservableList<Location> activeRadiator = new ObservableList<>();
//        ObservableBoolean isPlanningRadiator = pathPlannerInfo.getIsMenuOpened().and(pathPlannerInfo.getCurrnetPlanningMode().equalsTo(WaypointAttributePlannerInfo.PlaningMode.RADIATOR_PLAN));
//
//        final Removable[] activeRadiatorBinder = {Removable.STUB};
//        removableList.add(activeRadiatorBinder[0]);
//        removableList.add(
//                isPlanningRadiator.observe(new Observer<Boolean>() {
//                    @Override
//                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
//
//                        activeRadiatorBinder[0].remove();
//
//                        if(newValue){
//                            activeRadiatorBinder[0] = pathPlannerInfo.getChoosenRadiator().observe(new Observer<RadiatorInfo>() {
//                                @Override
//                                public void observe(RadiatorInfo oldValue, RadiatorInfo newValue, Observation<RadiatorInfo> observation) {
//                                    activeRadiator.clear();
//                                    if(newValue != null){
//                                        for(Location location : newValue.getRadiatorWayPoints().getLocations()){
//                                            activeRadiator.add(location);
//                                        }
//                                    }
//                                }
//                            }).observeCurrentValue();
//                        }
//                        else{
//                            activeRadiatorBinder[0] = currentMissionExecution.getRadiator().observe(new Observer<RadiatorInfo>() {
//                                @Override
//                                public void observe(RadiatorInfo oldValue, RadiatorInfo newValue, Observation<RadiatorInfo> observation) {
//                                    activeRadiator.clear();
//                                    if(newValue != null){
//                                        for(Location location : newValue.getRadiatorWayPoints().getLocations()){
//                                            activeRadiator.add(location);
//                                        }
//                                    }
//                                }
//                            }).observeCurrentValue();
//                        }
//                    }
//                }).observeCurrentValue()
//        );
//
//        final MapPolyline radiatorPolyline = new MapPolyline(activeRadiator, Color.BLUE);
//        mapViewModel.addMapItem(radiatorPolyline);
//
//        final BooleanProperty activeRadiatorExists = new BooleanProperty();
//        final Property<Location> radiatorStartLocation = new Property<>();
//        final Property<Location> radiatorEndLocation = new Property<>();
//
//        removableList.add(
//                activeRadiator.observe(new CollectionObserver<Location>(){
//                    @Override
//                    public void added(Location value, Observation<Location> observation) {
//                        activeRadiatorExists.set(activeRadiator.size() > 0);
//                        if(activeRadiator.size() > 0){
//                            try{
//                                radiatorStartLocation.set(activeRadiator.get(0));
//                                radiatorEndLocation.set(activeRadiator.get(activeRadiator.size()-1));
//                            }
//                            catch (Exception e){
//                                radiatorStartLocation.set(null);
//                                radiatorEndLocation.set(null);
//                            }
//                        }
//                        else{
//                            radiatorStartLocation.set(null);
//                            radiatorEndLocation.set(null);
//                        }
//                    }
//
//                    @Override
//                    public void removed(Location value, Observation<Location> observation) {
//                        activeRadiatorExists.set(activeRadiator.size() > 0);
//                        if(activeRadiator.size() > 0){
//                            try{
//                                radiatorStartLocation.set(activeRadiator.get(0));
//                                radiatorEndLocation.set(activeRadiator.get(activeRadiator.size()-1));
//                            }
//                            catch (Exception e){
//                                radiatorStartLocation.set(null);
//                                radiatorEndLocation.set(null);
//                            }
//                        }
//                        else{
//                            radiatorStartLocation.set(null);
//                            radiatorEndLocation.set(null);
//                        }
//                    }
//                })
//        );
//
//        final ObservableList<Location> activeStartMissionPath = new ObservableList<>();
//        ObservableBoolean isPlanningStartMissionPath = pathPlannerInfo.getIsMenuOpened().and(pathPlannerInfo.getCurrnetPlanningMode().equalsTo(WaypointAttributePlannerInfo.PlaningMode.START_MISSION_EXECUTION));
//
//        final Removable[] activeStartMissionBinder = {Removable.STUB};
//        removableList.add(
//                isPlanningStartMissionPath.observe(new Observer<Boolean>() {
//                    @Override
//                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
//
//                        activeStartMissionBinder[0].remove();
//                        if(newValue) {
//                            activeStartMissionBinder[0] = activeStartMissionPath.bindToOtherList((ObservableList<Location>) (ObservableList<?>) pathPlannerInfo.getWaypoints());
//                        }
//                        else{
//                            activeStartMissionBinder[0] = activeStartMissionPath.bindToOtherList(currentMissionExecution.getGoToMissionWaypoints());
//                        }
//                    }
//                }).observeCurrentValue()
//        );
//
//        final MapPolyline startMissionPolyline = new MapPolyline(activeStartMissionPath,Color.GREEN);
//        mapViewModel.addMapItem(startMissionPolyline);
//
//        final ObservableList<Location> activeFinishMissionPath = new ObservableList<>();
//        ObservableBoolean isPlanningFinishMissionPath = pathPlannerInfo.getIsMenuOpened().and(pathPlannerInfo.getCurrnetPlanningMode().equalsTo(WaypointAttributePlannerInfo.PlaningMode.FINISH_MISSION_EXECUTION));
//
//        final Removable[] activeFinishMissionBinder = {Removable.STUB};
//        removableList.add(
//                isPlanningFinishMissionPath.observe(new Observer<Boolean>() {
//                    @Override
//                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
//
//                        activeFinishMissionBinder[0].remove();
//                        if(newValue) {
//                            activeFinishMissionBinder[0] = activeFinishMissionPath.bindToOtherList((ObservableList<Location>) (ObservableList<?>) pathPlannerInfo.getWaypoints());
//                        }
//                        else{
//                            activeFinishMissionBinder[0] = activeFinishMissionPath.bindToOtherList(currentMissionExecution.getEndMissionWaypoints());
//                        }
//                    }
//                }).observeCurrentValue()
//        );
//
//        final MapPolyline finishMissionPolyline = new MapPolyline(activeFinishMissionPath,Color.GREEN);
//        mapViewModel.addMapItem(finishMissionPolyline);
//
//        final BooleanProperty activeStartPathExists = new BooleanProperty();
//        final Property<Location> startPathStartLocation = new Property<>();
//        final Property<Location> startPathEndLocation = new Property<>();
//
//        removableList.add(
//                activeStartMissionPath.observe(new CollectionObserver<Location>(){
//                    @Override
//                    public void added(Location value, Observation<Location> observation) {
//                        activeStartPathExists.set(activeStartMissionPath.size() > 0);
//                        if(activeStartMissionPath.size() > 0){
//                            try{
//                                startPathStartLocation.set(activeStartMissionPath.get(0));
//                                startPathEndLocation.set(activeStartMissionPath.get(activeStartMissionPath.size()-1));
//                            }
//                            catch (Exception e){
//                                startPathStartLocation.set(null);
//                                startPathEndLocation.set(null);
//                            }
//                        }
//                        else{
//                            startPathStartLocation.set(null);
//                            startPathEndLocation.set(null);
//                        }
//                    }
//
//                    @Override
//                    public void removed(Location value, Observation<Location> observation) {
//                        activeStartPathExists.set(activeStartMissionPath.size() > 0);
//                        if(activeStartMissionPath.size() > 0){
//                            try{
//                                startPathStartLocation.set(activeStartMissionPath.get(0));
//                                startPathEndLocation.set(activeStartMissionPath.get(activeStartMissionPath.size()-1));
//                            }
//                            catch (Exception e){
//                                startPathStartLocation.set(null);
//                                startPathEndLocation.set(null);
//                            }
//                        }
//                        else{
//                            startPathStartLocation.set(null);
//                            startPathEndLocation.set(null);
//                        }
//                    }
//                })
//        );
//
//        final BooleanProperty activeFinishPathExists = new BooleanProperty();
//        final Property<Location> finishPathStartLocation = new Property<>();
//        final Property<Location> finishPathEndLocation = new Property<>();
//
//        removableList.add(
//                activeFinishMissionPath.observe(new CollectionObserver<Location>(){
//                    @Override
//                    public void added(Location value, Observation<Location> observation) {
//                        activeFinishPathExists.set(activeFinishMissionPath.size() > 0);
//                        if(activeFinishMissionPath.size() > 0){
//                            try{
//                                finishPathStartLocation.set(activeFinishMissionPath.get(0));
//                                finishPathEndLocation.set(activeFinishMissionPath.get(activeFinishMissionPath.size()-1));
//                            }
//                            catch (Exception e){
//                                finishPathStartLocation.set(null);
//                                finishPathEndLocation.set(null);
//                            }
//                        }
//                        else{
//                            finishPathStartLocation.set(null);
//                            finishPathEndLocation.set(null);
//                        }
//                    }
//
//                    @Override
//                    public void removed(Location value, Observation<Location> observation) {
//                        activeFinishPathExists.set(activeFinishMissionPath.size() > 0);
//                        if(activeFinishMissionPath.size() > 0){
//                            try{
//                                finishPathStartLocation.set(activeFinishMissionPath.get(0));
//                                finishPathEndLocation.set(activeFinishMissionPath.get(activeFinishMissionPath.size()-1));
//                            }
//                            catch (Exception e){
//                                finishPathStartLocation.set(null);
//                                finishPathEndLocation.set(null);
//                            }
//                        }
//                        else{
//                            finishPathStartLocation.set(null);
//                            finishPathEndLocation.set(null);
//                        }
//                    }
//                })
//        );
//
//        final MapLine connectStartPathToRadiatorDrawable = new MapLine();
//        connectStartPathToRadiatorDrawable.getColor().set(Color.YELLOW);
//        removableList.add(connectStartPathToRadiatorDrawable.getStartPoint().bind(startPathEndLocation));
//        removableList.add(connectStartPathToRadiatorDrawable.getEndPoint().bind(radiatorStartLocation));
//        mapViewModel.addMapItem(connectStartPathToRadiatorDrawable);
//
//        final MapLine connectFinishPathToRadiatorDrawable = new MapLine();
//        connectFinishPathToRadiatorDrawable.getColor().set(Color.YELLOW);
//        removableList.add(connectFinishPathToRadiatorDrawable.getStartPoint().bind(radiatorEndLocation));
//        removableList.add(connectFinishPathToRadiatorDrawable.getEndPoint().bind(finishPathStartLocation));
//        mapViewModel.addMapItem(connectFinishPathToRadiatorDrawable);
//
//        final MapPolyline radiatorAnchorPolylineUnderPlan = new MapPolyline((ObservableList<Location>) (ObservableList<?>)pathPlannerInfo.getWaypoints(),Color.GREEN);
//        radiatorAnchorPolylineUnderPlan.getIsClosedShape().set(true);
//        removableList.add(radiatorAnchorPolylineUnderPlan.getVisibility().bind(isPlanningRadiator));
//
////        removableList.add(radiatorAnchorPolylineUnderPlan.getColor().bind(pathPlannerInfo.getChoosenRadiator().notNull().toggle(Color.GREEN,Color.RED)));
//        mapViewModel.addMapItem(radiatorAnchorPolylineUnderPlan);
//
//        final MapDrawable radiatorStartPointDrawable = new MapDrawable(4F);
//        radiatorStartPointDrawable.drawable().set(ContextCompat.getDrawable(mapViewModel.view().getContext(),R.drawable.start));
//
//        final Property<Location> missionStartLocation = new Property<>();
//
//        removableList.add(
//                radiatorStartLocation.observe(new Observer<Location>() {
//                    @Override
//                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                        Location goToMissionStartLocation = startPathStartLocation.value();
//                        Location radiatorFirstPoint = radiatorStartLocation.value();
//
//                        if(goToMissionStartLocation != null){
//                            missionStartLocation.set(goToMissionStartLocation);
//                        }
//                        else{
//                            missionStartLocation.set(radiatorFirstPoint);
//                        }
//                    }
//                })
//        );
//        removableList.add(
//                startPathStartLocation.observe(new Observer<Location>() {
//                    @Override
//                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                        Location goToMissionStartLocation = startPathStartLocation.value();
//                        Location radiatorFirstPoint = radiatorStartLocation.value();
//
//                        if(goToMissionStartLocation != null){
//                            missionStartLocation.set(goToMissionStartLocation);
//                        }
//                        else{
//                            missionStartLocation.set(radiatorFirstPoint);
//                        }
//                    }
//                })
//        );
//
//        removableList.add(radiatorStartPointDrawable.location().bind(missionStartLocation));
//
//        final MapDrawable radiatorEndPointDrawable = new MapDrawable(4F);
//        radiatorEndPointDrawable.drawable().set(ContextCompat.getDrawable(mapViewModel.view().getContext(),R.drawable.finish));
//
//        final Property<Location> missionFinishLocation = new Property<>();
//
//        removableList.add(
//                radiatorEndLocation.observe(new Observer<Location>() {
//                    @Override
//                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                        Location endMissionEndLocation = finishPathEndLocation.value();
//                        Location radiatorLastPoint = radiatorEndLocation.value();
//
//                        if(endMissionEndLocation != null){
//                            missionFinishLocation.set(endMissionEndLocation);
//                        }
//                        else{
//                            missionFinishLocation.set(radiatorLastPoint);
//                        }
//                    }
//                })
//        );
//        removableList.add(
//                finishPathEndLocation.observe(new Observer<Location>() {
//                    @Override
//                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                        Location endMissionEndLocation = finishPathEndLocation.value();
//                        Location radiatorLastPoint = radiatorEndLocation.value();
//
//                        if(endMissionEndLocation != null){
//                            missionFinishLocation.set(endMissionEndLocation);
//                        }
//                        else{
//                            missionFinishLocation.set(radiatorLastPoint);
//                        }
//                    }
//                })
//        );
//
//        removableList.add(radiatorEndPointDrawable.location().bind(missionFinishLocation));
//
//        mapViewModel.addMapItem(radiatorStartPointDrawable);
//        mapViewModel.addMapItem(radiatorEndPointDrawable);
//
//        removableList.add(new Removable() {
//            @Override
//            public void remove() {
//                mapViewModel.removeMapItem(radiatorPolyline);
//                mapViewModel.removeMapItem(radiatorStartPointDrawable);
//                mapViewModel.removeMapItem(radiatorEndPointDrawable);
//                mapViewModel.removeMapItem(startMissionPolyline);
//                mapViewModel.removeMapItem(finishMissionPolyline);
//                mapViewModel.removeMapItem(connectStartPathToRadiatorDrawable);
//                mapViewModel.removeMapItem(radiatorAnchorPolylineUnderPlan);
//            }
//        });
//
//        return new RemovableCollection(removableList);
//    }


    public FileExplorer getDeletePlanFileExplorer() {
        return deletePlanFileExplorer;
    }

    public BooleanProperty getIsRelocateMarked() {
        return isRelocateMarked;
    }

    public Property<FlightPlanComponent> getCurrentEditedFlightPlanComponent() {
        return currentEditedFlightPlanComponent;
    }
}
