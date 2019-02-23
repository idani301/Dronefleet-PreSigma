package eyesatop.unit.ui.models.specialfunctions;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.example.abstractcontroller.AbstractDroneController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.map.mission.MissionPlan;
import eyesatop.unit.ui.models.map.mission.MissionPlanSnapshot;
import eyesatop.unit.ui.models.map.mission.MissionSession;
import eyesatop.unit.ui.models.map.mission.MissionSessionSnapshot;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.unit.ui.models.tabs.SpecialFunctionType;
import eyesatop.unit.ui.utils.ControllerUtils;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.android.FileExplorer.FileExplorer;
import eyesatop.util.android.files.EyesatopAppsFilesUtils;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import eyesatop.util.serialization.Serialization;
import logs.LoggerTypes;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

/**
 * Created by Idan on 29/04/2018.
 */

public class MissionExecutionFunction extends SpecialFunction {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm");
    private static final String MAIN_FOLDER = "Eyesatop Mission Sessions";

    private BooleanProperty isFunctionScreenOpened;
    private final FileExplorer sessionsFileExplorer;
    private final FileExplorer sessionDeleteFileExplorer;
    private final FileExplorer plansFileExplorer;
    private BooleanProperty isMissionExecutionMenuOpened = new BooleanProperty(false);

    private final BooleanProperty isRelocateMarked = new BooleanProperty(false);

    private final LittleMessageViewModel littleMessageViewModel;
    private final Property<MissionSession> currentMissionSession = new Property<>();

    private ExecutorService goHomeExecutor = Executors.newSingleThreadExecutor();

    private final MapViewModel mapViewModel;
    private final RemovableCollection bindings = new RemovableCollection();

    private final RemovableCollection mapRemovable = new RemovableCollection();
    private final DroneController controller;
    private final MessageViewModel messageViewModel;

    public MissionExecutionFunction(final Activity activity, final DroneController controller, final MapViewModel mapViewModel, BooleanProperty isFunctionScreenOpened, final MessageViewModel messageViewModel, final LittleMessageViewModel littleMessageViewModel) {
        super(activity);

        this.controller = controller;
        this.mapViewModel = mapViewModel;
        this.messageViewModel = messageViewModel;

        plansFileExplorer = new FileExplorer(Arrays.asList(".mef"),EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.MISSION_PLANS,false),activity, "Choose a Mission Plan");
        sessionsFileExplorer = new FileExplorer(Arrays.asList(".mes"), EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.MISSION_SESSIONS,false), activity, "Choose a Mission Session");
        sessionDeleteFileExplorer = new FileExplorer(Arrays.asList(".mes"), EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.MISSION_SESSIONS,false), activity, "Choose a Mission Session To Delete");

        this.isFunctionScreenOpened = isFunctionScreenOpened;
        this.littleMessageViewModel = littleMessageViewModel;

        bindings.add(plansFileExplorer.getChosenFile().observe(new Observer<File>() {
            @Override
            public void observe(File oldValue, File newValue, Observation<File> observation) {

                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(newValue));
                    String objectString = bufferedReader.readLine();

                    MissionPlanSnapshot loadedSnapshot = Serialization.JSON.deserialize(objectString,MissionPlanSnapshot.class);
                    MissionPlan restoredMissionPlan = new MissionPlan();
                    restoredMissionPlan.restoreFromSnapshot(loadedSnapshot);
                    MissionSession newMission = new MissionSession(restoredMissionPlan);
                    currentMissionSession.set(newMission);
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
        }));

        bindings.add(
                sessionDeleteFileExplorer.getChosenFile().observe(new Observer<File>() {
                    @Override
                    public void observe(File oldValue, final File newValue, Observation<File> observation) {

                        if(newValue == null || !newValue.exists()){
                            return;
                        }

                        messageViewModel.addGeneralMessage("Delete File", "You are about the delete the file : " + newValue.getAbsolutePath() + " , Are you sure ?", ContextCompat.getDrawable(activity, R.drawable.clear), new MessageViewModel.MessageViewModelListener() {
                            @Override
                            public void onOkButtonPressed() {
                                newValue.delete();
                            }

                            @Override
                            public void onCancelButtonPressed() {

                            }
                        });
                    }
                })
        );

        bindings.add(sessionsFileExplorer.getChosenFile().observe(new Observer<File>() {
            @Override
            public void observe(File oldValue, File newValue, Observation<File> observation) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new FileReader(newValue));
                    String objectString = bufferedReader.readLine();

                    MissionSessionSnapshot loadedMission = Serialization.JSON.deserialize(objectString,MissionSessionSnapshot.class);
                    currentMissionSession.set(MissionSession.restoreFromSnapshot(loadedMission));
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

        bindings.add(
                currentMissionSession.observe(new Observer<MissionSession>() {
                    @Override
                    public void observe(MissionSession oldValue, MissionSession newValue, Observation<MissionSession> observation) {

                        mapRemovable.remove();

                        if(newValue != null && newValue.getMissionPlan() != null){
                            mapRemovable.add(newValue.addToMap(controller,mapViewModel));
                        }
                    }
                },UI_EXECUTOR).observeCurrentValue()
        );


        final RemovableCollection indexRemovable = new RemovableCollection();
        bindings.add(new Removable() {
            @Override
            public void remove() {
                indexRemovable.remove();
            }
        });

        bindings.add(currentMissionSession.observe(new Observer<MissionSession>() {

            @Override
            public void observe(MissionSession oldValue, final MissionSession newMissionSession, Observation<MissionSession> observation) {

                indexRemovable.remove();

                if(newMissionSession == null){
                    return;
                }

//                indexRemovable.add(newMissionSession.getLastKnownLocation().observe(new Observer<Location>() {
//                    @Override
//                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
//                        saveSession(newMissionSession);
//                    }
//                }));

                indexRemovable.add(newMissionSession.getIndex().observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                        if(oldValue == null && newValue == 0){
                            String fileName = dateFormat.format(new Date());
                            newMissionSession.setSessionName(fileName);
                            saveSession(newMissionSession);
                        }
                        else if(oldValue != null && newValue != null && !newValue.equals(oldValue)){
                            saveSession(newMissionSession);
                        }
                    }
                }));

            }
        }).observeCurrentValue());
    }

    private void saveSession(MissionSession session){

        Location currentLocation = Telemetry.telemetryToLocation(controller.telemetry().value());

        session.getLastKnownLocation().set(new Location(currentLocation.getLatitude(), currentLocation.getLongitude(), controller.aboveSeaAltitude().value()));
        session.getLastKnownState().set(ControllerUtils.generateState(controller,new DtmProvider.Stub()));

        final File file = new File(
                EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.MISSION_SESSIONS,false).getAbsolutePath() + "/" +
                        session.getSessionNameString() + ".mes");

        if(file.exists()){
            file.delete();
        }

        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write((Serialization.JSON.serialize(session.createSnapshot()) + "\n").getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
//            littleMessageViewModel.addNewMessage("Save Completed for : " + session.getSessionNameString());
        } catch (IOException e) {
            littleMessageViewModel.addNewMessage("Failed to save mission : " + e.getMessage());
            session.setSessionName(null);
            e.printStackTrace();
        }
    }

    private void deleteSession(MissionSession session){
        final File file = new File(EyesatopAppsFilesUtils.getInstance().getFolder(EyesatopAppsFilesUtils.FoldersType.MISSION_SESSIONS,false) + "/" +
                        session.getSessionNameString() + ".mes");

        if(file.exists()){
            file.delete();
        }
    }

    public void destroy(){
        bindings.remove();
    }

    @Override
    public Drawable getFunctionDrawable() {
        return ContextCompat.getDrawable(activity, R.drawable.btn_fmode_obli);
    }

    @Override
    public void actionMenuButtonPressed() {
        isFunctionScreenOpened.set(false);
        isMissionExecutionMenuOpened.set(!isMissionExecutionMenuOpened.value());

    }

    public BooleanProperty getIsMissionExecutionMenuOpened() {
        return isMissionExecutionMenuOpened;
    }

    public synchronized void startMissionSession() throws DroneTaskException {

        final AbstractDroneController controller = (AbstractDroneController) this.controller;
        controller.getDtmProvider().clearRaiseValue();
        final MissionSession currentSession = currentMissionSession.value();

        if (currentSession == null) {
            throw new DroneTaskException("Don't have Session");
        }

        if (currentSession.getIsRunning().value()) {
            throw new DroneTaskException("Session already running");
        }

        final MissionSession.GetMissionResult missionResult = currentSession.getMission();
        controller.getMissionManager().startMission(missionResult.getMission());
        final RemovableCollection bindings = new RemovableCollection();

        bindings.add(missionResult.getMission().status().observe(new Observer<TaskStatus>() {
            @Override
            public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {

                currentSession.getIsRunning().set(!newValue.isTaskDone());

                if (newValue.isTaskDone()) {
                    MainLogger.logger.write_message(LoggerTypes.MISSION_UI,"Mission ended with status : " + newValue);
                    controller.getDtmProvider().clearRaiseValue();
                    bindings.remove();
                    if (newValue == TaskStatus.FINISHED) {

                        deleteSession(currentSession);

                        currentMissionSession.set(null);

                        goHomeExecutor.shutdownNow();
                        goHomeExecutor = Executors.newSingleThreadExecutor();

                        goHomeExecutor.execute(new Runnable() {
                            @Override
                            public void run() {

                                final CountDownLatch latch = new CountDownLatch(1);

                                Removable removable = controller.getMissionManager().getTakenResources().observe(new Observer<TaskCategory>() {
                                    @Override
                                    public void observe(TaskCategory oldValue, TaskCategory newValue, Observation<TaskCategory> observation) {

                                        try {

                                            if (!controller.getMissionManager().getTakenResources().contains(TaskCategory.FLIGHT)) {
                                                observation.remove();
                                                latch.countDown();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                if(!controller.getMissionManager().getTakenResources().contains(TaskCategory.FLIGHT)){
                                    try{
                                        removable.remove();
                                        latch.countDown();
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }

                                try {
                                    latch.await(1, TimeUnit.SECONDS);
                                    controller.flightTasks().goHome();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (DroneTaskException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }).observeCurrentValue());

        final RemovableCollection telemetryObserver = new RemovableCollection();

        bindings.add(new Removable() {
            @Override
            public void remove() {
                telemetryObserver.remove();
            }
        });

        bindings.add(
                missionResult.getMission().getCurrentIndex().observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {

//                        System.out.println("Index Change. Old Value : " + oldValue + ", new Value : " + newValue);
//                        System.out.println("Index Change. start mission row size : " + missionResult.getStartMissionRowsSize());

                        int realProgress = newValue - missionResult.getStartMissionRowsSize();
                        if(realProgress >= 0){
                            currentSession.getIndex().set(realProgress + missionResult.getRemovedLinesNumber());

//                            if(telemetryObserver.size() == 0){
//                                telemetryObserver.add(controller.telemetry().observe(new Observer<Telemetry>() {
//
//                                    private long lastSaveTime = System.currentTimeMillis();
//
//                                    @Override
//                                    public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
//
//                                        if (newValue == null || newValue.location() == null) {
//                                            return;
//                                        }
//
//                                        long currentTime = System.currentTimeMillis();
//                                        if (currentTime - lastSaveTime > 5000) {
//                                            lastSaveTime = currentTime;
//
//                                            currentSession.getLastKnownState().set(ControllerUtils.generateState(controller,controller.getDtmProvider()));
//                                            currentSession.getLastKnownLocation().set(new Location(newValue.location().getLatitude(), newValue.location().getLongitude(), controller.aboveSeaAltitude().value()));
//                                        }
//                                    }
//                                }));
//                            }

                        }
                    }
                })
        );
    }

    public FileExplorer getSessionsFileExplorer() {
        return sessionsFileExplorer;
    }

    public FileExplorer getPlansFileExplorer() {
        return plansFileExplorer;
    }

    public Property<MissionSession> getCurrentMissionSession() {
        return currentMissionSession;
    }

    public BooleanProperty getIsRelocateMarked() {
        return isRelocateMarked;
    }

    public FileExplorer getSessionDeleteFileExplorer() {
        return sessionDeleteFileExplorer;
    }

    @Override
    public SpecialFunctionType functionType() {
        return SpecialFunctionType.MISSION_EXECUTION;
    }
}
