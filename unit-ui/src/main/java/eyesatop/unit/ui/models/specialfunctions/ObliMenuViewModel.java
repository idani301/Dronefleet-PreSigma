//package eyesatop.unit.ui.models.specialfunctions;
//
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.abstractcontroller.AbstractDroneController;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//
//import eyesatop.controller.DroneController;
//import eyesatop.controller.GimbalRequest;
//import eyesatop.unit.ui.models.tabs.DroneTab;
//import eyesatop.util.geo.GimbalState;
//import eyesatop.controller.beans.RotationType;
//import eyesatop.controller.mission.IteratorCommandInfo;
//import eyesatop.controller.mission.Mission;
//import eyesatop.controller.mission.MissionIteratorType;
//import eyesatop.controller.mission.MissionRow;
//import eyesatop.controller.mission.MissionTaskInfo;
//import eyesatop.controller.mission.MissionTaskType;
//import eyesatop.controller.tasks.DroneTask;
//import eyesatop.controller.tasks.TaskCategory;
//import eyesatop.controller.tasks.TaskStatus;
//import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
//import eyesatop.controller.tasks.camera.CameraTaskType;
//import eyesatop.controller.tasks.camera.StopShootingPhotos;
//import eyesatop.controller.tasks.camera.TakePhotoInInterval;
//import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
//import eyesatop.controller.tasks.flight.FlightTaskType;
//import eyesatop.controller.tasks.flight.FlyInCircle;
//import eyesatop.controller.tasks.flight.GoHome;
//import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
//import eyesatop.controller.tasks.gimbal.GimbalTaskType;
//import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
//import eyesatop.controller.tasks.gimbal.RotateGimbal;
//import eyesatop.controller.tasks.takeoff.TakeOff;
//import eyesatop.unit.ui.R;
//import eyesatop.unit.ui.models.actionmenus.ActionMenuItemModel;
//import eyesatop.unit.ui.models.generic.AbstractViewModel;
//import eyesatop.unit.ui.models.generic.ImageViewModel;
//import eyesatop.unit.ui.models.generic.TextViewModel;
//import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
//import eyesatop.unit.ui.models.massage.MessageViewModel;
//import eyesatop.unit.ui.models.tabs.DroneTabModel;
//import eyesatop.unit.ui.specialfunctions.oblimapper.ObliCircle;
//import eyesatop.unit.ui.specialfunctions.oblimapper.OblimapperMission;
//import eyesatop.util.Function;
//import eyesatop.util.Removable;
//import eyesatop.util.RemovableCollection;
//import eyesatop.util.geo.Location;
//import eyesatop.util.model.CollectionObserver;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//
///**
// * Created by einav on 29/06/2017.
// */
//
//public class ObliMenuViewModel extends AbstractViewModel<View> {
//
//    private final View mainMenu;
//    private final ImageViewModel returnButton;
//    private final ImageViewModel loadButton;
//    private final ImageViewModel clearButton;
//  //  private final TextViewModel currentFileText;
//    private final ActionMenuItemModel goButton;
//    private final Property<Location> crosshairLocation;
//    private Removable buildObliRemovable = Removable.STUB;
//    private Removable centerObserver = Removable.STUB;
//    private final LittleMessageViewModel littleMessages;
//    private final MessageViewModel messageViewModel;
//
//    public ObliMenuViewModel(View view, Property<Location> crosshairLocation, MessageViewModel messageViewModel, LittleMessageViewModel littleMessages) {
//        super(view);
//        this.crosshairLocation = crosshairLocation;
//        mainMenu = view;
//        this.littleMessages = littleMessages;
//        this.messageViewModel = messageViewModel;
//        returnButton = new ImageViewModel(super.<ImageView>find(R.id.obliReturnToMainButton));
//        loadButton = new ImageViewModel(super.<ImageView>find(R.id.mappingButtonLoad));
//        clearButton = new ImageViewModel(super.<ImageView>find(R.id.mappingButtonClear));
//      //  currentFileText= new TextViewModel(super.<TextView>find(R.id.obliCurrentFileText));
//        goButton = new ActionMenuItemModel(super.<ImageView>find(R.id.mappingButtonGo));
//    }
//
//    public void setAlwaysGoneReturnButton(boolean isAlwaysGone){
//        returnButton.setAlwaysGone(isAlwaysGone);
//    }
//
//    public Removable bindToTab(final DroneTab tabModel){
//
//        goButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                if(goButton.clickable().value() == false){
//
//                    final OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    if(currentMission == null){
//                        littleMessages.addNewMessage("Current Mission is NULL");
//                        return false;
//                    }
//                    final Location centerLocation = currentMission.getCenter().value();
//
//                    if(((AbstractDroneController)tabModel.getDroneController()).getMissionManager().currentTask().value() != null){
//                        littleMessages.addNewMessage("Already have mission in progress");
//                        return false;
//                    }
//
//                    String errorReason = "Can't Start Mission : ";
//
//                    String flightBlockers = flightBlockerReason(tabModel.getDroneController(),FlightTaskType.TAKE_OFF);
//                    if(flightBlockers != null){
//                        errorReason += "\nFlight Blockers : " + flightBlockers;
//                    }
//
//                    String gimbalBlockers = gimbalBlockerReason(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL);
//                    if(gimbalBlockers != null){
//                        errorReason += "\nGimbal Blockers : " + gimbalBlockers;
//                    }
//
//                    String cameraBlockers = cameraBlockerReason(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL);
//                    if(cameraBlockers != null){
//                        errorReason += "\nCamera Blockers : " + cameraBlockers;
//                    }
//
//                    if(cameraBlockers != null | gimbalBlockers != null || flightBlockers != null) {
//                        littleMessages.addNewMessage(errorReason);
//                        return false;
//                    }
//                    else{
//
//                        if(centerLocation == null){
//                            littleMessages.addNewMessage("Can't start Mission: Tap the map to place Oblimapper");
//                            return false;
//                        }
//                        else {
//                            littleMessages.addNewMessage("Can't start mission, Unknown Reason");
//                            return false;
//                        }
//                    }
//                }
//
//                final OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                if(currentMission == null){
//                    littleMessages.addNewMessage("Current Mission is NULL");
//                    return false;
//                }
//                final Location centerLocation = currentMission.getCenter().value();
//
//                if(centerLocation == null){
//                    littleMessages.addNewMessage("Tap the map to place Oblimapper");
//                    return false;
//                }
//                if(currentMission.getCircles().size() == 0){
//                    littleMessages.addNewMessage("No Circles found");
//                    return false;
//                }
//
//                final double takeOffAltitude = currentMission.getCircles().get(0).getAltitudeFromGround();
//
//                final ArrayList<MissionRow.MissionRowStub> missionRows = new ArrayList<MissionRow.MissionRowStub>();
//
//                missionRows.add(new MissionRow.MissionRowStub() {
//                    @Override
//                    public List<TaskCategory> getPreRunCleanUpList() {
//                        return Collections.<TaskCategory>emptyList();
//                    }
//
//                    @Override
//                    public HashMap<TaskCategory, MissionTaskInfo> getTasksMap() {
//                        HashMap<TaskCategory, MissionTaskInfo> map = new HashMap<TaskCategory, MissionTaskInfo>();
//                        map.put(TaskCategory.FLIGHT,new MissionTaskInfo(new TakeOff.TakeOffStub(takeOffAltitude),true,false,null));
//                        map.put(TaskCategory.CAMERA,new MissionTaskInfo(new TakePhotoInInterval.StubTakePhotoInInterval(255, 3),true,false,null));
//                        map.put(TaskCategory.GIMBAL,new MissionTaskInfo(new RotateGimbal.
//                                RotateGimbalStub(new GimbalRequest(new GimbalState(0,currentMission.getCircles().get(0).getGimbalPosition(),0), true,false,false)),
//                                true, false, null));
//                        return map;
//                    }
//
//                    @Override
//                    public List<TaskCategory> getPostRunCleanUpList() {
//                        return Collections.<TaskCategory>emptyList();
//                    }
//
//                    @Override
//                    public IteratorCommandInfo getIteratorUpdate() {
//                        return new IteratorCommandInfo(MissionIteratorType.INCREASE,1);
//                    }
//                });
//
//                for(final ObliCircle circle : currentMission.getCircles()){
//                    double degreeShift = circle.isLookInside() ? 0 : 180;
//                    final LockYawAtLocation.LockYawAtLocationStub lockYawTask = new LockYawAtLocation.LockYawAtLocationStub(centerLocation,degreeShift);
//                    missionRows.add(
//                            new MissionRow.MissionRowStub() {
//                        @Override
//                        public List<TaskCategory> getPreRunCleanUpList() {
//                            return Collections.emptyList();
//                        }
//
//                        @Override
//                        public HashMap<TaskCategory, MissionTaskInfo> getTasksMap() {
//                            HashMap<TaskCategory,MissionTaskInfo> map = new HashMap<TaskCategory, MissionTaskInfo>();
//                            map.put(TaskCategory.FLIGHT,new MissionTaskInfo(new FlyInCircle.
//                                    FlyInCircleStub(centerLocation,circle.getRadius(), RotationType.CLOCKWISE,360,-1, circle.getAltitudeFromGround(), 5),true,false,null));
//                            map.put(TaskCategory.GIMBAL,new MissionTaskInfo(lockYawTask,false,false,null));
//                            return map;
//                        }
//
//                        @Override
//                        public List<TaskCategory> getPostRunCleanUpList() {
//                            return Arrays.asList(TaskCategory.GIMBAL);
//                        }
//
//                        @Override
//                        public IteratorCommandInfo getIteratorUpdate() {
//                            return new IteratorCommandInfo(MissionIteratorType.INCREASE,1);
//                        }
//                    });
//                }
//
//                missionRows.add(new MissionRow.MissionRowStub() {
//                    @Override
//                    public List<TaskCategory> getPreRunCleanUpList() {
//                        return Arrays.asList(TaskCategory.CAMERA,TaskCategory.GIMBAL,TaskCategory.FLIGHT);
//                    }
//
//                    @Override
//                    public HashMap<TaskCategory, MissionTaskInfo> getTasksMap() {
//                        HashMap<TaskCategory,MissionTaskInfo> map = new HashMap<TaskCategory, MissionTaskInfo>();
//                        map.put(TaskCategory.FLIGHT,new MissionTaskInfo(new GoHome.GoHomeStub(),true,false,null));
//                        map.put(TaskCategory.CAMERA,new MissionTaskInfo(new StopShootingPhotos.StubStopShootingPhotos(),true,false,null));
//                        return map;
//                    }
//
//                    @Override
//                    public List<TaskCategory> getPostRunCleanUpList() {
//                        return Collections.emptyList();
//                    }
//
//                    @Override
//                    public IteratorCommandInfo getIteratorUpdate() {
//                        return new IteratorCommandInfo(MissionIteratorType.INCREASE,1);
//                    }
//                });
//
//
//                Mission.MissionStub missionStub = new Mission.MissionStub() {
//                    @Override
//                    public int getCurrentIndex() {
//                        return 0;
//                    }
//
//                    @Override
//                    public List<MissionRow> getRows() {
//                        return (List)missionRows;
//                    }
//                };
//
//                messageViewModel.addMissionConfirmationMessage(tabModel.getDroneController(),missionStub,takeOffAltitude);
//                tabModel.getFunctionsModel().getObliFunction().isObliMenuOpened().set(false);
//                return false;
//            }
//        });
//
//        clearButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//
//                tabModel.getFunctionsModel().getObliFunction().selectedFile().set(null);
//                return false;
//            }
//        });
//
//        returnButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//                tabModel.getFunctionsModel().getObliFunction().isObliMenuOpened().set(false);
//                tabModel.getFunctionsModel().getObliFunction().isFunctionScreenOpened().set(true);
//                return false;
//            }
//        });
//
//        loadButton.singleTap().set(new Function<MotionEvent, Boolean>() {
//            @Override
//            public Boolean apply(MotionEvent input) {
//                tabModel.getFunctionsModel().getObliFunction().showFileExplorer();
//                return false;
//            }
//        });
//
//        final ArrayList<Removable> removablesList = new ArrayList<>();
//
//        removablesList.add(tabModel.getFunctionsModel().getObliFunction().selectedFile().observe(new Observer<File>() {
//            @Override
//            public void observe(File oldValue, File newValue, Observation<File> observation) {
//                if(newValue == null){
//                   // currentFileText.text().set("System Default");
//                }
//                else{
//                   // currentFileText.text().set(newValue.getName());
//                }
//            }
//        }).observeCurrentValue());
//
//
//        if(tabModel.getDroneController() instanceof AbstractDroneController) {
//
//            removablesList.add(tabModel.getFunctionsModel().getObliFunction().getCurrentMission().observe(new Observer<OblimapperMission>() {
//                @Override
//                public void observe(OblimapperMission oldValue, OblimapperMission newValue, Observation<OblimapperMission> observation) {
//                    centerObserver.remove();
//
//                    OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//                    goButton.clickable().set(
//                            center != null &&
//                                    !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                                    !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                                    !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//                    );
//
//                    if(newValue != null){
//                        centerObserver = newValue.getCenter().observe(new Observer<Location>() {
//                            @Override
//                            public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                                OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                                Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//                                goButton.clickable().set(
//                                        center != null &&
//                                                !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                                                !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                                                !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//                                );
//                            }
//                        });
//                    }
//                    else{
//                        centerObserver = Removable.STUB;
//                    }
//                }
//            }).observeCurrentValue());
//
//            removablesList.add(tabModel.getDroneController().flightTasks().tasksBlockers().observe(new CollectionObserver<FlightTaskBlockerType>(){
//                @Override
//                public void added(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
//
//                    OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//                    goButton.clickable().set(
//                            center != null &&
//                            !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                                    !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                                    !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//                    );
//                }
//
//                @Override
//                public void removed(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
//                    OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//                    goButton.clickable().set(
//                            center != null &&
//                                    !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                                    !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                                    !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//                    );
//                }
//            }));
//
//            removablesList.add(tabModel.getDroneController().gimbal().tasksBlockers().observe(new CollectionObserver<GimbalTaskBlockerType>(){
//                @Override
//                public void added(GimbalTaskBlockerType value, Observation<GimbalTaskBlockerType> observation) {
//                    OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//                    goButton.clickable().set(
//                            center != null &&
//                                    !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                                    !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                                    !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//                    );
//                }
//
//                @Override
//                public void removed(GimbalTaskBlockerType value, Observation<GimbalTaskBlockerType> observation) {
//                    OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//                    goButton.clickable().set(
//                            center != null &&
//                                    !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                                    !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                                    !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//                    );
//                }
//            }));
//
//            removablesList.add(new Removable() {
//                @Override
//                public void remove() {
//                    centerObserver.remove();
//                    centerObserver = STUB;
//                }
//            });
//
//            removablesList.add(tabModel.getDroneController().camera().tasksBlockers().observe(new CollectionObserver<CameraTaskBlockerType>(){
//                @Override
//                public void added(CameraTaskBlockerType value, Observation<CameraTaskBlockerType> observation) {
//                    OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//                    goButton.clickable().set(
//                            center != null &&
//                                    !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                                    !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                                    !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//                    );
//                }
//
//                @Override
//                public void removed(CameraTaskBlockerType value, Observation<CameraTaskBlockerType> observation) {
//                    OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//                    goButton.clickable().set(
//                            center != null &&
//                                    !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                                    !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                                    !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//                    );
//                }
//            }));
//
//            OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//            Location center = currentMission == null ? null : currentMission.getCenter().value();
//
//            goButton.clickable().set(
//                    center != null &&
//                            !containCameraBlocker(tabModel.getDroneController(),CameraTaskType.TAKE_PHOTO_INTERVAL) &&
//                            !containGimbalBlocker(tabModel.getDroneController(),GimbalTaskType.ROTATE_GIMBAL) &&
//                            !containTaskBlocker(tabModel.getDroneController(),FlightTaskType.TAKE_OFF)
//            );
//        }
//
//        removablesList.add(buildObliRemovable);
//
//        if(tabModel.getDroneController() instanceof AbstractDroneController) {
//            removablesList.add(((AbstractDroneController) tabModel.getDroneController()).getMissionManager().currentTask().observe(new Observer<DroneTask<MissionTaskType>>() {
//                @Override
//                public void observe(DroneTask<MissionTaskType> oldValue, DroneTask<MissionTaskType> newValue, Observation<DroneTask<MissionTaskType>> observation) {
//                    connectCrosshairLocationToObli(tabModel);
//                }
//            }).observeCurrentValue());
//
//            removablesList.add(tabModel.getFunctionsModel().getObliFunction().isObliMenuOpened().observe(new Observer<Boolean>() {
//                @Override
//                public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
//                    connectCrosshairLocationToObli(tabModel);
//                }
//            }));
//        }
//
//        removablesList.add(tabModel.getFunctionsModel().getObliFunction().isObliMenuOpened().observe(new Observer<Boolean>() {
//            @Override
//            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
//                mainMenu.setVisibility(newValue ? View.VISIBLE : View.GONE);
//            }
//        },UI_EXECUTOR).observeCurrentValue());
//
//        return new RemovableCollection(removablesList);
//    }
//
//    private void connectCrosshairLocationToObli(final DroneTab tabModel){
//
//
//        Boolean isMenuOpened = tabModel.getFunctionsModel().getObliFunction().isObliMenuOpened().value();
//        DroneTask currentMission = ((AbstractDroneController)tabModel.getDroneController()).getMissionManager().currentTask().value();
//
//        buildObliRemovable.remove();
//
//        if(isMenuOpened != null && isMenuOpened && (currentMission == null || ((TaskStatus)currentMission.status().value()).isTaskDone())){
//            buildObliRemovable = crosshairLocation.observe(new Observer<Location>() {
//                @Override
//                public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//
//                    OblimapperMission currentMission = tabModel.getFunctionsModel().getObliFunction().getCurrentMission().value();
//                    if(currentMission != null){
//                        currentMission.getCenter().set(newValue);
//                    }
//                    System.out.println("Supposed to create obli around this location");
//                }
//            });
//        }
//        else{
//            buildObliRemovable = Removable.STUB;
//        }
//    }
//
//    private boolean containTaskBlocker(DroneController controller, FlightTaskType taskType){
//
//        if(controller == null){
//            return true;
//        }
//
//        for(FlightTaskBlockerType taskBlocker : controller.flightTasks().tasksBlockers()){
//            if(taskBlocker.affectedTasks().contains(taskType)){
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private String flightBlockerReason(DroneController controller,FlightTaskType taskType){
//
//        if(controller == null){
//            return "No DroneController connected.";
//        }
//
//        String blockers = null;
//        for(FlightTaskBlockerType taskBlocker : controller.flightTasks().tasksBlockers()){
//            if(taskBlocker.affectedTasks().contains(taskType)){
//                if(blockers == null) {
//                    blockers = taskBlocker.getName();
//                }
//                else{
//                    blockers += "," + taskBlocker.getName();
//                }
//            }
//        }
//
//        return blockers;
//    }
//
//
//    private String gimbalBlockerReason(DroneController controller,GimbalTaskType taskType){
//
//        if(controller == null){
//            return "No DroneController connected.";
//        }
//
//        String blockers = null;
//        for(GimbalTaskBlockerType taskBlocker : controller.gimbal().tasksBlockers()){
//            if(taskBlocker.affectedTasks().contains(taskType)){
//                if(blockers == null) {
//                    blockers = taskBlocker.getName();
//                }
//                else{
//                    blockers += "," + taskBlocker.getName();
//                }
//            }
//        }
//
//        return blockers;
//    }
//
//    private String cameraBlockerReason(DroneController controller,CameraTaskType taskType){
//
//
//        if(controller == null){
//            return "No DroneController connected.";
//        }
//
//        String blockers = null;
//        for(CameraTaskBlockerType taskBlocker : controller.camera().tasksBlockers()){
//            if(taskBlocker.affectedTasks().contains(taskType)){
//                if(blockers == null) {
//                    blockers = taskBlocker.getName();
//                }
//                else{
//                    blockers += "," + taskBlocker.getName();
//                }
//            }
//        }
//
//        return blockers;
//    }
//
//
//    private boolean containCameraBlocker(DroneController controller, CameraTaskType taskType){
//
//        if(controller == null){
//            return true;
//        }
//
//        for(CameraTaskBlockerType taskBlocker : controller.camera().tasksBlockers()){
//            if(taskBlocker.affectedTasks().contains(taskType)){
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private boolean containGimbalBlocker(DroneController controller, GimbalTaskType taskType){
//
//        if(controller == null){
//            return true;
//        }
//
//        for(GimbalTaskBlockerType taskBlocker : controller.gimbal().tasksBlockers()){
//            if(taskBlocker.affectedTasks().contains(taskType)){
//                return true;
//            }
//        }
//
//        return false;
//    }
//}
