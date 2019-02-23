package eyesatop.unit.ui.models.specialfunctions;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.functions.TelemetryLocation;
import eyesatop.controller.mission.Mission;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.TaskBlocker;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;
import eyesatop.unit.ui.Colour;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.activities.EyesatopAppConfiguration;
import eyesatop.unit.ui.models.actionmenus.ActionMenuItemModel;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.map.mission.FlightPlanComponent;
import eyesatop.unit.ui.models.map.mission.MissionPlan;
import eyesatop.unit.ui.models.map.mission.MissionSession;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.DroneTabsModel;
import eyesatop.unit.ui.utils.ControllerUtils;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.DistanceUnitType;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.observablelistidan.ObservableListIdan;
import logs.LoggerTypes;

/**
 * Created by Idan on 29/04/2018.
 */

public class MissionExecutionMenuViewModel {

    private final ViewModel mainMenu;
    private Removable tabsBindings = Removable.STUB;

    private final MapViewModel mapViewModel;
    private final ObservableValue<Location> crosshairLocation;
    private final MessageViewModel messageViewModel;
    private final LittleMessageViewModel littleMessageViewModel;

    private final ImageViewModel loadSessionButton;
    private final ImageViewModel loadPlanButton;
    private final ImageViewModel returnButton;
    private final ImageViewModel relocateButton;
    private final ImageViewModel clearButton;
    private final ActionMenuItemModel goButton;
    private final ImageViewModel stopButton;

    private final ImageViewModel deleteButton;

    private final ImageViewModel dtmPlusButton;
    private final ImageViewModel dtmMinusButton;
    private final ViewModel dtmLayout;
    private final TextViewModel dtmCurrentValueText;

    private final ViewModel sessionInfoLayout;
    private final TextViewModel noSessionText;
    private final TextViewModel sessionNameText;
    private final TextViewModel flightPlanNameText;
    private final TextViewModel flightPlanInfoText;
    private final TextViewModel sessionRunStatusText;
    private final TextViewModel currentRowInfoText;

    private final Activity activity;

    final Executor blockersExecutor = Executors.newSingleThreadExecutor();
    final ObservableListIdan<TaskBlocker> flightBlockers = new ObservableListIdan<>();
    final ObservableListIdan<TaskBlocker> cameraBlockers = new ObservableListIdan<>();
    final ObservableListIdan<TaskBlocker> gimbalBlockers = new ObservableListIdan<>();
    final ObservableListIdan<TaskBlocker> homeBlockers = new ObservableListIdan<>();
    final Property<Mission.MissionStub> currentMissionStub = new Property<>();

    public MissionExecutionMenuViewModel(DroneTabsModel tabsModel,
                                         Activity activity,
                                         MapViewModel mapViewModel,
                                         ObservableValue<Location> crosshairLocation,
                                         MessageViewModel messageViewModel,
                                         LittleMessageViewModel littleMessageViewModel) {

        this.activity = activity;

        this.mainMenu = new ViewModel(activity.findViewById(R.id.includeMissionExecutionMenu));
        this.mapViewModel = mapViewModel;
        this.crosshairLocation = crosshairLocation;
        this.messageViewModel = messageViewModel;
        this.littleMessageViewModel = littleMessageViewModel;

        loadSessionButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionExecutionLoadSession));
        loadPlanButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionExecutionLoadPlan));
        returnButton = new ImageViewModel((ImageView)activity.findViewById(R.id.missionExecutionReturnToMainButton));
        relocateButton = new ImageViewModel((ImageView)activity.findViewById(R.id.missionExecutionRelocationButton));
        clearButton = new ImageViewModel((ImageView)activity.findViewById(R.id.missionExecutionClear));
        stopButton = new ImageViewModel((ImageView)activity.findViewById(R.id.missionExecutionStopButton));

        dtmLayout = new ViewModel(activity.findViewById(R.id.missionExecutionDtmLayout));
        dtmPlusButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionExecutionPlusButton));
        dtmMinusButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionExecutionMinusButton));
        dtmCurrentValueText = new TextViewModel((TextView) activity.findViewById(R.id.missionExecutionDtmTextView));

        sessionInfoLayout = new ViewModel(activity.findViewById(R.id.sessionInfoLayout));
        noSessionText = new TextViewModel((TextView) activity.findViewById(R.id.noSessionMessageTextView));
        sessionNameText = new TextViewModel((TextView) activity.findViewById(R.id.missionExecutionSessionName));
        flightPlanNameText = new TextViewModel((TextView) activity.findViewById(R.id.missionExecutionFlightPlanName));
        flightPlanInfoText = new TextViewModel((TextView) activity.findViewById(R.id.missionExecutionFlightPlanInfoText));
        sessionRunStatusText = new TextViewModel((TextView) activity.findViewById(R.id.missionExecutionRunStatusText));
        currentRowInfoText = new TextViewModel((TextView) activity.findViewById(R.id.missionExecutionCurrentRowRunning));

        this.deleteButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionExecutionDelete));

        goButton = new ActionMenuItemModel((ImageView)activity.findViewById(R.id.missionExecutionGo));

        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
                tabsBindings.remove();
                tabsBindings = bindToTab(newValue);
            }
        }).observeCurrentValue();
    }

    public void hideDtmPlusMinus(){
        dtmLayout.setAlwaysGone(true);
    }

    private Removable bindToTab(final DroneTabModel tabModel){

        if(tabModel == null){
            mainMenu.visibility().set(ViewModel.Visibility.GONE);
            return Removable.STUB;
        }

        final MissionExecutionFunction missionExecutionFunction = tabModel.getFunctionsModel().getMissionExecutionFunction();

        final RemovableCollection removables = new RemovableCollection();

        removables.add(
                mainMenu.visibility().bind(tabModel.getFunctionsModel().getMissionExecutionFunction().getIsMissionExecutionMenuOpened().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE))
        );

        removables.add(
                sessionInfoLayout.visibility().bind(missionExecutionFunction.getCurrentMissionSession().notNull().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE))
        );
        removables.add(
                noSessionText.visibility().bind(missionExecutionFunction.getCurrentMissionSession().notNull().toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE))
        );

        final RemovableCollection sessionInfoRemovables = new RemovableCollection();
        removables.add(sessionInfoRemovables);

        removables.add(
                missionExecutionFunction.getCurrentMissionSession().observe(new Observer<MissionSession>() {
                    @Override
                    public void observe(MissionSession oldValue, MissionSession newValue, Observation<MissionSession> observation) {
                        
                        sessionInfoRemovables.remove();

                        if(newValue == null) {
                            return;
                        }

                        ControllerUtils.MissionInfoResult result = ControllerUtils.missionString((AbstractDroneController) tabModel.getDroneController());
                        sessionInfoRemovables.add(result.getBindings());
                        sessionInfoRemovables.add(currentRowInfoText.text().bind(result.getMissionInfo()));

                        sessionInfoRemovables.add(
                                sessionNameText.text().bind(newValue.getSessionName().withDefault("N/A"))
                        );
                        flightPlanNameText.text().set(newValue.getMissionPlan().getName().withDefault("N/A").value());
                        flightPlanInfoText.text().set(newValue.getMissionPlan().toString());
                        sessionInfoRemovables.add(
                                sessionRunStatusText.text().bind(newValue.getIsRunning().transform(new Function<Boolean, String>() {
                                    @Override
                                    public String apply(Boolean input) {

                                        if(input == null){
                                            return "Unknown Status";
                                        }

                                        return input ? "Running" : "Not Running";
                                    }
                                }))
                        );
                    }
                }).observeCurrentValue()
        );

        if(tabModel.getDroneController() instanceof AbstractDroneController){

            AbstractDroneController controller = (AbstractDroneController) tabModel.getDroneController();
//            ControllerUtils.MissionInfoResult result = ControllerUtils.missionString(controller);
//            missionRowInfoTextViewModel.text().bind(result.getMissionInfo());
//            removables.add(result.getBindings());

            final DtmProvider dtmProvider = controller.getDtmProvider();

            dtmPlusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                @Override
                public Boolean apply(MotionEvent input) {
                    dtmProvider.raiseDTM(0.5D);
                    return false;
                }
            });

            dtmMinusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                @Override
                public Boolean apply(MotionEvent input) {
                    dtmProvider.lowerDTM(0.5D);
                    return false;
                }
            });

            removables.add(dtmCurrentValueText.text().bind(dtmProvider.dtmRaiseValue().transform(new Function<Double, String>() {
                @Override
                public String apply(Double input) {

                    if(input == null){
                        return "N/A";
                    }

                    return DistanceUnitType.formatNumber(EyesatopAppConfiguration.getInstance().getAppMeasureType().value(), 1, input);
                }
            })));
        }
        else{
//            missionRowInfoTextViewModel.text().set("");
            dtmMinusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                @Override
                public Boolean apply(MotionEvent input) {
                    return false;
                }
            });
            dtmPlusButton.singleTap().set(new Function<MotionEvent, Boolean>() {
                @Override
                public Boolean apply(MotionEvent input) {
                    return false;
                }
            });
            dtmCurrentValueText.text().set("N/A");
        }

        removables.add(relocateButton.tint().bind(missionExecutionFunction.getIsRelocateMarked().toggle(Colour.WRAP_ID.apply(R.color.cyan),Colour.WRAP_ID.apply(R.color.foreground))));

        relocateButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                missionExecutionFunction.getIsRelocateMarked().set(!missionExecutionFunction.getIsRelocateMarked().value());
                return false;
            }
        });

        clearButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                messageViewModel.addGeneralMessage(
                        "Clear Session",
                        "You are about the clear the current session, Are you sure?",
                        ContextCompat.getDrawable(activity,R.drawable.cancel), new MessageViewModel.MessageViewModelListener() {
                            @Override
                            public void onOkButtonPressed() {
                                missionExecutionFunction.getCurrentMissionSession().set(null);
                            }

                            @Override
                            public void onCancelButtonPressed() {

                            }
                        });

                return false;
            }
        });

        removables.add(
                missionExecutionFunction.getIsMissionExecutionMenuOpened().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        if(!newValue){
                            missionExecutionFunction.getIsRelocateMarked().setIfNew(false);
                        }
                    }
                }).observeCurrentValue()
        );

        removables.add(missionExecutionFunction.getCurrentMissionSession().observe(new Observer<MissionSession>() {
            @Override
            public void observe(MissionSession oldValue, MissionSession newValue, Observation<MissionSession> observation) {

                if(newValue == null){
                    missionExecutionFunction.getIsRelocateMarked().setIfNew(false);
                }
            }
        }));

        removables.add(new Removable() {
            @Override
            public void remove() {
                missionExecutionFunction.getIsRelocateMarked().setIfNew(false);
            }
        });

        removables.add(
                missionExecutionFunction.getCurrentMissionSession().observe(new Observer<MissionSession>() {
                    @Override
                    public void observe(MissionSession oldValue, MissionSession newValue, Observation<MissionSession> observation) {


                        if(newValue == null){
                            relocateButton.visibility().set(ViewModel.Visibility.GONE);
                        }
                        else{
                            removables.add(relocateButton.visibility().bind(newValue.getIsRunning().or(newValue.getIndex().notNull()).toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)));
                        }
                    }
                }).observeCurrentValue()
        );

        final AtomicReference<Removable> crosshairRemovable = new AtomicReference<>(Removable.STUB);

        removables.add(missionExecutionFunction.getIsRelocateMarked().observe(new Observer<Boolean>() {

            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {

                if(newValue) {
                    crosshairRemovable.get().remove();
                    crosshairRemovable.set(
                            crosshairLocation.observe(new Observer<Location>() {
                                @Override
                                public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                                    MissionSession currentSession = missionExecutionFunction.getCurrentMissionSession().value();
                                    MissionPlan currentPlan = currentSession == null ? null : currentSession.getMissionPlan();
                                    if(currentPlan != null){
                                        currentSession.getMissionPlan().relocate(newValue);
                                    }
                                }
                            })
                    );
                }
                else{
                    crosshairRemovable.get().remove();
                    crosshairRemovable.set(Removable.STUB);
                }
            }
        }).observeCurrentValue());

        removables.add(new Removable() {
            @Override
            public void remove() {
                crosshairRemovable.get().remove();
            }
        });

        final RemovableCollection missionSessionBindings = new RemovableCollection();

        removables.add(tabModel.getFunctionsModel().getMissionExecutionFunction().getCurrentMissionSession().observe(new Observer<MissionSession>() {
            @Override
            public void observe(MissionSession oldValue, final MissionSession newSession, Observation<MissionSession> observation) {

                missionSessionBindings.remove();

                goButton.visibility().set(ViewModel.Visibility.GONE);
                stopButton.visibility().set(ViewModel.Visibility.GONE);
                dtmLayout.visibility().set(ViewModel.Visibility.GONE);

                missionExecutionFunction.getIsRelocateMarked().setIfNew(false);

                if(newSession == null){
                    loadSessionButton.visibility().set(ViewModel.Visibility.VISIBLE);
                    loadPlanButton.visibility().set(ViewModel.Visibility.VISIBLE);
                    clearButton.visibility().set(ViewModel.Visibility.VISIBLE);
                    deleteButton.visibility().set(ViewModel.Visibility.VISIBLE);
                    return;
                }

                missionSessionBindings.add(goButton.visibility().bind(newSession.getIsRunning().toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)));
                missionSessionBindings.add(loadSessionButton.visibility().bind(newSession.getIsRunning().toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)));
                missionSessionBindings.add(loadPlanButton.visibility().bind(newSession.getIsRunning().toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)));
                missionSessionBindings.add(clearButton.visibility().bind(newSession.getIsRunning().toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)));
                missionSessionBindings.add(deleteButton.visibility().bind(newSession.getIsRunning().toggle(ViewModel.Visibility.GONE, ViewModel.Visibility.VISIBLE)));

                missionSessionBindings.add(stopButton.visibility().bind(newSession.getIsRunning().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)));
                missionSessionBindings.add(dtmLayout.visibility().bind(newSession.getIsRunning().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)));
            }
        }).observeCurrentValue());

        loadPlanButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabModel.getFunctionsModel().getMissionExecutionFunction().getPlansFileExplorer().showDialog();
                return false;
            }
        });

        deleteButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabModel.getFunctionsModel().getMissionExecutionFunction().getSessionDeleteFileExplorer().showDialog();
                return false;
            }
        });

        returnButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabModel.getFunctionsModel().getMissionExecutionFunction().getIsMissionExecutionMenuOpened().set(false);
                tabModel.getFunctionsModel().isFunctionScreenOpen().set(true);
                return false;
            }
        });

        loadSessionButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabModel.getFunctionsModel().getMissionExecutionFunction().getSessionsFileExplorer().showDialog();
                return false;
            }
        });

        final RemovableCollection goButtonBindings = new RemovableCollection();
        final RemovableCollection firstLocationBeforeSessionBindings = new RemovableCollection();
        final RemovableCollection currentMissionBindings = new RemovableCollection();

        removables.add(new Removable() {
            @Override
            public void remove() {
                firstLocationBeforeSessionBindings.remove();
            }
        });


        removables.add(new Removable() {
            @Override
            public void remove() {
                currentMissionBindings.remove();
            }
        });

        removables.add(new Removable() {
            @Override
            public void remove() {
                goButtonBindings.remove();
            }
        });

        removables.add(currentMissionStub.observe(new Observer<Mission.MissionStub>() {
            @Override
            public void observe(Mission.MissionStub oldValue, Mission.MissionStub newValue, Observation<Mission.MissionStub> observation) {

                flightBlockers.clear();
                cameraBlockers.clear();
                gimbalBlockers.clear();
                homeBlockers.clear();
                goButtonBindings.remove();

                if(newValue != null){
                    final HashMap<TaskCategory,List<EnumWithName>> resourceMap = newValue.resourcesRequired();
                    goButtonBindings.add(
                            tabModel.getDroneController().flightTasks().tasksBlockers().observe(new Observer<FlightTaskBlockerType>() {
                                @Override
                                public void observe(FlightTaskBlockerType oldValue, FlightTaskBlockerType newValue, Observation<FlightTaskBlockerType> observation) {

                                    flightBlockers.clear();

                                    for(EnumWithName requiredTask : resourceMap.get(TaskCategory.FLIGHT)){
                                        for(TaskBlocker flightBlocker : ControllerUtils.taskBlockers(tabModel.getDroneController(),TaskCategory.FLIGHT,requiredTask)){
                                            if(!flightBlockers.contains(flightBlocker)){
                                                flightBlockers.add(flightBlocker);
                                            }
                                        }
                                    }
                                }
                            },blockersExecutor)
                    );

                    goButtonBindings.add(
                            tabModel.getDroneController().gimbal().tasksBlockers().observe(new Observer<GimbalTaskBlockerType>() {
                                @Override
                                public void observe(GimbalTaskBlockerType oldValue, GimbalTaskBlockerType newValue, Observation<GimbalTaskBlockerType> observation) {

                                    gimbalBlockers.clear();

                                    for(EnumWithName requiredTask : resourceMap.get(TaskCategory.GIMBAL)){
                                        for(TaskBlocker gimbalBlocker : ControllerUtils.taskBlockers(tabModel.getDroneController(),TaskCategory.GIMBAL,requiredTask)){
                                            if(!gimbalBlockers.contains(gimbalBlocker)){
                                                gimbalBlockers.add(gimbalBlocker);
                                            }
                                        }
                                    }
                                }
                            },blockersExecutor)
                    );

                    goButtonBindings.add(
                            tabModel.getDroneController().camera().tasksBlockers().observe(new Observer<CameraTaskBlockerType>() {
                                @Override
                                public void observe(CameraTaskBlockerType oldValue, CameraTaskBlockerType newValue, Observation<CameraTaskBlockerType> observation) {

                                    cameraBlockers.clear();

                                    for(EnumWithName requiredTask : resourceMap.get(TaskCategory.CAMERA)){
                                        for(TaskBlocker cameraBlocker : ControllerUtils.taskBlockers(tabModel.getDroneController(),TaskCategory.CAMERA,requiredTask)){
                                            if(!cameraBlockers.contains(cameraBlocker)){
                                                cameraBlockers.add(cameraBlocker);
                                            }
                                        }
                                    }
                                }
                            },blockersExecutor)
                    );

                    goButtonBindings.add(
                            tabModel.getDroneController().droneHome().taskBlockers().observe(new Observer<HomeTaskBlockerType>() {
                                @Override
                                public void observe(HomeTaskBlockerType oldValue, HomeTaskBlockerType newValue, Observation<HomeTaskBlockerType> observation) {

                                    homeBlockers.clear();

                                    for(EnumWithName requiredTask : resourceMap.get(TaskCategory.HOME)){
                                        for(TaskBlocker homeBlocker : ControllerUtils.taskBlockers(tabModel.getDroneController(),TaskCategory.HOME,requiredTask)){
                                            if(!cameraBlockers.contains(homeBlocker)){
                                                cameraBlockers.add(homeBlocker);
                                            }
                                        }
                                    }
                                }
                            },blockersExecutor)
                    );

                    blockersExecutor.execute(new Runnable() {
                        @Override
                        public void run() {

                            for(EnumWithName requiredTask : resourceMap.get(TaskCategory.HOME)){
                                for(TaskBlocker homeBlocker : ControllerUtils.taskBlockers(tabModel.getDroneController(),TaskCategory.HOME,requiredTask)){
                                    if(!homeBlockers.contains(homeBlocker)){
                                        homeBlockers.add(homeBlocker);
                                    }
                                }
                            }

                            for(EnumWithName requiredTask : resourceMap.get(TaskCategory.FLIGHT)){
                                for(TaskBlocker flightBlocker : ControllerUtils.taskBlockers(tabModel.getDroneController(),TaskCategory.FLIGHT,requiredTask)){
                                    if(!flightBlockers.contains(flightBlocker)){
                                        flightBlockers.add(flightBlocker);
                                    }
                                }
                            }

                            for(EnumWithName requiredTask : resourceMap.get(TaskCategory.GIMBAL)){
                                for(TaskBlocker gimbalBlocker : ControllerUtils.taskBlockers(tabModel.getDroneController(),TaskCategory.GIMBAL,requiredTask)){
                                    if(!gimbalBlockers.contains(gimbalBlocker)){
                                        gimbalBlockers.add(gimbalBlocker);
                                    }
                                }
                            }

                            for(EnumWithName requiredTask : resourceMap.get(TaskCategory.CAMERA)){
                                for(TaskBlocker cameraBlocker : ControllerUtils.taskBlockers(tabModel.getDroneController(),TaskCategory.CAMERA,requiredTask)){
                                    if(!cameraBlockers.contains(cameraBlocker)){
                                        cameraBlockers.add(cameraBlocker);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        }).observeCurrentValue());

        removables.add(
                goButton.clickable().bind(flightBlockers.isListEmpty().and(gimbalBlockers.isListEmpty().and(cameraBlockers.isListEmpty().and(homeBlockers.isListEmpty().and(currentMissionStub.notNull())))))
        );

        removables.add(missionExecutionFunction.getCurrentMissionSession().observe(new Observer<MissionSession>() {
            @Override
            public void observe(MissionSession oldValue, final MissionSession newValue, Observation<MissionSession> observation) {

                firstLocationBeforeSessionBindings.remove();

                if(newValue != null && tabModel.getDroneController() != null){

                    if(newValue.getIndex().isNull()){
                        firstLocationBeforeSessionBindings.add(newValue.getFirstLocationBeforeSession().bind(tabModel.getDroneController().telemetry().transform(new TelemetryLocation())));
                        firstLocationBeforeSessionBindings.add(newValue.getIndex().observe(new Observer<Integer>() {
                            @Override
                            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                                if(newValue != null && oldValue == null){
                                    firstLocationBeforeSessionBindings.remove();
                                }
                            }
                        }).observeCurrentValue());
                    }

                    currentMissionBindings.add(newValue.getFirstLocationBeforeSession().observe(new Observer<Location>() {
                        @Override
                        public void observe(Location oldValue, Location newLocation, Observation<Location> observation) {
                            if(newLocation != null && oldValue == null){
                                try {
                                    currentMissionStub.set(newValue.getMission().getMission());
                                } catch (DroneTaskException e) {
                                    MainLogger.logger.write_message(LoggerTypes.ERROR,"Unable to create mission : " + e.getErrorString());
                                    currentMissionStub.set(null);
                                }
                            }
                            else if(newLocation == null){
                                currentMissionStub.setIfNew(null);
                            }
                        }
                    }).observeCurrentValue());
                }

            }
        }).observeCurrentValue());

        stopButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                messageViewModel.addGeneralMessage("Stop Mission", "You are about to stop the current mission, are you sure?", ContextCompat.getDrawable(activity,R.drawable.stop), new MessageViewModel.MessageViewModelListener() {
                    @Override
                    public void onOkButtonPressed() {

                        AbstractDroneController controller = (AbstractDroneController) tabModel.getDroneController();
                        Mission currentMission = (Mission) controller.getMissionManager().currentTask().value();
                        if(currentMission != null){
                            currentMission.cancel();
                        }

                    }

                    @Override
                    public void onCancelButtonPressed() {

                    }
                });

                return false;
            }
        });

        goButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                missionExecutionFunction.getIsRelocateMarked().setIfNew(false);

                if(goButton.clickable().withDefault(true).value() == false){
                    try {

                        String errorString = "Unable to Start Mission.\n";

                        if(currentMissionStub.isNull()){
                            errorString += "Has no Drone Location before the mission.\n";
                        }

                        if (flightBlockers.size() > 0) {
                            errorString += "Flight : ";
                            errorString += flightBlockers.get(0).getName();
                            for (int i = 1; i < flightBlockers.size();i++) {
                                errorString += " , " + flightBlockers.get(i).getName();
                            }
                            errorString += "\n";
                        }

                        if (gimbalBlockers.size() > 0) {
                            errorString += "Gimbal : ";
                            errorString += gimbalBlockers.get(0).getName();
                            for (int i = 1; i < gimbalBlockers.size();i++) {
                                errorString += " , " + gimbalBlockers.get(i).getName();
                            }
                            errorString += "\n";
                        }

                        if (cameraBlockers.size() > 0) {
                            errorString += "Camera : ";
                            errorString += cameraBlockers.get(0).getName();
                            for (int i = 1; i < cameraBlockers.size();i++) {
                                errorString += " , " + cameraBlockers.get(i).getName();
                            }
                            errorString += "\n";
                        }

                        if (homeBlockers.size() > 0) {
                            errorString += "Home : ";
                            errorString += homeBlockers.get(0).getName();
                            for (int i = 1; i < homeBlockers.size();i++) {
                                errorString += " , " + homeBlockers.get(i).getName();
                            }
                            errorString += "\n";
                        }
                        littleMessageViewModel.addNewMessage(errorString);
                    }
                    catch (Exception e){
                        littleMessageViewModel.addNewMessage("Error when tried to find why we can't GO");
                    }
                    return false;
                }

                MissionSession missionSession = missionExecutionFunction.getCurrentMissionSession().value();

                if(missionSession == null){
                    littleMessageViewModel.addNewMessage("Failed to Start Mission,has no mission session.");
                    return false;
                }

                final AbstractDroneController controller = (AbstractDroneController) tabModel.getDroneController();

                Double maxAltitude = controller.droneHome().maxAltitudeFromTakeOffLocation().value();
                if(maxAltitude != null) {
                    try {

                        Location homeLocation = null;
                        Boolean isDroneFlying = controller.flying().value();

                        if(isDroneFlying != null && isDroneFlying){
                            homeLocation = controller.droneHome().homeLocation().value();
                        }
                        else{
                            homeLocation = Telemetry.telemetryToLocation(controller.telemetry().value());
                        }

                        if(homeLocation == null){
                            throw new TerrainNotFoundException("Unknown home location");
                        }

                        double highestASL = missionSession.highestASLOnSession(controller.getDtmProvider(),controller.droneHome().homeLocation().value(),Telemetry.telemetryToLocation(controller.telemetry().value()));

                        final double maxAltitudeRequired = 10D + highestASL - controller.getDtmProvider().terrainAltitude(homeLocation);

                        if(maxAltitudeRequired >= 400D){
                            littleMessageViewModel.addNewMessage("max altitude required is too high : " + maxAltitudeRequired);
                            return false;
                        }

                        if(maxAltitudeRequired > maxAltitude){
                            messageViewModel.addGeneralMessage(
                                    "Max Altitude Update",
                                    "You need at least " + maxAltitudeRequired + " meter of max altitude. We will change the max altitude to that value. Confirm?",
                                    ContextCompat.getDrawable(activity, R.drawable.btn_takeoff), new MessageViewModel.MessageViewModelListener() {
                                        @Override
                                        public void onOkButtonPressed() {
                                            try {
                                                controller.droneHome().setMaxAltitudeFromTakeOffLocation(maxAltitudeRequired);
                                            } catch (DroneTaskException e) {
                                                littleMessageViewModel.addNewMessage("Failed to set max altitude to : " + maxAltitudeRequired);
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelButtonPressed() {

                                        }
                                    });
                            return false;
                        }
                    } catch (TerrainNotFoundException e) {
                        e.printStackTrace();
                        littleMessageViewModel.addNewMessage("Unable to calc terrain : " + e.getErrorInfo());
                    }
                }

                messageViewModel.addGeneralMessage("Start Mission", "The Drone will takeoff to altitude of 15(m) and start the mission. Are you sure? ", ContextCompat.getDrawable(activity, R.drawable.btn_fmode_obli), new MessageViewModel.MessageViewModelListener() {
                    @Override
                    public void onOkButtonPressed() {
                        try {
                            missionExecutionFunction.startMissionSession();
                        } catch (DroneTaskException e) {
                            e.printStackTrace();
                            littleMessageViewModel.addNewMessage("Unable to start Mission : " + e.getErrorString());
                        }
                    }

                    @Override
                    public void onCancelButtonPressed() {
                    }
                });

                return false;
            }
        });

        return removables;
    }
}
