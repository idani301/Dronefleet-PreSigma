package eyesatop.unit.ui.models.actionmenus;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.LocationFix;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.DroneController;
import eyesatop.controller.GimbalRequest;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.beans.MediaStorage;
import eyesatop.controller.tasks.ConfirmationData;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.imageprocess.DetectionData;
import eyesatop.imageprocess.android.ImageprocessAnyvisionAndroid;
import eyesatop.unit.DroneUnit;
import eyesatop.unit.exceptions.ComponentNotFoundException;
import eyesatop.unit.tasks.AnytargetAction;
import eyesatop.unit.tasks.Swap;
import eyesatop.unit.tasks.UnitTaskType;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.activities.EyesatopAppConfiguration;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.unit.ui.models.specialfunctions.SpecialFunction;
import eyesatop.unit.ui.models.tabs.DroneTab;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.DroneTabsModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.DistanceUnitType;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.videoclicks.VideoClickInfo;

import static eyesatop.controller.tasks.TaskStatus.FINISHED;
import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

//import eyesatop.imageprocess.android.ImageprocessAnyvisionAndroid;

enum CameraButtonModes {
    START_RECORD,
    STOP_RECORD,
    START_RECORD_SD_FULL,
    STILLS,
    STILLS_SD_FULL_DISABLED,
    STILLS_DISABLED,
    STILLS_SHOOTING_PHOTOS,
    UNKNOWN;
}

public class DroneActionMenu {

    public enum ActionMenuButtonsType {
        STOP_UNIT,
        STOP_GIMBAL,
        STOP_FLIGHT,
        ROTATE_GIMBAL,
        CONFIRM_LAND,
        GO_HOME,
        RECORD_TIME,
        FLY_TO,
        SWAP,
        RIGHT_LAYOUT,
        CAMERA_ACTION,
        CAMERA_MODE,
        LOCATION_FIX,
        LOOK_AT_POINT,
        LOCK_LOOK_AT_POINT,
        EXPLORE,
        SPECIAL_FUNCTIONS,
        TAKE_OFF,
        COORDINATE_BUTTON,
        ANYVISION_BUTTON,
        INTERNAL_RECORD;
    }

    private final Activity activity;
    private final MessageViewModel messageViewModel;
    private final LittleMessageViewModel littleMessageViewModel;

    private final ActionMenuItemModel takeOffButton;
    private final ActionMenuItemModel landButton;
    private final ActionMenuItemModel flyToButton;
    private final ActionMenuItemModel lookAtPointButton;
    private final ActionMenuItemModel rotateGimbalButton;
    private final ActionMenuItemModel specialFunctionButton;
    private final ActionMenuItemModel swapButton;
    private final ObservableValue<Location> crosshairLocation;

    private final ImageViewModel stopUnitButton;
    private final ImageViewModel stopFlightButton;
    private final ImageViewModel confirmLandButton;
    private final ActionMenuItemModel stopGimbalButton;
    private final ActionMenuItemModel lockGimbalButton;
    private final ActionMenuItemModel exploreButton;

    private final ActionMenuItemModel anyvisionButton;

    private final ActionMenuItemModel locationFixButton;
    private final ViewModel sticksView;
    private final ImageViewModel upStick;
    private final ImageViewModel downStick;
    private final ImageViewModel leftStick;
    private final ImageViewModel rightStick;

    private final HashMap<DroneController,Property<VideoClickInfo>> videoClickedLocations;

    private final ImageViewModel powerButton;
    private final ImageViewModel internalRecordButton;

    private final ImageViewModel gimbalPitchUpStick;
    private final ImageViewModel gimbalPitchDownStick;

    private final ImageViewModel coordinateProviderButton;

    private final DtmProvider dtmProvider;

    private final TextViewModel recordTimeText;
    private final ImageViewModel cameraActionButton;
    private final ActionMenuItemModel cameraToVideButton;

    private final BooleanProperty fullScreenVideo;

    private final DecimalFormat formatter = new DecimalFormat("00");

    private DroneTab tabModel;
    private DroneController controller;

    private final ExecutorService stamExecutor = Executors.newSingleThreadExecutor();

    private final DroneUnit unit;
    private final DroneTabsModel tabsModel;

    private final BooleanProperty hasMultiDrones = new BooleanProperty(false);

    public void showAllButtons(){

        for(ActionMenuButtonsType type : ActionMenuButtonsType.values()){
            showButton(type,false);
        }
    }

    public void showButton(ActionMenuButtonsType type, boolean isAlwaysGone){
        switch (type){

            case CONFIRM_LAND:
                confirmLandButton.setAlwaysGone(isAlwaysGone);
                break;
            case STOP_UNIT:
                stopUnitButton.setAlwaysGone(isAlwaysGone);
                break;
            case STOP_GIMBAL:
                stopGimbalButton.setAlwaysGone(isAlwaysGone);
                break;
            case STOP_FLIGHT:
                stopFlightButton.setAlwaysGone(isAlwaysGone);
                break;
            case GO_HOME:
                landButton.setAlwaysGone(isAlwaysGone);
                break;
            case RECORD_TIME:
                recordTimeText.setAlwaysGone(isAlwaysGone);
                break;
            case FLY_TO:
                flyToButton.setAlwaysGone(isAlwaysGone);
                break;
            case SWAP:
                swapButton.setAlwaysGone(isAlwaysGone);
                break;
            case RIGHT_LAYOUT:
                break;
            case CAMERA_ACTION:
                cameraActionButton.setAlwaysGone(isAlwaysGone);
                break;
            case CAMERA_MODE:
                cameraToVideButton.setAlwaysGone(isAlwaysGone);
                break;
            case LOCATION_FIX:
                locationFixButton.setAlwaysGone(isAlwaysGone);
                break;
            case LOOK_AT_POINT:
                lookAtPointButton.setAlwaysGone(isAlwaysGone);
                break;
            case ROTATE_GIMBAL:
                rotateGimbalButton.setAlwaysGone(isAlwaysGone);
                break;
            case LOCK_LOOK_AT_POINT:
                lockGimbalButton.setAlwaysGone(isAlwaysGone);
                break;
            case EXPLORE:
                exploreButton.setAlwaysGone(isAlwaysGone);
                break;
            case SPECIAL_FUNCTIONS:
                specialFunctionButton.setAlwaysGone(isAlwaysGone);
                break;
            case TAKE_OFF:
                takeOffButton.setAlwaysGone(isAlwaysGone);
                break;
            case COORDINATE_BUTTON:
                coordinateProviderButton.setAlwaysGone(isAlwaysGone);
                break;
            case ANYVISION_BUTTON:
                anyvisionButton.setAlwaysGone(isAlwaysGone);
                break;
            case INTERNAL_RECORD:
                internalRecordButton.setAlwaysGone(isAlwaysGone);
                break;
        }
    }

    public DroneActionMenu(Activity activity,
                           final DroneTabsModel tabsModel,
                           ObservableValue<Location> crosshairLocation,
                           HashMap<DroneController, Property<VideoClickInfo>> videoClickedLocations, DroneUnit unit,
                           final DtmProvider terrainAltitude,
                           MessageViewModel messageViewModel,
                           LittleMessageViewModel littleMessageViewModel,
                           BooleanProperty fullScreenVideo) {
        this.activity = activity;
        this.tabsModel = tabsModel;
        this.videoClickedLocations = videoClickedLocations;
        this.fullScreenVideo = fullScreenVideo;

        this.dtmProvider = terrainAltitude;

        this.messageViewModel = messageViewModel;
        this.littleMessageViewModel = littleMessageViewModel;
        this.crosshairLocation = crosshairLocation;
        this.unit = unit;

        stopFlightButton = new ImageViewModel((ImageView) activity.findViewById(R.id.stopFlightButton));
        stopGimbalButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.stopGimbal));
        stopUnitButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.stopUnitButton));

        lockGimbalButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.lockGimbalAtLocation));
        exploreButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.exploreButton));
        swapButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.swapButton));
        takeOffButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.takeOffButton));
        landButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.landButton));
        flyToButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.flyToButton));
        lookAtPointButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.lookAtPointButton));
        rotateGimbalButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.rotateGimbalButton));
        specialFunctionButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.specialFunctionsButton));

        confirmLandButton = new ImageViewModel((ImageView) activity.findViewById(R.id.confirmLandButton));

        cameraActionButton = new ImageViewModel((ImageView) activity.findViewById(R.id.recordButton));
        cameraToVideButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.cameraToVideoButton));
        recordTimeText = new TextViewModel((TextView)activity.findViewById(R.id.timeOfReccordText));

        locationFixButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.stickButton));
        locationFixButton.setAlwaysGone(true);

        View sticksView = activity.findViewById(R.id.virtualSticksView);
        this.sticksView = new ViewModel(sticksView);

        upStick = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.verticalPositiveStick));
        downStick = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.verticalNegativeStick));
        leftStick = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.yawNegativeStick));
        rightStick = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.yawPositiveStick));

        coordinateProviderButton= new ImageViewModel((ImageView)activity.findViewById(R.id.coordinateButton));

        gimbalPitchUpStick = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.plusPitch));
        gimbalPitchDownStick = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.minusPitch));
        powerButton = new ImageViewModel((ImageView)activity.findViewById(R.id.powerButton));

        anyvisionButton = new ActionMenuItemModel((ImageView)activity.findViewById(R.id.anyvisionButton));

        internalRecordButton = new ImageViewModel((ImageView)activity.findViewById(R.id.internalRecord));

        this.sticksView.visibility().set(ViewModel.Visibility.GONE);
        setSingleTaps();

        cameraActionButton.visibility().set(ViewModel.Visibility.VISIBLE);
        cameraToVideButton.visibility().set(ViewModel.Visibility.VISIBLE);
        recordTimeText.visibility().set(ViewModel.Visibility.VISIBLE);

        tabsModel.items().observe(new CollectionObserver<DroneTabModel>(){
            @Override
            public void added(DroneTabModel value, Observation<DroneTabModel> observation) {
                int size = tabsModel.items().size();
                hasMultiDrones.set(size > 1);
            }

            @Override
            public void removed(DroneTabModel value, Observation<DroneTabModel> observation) {
                int size = tabsModel.items().size();
                hasMultiDrones.set(size > 1);
            }
        }).observeCurrentValue();
    }

    private void setSingleTaps(){

        coordinateProviderButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                DroneController controller = tabModel.getDroneController();
                if(controller == null){
                    return false;
                }
                messageViewModel.addCoordinateMessage(controller,dtmProvider);

                return false;
            }
        });

        powerButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(tabsModel == null){
                    littleMessageViewModel.addNewMessage("Has no controller");
                    return false;
                }
                DroneController controller = tabModel.getDroneController();
                if(!(controller instanceof ControllerSimulator)){
                    return false;
                }

                DroneConnectivity currentConnecivity = controller.connectivity().value();
                Location currentCrosshairLocation = crosshairLocation.value();

                if(currentCrosshairLocation == null && currentConnecivity != DroneConnectivity.DRONE_CONNECTED){
                    littleMessageViewModel.addNewMessage("Has no crosshair on the map");
                    return false;
                }
                ((ControllerSimulator) controller).togglePower(currentCrosshairLocation);

                return false;
            }
        });

        locationFixButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                Location crosLocation = crosshairLocation.value();

                if(crosLocation == null){
                    littleMessageViewModel.addNewMessage("No Crosshair Location");
                    return false;
                }

                Location currentLocation = Telemetry.telemetryToLocation(controller.telemetry().value());

                if(currentLocation == null){
                    littleMessageViewModel.addNewMessage("No Drone Location");
                    return false;
                }

                LocationFix locationFix = new LocationFix(currentLocation.distance(crosLocation),currentLocation.az(crosLocation));
                ((AbstractDroneController)controller).flightTasks().getLocationFix().set(locationFix);
                littleMessageViewModel.addNewMessage("Success to set location fix with : " + locationFix.toString());

                return false;
            }
        });

//        locationFixButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//                sticksView.visibility().set(sticksView.visibility().value() == ViewModel.Visibility.GONE ? ViewModel.Visibility.VISIBLE : ViewModel.Visibility.GONE);
//                return false;
//            }
//        });

        internalRecordButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(tabModel == null){
                    return false;
                }

                DroneController tabController = tabModel.getDroneController();
//                if(!(tabController instanceof ControllerDjiNew)){
//                    return false;
//                }

//                ControllerDjiNew controller = (ControllerDjiNew) tabController;
//                controller.camera().getInternalRecordVideo().set(!controller.camera().getInternalRecordVideo().value());

                return false;
            }
        });

        upStick.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(containTaskBlocker(controller, FlightTaskType.GOTO_POINT)) {
                    littleMessageViewModel.addNewMessage("Can't move up : " + flightBlockerReason(controller,FlightTaskType.GOTO_POINT));
                    return false;
                }

                try {
                    Telemetry currentDroneTelemetry = controller.telemetry().value();
                    Location currentDroneLocation = currentDroneTelemetry == null ? null : currentDroneTelemetry.location();
                    if (currentDroneLocation == null) {
                        littleMessageViewModel.addNewMessage("Unable to go up, unknown drone location");
                        return false;
                    }
                    Location newLocation = currentDroneLocation.altitude(currentDroneLocation.getAltitude() + 5);
                    tabModel.getDroneTasks().flyTo(newLocation);
                }
                catch (Exception e){
                    littleMessageViewModel.addNewMessage("Error inside moving up : " + e.getMessage());
                    return false;
                }
                return false;
            }
        });

        downStick.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                if(containTaskBlocker(controller, FlightTaskType.GOTO_POINT)) {
                    littleMessageViewModel.addNewMessage("Can't move down : " + flightBlockerReason(controller,FlightTaskType.GOTO_POINT));
                    return false;
                }

                try {
                    Telemetry currentDroneTelemetry = controller.telemetry().value();
                    Location currentDroneLocation = currentDroneTelemetry == null ? null : currentDroneTelemetry.location();
                    if (currentDroneLocation == null) {
                        littleMessageViewModel.addNewMessage("Unable to go up, unknown drone location");
                        return false;
                    }
                    Location newLocation = currentDroneLocation.altitude(currentDroneLocation.getAltitude() - 5);
                    tabModel.getDroneTasks().flyTo(newLocation);
                }
                catch (Exception e){
                    littleMessageViewModel.addNewMessage("Error inside moving down : " + e.getMessage());
                    return false;
                }
                return false;
            }
        });


        gimbalPitchUpStick.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                if(containGimbalBlocker(controller, GimbalTaskType.LOOK_AT_POINT)) {
                    littleMessageViewModel.addNewMessage("Can't rotate gimbal up : " + gimbalBlockerReason(controller,GimbalTaskType.LOOK_AT_POINT));
                    return false;
                }

                try{
                    GimbalState currentGimbal = controller.gimbal().gimbalState().value();
                    if(currentGimbal == null){
                        littleMessageViewModel.addNewMessage("Unable to rotate gimbal , unknown gimbal position");
                        return false;
                    }

                    tabModel.getDroneTasks().rotateGimbal(new GimbalRequest(new GimbalState(0,Math.max(currentGimbal.getPitch()-5,0),0),true,false,false));
                }
                catch (Exception e){
                    littleMessageViewModel.addNewMessage("Error inside rotating gimbal to down : " + e.getMessage());
                }

                return false;
            }
        });


        gimbalPitchDownStick.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                if(containGimbalBlocker(controller, GimbalTaskType.LOOK_AT_POINT)) {
                    littleMessageViewModel.addNewMessage("Can't rotate gimbal down : " + gimbalBlockerReason(controller,GimbalTaskType.LOOK_AT_POINT));
                    return false;
                }

                try{
                    GimbalState currentGimbal = controller.gimbal().gimbalState().value();
                    if(currentGimbal == null){
                        littleMessageViewModel.addNewMessage("Unable to rotate gimbal , unknown gimbal position");
                        return false;
                    }

                    tabModel.getDroneTasks().rotateGimbal(new GimbalRequest(new GimbalState(0,Math.min(currentGimbal.getPitch() +5,90),0),true,false,false));
                }
                catch (Exception e){
                    littleMessageViewModel.addNewMessage("Error inside rotating gimbal to down : " + e.getMessage());
                }

                return false;
            }
        });

        leftStick.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                if(containGimbalBlocker(controller, GimbalTaskType.LOOK_AT_POINT)) {
                    littleMessageViewModel.addNewMessage("Can't rotate gimbal left : " + gimbalBlockerReason(controller,GimbalTaskType.LOOK_AT_POINT));
                    return false;
                }

                try{
                    GimbalState currentGimbal = controller.gimbal().gimbalState().value();
                    if(currentGimbal == null){
                        littleMessageViewModel.addNewMessage("Unable to rotate gimbal , unknown gimbal position");
                        return false;
                    }
                    double yaw = currentGimbal.getYaw() - 10;
                    tabModel.getDroneTasks().rotateGimbal(new GimbalRequest(new GimbalState(0,0,yaw),false,false,true));
                }
                catch (Exception e){
                    littleMessageViewModel.addNewMessage("Error inside rotating gimbal to right : " + e.getMessage());
                }

                return false;
            }
        });

        rightStick.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                if(containGimbalBlocker(controller, GimbalTaskType.LOOK_AT_POINT)) {
                    littleMessageViewModel.addNewMessage("Can't rotate gimbal right : " + gimbalBlockerReason(controller,GimbalTaskType.LOOK_AT_POINT));
                    return false;
                }

                try{
                    GimbalState currentGimbal = controller.gimbal().gimbalState().value();
                    if(currentGimbal == null){
                        littleMessageViewModel.addNewMessage("Unable to rotate gimbal , unknown gimbal position");
                        return false;
                    }
                    double yaw = currentGimbal.getYaw() + 10;
                    tabModel.getDroneTasks().rotateGimbal(new GimbalRequest(new GimbalState(0,0,yaw),false,false,true));
                }
                catch (Exception e){
                    littleMessageViewModel.addNewMessage("Error inside rotating gimbal to right : " + e.getMessage());
                }

                return false;
            }
        });

        cameraToVideButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(controller == null){
                    littleMessageViewModel.addNewMessage("Has No DroneController connected");
                    return false;
                }

                if(cameraToVideButton.clickable().value() == false){
                    String blockers = cameraBlockerReason(controller,CameraTaskType.CHANGE_MODE);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't " + CameraTaskType.CHANGE_MODE.getName() + ":" + blockers);
                    return false;
                }

                CameraMode currentMode = controller.camera().mode().value();
                CameraMode newMode = CameraMode.STILLS;
                if(currentMode == null || currentMode == CameraMode.UNKNOWN){
                    newMode = CameraMode.STILLS;
                }
                else if(currentMode == CameraMode.STILLS){
                    newMode = CameraMode.VIDEO;
                }
                else if(currentMode == CameraMode.VIDEO){
                    newMode = CameraMode.STILLS;
                }

                tabModel.getDroneTasks().changeMode(newMode);
                return false;
            }
        });

        cameraActionButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(controller == null){
                    littleMessageViewModel.addNewMessage("Has No DroneController connected");
                    return false;
                }

                CameraMode currentMode = controller.camera().mode().value();

                if(currentMode == null || currentMode == CameraMode.UNKNOWN){
                    littleMessageViewModel.addNewMessage("Camera : Can't do anything,Unknown mode");
                    return false;
                }

                switch (currentMode){

                    case VIDEO:
                        Boolean isRecording = controller.camera().recording().value();
                        if(isRecording == null){
                            littleMessageViewModel.addNewMessage("Camera : Can't start/stop record,don't know if now recording");
                            return false;
                        }

                        if(isRecording){
                            if(cameraActionButton.clickable().value() == false){
                                String blockers = cameraBlockerReason(controller,CameraTaskType.STOP_RECORD);
                                if(blockers == null){
                                    blockers = "Unknown Reason";
                                }
                                littleMessageViewModel.addNewMessage("Can't " + CameraTaskType.STOP_RECORD.getName() + ":" + blockers);
                                return false;
                            }
                            tabModel.getDroneTasks().stopRecord();
                            return false;
                        }
                        else{
                            if(cameraActionButton.clickable().value() == false){
                                String blockers = cameraBlockerReason(controller,CameraTaskType.START_RECORD);
                                if(blockers == null){
                                    blockers = "Unknown Reason";
                                }
                                littleMessageViewModel.addNewMessage("Can't " + CameraTaskType.START_RECORD.getName() + ":" + blockers);
                                return false;
                            }
                            tabModel.getDroneTasks().startRecord();
                            return false;
                        }
                    case STILLS:

                        Boolean isShootingPhoto = controller.camera().isShootingPhoto().value();
                        if(isShootingPhoto == null){
                            littleMessageViewModel.addNewMessage("Camera : Can't start/stop taking photos,don't know if now shooting photos");
                            return false;
                        }

                        if(isShootingPhoto){
                            if(cameraActionButton.clickable().value() == false){
                                String blockers = cameraBlockerReason(controller,CameraTaskType.STOP_SHOOTING_PHOTOS);
                                if(blockers == null){
                                    blockers = "Unknown Reason";
                                }
                                littleMessageViewModel.addNewMessage("Can't " + CameraTaskType.STOP_SHOOTING_PHOTOS.getName() + ":" + blockers);
                                return false;
                            }
                            tabModel.getDroneTasks().stopShootingPhotos();
                            return false;
                        }

                        if(cameraActionButton.clickable().value() == false){
                            String blockers = cameraBlockerReason(controller,CameraTaskType.TAKE_PHOTO);
                            if(blockers == null){
                                blockers = "Unknown Reason";
                            }
                            littleMessageViewModel.addNewMessage("Can't " + CameraTaskType.TAKE_PHOTO.getName() + ":" + blockers);
                            return false;
                        }
                        tabModel.getDroneTasks().takePhoto();
                        return false;
                    case UNKNOWN:
                        break;
                }

                return false;
            }
        });

        specialFunctionButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                tabModel.getFunctionsModel().getCurrentFunction().value().actionMenuButtonPressed();

                return false;
            }
        });

        landButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(landButton.clickable().value() == false){
                    String blockers = flightBlockerReason(controller,FlightTaskType.GO_HOME);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't " + FlightTaskType.GO_HOME.getName() + ":" + blockers);
                    return false;
                }

                tabModel.getDroneTasks().goHome();

                return false;
            }
        });

        lockGimbalButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(lockGimbalButton.clickable().value() == false){
                    String blockers = gimbalBlockerReason(controller,GimbalTaskType.LOCK_LOOK_AT_LOCATION);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't " + GimbalTaskType.LOCK_LOOK_AT_LOCATION.getName() + ":" + blockers);
                    return false;
                }

                Location crossHairLocation = crosshairLocation.value();
                if(crossHairLocation == null){
                    littleMessageViewModel.addNewMessage("Can't " + GimbalTaskType.LOCK_LOOK_AT_LOCATION.getName() + ":Unknown crosshair location");
                    return false;
                }

                Location lookAtLocation = DtmProvider.DtmTools.getGroundLocationRelativeToRefPoint(
                        crossHairLocation,
                        controller.droneHome().takeOffDTM().value(),
                        dtmProvider);
                tabModel.getDroneTasks().lockGimbalAtLocation(lookAtLocation);

                return false;
            }
        });

        exploreButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(exploreButton.clickable().value() == false){
                    String blockers = gimbalBlockerReason(controller,GimbalTaskType.EXPLORE);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't " + GimbalTaskType.EXPLORE.getName() + ":" + blockers);
                    return false;
                }

                tabModel.getDroneTasks().explore();
                return false;
            }
        });

        stopUnitButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                DroneTask<UnitTaskType> currentUnitTask = unit.currentTask(controller.uuid()).value();
                if(currentUnitTask != null && !currentUnitTask.status().value().isTaskDone()){
                    currentUnitTask.cancel();
                }
                return false;
            }
        });

        stopFlightButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                stamExecutor.submit(new Runnable() {
                    @Override
                    public void run() {

                        DroneTask currentFlightTask = controller.flightTasks().current().value();
                        if(currentFlightTask != null && !((TaskStatus)currentFlightTask.status().value()).isTaskDone()){
                            currentFlightTask.cancel();
                        }
                    }
                });

                return false;
            }
        });

        stopGimbalButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                stamExecutor.submit(new Runnable() {
                    @Override
                    public void run() {

                        DroneTask currentGimbalTask = controller.gimbal().currentTask().value();
                        if(currentGimbalTask != null && !((TaskStatus)currentGimbalTask.status().value()).isTaskDone()){
                            currentGimbalTask.cancel();
                        }
                    }
                });

                return false;
            }
        });

//        anyvisionButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                final DroneController otherController = unit.controllers().get(1);
//
//                final Location otherControllerLocation = Telemetry.telemetryToLocation(otherController.telemetry().value());
//
//                final Location droneLocation = Telemetry.telemetryToLocation(controller.telemetry().value());
//                final Location targetLocation = droneLocation.getLocationFromAzAndDistance(3D,droneLocation.az(otherControllerLocation));
//                final Location lookAtLocation = DtmProvider.DtmTools.getGroundLocationRelativeToRefPoint(
//                        droneLocation,
//                        controller.droneHome().takeOffDTM().value(),
//                        dtmProvider);
//
//                if(droneLocation == null){
//                    MainLogger.logger.write_message(LoggerTypes.ANYVISION,"Unknown drone location");
//                    return false;
//                }
//
//                try {
//
//                    ExecutorService anyVisionExecutor = Executors.newSingleThreadExecutor();
//                    anyvisionButton.clickable().setIfNew(false);
//                    anyVisionExecutor.execute(new Runnable() {
//                        @Override
//                        public void run() {
//
//                            try {
//                                TakeOff takeOff = otherController.flightTasks().takeOff(20D);
//
//                                takeOff.status().await(new Predicate<TaskStatus>() {
//                                    @Override
//                                    public boolean test(TaskStatus subject) {
//                                        return subject.isTaskDone();
//                                    }
//                                });
//
//                                if(takeOff.status().value() != FINISHED){
//                                    throw new DroneTaskException("Take off failed : " + takeOff.status().value());
//                                }
//
//                                final LockGimbalAtLocation lockGimbalAtLocation = otherController.gimbal().lockGimbalAtLocation(lookAtLocation);
//
//                                FlyTo flyTo = otherController.flightTasks().flyTo(targetLocation,new AltitudeInfo(AltitudeType.ABOVE_GROUND_LEVEL, (double) 20),null,null,3D);
//                                flyTo.status().await(new Predicate<TaskStatus>() {
//                                    @Override
//                                    public boolean test(TaskStatus subject) {
//
//                                        return subject.isTaskDone();
//                                    }
//                                });
//
//                                if(flyTo.status().value() != FINISHED){
//                                    throw new DroneTaskException("Fly to failed : " + flyTo.status().value());
//                                }
//
//                                final ExecutorService untilDetectionExecutor = Executors.newSingleThreadExecutor();
//                                final CountDownLatch latch = new CountDownLatch(1);
//
//                                untilDetectionExecutor.execute(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try{
//                                            FlyTo reduceAltitude = otherController.flightTasks().flyTo(targetLocation,new AltitudeInfo(AltitudeType.ABOVE_GROUND_LEVEL,2.0D),null,null,3D);
//
//                                            reduceAltitude.status().await(new Predicate<TaskStatus>() {
//                                                @Override
//                                                public boolean test(TaskStatus subject) {
//                                                    return subject.isTaskDone();
//                                                }
//                                            });
//
//                                            lockGimbalAtLocation.cancel();
//
//                                            lockGimbalAtLocation.status().await(new Predicate<TaskStatus>() {
//                                                @Override
//                                                public boolean test(TaskStatus subject) {
//                                                    return subject.isTaskDone();
//                                                }
//                                            });
//
//                                            Explore explore = otherController.gimbal().explore();
//
//                                            explore.status().await(new Predicate<TaskStatus>() {
//                                                @Override
//                                                public boolean test(TaskStatus subject) {
//                                                    return subject.isTaskDone();
//                                                }
//                                            });
//
//                                            latch.countDown();
//                                        }
//                                        catch (InterruptedException e){
//                                            MainLogger.logger.write_message(LoggerTypes.ANYVISION,"Interupt caused detection to over");
//                                            latch.countDown();
//                                        } catch (DroneTaskException e) {
//                                            MainLogger.logger.writeError(LoggerTypes.ANYVISION,e);
//                                            latch.countDown();
//                                        }
//                                    }
//                                });
//
//
//                                UnitModel.instance.setListener(new ImageprocessAnyvisionAndroid.DetectionListener() {
//                                    @Override
//                                    public void onDetection() {
//                                        untilDetectionExecutor.shutdownNow();
//                                    }
//                                });
//
//                                latch.await();
//
//                                DroneTask currentTask = otherController.flightTasks().current().value();
//                                DroneTask gimbalTask = otherController.gimbal().currentTask().value();
//
//                                if(currentTask != null && !((TaskStatus)currentTask.status().value()).isTaskDone()){
//                                    currentTask.cancel();
//                                }
//
//                                if(gimbalTask != null && !((TaskStatus)gimbalTask.status().value()).isTaskDone()){
//                                    gimbalTask.cancel();
//                                }
//
//                                if(currentTask != null) {
//                                    currentTask.status().await(new Predicate<TaskStatus>() {
//                                        @Override
//                                        public boolean test(TaskStatus subject) {
//                                            return subject.isTaskDone();
//                                        }
//                                    });
//                                }
//
//                                if(gimbalTask != null){
//                                    gimbalTask.status().await(new Predicate<TaskStatus>() {
//                                        @Override
//                                        public boolean test(TaskStatus subject) {
//                                            return subject.isTaskDone();
//                                        }
//                                    });
//                                }
//
//                                final FlyTo increaseAltitude = otherController.flightTasks().flyTo(targetLocation,new AltitudeInfo(AltitudeType.ABOVE_GROUND_LEVEL,20D),null,null,3D);
//
//                                increaseAltitude.status().await(new Predicate<TaskStatus>() {
//                                    @Override
//                                    public boolean test(TaskStatus subject) {
//                                        return subject.isTaskDone();
//                                    }
//                                });
//
//                                otherController.flightTasks().goHome();
//
//                            } catch (DroneTaskException e) {
//                                littleMessageViewModel.addNewMessage(e.getMessage());
//                                MainLogger.logger.writeError(LoggerTypes.ANYVISION,e);
//                                e.printStackTrace();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                            anyvisionButton.clickable().setIfNew(true);
//                        }
//                    });
//
//                }
//                catch (Exception e){
//                    MainLogger.logger.writeError(LoggerTypes.ANYVISION,e);
//                }
//
//                return false;
//            }
//        });

        anyvisionButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(swapButton.clickable().value() == false){
                    String blockers = flightBlockerReason(controller,FlightTaskType.GOTO_POINT);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't Swap " + ":" + blockers);
                    return false;
                }

                try {
                    final AnytargetAction anytargetAction = unit.flightTasks().anytargetAction(
                            tabModel.getDroneController(),
                            videoClickedLocations,
                            ImageprocessAnyvisionAndroid.getInstance().detection());
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Anytarget Failed : " + e.getErrorString());
                }


                return false;
            }
        });

        swapButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(swapButton.clickable().value() == false){
                    String blockers = flightBlockerReason(controller,FlightTaskType.GO_HOME);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't Swap " + ":" + blockers);
                    return false;
                }

                try {
                    final Swap swapTask = unit.flightTasks().swap(tabModel.getDroneController());

                    final Removable pendingConfirmationRemovable = swapTask.pendingConfirmation().observe(new Observer<ConfirmationData>() {
                        @Override
                        public void observe(ConfirmationData oldValue, final ConfirmationData newValue, Observation<ConfirmationData> observation) {

                            try {
                                if(newValue != null) {

                                    UUID controllerToSwapUUID = swapTask.controllerToSwap();
                                    UUID swappingControllerUUID = swapTask.swappingController();

                                    if (controllerToSwapUUID == null || swappingControllerUUID == null) {
                                        return;
                                    }

                                    DroneController controllerToSwap = unit.getControllerByUUID(swapTask.controllerToSwap());
                                    final DroneController swappingController = unit.getControllerByUUID(swapTask.swappingController());

                                    String controllerToSwapName = tabsModel.resolve(controllerToSwap.uuid());
                                    String swappingControllerName = tabsModel.resolve(swappingController.uuid());
                                    final DroneTabModel swappingControllerTab = tabsModel.getByControllerUUID(swappingController.uuid());

                                    String bodyText = "";
                                    String headerText = "";
                                    MessageViewModel.MessageViewModelListener listener = null;
                                    Drawable imageDrawable;

                                    switch (newValue.getType()){

                                        case TAKE_OFF:

                                            bodyText = "Aircraft " + swappingControllerName + ":" + newValue.getInfo();
                                            headerText = "Take Off For Swap";
                                            imageDrawable = ContextCompat.getDrawable(activity, R.drawable.btn_takeoff_orange);
                                            listener = new MessageViewModel.MessageViewModelListener() {
                                                @Override
                                                public void onOkButtonPressed() {

                                                    try {
                                                        swapTask.confirm(newValue.getType());
                                                    } catch (DroneTaskException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onCancelButtonPressed() {
                                                    swapTask.cancel();
                                                }
                                            };
                                            messageViewModel.addGeneralMessage(headerText,bodyText,imageDrawable,listener);
                                            break;
                                        case SWAP:

                                            DroneTabModel currentTab = tabsModel.selected().value();
                                            if(currentTab == null ||
                                                    currentTab.getDroneController() == null ||
                                                    !currentTab.getDroneController().uuid().equals(controllerToSwapUUID)){
                                                try {
                                                    swapTask.confirm(newValue.getType());
                                                    return;
                                                } catch (DroneTaskException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            headerText = "Swap";
                                            bodyText = "Aircraft " + controllerToSwapName + " will go home and aircraft " + swappingControllerName + " will take his place";
                                            imageDrawable = ContextCompat.getDrawable(activity, R.drawable.btn_swap_orange);
                                            listener = new MessageViewModel.MessageViewModelListener() {
                                                @Override
                                                public void onOkButtonPressed() {
                                                    try {
                                                        swapTask.confirm(newValue.getType());
                                                        if(swappingControllerTab != null){
                                                            tabsModel.selected().set(swappingControllerTab);
                                                        }
                                                    } catch (DroneTaskException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onCancelButtonPressed() {
                                                    swapTask.cancel();
                                                }
                                            };
                                            messageViewModel.addGeneralMessage(headerText,bodyText,imageDrawable,listener);
                                            break;
                                        case NONE:
                                            break;
                                    }
                                }
                            } catch (ComponentNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    },UI_EXECUTOR).observeCurrentValue();

                    swapTask.status().observe(new Observer<TaskStatus>() {
                        @Override
                        public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {
                            if(newValue.isTaskDone()){
                                observation.remove();
                                pendingConfirmationRemovable.remove();

                                if(newValue != FINISHED){
                                    DroneTaskException error = swapTask.error().value();
                                    String errorString = "";
                                    if(error != null){
                                        errorString += error.getErrorString();
                                    }
                                    if(newValue == FINISHED){
                                        littleMessageViewModel.addNewMessage("Swap Ended Successfully");
                                    }
                                    else{
                                        littleMessageViewModel.addNewMessage("Swap ended with Status : " + newValue +"," + errorString);
                                    }
                                }

                            }
                        }
                    }).observeCurrentValue();

                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Swap Failed : " + e.getErrorString());
                }
                return false;
            }
        });

        rotateGimbalButton.singleTap().setIfNew(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                if(rotateGimbalButton.clickable().value() == false){
                    String blockers = gimbalBlockerReason(controller,GimbalTaskType.LOOK_AT_POINT);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't " + GimbalTaskType.ROTATE_GIMBAL.getName() + ":" + blockers);
                    return false;
                }

                VideoClickInfo videoClickInfo = videoClickedLocations.get(controller).value();

                if(videoClickInfo == null){
                    littleMessageViewModel.addNewMessage("Can't " + GimbalTaskType.ROTATE_GIMBAL.getName() + " : Unknown crosshair location");
                    return false;
                }


                tabModel.getDroneTasks().rotateGimbal(new GimbalRequest(videoClickInfo.getGimbalState(),true,false,true));

                return false;
            }
        });

        lookAtPointButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(lookAtPointButton.clickable().value() == false){
                    String blockers = gimbalBlockerReason(controller,GimbalTaskType.LOOK_AT_POINT);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't " + GimbalTaskType.LOOK_AT_POINT.getName() + ":" + blockers);
                    return false;
                }

                Location crossHairLocation = new Location(crosshairLocation.value().getLatitude(),crosshairLocation.value().getLongitude());
                if(crossHairLocation == null){
                    littleMessageViewModel.addNewMessage("Can't " + GimbalTaskType.LOOK_AT_POINT.getName() + ":Unknown crosshair location");
                    return false;
                }

                Location lookAtLocation = DtmProvider.DtmTools.getGroundLocationRelativeToRefPoint(
                        crossHairLocation,
                        controller.droneHome().takeOffDTM().value(),
                        dtmProvider);
                tabModel.getDroneTasks().lookAtPoint(lookAtLocation);

                return false;
            }
        });

        takeOffButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(takeOffButton.clickable().value() == false){
                    String blockers = flightBlockerReason(controller,FlightTaskType.TAKE_OFF);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't " + FlightTaskType.TAKE_OFF.getName() + ":" + blockers);
                    return false;
                }

                final Double takeOffAltitude = EyesatopAppConfiguration.getInstance().getTakeoffAltitude().withDefault(50).value().doubleValue();

                String controllerName = tabsModel.resolve(controller.uuid());
                Drawable imageDrawable = ContextCompat.getDrawable(activity, R.drawable.btn_takeoff_orange);
                String headerText = "Take Off";
                String bodyText = "Aircraft " + controllerName + " will turn motors-on and rise to altitude of " +
                        DistanceUnitType.formatNumber(EyesatopAppConfiguration.getInstance().getAppMeasureType().value(), 1, takeOffAltitude);
                MessageViewModel.MessageViewModelListener listener = new MessageViewModel.MessageViewModelListener() {
                    @Override
                    public void onOkButtonPressed() {
                        tabsModel.getItem(controller).getDroneTasks().takeOff(takeOffAltitude);
                    }

                    @Override
                    public void onCancelButtonPressed() {

                    }
                };
                messageViewModel.addGeneralMessage(headerText,bodyText,imageDrawable,listener);

                return false;
            }
        });

        flyToButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(flyToButton.clickable().value() == false){
                    String blockers = flightBlockerReason(controller,FlightTaskType.GOTO_POINT);
                    if(blockers == null){
                        blockers = "Unknown Reason";
                    }
                    littleMessageViewModel.addNewMessage("Can't " + FlightTaskType.GOTO_POINT.getName() + ":" + blockers);
                    return false;
                }

                if(crosshairLocation.isNull()){
                    littleMessageViewModel.addNewMessage("Can't " + FlightTaskType.GOTO_POINT.getName() + ":Unknown crosshair location");
                    return false;
                }

                Location locationToFly = new Location(crosshairLocation.value().getLatitude(),
                        crosshairLocation.value().getLongitude(),controller.telemetry().value().location().getAltitude());
                tabModel.getDroneTasks().flyTo(locationToFly);

                return false;
            }
        });
    }

    public Removable bind(final DroneTab tabModel, final DroneController controller) {

        this.tabModel = tabModel;
        this.controller = controller;

        confirmLandButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                try {
                    controller.flightTasks().confirmLand();
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Confirm Land : " + e.getErrorString());
                }
                return false;
            }
        });
        Removable internalRecordRemovable = Removable.STUB;
        Removable locationFixRemovable = Removable.STUB;
//        if((tabController instanceof ControllerDjiNew)){
//            internalRecordButton.visibility().set(ViewModel.Visibility.VISIBLE);
//            internalRecordRemovable = internalRecordButton.imageDrawable()
//                    .bind(((ControllerDjiNew)tabController)
//                            .camera().getInternalRecordVideo()
//                            .toggle(ContextCompat.getDrawable(activity,R.drawable.stop_record_new),ContextCompat.getDrawable(activity,R.drawable.record_button_main)));
//        }
//        else{
            internalRecordButton.visibility().set(ViewModel.Visibility.GONE);
//        }

        powerButton.visibility().set((controller instanceof ControllerSimulator) ? ViewModel.Visibility.VISIBLE : ViewModel.Visibility.GONE);

        if(controller instanceof AbstractDroneController){
            AbstractDroneController abstractDroneController = (AbstractDroneController) controller;
            locationFixRemovable = locationFixButton.visibility().bind(abstractDroneController.telemetry().notNull().not().or(abstractDroneController.flightTasks().getLocationFix().notNull()).toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE));
        }
        else{
            locationFixButton.visibility().set(ViewModel.Visibility.GONE);
        }

        specialFunctionButton.visibility().bind(fullScreenVideo.toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE));
//        specialFunctionButton.clickable().set(false);

        stopUnitButton.clickable().set(true);
        stopFlightButton.clickable().set(true);
        stopGimbalButton.clickable().set(true);

        stopUnitButton.visibility().set(unit.currentTask(controller.uuid()).value() != null ? ViewModel.Visibility.VISIBLE : ViewModel.Visibility.GONE);
        stopFlightButton.visibility().set(controller.flightTasks().current().value() != null ? ViewModel.Visibility.VISIBLE : ViewModel.Visibility.GONE);
        stopGimbalButton.visibility().set(controller.gimbal().currentTask().value() != null ? ViewModel.Visibility.VISIBLE : ViewModel.Visibility.GONE);

        takeOffButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.TAKE_OFF));
        landButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GO_HOME));
        flyToButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
        upStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
        downStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));

        swapButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GO_HOME));
        anyvisionButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
        lookAtPointButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
        rotateGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
        leftStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
        rightStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
        gimbalPitchUpStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
        gimbalPitchDownStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));


        lockGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOCK_LOOK_AT_LOCATION));
        exploreButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.EXPLORE));

        cameraActionButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.START_RECORD));
        cameraToVideButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.CHANGE_MODE));

        anyvisionButton.visibility().set(ViewModel.Visibility.VISIBLE);

        ObservableBoolean flightNotBusy = controller.flightTasks().current().equalsTo(null);
        ObservableBoolean gimbalNotBusy = controller.gimbal().currentTask().equalsTo(null);

        ObservableBoolean showFlightTasks = flightNotBusy
                .and(controller.connectivity().equalsTo(DroneConnectivity.DRONE_CONNECTED))
                .and(controller.flying());
        ObservableBoolean showGimbalTasks = gimbalNotBusy
                        .and(controller.connectivity().equalsTo(DroneConnectivity.DRONE_CONNECTED));

        ObservableBoolean showTakeOffButton = flightNotBusy
                .and(controller.connectivity().equalsTo(DroneConnectivity.DRONE_CONNECTED))
                .and(controller.flying().equalsTo(false));

        ObservableBoolean showSwapButton = showFlightTasks.and(hasMultiDrones);

        final Property<CameraButtonModes> cameraButtonModes = new Property<>(CameraButtonModes.UNKNOWN);

        return new RemovableCollection(
                confirmLandButton.visibility().bind(controller.flightTasks().confirmLandRequire().transform(new Function<Boolean, ViewModel.Visibility>() {
                    @Override
                    public ViewModel.Visibility apply(Boolean input) {

                        if(input == null){
                            return ViewModel.Visibility.GONE;
                        }

                        if(input){
                            return ViewModel.Visibility.VISIBLE;
                        }

                        return ViewModel.Visibility.GONE;
                    }
                },false)),
                internalRecordRemovable,
                controller.camera().recordTimeInSeconds().observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {

                        if(newValue != null) {
                            int numOfHours = newValue / 3600;
                            int numOfMinutes = (newValue - 3600*numOfHours)/60;
                            int numOfSeconds = newValue - 3600*numOfHours - 60 * numOfMinutes;

                            String finalRecordText = formatter.format(numOfHours) + ":" +
                                    formatter.format(numOfMinutes) + ":" +
                                    formatter.format(numOfSeconds);

                            recordTimeText.text().set(finalRecordText);
                        }
                        else{
                            recordTimeText.text().set("00:00:00");
                        }
                    }
                }).observeCurrentValue(),

                controller.camera().mode().observe(new Observer<CameraMode>() {
                    @Override
                    public void observe(CameraMode oldValue, CameraMode newValue, Observation<CameraMode> observation) {
                        updateCameraButtonMode(cameraButtonModes);
                    }
                }),

                controller.camera().isShootingPhoto().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updateCameraButtonMode(cameraButtonModes);
                    }
                }),

                controller.camera().recording().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updateCameraButtonMode(cameraButtonModes);
                    }
                }).observeCurrentValue(),

                cameraActionButton.imageDrawable().bind(cameraButtonModes.transform(new CameraButtonModeToDrawable(activity))),

                tabModel.getFunctionsModel().getCurrentFunction().observe(new Observer<SpecialFunction>() {
                    @Override
                    public void observe(SpecialFunction oldValue, SpecialFunction newValue, Observation<SpecialFunction> observation) {
                        specialFunctionButton.imageDrawable().set(newValue.getFunctionDrawable());
                    }
                }).observeCurrentValue(),

                stopFlightButton.visibility().bind(controller.flightTasks().current().equalsTo(null).toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)),
                stopGimbalButton.visibility().bind(controller.gimbal().currentTask().equalsTo(null).toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)),
                stopUnitButton.visibility().bind(unit.currentTask(controller.uuid()).equalsTo(null).toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)),

                // toggle between takeoff/land visibilities
                takeOffButton.visibility().bind(showTakeOffButton.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
                landButton.visibility().bind(showFlightTasks.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
                flyToButton.visibility().bind(showFlightTasks.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
                swapButton.visibility().bind(showSwapButton.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
                anyvisionButton.visibility().bind(showSwapButton.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),

                coordinateProviderButton.visibility().bind(controller.connectivity().equalsTo(DroneConnectivity.DRONE_CONNECTED).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
                lookAtPointButton.visibility().bind(showGimbalTasks.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
                rotateGimbalButton.visibility().bind(showGimbalTasks.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
                lockGimbalButton.visibility().bind(showGimbalTasks.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
                exploreButton.visibility().bind(showGimbalTasks.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE))

                ,controller.flightTasks().tasksBlockers().observe(new CollectionObserver<FlightTaskBlockerType>(){
            @Override
            public void added(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
                takeOffButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.TAKE_OFF));
                landButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GO_HOME));
                flyToButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                swapButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                anyvisionButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                upStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                downStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
            }

            @Override
            public void removed(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
                takeOffButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.TAKE_OFF));
                landButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GO_HOME));
                flyToButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                upStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                downStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                swapButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                anyvisionButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
            }
        }),
                unit.flightTasks().busyList().observe(new CollectionObserver<UUID>(){
                    @Override
                    public void added(UUID value, Observation<UUID> observation) {
                        takeOffButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.TAKE_OFF));
                        landButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GO_HOME));
                        flyToButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                        upStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                        downStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                        swapButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                        anyvisionButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));

                        cameraActionButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.START_RECORD));
                        cameraToVideButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.CHANGE_MODE));

                        lookAtPointButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        rotateGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        leftStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        rightStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        gimbalPitchUpStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        gimbalPitchDownStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        lockGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOCK_LOOK_AT_LOCATION));
                        exploreButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.EXPLORE));

                        updateCameraButtonMode(cameraButtonModes);
                    }

                    @Override
                    public void removed(UUID value, Observation<UUID> observation) {
                        takeOffButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.TAKE_OFF));
                        landButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GO_HOME));
                        flyToButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                        upStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                        downStick.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                        swapButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));
                        anyvisionButton.clickable().set(!containTaskBlocker(controller,FlightTaskType.GOTO_POINT));

                        cameraActionButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.START_RECORD));
                        cameraToVideButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.CHANGE_MODE));

                        lookAtPointButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        rotateGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        leftStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        rightStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        gimbalPitchUpStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        gimbalPitchDownStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                        lockGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOCK_LOOK_AT_LOCATION));
                        exploreButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.EXPLORE));

                        updateCameraButtonMode(cameraButtonModes);
                    }
                }),
            controller.gimbal().tasksBlockers().observe(new CollectionObserver<GimbalTaskBlockerType>(){
                @Override
                public void added(GimbalTaskBlockerType value, Observation<GimbalTaskBlockerType> observation) {
                    lookAtPointButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    rotateGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    leftStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    rightStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    gimbalPitchUpStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    gimbalPitchDownStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    lockGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOCK_LOOK_AT_LOCATION));
                    exploreButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.EXPLORE));
                }

                @Override
                public void removed(GimbalTaskBlockerType value, Observation<GimbalTaskBlockerType> observation) {
                    lookAtPointButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    rotateGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    leftStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    rightStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    gimbalPitchUpStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    gimbalPitchDownStick.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOOK_AT_POINT));
                    lockGimbalButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.LOCK_LOOK_AT_LOCATION));
                    exploreButton.clickable().set(!containGimbalBlocker(controller,GimbalTaskType.EXPLORE));
                }
            }),
                controller.camera().tasksBlockers().observe(new CollectionObserver<CameraTaskBlockerType>(){
                    @Override
                    public void added(CameraTaskBlockerType value, Observation<CameraTaskBlockerType> observation) {
                        cameraActionButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.START_RECORD));
                        cameraToVideButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.CHANGE_MODE));
                        updateCameraButtonMode(cameraButtonModes);
                    }

                    @Override
                    public void removed(CameraTaskBlockerType value, Observation<CameraTaskBlockerType> observation) {
                        cameraActionButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.START_RECORD));
                        cameraToVideButton.clickable().set(!containCameraBlocker(controller,CameraTaskType.CHANGE_MODE));
                        updateCameraButtonMode(cameraButtonModes);
                    }
                })
        );
    }

    private boolean containTaskBlocker(DroneController controller, FlightTaskType taskType){

        if(controller == null){
            return true;
        }

        for(FlightTaskBlockerType taskBlocker : controller.flightTasks().tasksBlockers()){
            if(taskBlocker.affectedTasks().contains(taskType)){
                return true;
            }
        }

        if(unit.flightTasks().busyList().contains(controller.uuid())){
            return true;
        }

        return false;
    }

    private String flightBlockerReason(DroneController controller,FlightTaskType taskType){

        if(controller == null){
            return "No DroneController connected.";
        }

        if(unit.flightTasks().busyList().contains(controller.uuid())){
            return "Busy with unit task";
        }

        String blockers = null;
        for(FlightTaskBlockerType taskBlocker : controller.flightTasks().tasksBlockers()){
            if(taskBlocker.affectedTasks().contains(taskType)){
                if(blockers == null) {
                    blockers = taskBlocker.getName();
                }
                else{
                    blockers += "," + taskBlocker.getName();
                }
            }
        }

        return blockers;
    }


    private String gimbalBlockerReason(DroneController controller,GimbalTaskType taskType){

        if(controller == null){
            return "No DroneController connected.";
        }

        if(unit.flightTasks().busyList().contains(controller.uuid())){
            return "Busy with unit task";
        }

        String blockers = null;
        for(GimbalTaskBlockerType taskBlocker : controller.gimbal().tasksBlockers()){
            if(taskBlocker.affectedTasks().contains(taskType)){
                if(blockers == null) {
                    blockers = taskBlocker.getName();
                }
                else{
                    blockers += "," + taskBlocker.getName();
                }
            }
        }

        return blockers;
    }

    private String cameraBlockerReason(DroneController controller,CameraTaskType taskType){


        if(controller == null){
            return "No DroneController connected.";
        }

        if(unit.flightTasks().busyList().contains(controller.uuid())){
            return "Busy with unit task";
        }

        String blockers = null;
        for(CameraTaskBlockerType taskBlocker : controller.camera().tasksBlockers()){
            if(taskBlocker.affectedTasks().contains(taskType)){
                if(blockers == null) {
                    blockers = taskBlocker.getName();
                }
                else{
                    blockers += "," + taskBlocker.getName();
                }
            }
        }

        return blockers;
    }


    private boolean containCameraBlocker(DroneController controller, CameraTaskType taskType){

        if(controller == null){
            return true;
        }

        for(CameraTaskBlockerType taskBlocker : controller.camera().tasksBlockers()){
            if(taskBlocker.affectedTasks().contains(taskType)){
                return true;
            }
        }

        if(unit.flightTasks().busyList().contains(controller.uuid())){
            return true;
        }
        return false;
    }

    private boolean containGimbalBlocker(DroneController controller, GimbalTaskType taskType){

        if(controller == null){
            return true;
        }

        for(GimbalTaskBlockerType taskBlocker : controller.gimbal().tasksBlockers()){
            if(taskBlocker.affectedTasks().contains(taskType)){
                return true;
            }
        }

        if(unit.flightTasks().busyList().contains(controller.uuid())){
            return true;
        }
        return false;
    }

    private void updateCameraButtonMode(Property<CameraButtonModes> buttonMode){

        CameraMode currentMode = controller.camera().mode().value();
        Boolean isRecording = controller.camera().recording().value();
        Boolean isTakingPhoto = controller.camera().isShootingPhoto().value();
        MediaStorage storage = controller.camera().mediaStorage().value();

        boolean sdCardFull;
        if(storage == null || storage.getRemainingSpaceInBytes() < 4){
            sdCardFull = true;
        }
        else{
            sdCardFull = false;
        }

        boolean containCameraTaskBlocker = containCameraBlocker(controller,CameraTaskType.TAKE_PHOTO);

        if(currentMode == null || currentMode == CameraMode.UNKNOWN){
            buttonMode.set(CameraButtonModes.UNKNOWN);
            return;
        }

        switch (currentMode){

            case VIDEO:

                if(isRecording == null){
                    buttonMode.set(CameraButtonModes.UNKNOWN);
                    return;
                }

                if(isRecording){
                    buttonMode.set(CameraButtonModes.STOP_RECORD);
                }
                else{
                    if(sdCardFull){
                        buttonMode.set(CameraButtonModes.START_RECORD_SD_FULL);
                    }
                    else{
                        buttonMode.set(CameraButtonModes.START_RECORD);
                    }
                }
                return;
            case STILLS:

                if(isTakingPhoto == null){
                    buttonMode.set(CameraButtonModes.UNKNOWN);
                }
                else if(isTakingPhoto){
                    buttonMode.set(CameraButtonModes.STILLS_SHOOTING_PHOTOS);
                }
                else if(!sdCardFull){
                    buttonMode.set(CameraButtonModes.STILLS);
                }
                else{
                    buttonMode.set(CameraButtonModes.STILLS_SD_FULL_DISABLED);
                }
                return;
            case UNKNOWN:
                buttonMode.set(CameraButtonModes.UNKNOWN);
                return;
        }
    }

    public void addLocationFixButton(){
//        locationFixButton.visibility().set(ViewModel.Visibility.VISIBLE);
        locationFixButton.setAlwaysGone(false);
    }

    public void setForNull(){

        tabModel = null;
        this.controller = null;

        UI_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                stopUnitButton.visibility().set(ViewModel.Visibility.GONE);
                stopFlightButton.visibility().set(ViewModel.Visibility.GONE);
                stopGimbalButton.visibility().set(ViewModel.Visibility.GONE);

                lockGimbalButton.visibility().set(ViewModel.Visibility.GONE);
                exploreButton.visibility().set(ViewModel.Visibility.GONE);
                anyvisionButton.visibility().set(ViewModel.Visibility.GONE);
                swapButton.visibility().set(ViewModel.Visibility.GONE);
                takeOffButton.visibility().set(ViewModel.Visibility.GONE);
                landButton.visibility().set(ViewModel.Visibility.GONE);
                flyToButton.visibility().set(ViewModel.Visibility.GONE);
                lookAtPointButton.visibility().set(ViewModel.Visibility.GONE);
                rotateGimbalButton.visibility().set(ViewModel.Visibility.GONE);
                coordinateProviderButton.visibility().set(ViewModel.Visibility.GONE);

                specialFunctionButton.visibility().set(ViewModel.Visibility.GONE);

                recordTimeText.text().set("00:00:00");
//                cameraActionButton.visibility().set(Visibility.GONE);
//                cameraToVideButton.visibility().set(Visibility.GONE);
                cameraActionButton.clickable().set(false);
                cameraToVideButton.clickable().set(false);
                cameraActionButton.imageDrawable().set(ContextCompat.getDrawable(activity,R.drawable.take_photo_disable));

                upStick.clickable().set(false);
                downStick.clickable().set(false);
                rightStick.clickable().set(false);
                leftStick.clickable().set(false);
                gimbalPitchUpStick.clickable().set(false);
                gimbalPitchDownStick.clickable().set(false);

            }
        });
    }
}
