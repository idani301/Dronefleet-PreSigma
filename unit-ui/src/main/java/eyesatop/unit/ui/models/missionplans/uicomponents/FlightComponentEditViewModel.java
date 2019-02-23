package eyesatop.unit.ui.models.missionplans.uicomponents;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.AltitudeType;
import eyesatop.controller.beans.CameraActionType;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.generic.ViewGroupModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.map.mission.AttributeDataResult;
import eyesatop.unit.ui.models.map.mission.CircleFlightPlanComponent;
import eyesatop.unit.ui.models.map.mission.FlightPlanComponent;
import eyesatop.unit.ui.models.map.mission.RadiatorFlightPlanComponent;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.unit.ui.models.missionplans.components.AttributeData;
import eyesatop.unit.ui.models.specialfunctions.MissionState;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.DroneTabsModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;
import static eyesatop.unit.ui.models.generic.ViewModel.Visibility.GONE;
import static eyesatop.unit.ui.models.generic.ViewModel.Visibility.VISIBLE;

/**
 * Created by Idan on 14/11/2017.
 */

public class FlightComponentEditViewModel {

    private final ViewModel mainMenu;
    private Removable bindToTabRemovable = Removable.STUB;

    private final ScrollView waypointsScrollView;
    private final TextViewModel headerTextView;
    private final ViewGroupModel waypointsGroupModel;
    private final ViewGroupModel generalAttributesGroupModel;
    private final ViewGroupModel flightAttributesGroupModel;
    private final ViewGroupModel cameraAttributesGroupModel;
    private final ViewGroupModel gimbalAttributesGroupModel;
    private final ImageViewModel addWaypointButton;
    private final ImageViewModel clearWaypointsButton;
    private final ImageViewModel returnButton;
    private final ImageViewModel saveButton;
    private final ImageViewModel clearAllButton;
    private final ViewModel waypointScrollView;
    private final ImageViewModel pointInfo;
    private final TextViewModel generalHeaderTextView;

    private final Property<Location> crosshairLocation;

    private final TextViewModel estimatedTimeTextView;

    private final DroneTabsModel tabsModel;
    private final MessageViewModel messageViewModel;
    private final LittleMessageViewModel littleMessageViewModel;
    private final MapViewModel mapViewModel;
    private final ScrollView attributeScroll;
    private final Activity activity;

    private AltitudeType[] altitudeTypes = AltitudeType.values();

    public enum FlightComponentHeadersType {
        COMPONENT,
        FLIGHT,
        CAMERA,
        GIMBAL;
    }
    public enum FlightComponentFieldType {
        HOVER,
        HEADING;
    }

    public void setAltitudeTypes(AltitudeType[] altitudeTypes){
        this.altitudeTypes = altitudeTypes;
    }

    public void hideComponentHeaderType(FlightComponentHeadersType type,boolean isAlwaysGone){
        switch (type){

            case COMPONENT:
                generalAttributesGroupModel.setAlwaysGone(isAlwaysGone);
                break;
            case FLIGHT:
                flightAttributesGroupModel.setAlwaysGone(isAlwaysGone);
                break;
            case CAMERA:
                cameraAttributesGroupModel.setAlwaysGone(isAlwaysGone);
                break;
            case GIMBAL:
                gimbalAttributesGroupModel.setAlwaysGone(isAlwaysGone);
                break;
        }
    }

    private boolean isHideHover = false;
    private boolean isHideHeading = false;

    public void hideField(FlightComponentFieldType type){
        switch (type){
            case HOVER:
                isHideHover = true;
                break;
            case HEADING:
                isHideHeading = true;
                break;
        }
    }

    public FlightComponentEditViewModel(Activity activity, DroneTabsModel tabsModel, MessageViewModel messageViewModel, LittleMessageViewModel littleMessageViewModel, MapViewModel mapViewModel, Property<Location> crosshairLocation){

        this.activity = activity;
        this.mapViewModel = mapViewModel;
        this.messageViewModel = messageViewModel;
        this.littleMessageViewModel = littleMessageViewModel;

        this.tabsModel = tabsModel;
        this.crosshairLocation = crosshairLocation;
        mainMenu = new ViewModel(activity.findViewById(R.id.includePathPlannerLayout));

        attributeScroll = (ScrollView) activity.findViewById(R.id.attributeScroll);

        waypointsGroupModel = new ViewGroupModel((ViewGroup) activity.findViewById(R.id.waypointPlannerWaypointsLayout));

        generalAttributesGroupModel = new ViewGroupModel((ViewGroup) activity.findViewById(R.id.flightComponentPlannerGeneralAttributeLayout));
        flightAttributesGroupModel = new ViewGroupModel((ViewGroup) activity.findViewById(R.id.flightComponentPlannerFlightAttributeLayout));
        cameraAttributesGroupModel = new ViewGroupModel((ViewGroup) activity.findViewById(R.id.flightComponentPlannerCameraAttributeLayout));
        gimbalAttributesGroupModel = new ViewGroupModel((ViewGroup) activity.findViewById(R.id.flightComponentPlannerGimbalAttributeLayout));

        waypointsScrollView = (ScrollView) activity.findViewById(R.id.waypointsScrollView);

        returnButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionWayPointCloseButton));
        addWaypointButton = new ImageViewModel((ImageView) activity.findViewById(R.id.addMissionPoint));
        clearWaypointsButton = new ImageViewModel((ImageView) activity.findViewById(R.id.clearAllMissionPoints));
        clearAllButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionWayPointsClearButton));
        saveButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionWayPointsSaveButton));

        generalHeaderTextView = new TextViewModel((TextView) activity.findViewById(R.id.flightComponentPlannerGeneralHeader));
//        generalHeaderTextView.text().set("No Name");

        pointInfo = new ImageViewModel((ImageView) activity.findViewById(R.id.missionPointInfo));

        headerTextView = new TextViewModel((TextView) activity.findViewById(R.id.missionWayPointHeader));
        headerTextView.textColor().set(Color.YELLOW);

        waypointScrollView = new ViewModel(activity.findViewById(R.id.waypointsScrollView));

        estimatedTimeTextView = new TextViewModel((TextView) activity.findViewById(R.id.missionWayPointEstimateTime));

        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            @Override
            public void observe(DroneTabModel oldValue, final DroneTabModel newValue, Observation<DroneTabModel> observation) {

                bindToTabRemovable.remove();

                if(newValue == null){
                    bindToTabRemovable = bindToNULL();
                    return;
                }
                bindToTabRemovable = bindToTab(newValue);

            }
        }).observeCurrentValue();
    }

    private Removable crossHairRemovable = Removable.STUB;

    private final HashMap<FlightPlanComponent,ArrayList<AttributesViewModel>> flightPlanLiveAttributesMap = new HashMap<>();
//    private final HashMap<FlightPlanComponent,Removable> flightPlanAttributeBindingsMap = new HashMap<>();

    public Removable bindToTab(final DroneTabModel tabModel){

        final ObservableValue<FlightPlanComponent> currentFlightPlanComponent = tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentEditedFlightPlanComponent();

        final RemovableCollection removableList = new RemovableCollection();

        final RemovableCollection flightPlanComponentChangeBindings = new RemovableCollection();

        removableList.add(new Removable() {
            @Override
            public void remove() {
                flightPlanComponentChangeBindings.remove();
            }
        });

        removableList.add(
                currentFlightPlanComponent.observe(new Observer<FlightPlanComponent>() {
                @Override
                public void observe(FlightPlanComponent oldValue, final FlightPlanComponent newFlightPlanComponent, Observation<FlightPlanComponent> observation) {

                    flightPlanComponentChangeBindings.remove();

                    generalAttributesGroupModel.children().clear();
                    flightAttributesGroupModel.children().clear();
                    cameraAttributesGroupModel.children().clear();
                    gimbalAttributesGroupModel.children().clear();

                    waypointsGroupModel.children().clear();

                    for(FlightPlanComponent component : flightPlanLiveAttributesMap.keySet()){
                        ArrayList<AttributesViewModel> viewModelList = flightPlanLiveAttributesMap.get(component);
                        for(AttributesViewModel viewModel : viewModelList){
                            viewModel.destroy();
                        }
                    }
                    flightPlanLiveAttributesMap.clear();

                    if(newFlightPlanComponent == null){
                        return;
                    }

                    generalHeaderTextView.text().set(newFlightPlanComponent.componentType().getName() + " Options") ;

                    flightPlanLiveAttributesMap.put(newFlightPlanComponent,new ArrayList<AttributesViewModel>());

                    attributeScroll.scrollTo(0,0);
                    newFlightPlanComponent.startEdit();
                    flightPlanComponentChangeBindings.add(headerTextView.text().bind(newFlightPlanComponent.getName().transform(new Function<String,String>() {
                        @Override
                        public String apply(String input) {

                            if(input == null){
                                return "No Name";
                            }

                            return input;
                        }
                    })));

                    clearAllButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                        @Override
                        public Boolean apply(MotionEvent input) {

                            ArrayList<AttributesViewModel> viewModels = flightPlanLiveAttributesMap.get(newFlightPlanComponent);
                            for(AttributesViewModel viewModel : viewModels){
                                viewModel.clear();
                            }

                            return false;
                        }
                    });

                    AttributeData velocityAttribute = AttributeData.createSimpleDoubleValue("Velocity",0.5,1,null);
                    velocityAttribute.getDoubleValue().set((Double) newFlightPlanComponent.velocity().value());
                    flightPlanComponentChangeBindings.add(newFlightPlanComponent.velocity().bind(velocityAttribute.getDoubleValue()));
                    addAttribute(newFlightPlanComponent,velocityAttribute,flightAttributesGroupModel);

                    if(!isHideHover) {
                        AttributeData hoverAttribute = AttributeData.createSimpleIntegerValue("Hover Time", 1,1,10);
                        hoverAttribute.getIntValue().set((Integer) newFlightPlanComponent.hoverTime().value());
                        flightPlanComponentChangeBindings.add(newFlightPlanComponent.hoverTime().bind(hoverAttribute.getIntValue()));
                        addAttribute(newFlightPlanComponent, hoverAttribute, flightAttributesGroupModel);
                    }

                    if(!isHideHeading) {
                        AttributeData headingAttribute = AttributeData.createSimpleIntegerValue("Heading", 1, 0, 359);
                        headingAttribute.getIntValue().set((Integer) newFlightPlanComponent.heading().value());
                        flightPlanComponentChangeBindings.add(newFlightPlanComponent.heading().bind(headingAttribute.getIntValue()));
                        addAttribute(newFlightPlanComponent, headingAttribute, flightAttributesGroupModel);
                    }

                    AttributeData altitudeAttribute = AttributeData.createDoubleSpinner("Altitude",altitudeTypes,0.5,null,null);

                    AltitudeInfo currentAltitudeInfo = (AltitudeInfo) newFlightPlanComponent.altitudeInfo().value();

                    if(currentAltitudeInfo != null){
                        Double currentAltitude = currentAltitudeInfo.getValueInMeters();
                        AltitudeType currentAltitudeType = currentAltitudeInfo.getAltitudeType();

                        if(currentAltitude != null) {
                            altitudeAttribute.getDoubleValue().set(currentAltitude);
                        }

                        if(currentAltitudeType != null){
                            altitudeAttribute.getSelectedSpinner().set(currentAltitudeType);
                        }
                    }

                    flightPlanComponentChangeBindings.add(
                            altitudeAttribute.getDoubleValue().observe(new Observer<Double>() {
                                @Override
                                public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                                    AltitudeInfo currentInfo = (AltitudeInfo) newFlightPlanComponent.altitudeInfo().value();
                                    newFlightPlanComponent.altitudeInfo().set(currentInfo == null ? new AltitudeInfo(null,newValue) : currentInfo.valueInMeters(newValue));
                                }
                            })
                    );

                    flightPlanComponentChangeBindings.add(
                            altitudeAttribute.getSelectedSpinner().observe(new Observer<Object>() {
                                @Override
                                public void observe(Object oldValue, Object newValue, Observation<Object> observation) {
                                    AltitudeType newAltitudeType = (AltitudeType) newValue;
                                    AltitudeInfo currentInfo = (AltitudeInfo) newFlightPlanComponent.altitudeInfo().value();
                                    newFlightPlanComponent.altitudeInfo().set(currentInfo == null ? new AltitudeInfo(newAltitudeType,null) : currentInfo.altitudeType(newAltitudeType));
                                }
                            })
                    );
                    addAttribute(newFlightPlanComponent,altitudeAttribute,flightAttributesGroupModel);

                    AttributeData gimbalPitchAttribute = AttributeData.createSimpleIntegerValue("Pitch",1,0,90);
                    Integer pitchStartValue = (Integer) newFlightPlanComponent.gimbalPitch().value();
                    gimbalPitchAttribute.getIntValue().set(pitchStartValue != null ? -pitchStartValue : null);
                    flightPlanComponentChangeBindings.add(newFlightPlanComponent.gimbalPitch().bind(gimbalPitchAttribute.getIntValue().transform(new Function<Integer,Integer>() {
                        @Override
                        public Integer apply(Integer input) {

                            if(input == null){
                                return null;
                            }

                            return -input;
                        }
                    })));
                    addAttribute(newFlightPlanComponent,gimbalPitchAttribute,gimbalAttributesGroupModel);

                    AttributeData cameraActionAttribute = AttributeData.createSimpleSpinner("Action Type", CameraActionType.values());
                    cameraActionAttribute.getSelectedSpinner().set(newFlightPlanComponent.cameraActionType().value());
                    flightPlanComponentChangeBindings.add(

                            cameraActionAttribute.getSelectedSpinner().observe(new Observer<Object>() {

                                @Override
                                public void observe(Object oldValue, Object newValue, Observation<Object> observation) {
                                    CameraActionType newCameraActionType = (CameraActionType) newValue;
                                    newFlightPlanComponent.cameraActionType().set(newCameraActionType);
                                }
                            })
                    );
                    addAttribute(newFlightPlanComponent,cameraActionAttribute,cameraAttributesGroupModel);

//                    final AttributeData shootPhotoInIntervalAttribute = AttributeData.createSimpleIntegerValue("Interval Time",1,1,20);
//                    flightPlanComponentChangeBindings.add(newFlightPlanComponent.cameraActionType().observe(new Observer<CameraActionType>() {
//                        @Override
//                        public void observe(CameraActionType oldValue, CameraActionType newValue, Observation<CameraActionType> observation) {
//                            shootPhotoInIntervalAttribute.getVisbility().set(newValue == CameraActionType.STILLS ? VISIBLE : GONE);
//                        }
//                    }).observeCurrentValue());

//                    flightPlanComponentChangeBindings.add(shootPhotoInIntervalAttribute.getVisbility().bind(newFlightPlanComponent.cameraActionType().equalsTo(CameraActionType.STILLS).transform(new Function<Boolean, ViewModel.Visibility>() {
//                        @Override
//                        public ViewModel.Visibility apply(Boolean input) {
//                            return input ? VISIBLE : GONE;
//                        }
//                    })));
//                    shootPhotoInIntervalAttribute.getIntValue().set((Integer) newFlightPlanComponent.shootPhotoInIntervalNumber().value());
//                    flightPlanComponentChangeBindings.add(newFlightPlanComponent.shootPhotoInIntervalNumber().bind(shootPhotoInIntervalAttribute.getIntValue()));
//                    addAttribute(newFlightPlanComponent,shootPhotoInIntervalAttribute,cameraAttributesGroupModel);

                    final AttributeDataResult attributeDataResult = newFlightPlanComponent.attributeDataList();
                    flightPlanComponentChangeBindings.add(new Removable() {
                        @Override
                        public void remove() {
                            attributeDataResult.getBindings().remove();
                        }
                    });
                    for(AttributeData attributeData : attributeDataResult.getAttributeDataList()){
                        addAttribute(newFlightPlanComponent,attributeData,generalAttributesGroupModel);
                    }

                    switch (newFlightPlanComponent.componentType()){

                        case CIRCLE:

                            final CircleFlightPlanComponent circleFlightPlanComponent = (CircleFlightPlanComponent) newFlightPlanComponent;

                            flightPlanComponentChangeBindings.add(velocityAttribute.getVisbility().bind(circleFlightPlanComponent.cameraActionType().equalsTo(CameraActionType.STILLS).toggle(GONE,VISIBLE)));

                            AttributeData isLookToMidAttribute = AttributeData.createSimpleSwitch("Look to Mid");
                            isLookToMidAttribute.getSelectedSwitch().set(circleFlightPlanComponent.isLookToMid().value());
                            flightPlanComponentChangeBindings.add(circleFlightPlanComponent.isLookToMid().bind(isLookToMidAttribute.getSelectedSwitch()));
                            addAttribute(circleFlightPlanComponent,isLookToMidAttribute,gimbalAttributesGroupModel);

                            pointInfo.visibility().set(VISIBLE);
                            addWaypointButton.visibility().set(GONE);
                            clearWaypointsButton.visibility().set(VISIBLE);
                            waypointScrollView.visibility().set(GONE);

                            clearWaypointsButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                                @Override
                                public Boolean apply(MotionEvent input) {
                                    circleFlightPlanComponent.centerLocation().set(null);
                                    return false;
                                }
                            });

                            pointInfo.singleTap().set(new Function<MotionEvent, Boolean>() {
                                @Override
                                public Boolean apply(MotionEvent input) {

                                    Location pointLocation = circleFlightPlanComponent.centerLocation().value();
                                    if(pointLocation != null){
                                        mapViewModel.focusRequests().set(pointLocation);
                                    }

                                    return false;
                                }
                            });

                            flightPlanComponentChangeBindings.add(
                                    tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentState().observe(new Observer<MissionState>() {
                                        @Override
                                        public void observe(MissionState oldValue, MissionState newValue, Observation<MissionState> observation) {

                                            crossHairRemovable.remove();

                                            if(newValue == MissionState.EDIT_COMPONENT){
                                                crossHairRemovable = crosshairLocation.observe(new Observer<Location>() {
                                                    @Override
                                                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                                                        circleFlightPlanComponent.centerLocation().set(newValue);
                                                    }
                                                });
                                            }
                                            else{
                                                crossHairRemovable = Removable.STUB;
                                            }
                                        }
                                    }).observeCurrentValue()
                            );

                            flightPlanComponentChangeBindings.add(new Removable() {
                                @Override
                                public void remove() {
                                    crossHairRemovable.remove();
                                    crossHairRemovable = STUB;
                                }
                            });

                            break;
                        case WAYPOINTS:
                            break;
                        case RADIATOR:
                            final RadiatorFlightPlanComponent radiatorFlightPlanComponent = (RadiatorFlightPlanComponent) newFlightPlanComponent;

                            pointInfo.visibility().set(VISIBLE);
                            addWaypointButton.visibility().set(GONE);
                            clearWaypointsButton.visibility().set(VISIBLE);
                            waypointScrollView.visibility().set(GONE);

                            clearWaypointsButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                                @Override
                                public Boolean apply(MotionEvent input) {
                                    radiatorFlightPlanComponent.centerLocation().set(null);
                                    return false;
                                }
                            });

                            pointInfo.singleTap().set(new Function<MotionEvent, Boolean>() {
                                @Override
                                public Boolean apply(MotionEvent input) {

                                    Location pointLocation = radiatorFlightPlanComponent.centerLocation().value();
                                    if(pointLocation != null){
                                        mapViewModel.focusRequests().set(pointLocation);
                                    }

                                    return false;
                                }
                            });

                            flightPlanComponentChangeBindings.add(
                                    tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentState().observe(new Observer<MissionState>() {
                                        @Override
                                        public void observe(MissionState oldValue, MissionState newValue, Observation<MissionState> observation) {

                                            crossHairRemovable.remove();

                                            if(newValue == MissionState.EDIT_COMPONENT){
                                                crossHairRemovable = crosshairLocation.observe(new Observer<Location>() {
                                                    @Override
                                                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                                                        radiatorFlightPlanComponent.centerLocation().set(newValue);
                                                    }
                                                });
                                            }
                                            else{
                                                crossHairRemovable = Removable.STUB;
                                            }
                                        }
                                    }).observeCurrentValue()
                            );

                            flightPlanComponentChangeBindings.add(new Removable() {
                                @Override
                                public void remove() {
                                    crossHairRemovable.remove();
                                    crossHairRemovable = STUB;
                                }
                            });
                            break;
                    }


//                    if(oldValue != null){
//                        flightPlanAttributeBindingsMap.remove(oldValue).remove();
//
//                        ArrayList<AttributesViewModel> viewModelList = flightPlanLiveAttributesMap.remove(oldValue);
//                        for(AttributesViewModel viewModel : viewModelList){
//                            viewModel.destroy();
//                        }
//                    }
//
//                    if(newValue == null){
//                        return;
//                    }
//
//                    generalAttributesGroupModel.children().clear();
//                    waypointsGroupModel.children().clear();
//
//                    attributeScroll.scrollTo(0,0);
//
//                    newValue.startEdit();
//
//
//
//                    switch (newValue.componentType()){
//
//                        case CIRCLE:
//
//                            final CircleFlightPlanComponent circleFlightPlanComponent = (CircleFlightPlanComponent) newValue;
//
//                            pointInfo.visibility().set(VISIBLE);
//                            addWaypointButton.visibility().set(GONE);
//                            clearWaypointsButton.visibility().set(VISIBLE);
//
//                            clearWaypointsButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//                                @Override
//                                public Boolean apply(MotionEvent input) {
//                                    circleFlightPlanComponent.centerLocation().set(null);
//                                    return false;
//                                }
//                            });
//
//                            pointInfo.singleTap().set(new Function<MotionEvent, Boolean>() {
//                                @Override
//                                public Boolean apply(MotionEvent input) {
//
//                                    Location pointLocation = circleFlightPlanComponent.centerLocation().value();
//                                    if(pointLocation != null){
//                                        mapViewModel.focusRequests().set(pointLocation);
//                                    }
//
//                                    return false;
//                                }
//                            });
//
//                            removableList.add(tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentState().observe(new Observer<MissionState>() {
//                                @Override
//                                public void observe(MissionState oldValue, MissionState newValue, Observation<MissionState> observation) {
//                                    if(newValue == MissionState.EDIT_COMPONENT){
//                                        crossHairRemovable.remove();
//                                        crossHairRemovable = crosshairLocation.observe(new Observer<Location>() {
//                                            @Override
//                                            public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                                                circleFlightPlanComponent.centerLocation().set(newValue);
//                                            }
//                                        });
//                                    }
//                                    else{
//                                        crossHairRemovable.remove();
//                                        crossHairRemovable = Removable.STUB;
//                                    }
//                                }
//                                }).observeCurrentValue()
//                            );
//
//                            removableList.add(new Removable() {
//                                @Override
//                                public void remove() {
//                                    crossHairRemovable.remove();
//                                    crossHairRemovable = STUB;
//                                }
//                            });
//                            break;
//                        case RADIATOR:
//                            final RadiatorFlightPlanComponent radiatorFlightPlanComponent = (RadiatorFlightPlanComponent) newValue;
//
//                            pointInfo.visibility().set(VISIBLE);
//                            addWaypointButton.visibility().set(GONE);
//                            clearWaypointsButton.visibility().set(VISIBLE);
//
//                            clearWaypointsButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//                                @Override
//                                public Boolean apply(MotionEvent input) {
//                                    radiatorFlightPlanComponent.centerLocation().set(null);
//                                    return false;
//                                }
//                            });
//
//                            pointInfo.singleTap().set(new Function<MotionEvent, Boolean>() {
//                                @Override
//                                public Boolean apply(MotionEvent input) {
//
//                                    Location pointLocation = radiatorFlightPlanComponent.centerLocation().value();
//                                    if(pointLocation != null){
//                                        mapViewModel.focusRequests().set(pointLocation);
//                                    }
//
//                                    return false;
//                                }
//                            });
//
//                            removableList.add(tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentState().observe(new Observer<MissionState>() {
//                                        @Override
//                                        public void observe(MissionState oldValue, MissionState newValue, Observation<MissionState> observation) {
//                                            if(newValue == MissionState.EDIT_COMPONENT){
//                                                crossHairRemovable.remove();
//                                                crossHairRemovable = crosshairLocation.observe(new Observer<Location>() {
//                                                    @Override
//                                                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                                                        radiatorFlightPlanComponent.centerLocation().set(newValue);
//                                                    }
//                                                });
//                                            }
//                                            else{
//                                                crossHairRemovable.remove();
//                                                crossHairRemovable = Removable.STUB;
//                                            }
//                                        }
//                                    }).observeCurrentValue()
//                            );
//
//                            removableList.add(new Removable() {
//                                @Override
//                                public void remove() {
//                                    crossHairRemovable.remove();
//                                    crossHairRemovable = STUB;
//                                }
//                            });
//                            break;
//                        case WAYPOINTS:
//                            addWaypointButton.visibility().set(GONE);
//                            clearWaypointsButton.visibility().set(GONE);
//                            break;
//                    }
//
//                    waypointScrollView.visibility().set(newValue.componentType() == FlightPlanComponentType.WAYPOINTS ? VISIBLE : GONE);
//
//                    headerTextView.text().set(newValue.getName().value() == null ? "No Name" : (String) newValue.getName().value());
//
//                    AttributeDataResult result = newValue.attributeDataList();
//                    flightPlanAttributeBindingsMap.put(newValue,result.getBindings());
//
//                    final List<AttributeData> attributeDatas = result.getAttributeDataList();
//
//                    flightPlanLiveAttributesMap.put(newValue,new ArrayList<AttributesViewModel>());
//                    for(AttributeData data : attributeDatas){
//                        addAttribute(newValue,data);
//                    }
//
//                    clearAllButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//                        @Override
//                        public Boolean apply(MotionEvent input) {
//
//                            ArrayList<AttributesViewModel> viewModels = flightPlanLiveAttributesMap.get(newValue);
//                            for(AttributesViewModel viewModel : viewModels){
//                                viewModel.clear();
//                            }
//
//                            switch (newValue.componentType()){
//
//                                case CIRCLE:
//                                    ((CircleFlightPlanComponent)newValue).centerLocation().set(null);
//                                    break;
//                                case WAYPOINTS:
//                                    break;
//                                case RADIATOR:
//                                    break;
//                            }
//
//                            return false;
//                        }
//                    });
                }
            },UI_EXECUTOR)
        );

        removableList.add(
                mainMenu.visibility().bind(tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentState().equalsTo(MissionState.EDIT_COMPONENT).toggle(VISIBLE,GONE))
        );

//        final WaypointAttributePlannerInfo tabInfo = tabModel.getFunctionsModel().getMissionPlannerFunction().getPathPlannerInfo();
//
//        waypointsGroupModel.children().clear();
//        generalAttributesGroupModel.children().clear();
//
//        final ObservableList<Waypoint> tabWaypoints = tabInfo.getWaypoints();
//
//        for(Waypoint waypoint : tabWaypoints){
//            addWaypoint(waypoint,tabWaypoints,tabInfo);
//        }
//
//        final ObservableList<AttributeData> tabAttributes = tabInfo.getAttributes();
//
//        for(AttributeData attributeData : tabAttributes){
//            addAttribute(attributeData);
//        }
//
//        clearWaypointsButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//                tabWaypoints.clear();
//                return false;
//            }
//        });
//
//        addWaypointButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                Location currentCrosshairLocation = crosshairLocation.value();
//
//                if(currentCrosshairLocation == null){
//                    return false;
//                }
//
//                tabWaypoints.add(new Waypoint(currentCrosshairLocation));
//                if(tabInfo.getCurrnetPlanningMode().value() == WaypointAttributePlannerInfo.PlaningMode.RADIATOR_PLAN) {
//                    tabInfo.calcRadiatorPlans();
//                }
//
//                return false;
//            }
//        });
//
//        clearAllButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//                tabWaypoints.clear();
//                for(AttributeData tabAttribute : tabAttributes){
//                    tabAttribute.getValue().set(null);
//                }
//
//                if(tabInfo.getCurrnetPlanningMode().value() == WaypointAttributePlannerInfo.PlaningMode.RADIATOR_PLAN) {
//                    tabInfo.calcRadiatorPlans();
//                }
//
//                return false;
//            }
//        });
//
//        saveButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                MissionExecution currentMissionExecution = tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentMissionExecution();
//                boolean isSyncSuccess = tabInfo.syncIntoMission(currentMissionExecution);
//
//                if(isSyncSuccess) {
//                    tabInfo.getIsMenuOpened().set(false);
//                    tabModel.getFunctionsModel().getMissionPlannerFunction().getIsMissionPlannerOpened().set(true);
//                }
//
//                return false;
//            }
//        });
//
//        returnButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                switch (tabInfo.getCurrnetPlanningMode().value()){
//
//                    case START_MISSION_EXECUTION:
//                        tabInfo.getIsMenuOpened().set(false);
//                        tabModel.getFunctionsModel().getMissionPlannerFunction().getIsMissionPlannerOpened().set(true);
//                        break;
//                    case FINISH_MISSION_EXECUTION:
//                        tabInfo.getIsMenuOpened().set(false);
//                        tabModel.getFunctionsModel().getMissionPlannerFunction().getIsMissionPlannerOpened().set(true);
//                        break;
//                    case RADIATOR_PLAN:
//                        tabInfo.getIsMenuOpened().set(false);
//                        tabModel.getFunctionsModel().getMissionPlannerFunction().getIsMissionPlannerOpened().set(true);
//                        break;
//                }
//
//                return false;
//            }
//        });
//
//        calcEstimatedTime();
//
//        return new RemovableCollection(
//                tabWaypoints.observe(new CollectionObserver<Waypoint>(){
//                    @Override
//                    public void added(Waypoint value, Observation<Waypoint> observation) {
//                        addWaypoint(value,tabWaypoints,tabInfo);
//                    }
//
//                    @Override
//                    public void removed(Waypoint value, Observation<Waypoint> observation) {
//                        removeWaypoint(value);
//                    }
//                }),
//                tabAttributes.observe(new CollectionObserver<AttributeData>(){
//                    @Override
//                    public void added(AttributeData value, Observation<AttributeData> observation) {
//                        addAttribute(value);
//                    }
//
//                    @Override
//                    public void removed(AttributeData value, Observation<AttributeData> observation) {
//                        removeAttribute(value);
//                    }
//                }),
//                mainMenu.visibility().bind(tabInfo.getIsMenuOpened().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)),
//                headerTextView.text().bind(tabInfo.getCurrnetPlanningMode().transform(new Function<WaypointAttributePlannerInfo.PlaningMode, String>() {
//                    @Override
//                    public String apply(WaypointAttributePlannerInfo.PlaningMode input) {
//
//                        if(input == null){
//                            return "Unknown";
//                        }
//
//                        return input.getDescription();
//                    }
//                }))
//        );

        returnButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                final FlightPlanComponent currentComponent = tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentEditedFlightPlanComponent().value();

                if(currentComponent.isModified()){
                    messageViewModel.addGeneralMessage("Return Without save", "You Made Changes,you are About the go back without saving the current plan, are you sure?",
                            ContextCompat.getDrawable(activity, R.drawable.return1), new MessageViewModel.MessageViewModelListener() {
                                @Override
                                public void onOkButtonPressed() {

                                    try {
                                        currentComponent.restore();
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentEditedFlightPlanComponent().set(null);
                                    crossHairRemovable.remove();
                                    crossHairRemovable = Removable.STUB;
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            });
                }
                else{
                    try {
                        currentComponent.restore();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentEditedFlightPlanComponent().set(null);
                    crossHairRemovable.remove();
                    crossHairRemovable = Removable.STUB;
                }


                return false;
            }
        });


        saveButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                FlightPlanComponent component = tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentEditedFlightPlanComponent().value();
                List<String> problems = component.illegalFields();
                if(problems.size() > 0){
                    String bodyMessage = "Your following fields have problems : ";
                    for(String problem : problems){
                        bodyMessage += "\n" + problem;
                    }
                    bodyMessage += "\nAre you sure you want to continue?";
                    messageViewModel.addGeneralMessage("Save", bodyMessage, ContextCompat.getDrawable(activity, R.drawable.save), new MessageViewModel.MessageViewModelListener() {
                        @Override
                        public void onOkButtonPressed() {
                            tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentEditedFlightPlanComponent().set(null);

                            crossHairRemovable.remove();
                            crossHairRemovable = Removable.STUB;
                        }

                        @Override
                        public void onCancelButtonPressed() {

                        }
                    });
                }
                else {
                    tabModel.getFunctionsModel().getMissionPlannerFunction().getCurrentEditedFlightPlanComponent().set(null);

                    crossHairRemovable.remove();
                    crossHairRemovable = Removable.STUB;
                }
                return false;
            }
        });

        return removableList;
    }

    private void addAttribute(FlightPlanComponent component,AttributeData newAttribute,ViewGroupModel model){
        View newAttributeView = model.inflate(R.layout.flight_plan_double_enum_attribute);
        AttributesViewModel newAttributeViewModel = new AttributesViewModel(newAttributeView,newAttribute,littleMessageViewModel);
        model.children().add(newAttributeViewModel);
        flightPlanLiveAttributesMap.get(component).add(newAttributeViewModel);
     }

//    private void removeAttribute(AttributeData attributeData){
//        for(UiModel uiModel : generalAttributesGroupModel.children()){
//            AttributesViewModel attributeViewModel = (AttributesViewModel)uiModel;
//            if(attributeData.equals(attributeViewModel.getAttributeData())){
//                attributeViewModel.destroy();
//                generalAttributesGroupModel.children().remove(uiModel);
//                return;
//            }
//        }
//    }

//    private void addWaypoint(final Waypoint waypoint, final ObservableList<Waypoint> tabWaypoints,final WaypointAttributePlannerInfo tabInfo){
//
//        View newWaypointView = waypointsGroupModel.inflate(R.layout.radiator_point);
//        final WaypointViewModel newWaypointViewModel = new WaypointViewModel(newWaypointView,waypoint);
//        waypointsGroupModel.children().add(newWaypointViewModel);
//
//        newWaypointViewModel.setSelfRemoveSingleTap(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//                tabWaypoints.remove(waypoint);
//
//                if(tabInfo.getCurrnetPlanningMode().value() == WaypointAttributePlannerInfo.PlaningMode.RADIATOR_PLAN) {
//                    tabInfo.calcRadiatorPlans();
//                }
//
//                return false;
//            }
//        });
//
//        newWaypointViewModel.setSelfInformationSingleTap(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                tabsModel.focusRequests().set(waypoint);
//                crosshairLocation.set(waypoint);
//                return false;
//            }
//        });
//
//        calcEstimatedTime();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                UI_EXECUTOR.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        waypointsScrollView.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        }).start();
//    }

//    private void removeWaypoint(Waypoint waypoint){
//        for(UiModel uiModel : waypointsGroupModel.children()){
//            WaypointViewModel waypointViewModel = (WaypointViewModel) uiModel;
//            if(waypoint.equals(waypointViewModel.getWaypoint())){
//                waypointsGroupModel.children().remove(uiModel);
//                return;
//            }
//        }
//
//        calcEstimatedTime();
//    }

    private void calcEstimatedTime(){

        double totalDistanceToCover = 0;

        for(int i=1; i<waypointsGroupModel.children().size(); i++){
            Location currentWaypointLocation = ((WaypointViewModel)waypointsGroupModel.children().get(i)).getWaypoint();
            Location lastWaypointLocation = ((WaypointViewModel)waypointsGroupModel.children().get(i-1)).getWaypoint();
            totalDistanceToCover += currentWaypointLocation.distance(lastWaypointLocation);
        }

        estimatedTimeTextView.text().set((int)((totalDistanceToCover/7)/60) + "(min)");
    }

    private Removable bindToNULL(){
        mainMenu.visibility().set(GONE);
        return Removable.STUB;
    }
}
