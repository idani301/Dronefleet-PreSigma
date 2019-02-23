package eyesatop.unit.ui.models.tabs;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.BatteryState;
import eyesatop.unit.ui.DrawableIdBackground;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Property;

public class DroneTabModel extends AbstractViewModel<View> implements DroneTab{

    public static DroneTabModel forController(DroneController controller, MapViewModel mapViewModel, String label, View view,MessageViewModel messageViewModel,LittleMessageViewModel littleMessageViewModel, DtmProvider dtmProvider) {

        DroneTabModel model = new DroneTabModel(view,mapViewModel,controller,messageViewModel,littleMessageViewModel,dtmProvider);

        model.label().set(label);

        model.batteryPercent().bind(controller.droneBattery()
                .transform(new Function<BatteryState, Float>() {
                    @Override
                    public Float apply(BatteryState input) {
                        if (input == null) {
                            return null;
                        }
                        return input.getCurrent() / (float)input.getMax();
                    }
                }));

        model.droneLocation().bind(controller.telemetry());

        return model;
    }

    private Removable bindings = Removable.STUB;
    private final BooleanProperty selected = new BooleanProperty();
    private final Property<Float> batteryPercent = new Property<>();
    private final Property<String> label = new Property<>();
    private final Property<Telemetry> droneLocation = new Property<>();

    private final UiDroneTasks droneTasks;
    private final DroneController droneController;
    private final FunctionsModel functionsModel;
    private final BatteryBarModel batteryModel;
    private final TextViewModel labelModel;
    private final BooleanProperty isMcMenuOpened = new BooleanProperty(false);
    private final MapViewModel mapViewModel;

    private DroneTabModel(View view, MapViewModel mapViewModel, final DroneController droneController, MessageViewModel messageViewModel, LittleMessageViewModel littleMessageViewModel, final DtmProvider dtmProvider) {
        super(view);
        this.mapViewModel = mapViewModel;
        ArrayList<Removable> removableList = new ArrayList<>();

        batteryModel = new BatteryBarModel(view.findViewById(R.id.battery));
        labelModel = new TextViewModel((TextView)view.findViewById(R.id.name));
        labelModel.textColor().set(Color.WHITE);

        this.droneController = droneController;

        functionsModel = new FunctionsModel(mapViewModel,droneController,messageViewModel,littleMessageViewModel,(Activity) view.getContext());

        removableList.add(super.background()
                .bind(selected.toggle(R.color.status_bar, R.color.background)
                        .withDefault(R.color.background)
                        .transform(DrawableIdBackground.WRAP)));

        removableList.add(batteryModel.percent().bind(batteryPercent.withDefault(0f)));
        removableList.add(batteryModel.visibility()
                .bind(batteryPercent.equalsTo(null)
                        .toggle(Visibility.INVISIBLE,
                                Visibility.VISIBLE)));

        removableList.add(labelModel.text().bind(label));

        droneTasks = new UiDroneTasks(droneController,littleMessageViewModel);

//        removableList.add(functionsModel.getMissionPlannerFunction().addMissionToMap(droneController,mapViewModel));
        bindings = new RemovableCollection(removableList);
    }

    public UiDroneTasks getDroneTasks() {
        return droneTasks;
    }

    public DroneController getDroneController() {
        return droneController;
    }

    public BooleanProperty selected() {
        return selected;
    }

    public Property<Float> batteryPercent() {
        return batteryPercent;
    }

    public Property<Telemetry> droneLocation(){ return droneLocation; }

    public Property<String> label() {
        return label;
    }

    public FunctionsModel getFunctionsModel() {
        return functionsModel;
    }


    public void shutdown() {
        droneTasks.shutdown();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DroneTabModel that = (DroneTabModel) o;

        return droneController.uuid().equals(that.droneController.uuid());
    }

    public void removeBindings(){
        bindings.remove();
        bindings = Removable.STUB;
//        getFunctionsModel().getMissionPlannerFunction().getPathPlannerInfo().unbind();
    }

    public BooleanProperty getIsMcMenuOpened() {
        return isMcMenuOpened;
    }

    @Override
    public int hashCode() {
        return droneController.hashCode();
    }

}
