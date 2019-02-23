package eyesatop.unit.ui.models;

import android.app.Activity;
import android.view.MotionEvent;
import android.widget.ImageView;

import eyesatop.controller.DroneController;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.DroneTabsModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 19/09/2017.
 */

public class MapFocusButtonsModel {

    private Removable bindings = Removable.STUB;

    private final ImageViewModel mainButton;
    private final ImageViewModel droneFocusButton;
    private final ImageViewModel userFocusButton;
    private final ImageViewModel homeFocusButton;

    private final DroneTabsModel tabsModel;
    private final LittleMessageViewModel littleMessages;

    private final ViewModel buttonsMenu;

    private final Property<Location> focusRequests;
    private final Property<Location> myLocation;

    private DroneController currentController;

    public MapFocusButtonsModel(Activity activity, DroneTabsModel tabsModel,Property<Location> focusRequest,LittleMessageViewModel littleMessages, Property<Location> myLocation){

        this.focusRequests = focusRequest;
        mainButton = new ImageViewModel((ImageView) activity.findViewById(R.id.openMapButtons));
        droneFocusButton = new ImageViewModel((ImageView) activity.findViewById(R.id.mapDroneLocationFocus));
        userFocusButton = new ImageViewModel((ImageView) activity.findViewById(R.id.mapSelfLocationFocus));
        homeFocusButton = new ImageViewModel((ImageView) activity.findViewById(R.id.mapHomeLocationFocus));
        buttonsMenu = new ViewModel(activity.findViewById(R.id.mapControllerButtons));

        buttonsMenu.visibility().set(ViewModel.Visibility.GONE);

        this.tabsModel = tabsModel;
        this.littleMessages = littleMessages;
        this.myLocation = myLocation;

        userFocusButton.visibility().bind(myLocation.notNull().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));

        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
                bindToTab(newValue);
            }
        }).observeCurrentValue();

        setSingleTaps();
    }

    private void bindToTab(DroneTabModel tabModel){

        bindings.remove();
        buttonsMenu.visibility().set(ViewModel.Visibility.GONE);

        if(tabModel == null){
            currentController = null;
            bindings = Removable.STUB;
            droneFocusButton.visibility().set(ViewModel.Visibility.GONE);
            homeFocusButton.visibility().set(ViewModel.Visibility.GONE);
        }
        else{

            currentController = tabModel.getDroneController();

            bindings = new RemovableCollection(

                    droneFocusButton.visibility().
                            bind(currentController.telemetry().notNull().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),

                    homeFocusButton.visibility().
                            bind(currentController.droneHome().homeLocation().notNull().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE))
                    );
        }
    }

    private void setSingleTaps(){
        mainButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                buttonsMenu.visibility().set(buttonsMenu.visibility().value() == ViewModel.Visibility.VISIBLE ? ViewModel.Visibility.GONE : ViewModel.Visibility.VISIBLE);
                return false;
            }
        });

        userFocusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                buttonsMenu.visibility().set(ViewModel.Visibility.GONE);

                Location userLocation = myLocation.value();

                if(userLocation == null){
                    littleMessages.addNewMessage("Unknown user location");
                    return false;
                }

                focusRequests.set(userLocation);
                return false;
            }
        });

        droneFocusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                buttonsMenu.visibility().set(ViewModel.Visibility.GONE);

                if(currentController == null){
                    littleMessages.addNewMessage("Can't focus to drone, Has no drone");
                    return false;
                }

                Telemetry droneTelemetry = currentController.telemetry().value();
                Location droneLocation = droneTelemetry == null ? null : droneTelemetry.location();

                if(droneLocation == null){
                    littleMessages.addNewMessage("Unknown drone location");
                    return false;
                }

                focusRequests.set(droneLocation);
                return false;
            }
        });


        homeFocusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                buttonsMenu.visibility().set(ViewModel.Visibility.GONE);

                if(currentController == null){
                    littleMessages.addNewMessage("Can't focus to drone home, Has no drone");
                    return false;
                }

                Location homeLocation = currentController.droneHome().homeLocation().value();

                if(homeLocation == null){
                    littleMessages.addNewMessage("Unknown drone home location");
                    return false;
                }

                focusRequests.set(homeLocation);
                return false;
            }
        });
    }
}
