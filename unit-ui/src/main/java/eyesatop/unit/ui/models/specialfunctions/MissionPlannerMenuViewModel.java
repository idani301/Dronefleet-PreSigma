package eyesatop.unit.ui.models.specialfunctions;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import eyesatop.controller.DroneController;
import eyesatop.controller.functions.TelemetryLocation;
import eyesatop.controller.mission.flightplans.FlightPlanComponentType;
import eyesatop.unit.ui.Colour;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.EditTextViewModel;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.ViewGroupModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.map.mission.CircleFlightPlanComponent;
import eyesatop.unit.ui.models.map.mission.FlightPlanComponent;
import eyesatop.unit.ui.models.map.mission.MissionPlan;
import eyesatop.unit.ui.models.map.mission.MissionPlanSnapshot;
import eyesatop.unit.ui.models.map.mission.RadiatorFlightPlanComponent;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.unit.ui.models.missionplans.uicomponents.FlightComponentAttributeViewModel;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.DroneTabsModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.android.files.EyesatopAppsFilesUtils;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.observablelistidan.ListObserver;
import eyesatop.util.serialization.Serialization;

/**
 * Created by Idan on 13/11/2017.
 */

public class MissionPlannerMenuViewModel {

    private final ViewModel mainMenu;
    private Removable bindToTabRemovable = Removable.STUB;

    private final ImageViewModel returnButton;
    private final EditTextViewModel missionNameEditText;
    private final ImageViewModel loadFileButton;
    private final ImageViewModel clearButton;
    private final ImageViewModel saveButton;
    private final ImageViewModel deletePlanButton;

    private final ImageViewModel addCircleButton;
    private final ImageViewModel addRadiatorButton;
    private final ImageViewModel addWaypointsButton;
  //  private final TextViewModel startMissionPlanButton;
  //  private final TextViewModel finishMissionPlanButton;

 //   private final ImageViewModel editFlightPlanButton;
    private final LittleMessageViewModel littleMessageViewModel;

  //  private final TextViewModel estimatedTimeText;
    private final MessageViewModel messageViewModel;

    private final ImageViewModel addFlightComponentButton;
    private final ImageViewModel addFlightComponentExitButton;
    private final ViewModel addFlightComponentView;
    private final ViewGroupModel missionListContainer;
    private final MapViewModel mapViewModel;
    private final ImageViewModel relocateButton;
    private final ObservableValue<Location> crosshairLocation;
    private final Activity activity;

    public void hideAddComponentButton(FlightPlanComponentType type,boolean isAlwaysGone){

        switch (type){

            case CIRCLE:
                addCircleButton.setAlwaysGone(isAlwaysGone);
                break;
            case WAYPOINTS:
                addWaypointsButton.setAlwaysGone(isAlwaysGone);
                break;
            case RADIATOR:
                addRadiatorButton.setAlwaysGone(isAlwaysGone);
                break;
        }
    }

    public MissionPlannerMenuViewModel(DroneTabsModel tabsModel,
                                       Activity activity,
                                       MapViewModel mapViewModel,
                                       ObservableValue<Location> crosshairLocation,
                                       MessageViewModel messageViewModel,
                                       LittleMessageViewModel littleMessageViewModel) {

        this.activity = activity;
        this.littleMessageViewModel = littleMessageViewModel;
        this.mapViewModel = mapViewModel;
        this.crosshairLocation = crosshairLocation;

        mainMenu = new ViewModel(activity.findViewById(R.id.includeMissionPlanner));
        returnButton = new ImageViewModel((ImageView) activity.findViewById(R.id.closeMissionPlanner));
        missionNameEditText = new EditTextViewModel((EditText) activity.findViewById(R.id.missionPlannerName));
        loadFileButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionPlannerLoad));
        clearButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionPlannerClear));
        saveButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionPlannerSave));

        addCircleButton = new ImageViewModel((ImageView)activity.findViewById(R.id.ivCircle));
        addRadiatorButton = new ImageViewModel((ImageView)activity.findViewById(R.id.ivZigZag));
        addWaypointsButton = new ImageViewModel((ImageView) activity.findViewById(R.id.ivRoute));

        relocateButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionPlannerRelocateAll));

        addFlightComponentButton = new ImageViewModel((ImageView) activity.findViewById(R.id.ivAddComponent));
        addFlightComponentView = new ViewModel(activity.findViewById(R.id.includeMissionAddNew));
        addFlightComponentExitButton = new ImageViewModel((ImageView) activity.findViewById(R.id.addFlightComponentExit));
        missionListContainer = new ViewGroupModel((ViewGroup)activity.findViewById(R.id.missionsListContainer));

        deletePlanButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionPlannerDelete));

       // editFlightPlanButton = new ImageViewModel((ImageView) activity.findViewById(R.id.missionExecutionFlightPlanEditButton));

       // startMissionPlanButton = new TextViewModel((TextView) activity.findViewById(R.id.goToMissionPlanButton));
       // finishMissionPlanButton = new TextViewModel((TextView) activity.findViewById(R.id.finishMissionPlanButton));

      //  estimatedTimeText = new TextViewModel((TextView)activity.findViewById(R.id.missionExecutionFlightPlanEstimateTimeEstimateTime));
        this.messageViewModel = messageViewModel;

        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            @Override
            public void observe(DroneTabModel oldValue, final DroneTabModel newValue, Observation<DroneTabModel> observation) {

                bindToTabRemovable.remove();

                if(newValue == null){
                    bindToTabRemovable = bindToNULL();
                    return;
                }

                final DroneController tabController = newValue.getDroneController();
                if(tabController != null){
                    bindToTabRemovable = bindToTab(newValue,tabController);
                    return;
                }
                else{
                    bindToTabRemovable = bindToNULL();
                    return;
                }
            }
        }).observeCurrentValue();

    }

    private final HashMap<FlightPlanComponent,FlightComponentAttributeViewModel> componentViewModelMap = new HashMap<>();

    private Removable bindToTab(final DroneTabModel tabModel, final DroneController controller){

        ArrayList<Removable> removableList = new ArrayList<>();
        final MissionPlannerFunction tabMissionPlannerFunction = tabModel.getFunctionsModel().getMissionPlannerFunction();
        final MissionPlan currentMission = tabMissionPlannerFunction.getMissionPlan();

        componentViewModelMap.clear();
        missionListContainer.children().clear();

        removableList.add(
                currentMission.getVisibleOnMap().bind(tabMissionPlannerFunction.getCurrentState().equalsTo(MissionState.NONE).not())
        );

        removableList.add(new Removable() {
            @Override
            public void remove() {
                currentMission.getVisibleOnMap().set(false);
            }
        });

        removableList.add(currentMission.addToMap(mapViewModel));
        removableList.add(currentMission.getLastLocationBeforeMission().bind(tabModel.getDroneController().telemetry().transform(new TelemetryLocation())));

        ListObserver<FlightPlanComponent> listObserver = new ListObserver<FlightPlanComponent>() {
            @Override
            public void added(final FlightPlanComponent value, int index) {
                View newView = missionListContainer.inflate(R.layout.mission_attribute);
                FlightComponentAttributeViewModel newComponent = new FlightComponentAttributeViewModel(newView,value);
                missionListContainer.children().add(newComponent);
                componentViewModelMap.put(value,newComponent);

                newComponent.setListener(new FlightComponentAttributeViewModel.FlightComponentListener() {
                    @Override
                    public void onTextPressed(FlightPlanComponent flightPlanComponent) {
                        tabMissionPlannerFunction.getCurrentEditedFlightPlanComponent().set(flightPlanComponent);
                    }

                    @Override
                    public void onUpButtonPressed(FlightPlanComponent flightPlanComponent) {
                        tabMissionPlannerFunction.getMissionPlan().getFlightPlanComponents().pushDown(flightPlanComponent);
                    }

                    @Override
                    public void onDownButtonPressed(FlightPlanComponent flightPlanComponent) {
                        tabMissionPlannerFunction.getMissionPlan().getFlightPlanComponents().pushUp(flightPlanComponent);
                    }

                    @Override
                    public void onDeleteButtonPressed(FlightPlanComponent flightPlanComponent) {
                        tabMissionPlannerFunction.getMissionPlan().getFlightPlanComponents().remove(value);
                    }

                    @Override
                    public void onDuplicateButtonPressed(FlightPlanComponent flightPlanComponent) {
                        FlightPlanComponent duplicatedComponent = flightPlanComponent.duplicate();
                        String currentName = (String) duplicatedComponent.getName().value();
                        duplicatedComponent.getName().set((currentName == null ? "" : currentName) + "_copy");
                        tabMissionPlannerFunction.getMissionPlan().getFlightPlanComponents().add(duplicatedComponent);
                    }
                });
            }

            @Override
            public void swapped(FlightPlanComponent firstValue, FlightPlanComponent secondValue, int firstValueOldIndex, int secondValueOldIndex) {
                missionListContainer.children().swap(componentViewModelMap.get(firstValue),componentViewModelMap.get(secondValue));
            }

            @Override
            public void removed(FlightPlanComponent value, int oldIndex) {
                missionListContainer.children().remove(componentViewModelMap.remove(value));
                value.destroy();
            }
        };

        removableList.add(tabMissionPlannerFunction.getMissionPlan().getFlightPlanComponents().observe(listObserver));

        tabMissionPlannerFunction.getMissionPlan().getFlightPlanComponents().observeCurrentValue(listObserver);

        removableList.add(
            addFlightComponentView.visibility().bind(tabMissionPlannerFunction.getIsAddFlightComponentOpened().and(tabMissionPlannerFunction.getIsMissionPlannerOpened()).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE))
        );

        addFlightComponentButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabMissionPlannerFunction.getIsAddFlightComponentOpened().set(true);
                return false;
            }
        });

        relocateButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabMissionPlannerFunction.getIsRelocateMarked().set(!tabMissionPlannerFunction.getIsRelocateMarked().value());
                return false;
            }
        });

        removableList.add(relocateButton.tint().bind(tabMissionPlannerFunction.getIsRelocateMarked().toggle(Colour.WRAP_ID.apply(R.color.cyan),Colour.WRAP_ID.apply(R.color.foreground))));

        final AtomicReference<Removable> crosshairRemovable = new AtomicReference<>(Removable.STUB);

        removableList.add(new Removable() {
            @Override
            public void remove() {
                tabMissionPlannerFunction.getIsRelocateMarked().setIfNew(false);
            }
        });

        removableList.add(
                tabMissionPlannerFunction.getCurrentState().observe(new Observer<MissionState>() {
                    @Override
                    public void observe(MissionState oldValue, MissionState newValue, Observation<MissionState> observation) {
                        if(newValue != MissionState.EDIT_MISSION){
                            tabMissionPlannerFunction.getIsRelocateMarked().setIfNew(false);
                        }
                    }
                })
        );

        removableList.add(tabMissionPlannerFunction.getIsRelocateMarked().observe(new Observer<Boolean>() {

            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {

                if(newValue) {
                    crosshairRemovable.get().remove();
                    crosshairRemovable.set(
                        crosshairLocation.observe(new Observer<Location>() {
                            @Override
                            public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                                currentMission.relocate(newValue);
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

        removableList.add(new Removable() {
            @Override
            public void remove() {
                crosshairRemovable.get().remove();
            }
        });

        addFlightComponentExitButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabMissionPlannerFunction.getIsAddFlightComponentOpened().set(false);
                return false;
            }
        });

        addCircleButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabMissionPlannerFunction.getIsAddFlightComponentOpened().set(false);
                CircleFlightPlanComponent newCircle = tabMissionPlannerFunction.addCircle();
                tabMissionPlannerFunction.getCurrentEditedFlightPlanComponent().set(newCircle);
                return false;
            }
        });

        addRadiatorButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabMissionPlannerFunction.getIsAddFlightComponentOpened().set(false);
                RadiatorFlightPlanComponent newRadiator = tabMissionPlannerFunction.addRadiator();
                tabMissionPlannerFunction.getCurrentEditedFlightPlanComponent().set(newRadiator);

                return false;
            }
        });

        saveButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                final String fileName = currentMission.getName().value();
                if(fileName == null || fileName.equals("")){
                    littleMessageViewModel.addNewMessage("Unable to save mission, please provide name");
                    return false;
                }

                ArrayList<String> problemComponents = new ArrayList<String>();

                for(FlightPlanComponent flightPlanComponent : currentMission.getFlightPlanComponents()){
                    List<String> componentsIllegalFields = flightPlanComponent.illegalFields();
                    if(componentsIllegalFields.size() > 0){
                        problemComponents.add((String) flightPlanComponent.getName().value());
                    }
                }

                if(problemComponents.size() > 0){
                    String errorMessage = "Unable to save mission, Prblem components : ";
                    for(String problemComponent : problemComponents){
                        errorMessage += "\n" + problemComponent;
                    }
                    littleMessageViewModel.addNewMessage(errorMessage);
                    return false;
                }

                final File file = new File(EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.MISSION_PLANS,false).getAbsolutePath() + "/" +
                                fileName + ".mef");
                if(file.exists()){

                    messageViewModel.addGeneralMessage(
                            "Save Mission Plan",
                            "Mission Plan with the name \"" + fileName + "\" already exists, override?",
                            ContextCompat.getDrawable(activity, R.drawable.save), new MessageViewModel.MessageViewModelListener() {
                                @Override
                                public void onOkButtonPressed() {
                                    file.delete();
                                    try {
                                        file.createNewFile();
                                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                                        fileOutputStream.write((Serialization.JSON.serialize(currentMission.createSnapshot()) + "\n").getBytes());
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                        littleMessageViewModel.addNewMessage("Save Completed for : " + fileName);
                                    } catch (IOException e) {
                                        littleMessageViewModel.addNewMessage("Failed to save mission : " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelButtonPressed() {
                                    littleMessageViewModel.addNewMessage("Didn't save the file : " + fileName);
                                }
                            });
                }
                else {
                    try {
                        file.createNewFile();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write((Serialization.JSON.serialize(currentMission.createSnapshot()) + "\n").getBytes());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        littleMessageViewModel.addNewMessage("Save Completed for : " + fileName);
                    } catch (IOException e) {
                        littleMessageViewModel.addNewMessage("Failed to save mission : " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });

        missionNameEditText.setOnDoneListener(new EditTextViewModel.EditTextOnDone() {
            @Override
            public void onDoneCallback(String textWritten) {
                currentMission.getName().set(textWritten);
            }
        });

        loadFileButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabMissionPlannerFunction.getLoadMissionFileExplorer().showDialog();
                return false;
            }
        });

        deletePlanButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabMissionPlannerFunction.getDeletePlanFileExplorer().showDialog();
                return false;
            }
        });

        clearButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                messageViewModel.addGeneralMessage("Clear Mission Plan", "You are about the clear current mission plan, are you sure?", ContextCompat.getDrawable(activity, R.drawable.cancel), new MessageViewModel.MessageViewModelListener() {
                    @Override
                    public void onOkButtonPressed() {
                        currentMission.clear();
                    }

                    @Override
                    public void onCancelButtonPressed() {

                    }
                });

                return false;
            }
        });

//        goButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                final ArrayList<MissionRow.MissionRowStub> missionRowStubs = currentMission.missionRows();
//
//                Mission.MissionStub missionStub = new Mission.MissionStub() {
//                    @Override
//                    public int getCurrentIndex() {
//                        return 0;
//                    }
//
//                    @Override
//                    public List<MissionRow> getRows() {
//                        return (List)missionRowStubs;
//                    }
//                };
//
//                TakeOff.TakeOffStub takeOffStub = (TakeOff.TakeOffStub) missionRowStubs.get(0).getTasksMap().get(TaskCategory.FLIGHT).getStubDroneTask();
//                messageViewModel.addMissionConfirmationMessage(tabModel.getDroneController(),missionStub,takeOffStub.altitude());
//                return false;
//            }
//        });

//        startMissionPlanButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                startMissionPlanButton.view().setTextColor(Color.BLUE);
//                tabMissionPlannerFunction.getIsMissionPlannerOpened().set(false);
//
//                pathPlannerInfo.openForOperation(
//                        currentMission,
//                        WaypointAttributePlannerInfo.PlaningMode.START_MISSION_EXECUTION);
//
//                startMissionPlanButton.view().setTextColor(Color.BLACK);
//                return false;
//            }
//        });
//
//        finishMissionPlanButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//                tabMissionPlannerFunction.getIsMissionPlannerOpened().set(false);
//                pathPlannerInfo.openForOperation(
//                        currentMission,
//                        WaypointAttributePlannerInfo.PlaningMode.FINISH_MISSION_EXECUTION);
//                return false;
//            }
//        });

        removableList.add(
                missionNameEditText.text().bind(currentMission.getName())
        );

        removableList.add(
                mainMenu.visibility().bind(
                        tabMissionPlannerFunction
                                .getCurrentState().equalsTo(MissionState.EDIT_MISSION)
                                .toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE)));

//        removableList.add(
//                currentMission.getGoToMissionWaypoints().observe(new CollectionObserver<Location>(){
//                    @Override
//                    public void added(Location value, Observation<Location> observation) {
//                        calcEstimatedTime(currentMission);
//                    }
//
//                    @Override
//                    public void removed(Location value, Observation<Location> observation) {
//                        calcEstimatedTime(currentMission);
//                    }
//                })
//        );

//        removableList.add(
//                currentMission.getEndMissionWaypoints().observe(new CollectionObserver<Location>(){
//                    @Override
//                    public void added(Location value, Observation<Location> observation) {
//                        calcEstimatedTime(currentMission);
//                    }
//
//                    @Override
//                    public void removed(Location value, Observation<Location> observation) {
//                        calcEstimatedTime(currentMission);
//                    }
//                })
//        );

//        removableList.add(
//                currentMission.getRadiatorAnchorWaypoints().observe(new CollectionObserver<Location>(){
//                    @Override
//                    public void added(Location value, Observation<Location> observation) {
//                        calcEstimatedTime(currentMission);
//                    }
//
//                    @Override
//                    public void removed(Location value, Observation<Location> observation) {
//                        calcEstimatedTime(currentMission);
//                    }
//                })
//        );

        removableList.add(
                tabMissionPlannerFunction.getDeletePlanFileExplorer().getChosenFile().observe(new Observer<File>() {
                    @Override
                    public void observe(File oldValue, final File newValue, Observation<File> observation) {
                        if(newValue != null && newValue.exists()){

                            String header = "Delete Plan";
                            String body = "You are about the delete the plan : " + newValue.getAbsolutePath() + " , Are you sure ?";
                            Drawable drawable = ContextCompat.getDrawable(activity,R.drawable.clear);
                            MessageViewModel.MessageViewModelListener listener = new MessageViewModel.MessageViewModelListener() {
                                @Override
                                public void onOkButtonPressed() {
                                    newValue.delete();
                                }

                                @Override
                                public void onCancelButtonPressed() {

                                }
                            };

                            messageViewModel.addGeneralMessage(header,body,drawable,listener);
                        }
                    }
                })
        );

        removableList.add(
                tabMissionPlannerFunction.getLoadMissionFileExplorer().getChosenFile().observe(new Observer<File>() {
                    @Override
                    public void observe(File oldValue, File newValue, Observation<File> observation) {
                        BufferedReader bufferedReader = null;
                        try {
                            bufferedReader = new BufferedReader(new FileReader(newValue));
                            String objectString = bufferedReader.readLine();

                            MissionPlanSnapshot loadedMission = Serialization.JSON.deserialize(objectString,MissionPlanSnapshot.class);
                            currentMission.restoreFromSnapshot(loadedMission);
                        } catch (Exception e) {
                            littleMessageViewModel.addNewMessage("Failed to load file : " + e.getMessage());
                            e.printStackTrace();
                        }
                        finally {
                            if(bufferedReader != null){
                                try {
                                    bufferedReader.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                })
        );

        returnButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                tabMissionPlannerFunction.getIsMissionPlannerOpened().set(false);
                tabModel.getFunctionsModel().isFunctionScreenOpen().set(true);
                return false;
            }
        });
        return new RemovableCollection(removableList);
    }

    private Removable bindToNULL(){
        mainMenu.visibility().set(ViewModel.Visibility.GONE);
        addFlightComponentView.visibility().set(ViewModel.Visibility.GONE);
        return Removable.STUB;
    }
}
