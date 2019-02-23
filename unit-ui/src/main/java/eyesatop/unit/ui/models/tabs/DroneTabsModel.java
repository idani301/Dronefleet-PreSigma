package eyesatop.unit.ui.models.tabs;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eyesatop.controller.DroneController;
import eyesatop.unit.DroneUnit;
import eyesatop.unit.ui.ComponentNameResolver;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.ViewGroupModel;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

public class DroneTabsModel extends AbstractViewModel<View> implements ComponentNameResolver {

    public static DroneTabsModel forUnit(DroneUnit unit, MapViewModel mapViewModel, View view,final MessageViewModel messageViewModel, final LittleMessageViewModel littleMessageViewModel, DtmProvider dtmProvider) {
        final DroneTabsModel tabs = new DroneTabsModel(view,dtmProvider, mapViewModel);

        unit.controllers().observe(new CollectionObserver<DroneController>(){
            @Override
            public void added(DroneController value, Observation<DroneController> observation) {
                tabs.addItem(value,messageViewModel,littleMessageViewModel);
            }

            @Override
            public void removed(DroneController value, Observation<DroneController> observation) {
                tabs.removeItem(value);
            }
        },UI_EXECUTOR);


        for (DroneController component : unit.controllers()) {
            tabs.addItem(component,messageViewModel,littleMessageViewModel);
        }

        if (tabs.selected().isNull() && tabs.items().size() > 0) {
            tabs.selected().set(tabs.items().get(0));
        }
        return tabs;
    }

    private final Property<DroneTabModel> selected = new Property<>();
    private final ObservableList<DroneTabModel> items = new ObservableList<>();
    private final Property<Location> focusRequests;

    private final Map<UUID,String> nameMapping = new HashMap<>();
    private final Map<UUID,DroneTabModel> tabMapping = new HashMap<>();
    int seed = 0;

    private final BooleanProperty showTabsForOneDrone = new BooleanProperty(false);
    private final ViewGroupModel tabListModel;
    private final DtmProvider dtmProvider;
    private final MapViewModel mapViewModel;

    private DroneTabsModel(View view,DtmProvider dtmProvider, MapViewModel mapViewModel) {
        super(view);
        this.focusRequests = mapViewModel.focusRequests();
        this.dtmProvider = dtmProvider;
        tabListModel = new ViewGroupModel((ViewGroup)view.findViewById(R.id.tabs));
        this.mapViewModel = mapViewModel;

        showTabsForOneDrone.observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                updateTabsVisibility();
            }
        });

        items.observe(new CollectionObserver<DroneTabModel>() {
            @Override
            public void added(final DroneTabModel value, Observation<DroneTabModel> observation) {

                updateTabsVisibility();

                value.singleTap().set(new Function<MotionEvent, Boolean>() {
                    @Override
                    public Boolean apply(MotionEvent input) {
                        if(!value.equals(selected.value())){
                            selected.set(value);
                        }
                        else{
                            if(!value.droneLocation().isNull() && value.droneLocation().value().location() != null){
                                DroneTabsModel.this.focusRequests.set(value.droneLocation().value().location());
                            }
                        }
                        return true;
                    }
                });

                final RemovableCollection removables = new RemovableCollection(
                        value.selected()
                                .bind(selected.equalsTo(value)),

                        new Removable() {
                            @Override
                            public void remove() {
                                value.singleTap().set(null);
                            }
                        }
                );

                items.observe(new Observer<DroneTabModel>() {
                    @Override
                    public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
                        if (value == oldValue) {
                            observation.remove();
                            removables.remove();
                        }
                    }
                });
                tabListModel.children().add(value);

                if(items.size() == 1){
                    selected.set(value);
                }
            }

            @Override
            public void removed(DroneTabModel value, Observation<DroneTabModel> observation) {

                if(value.selected().value()){
                    if(items.size() > 0){
                        selected.set(items.get(0));
                    }
                    else{
                        selected.set(null);
                    }
                }

                tabListModel.children().remove(value);
                updateTabsVisibility();
            }

            @Override
            public void replaced(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
                updateTabsVisibility();
                tabListModel.children().set(tabListModel.children().indexOf(oldValue), newValue);
            }
        });
    }

    public void updateTabsVisibility(){

        int showThreshold = 2;
        if(showTabsForOneDrone.value()){
            showThreshold = 1;
        }

        Visibility tabsVisibility = items.size() >= showThreshold ? Visibility.VISIBLE : Visibility.GONE;

        for(DroneTabModel drone : items){
            drone.visibility().set(tabsVisibility);
        }
    }

    public ObservableList<DroneTabModel> items() {
        return items.unmodifiable();
    }

    public DroneTabModel addItem(DroneController newController,MessageViewModel messageViewModel, LittleMessageViewModel littleMessageViewModel) {

        View view = tabListModel.inflate(R.layout.drone_tab);

        String name;
        if (nameMapping.containsKey(newController.uuid())) {
            name = nameMapping.get(newController.uuid());
        } else {
            name = String.valueOf((char)('A' + seed++));
        }

        final DroneTabModel tab = DroneTabModel.forController(newController,mapViewModel,name, view,messageViewModel,littleMessageViewModel,dtmProvider);

        nameMapping.put(newController.uuid(), name);
        tabMapping.put(newController.uuid(), tab);

        items.add(tab);
        return tab;
    }

    public DroneTabModel getItem(eyesatop.controller.DroneController controller) {
        return tabMapping.get(controller.uuid());
    }

    public boolean removeItem(DroneController component) {

        tabMapping.remove(component.uuid());
        for(DroneTabModel tabModel : items()){
            if(tabModel.getDroneController().uuid().equals(component.uuid())){
                items.remove(tabModel);
                tabModel.removeBindings();
                tabModel.shutdown();
                return true;
            }
        }

        return false;
    }

    public UUID getControllerUuid(DroneTabModel tab) {
        for (Map.Entry<UUID, DroneTabModel> entry : tabMapping.entrySet()) {
            if (entry.getValue() == tab) {
                return entry.getKey();
            }
        }
        return null;
    }

    public Property<DroneTabModel> selected() {
        return selected;
    }

    @Override
    public String resolve(UUID uuid) {

        if(!nameMapping.containsKey(uuid)){
            return "NA";
        }

        return nameMapping.get(uuid);
    }

    public DroneTabModel getByControllerUUID(UUID controllerUUID){
        for(DroneTabModel tabModel : items){
            try{
                if(tabModel.getDroneController().uuid().equals(controllerUUID)){
                    return tabModel;
                }
            }
            catch (Exception e){
            }
        }

        return null;
    }

    public BooleanProperty getShowTabsForOneDrone() {
        return showTabsForOneDrone;
    }

    @Override
    public Property<Location> focusRequests() {
        return focusRequests;
    }
}
