//package eyesatop.unit.ui.missions;
//
//import java.util.List;
//
//import eyesatop.controller.mission.MissionRow;
//import eyesatop.controller.tasks.exceptions.DroneTaskException;
//import eyesatop.unit.ui.models.generic.ViewModel;
//import eyesatop.unit.ui.models.map.MapViewModel;
//import eyesatop.util.Removable;
//import eyesatop.util.geo.Location;
//import eyesatop.util.model.BooleanProperty;
//import eyesatop.util.model.ObservableBoolean;
//import eyesatop.util.model.ObservableValue;
//import eyesatop.util.model.Property;
//import eyesatop.util.snapshots.SnapshotableObject;
//
///**
// * Created by Idan on 27/11/2017.
// */
//
//public abstract class FlightPlanComponent {
//
//    private final Property<String> componentName = new Property<>();
//    private final BooleanProperty visibleOnMap = new BooleanProperty();
//    private final Property<Location> centerLocation = new Property<>();
//
//    private final Property<Location> startLocation = new Property<>();
//    private final Property<Location> endLocation = new Property<>();
//
//    public Property<String> componentName(){
//        return componentName;
//    }
//
//    public abstract Removable addToMap(MapViewModel mapViewModel);
//    public abstract Removable activeBinds();
//
//    public BooleanProperty visibleInMap(){
//        return visibleOnMap;
//    }
//
//    public ObservableValue<Location> center(){
//        return centerLocation;
//    }
//
//    public abstract void relocate(Location newCenter) throws DroneTaskException;
//
//    public ObservableValue<Location> startLocation(){
//        return startLocation;
//    }
//
//    public ObservableValue<Location> endLocation(){
//        return endLocation;
//    }
//
//    public abstract List<MissionRow.MissionRowStub> getMissionRows() throws DroneTaskException;
//
//    void startEdit();
//    void restoreToLastSnapshot() throws DroneTaskException;
//}
