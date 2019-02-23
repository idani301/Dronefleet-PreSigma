//package eyesatop.unit.ui.missions;
//
//import android.support.v4.content.ContextCompat;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//
//import eyesatop.controller.mission.MissionRow;
//import eyesatop.controller.tasks.exceptions.DroneTaskException;
//import eyesatop.unit.ui.R;
//import eyesatop.unit.ui.models.map.MapDrawable;
//import eyesatop.unit.ui.models.map.MapItem;
//import eyesatop.unit.ui.models.map.MapLine;
//import eyesatop.unit.ui.models.map.MapViewModel;
//import eyesatop.util.Removable;
//import eyesatop.util.RemovableCollection;
//import eyesatop.util.geo.Location;
//import eyesatop.util.model.BooleanProperty;
//import eyesatop.util.model.CollectionObserver;
//import eyesatop.util.model.ObservableList;
//import eyesatop.util.model.ObservableValue;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//import eyesatop.util.snapshots.SnapshotObject;
//import eyesatop.util.snapshots.SnapshotableObject;
//
///**
// * Created by Idan on 13/12/2017.
// */
//public class FlightPlan implements FlightPlanComponent {
//
//    private final Property<FlightPlanSnapshot> snapshot = new Property<>();
//
//    private final BooleanProperty isRelocating = new BooleanProperty(false);
//    private final BooleanProperty visibleInMap = new BooleanProperty(false);
//
//    private final Property<String> name = new Property<>();
//    private final ObservableList<FlightPlanComponent> components = new ObservableList<>();
//
//    private final HashMap<FlightPlanComponent,Removable> componentBindings = new HashMap<>();
//    private final Property<Location> startLocation = new Property<>();
//    private Removable startLocationRemovable = Removable.STUB;
//
//    private final Property<Location> endLocation = new Property<>();
//    private Removable endLocationRemovable = Removable.STUB;
//
//    private final Property<Location> center = new Property<>();
//
//    public Removable activeBinds(){
//        ArrayList<Removable> removableList = new ArrayList<>();
//
//        removableList.add(
//                components.observe(new CollectionObserver<FlightPlanComponent>(){
//                    @Override
//                    public void added(FlightPlanComponent value, Observation<FlightPlanComponent> observation) {
//                        if(components.get(0).equals(value)){
//                            startLocationRemovable.remove();
//                            startLocationRemovable = startLocation.bind(value.startLocation());
//                        }
//                        endLocationRemovable.remove();
//                        endLocationRemovable = endLocation.bind(value.endLocation());
//                        componentBindings.put(value,value.center().observe(new Observer<Location>() {
//                            @Override
//                            public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                                calcCenter();
//                            }
//                        }).observeCurrentValue());
//                    }
//
//                    @Override
//                    public void removed(FlightPlanComponent value, Observation<FlightPlanComponent> observation) {
//                        endLocationRemovable.remove();
//                        if(components.size() == 0){
//                            startLocationRemovable.remove();
//                            startLocationRemovable = Removable.STUB;
//                            endLocationRemovable = Removable.STUB;
//                        }
//                        else{
//                            endLocationRemovable = endLocation.bind(components.get(components.size()-1).endLocation());
//                        }
//                        componentBindings.remove(value).remove();
//                        calcCenter();
//                    }
//                })
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
//        final MapDrawable centerPoint = new MapDrawable(MapItem.Z_INDEX_HIGH,isRelocating.and(visibleInMap));
//        centerPoint.drawable().set(ContextCompat.getDrawable(mapViewModel.view().getContext(), R.drawable.x_mark));
//
//        removableList.add(centerPoint.location().bind(center));
//
//        final MapDrawable startPointDrawable = new MapDrawable(4F,visibleInMap);
//        startPointDrawable.drawable().set(ContextCompat.getDrawable(mapViewModel.view().getContext(),R.drawable.start));
//        removableList.add(startPointDrawable.location().bind(startLocation()));
//
//
//        final MapDrawable endPointDrawable = new MapDrawable(4F,visibleInMap);
//        endPointDrawable.drawable().set(ContextCompat.getDrawable(mapViewModel.view().getContext(),R.drawable.finish));
//        removableList.add(endPointDrawable.location().bind(endLocation()));
//
//        mapViewModel.addMapItem(centerPoint);
//        mapViewModel.addMapItem(startPointDrawable);
//        mapViewModel.addMapItem(endPointDrawable);
//
//        ObservableList<MapLine> connectionLines = new ObservableList<>();
//
//        components.observe(new CollectionObserver<FlightPlanComponent>(){
//
//
//            @Override
//            public void added(FlightPlanComponent value, Observation<FlightPlanComponent> observation) {
//
//                if(components.size() > 1) {
//                    final MapLine newMapLine = new MapLine();
//
//                    ArrayList<Removable> newMapLineRemovables = new ArrayList<Removable>();
//                    newMapLineRemovables.add(newMapLine.getEndPoint().bind(value.startLocation()));
//                    newMapLineRemovables.add(newMapLine.getStartPoint().bind(components.get(components.size() - 2).endLocation()));
//                    mapViewModel.addMapItem(newMapLine);
//                    newMapLineRemovables.add(new Removable() {
//                        @Override
//                        public void remove() {
//                            mapViewModel.removeMapItem(newMapLine);
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void removed(FlightPlanComponent value, Observation<FlightPlanComponent> observation) {
//
//            }
//        });
//
//        removableList.add(new Removable() {
//            @Override
//            public void remove() {
//                mapViewModel.removeMapItem(centerPoint);
//                mapViewModel.removeMapItem(startPointDrawable);
//                mapViewModel.removeMapItem(endPointDrawable);
//            }
//        });
//
//
//
//        return new RemovableCollection(removableList);
//    }
//
//    @Override
//    public BooleanProperty visibleInMap() {
//        return visibleInMap;
//    }
//
//    @Override
//    public ObservableValue<Location> center() {
//        return center;
//    }
//
//    public void relocate(Location newCenter) throws DroneTaskException {
//
//        Location currentCenter = center.value();
//
//        if(currentCenter == null || newCenter == null){
//            throw new DroneTaskException("Illegal relocate. center is null");
//        }
//
//        double distanceFromNewCenter = currentCenter.distance(newCenter);
//        double azToNewCenter = currentCenter.az(newCenter);
//
//        for(FlightPlanComponent component : components){
//            component.relocate(component.center().value().getLocationFromAzAndDistance(distanceFromNewCenter,azToNewCenter));
//        }
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
//        for(FlightPlanComponent component : components){
//            List<MissionRow.MissionRowStub> componentRows = component.getMissionRows();
//            missionRows.addAll(componentRows);
//        }
//
//        return missionRows;
//    }
//
//    @Override
//    public void startEdit() {
//        snapshot.set(FlightPlanSnapshot.createFromFlightPlan(this));
//    }
//
//    @Override
//    public void restoreToLastSnapshot() throws DroneTaskException {
//        FlightPlanSnapshot currentSnapshot = snapshot.value();
//        if(currentSnapshot != null){
//            loadFromSnapshot(currentSnapshot);
//        }
//    }
//
//    private void loadFromSnapshot(FlightPlanSnapshot snapshot){
//    }
//
//    private void calcCenter(){
//
//        if(components.size() == 0){
//            center.set(null);
//            return;
//        }
//
//        double latSum = 0;
//        double lonSum = 0;
//
//        for(FlightPlanComponent component : components){
//            Location componentCenter = component.center().value();
//            latSum += componentCenter.getLatitude();
//            lonSum += componentCenter.getLongitude();
//        }
//
//        Location newCenter = new Location(latSum/(double)components.size(),lonSum/(double)components.size());
//        center.set(newCenter);
//    }
//}
