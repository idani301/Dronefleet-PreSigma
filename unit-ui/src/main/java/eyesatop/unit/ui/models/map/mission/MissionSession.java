package eyesatop.unit.ui.models.map.mission;

import android.graphics.Color;

import java.util.HashMap;
import java.util.List;

import eyesatop.controller.DroneController;
import eyesatop.controller.GimbalRequest;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.AltitudeType;
import eyesatop.controller.beans.GeneralCameraState;
import eyesatop.controller.beans.GeneralDroneState;
import eyesatop.controller.beans.GeneralGimbalState;
import eyesatop.controller.functions.TelemetryLocation;
import eyesatop.controller.mission.Mission;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.MissionTaskInfo;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.camera.StartRecording;
import eyesatop.controller.tasks.camera.StopRecording;
import eyesatop.controller.tasks.camera.StopShootingPhotos;
import eyesatop.controller.tasks.camera.TakePhoto;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.unit.ui.models.map.MapLine;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 28/04/2018.
 */

public class MissionSession {

    public class GetMissionResult {
        private final Mission.MissionStub mission;
        private final int startMissionRowsSize;
        private final int removedLinesNumber;

        public GetMissionResult(Mission.MissionStub mission, int startMissionRowsSize, int removedLines) {
            this.mission = mission;
            this.startMissionRowsSize = startMissionRowsSize;
            this.removedLinesNumber = removedLines;
        }

        public Mission.MissionStub getMission() {
            return mission;
        }

        public int getStartMissionRowsSize() {
            return startMissionRowsSize;
        }

        public int getRemovedLinesNumber() {
            return removedLinesNumber;
        }
    }

    private final Property<String> sessionName = new Property<>();
    private final MissionPlan missionPlan;
    private final Property<Integer> index = new Property<>();
    private final Property<GeneralDroneState> lastKnownState = new Property<>();

    private final Property<Location> firstLocationBeforeSession = new Property<>();

    private final Property<Location> lastKnownLocation = new Property<>();
    private final BooleanProperty isRunning = new BooleanProperty(false);
    private final RemovableCollection firstLocationBeofreMissionBindings = new RemovableCollection();

    public MissionSession(MissionPlan missionPlan) {
        this.missionPlan = missionPlan;

        firstLocationBeofreMissionBindings.add(
                missionPlan.getLastLocationBeforeMission().bind(firstLocationBeforeSession)
        );

        firstLocationBeofreMissionBindings.add(index.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                if(newValue != null && oldValue == null){
                    firstLocationBeofreMissionBindings.remove();
                }
            }
        }));
    }

    public MissionPlan getMissionPlan() {
        return missionPlan;
    }

    public Property<Integer> getIndex() {
        return index;
    }

    public Property<Location> getLastKnownLocation() {
        return lastKnownLocation;
    }

    public MissionSessionSnapshot createSnapshot(){
        return new MissionSessionSnapshot(sessionName.value(), missionPlan.createSnapshot(),index.value(),lastKnownLocation.value(), lastKnownState.value(), firstLocationBeforeSession.value());
    }

    public void setSessionName(String sessionName) {
        this.sessionName.set(sessionName);
    }

    public String getSessionNameString() {
        return sessionName.value();
    }

    public Property<String> getSessionName() {
        return sessionName;
    }

    public GetMissionResult getMission() throws DroneTaskException {

        int addedRows = 0;
        int removedLines = 0;

        List<MissionRow.MissionRowStub> missionRows = missionPlan.getMissionRows();
        Integer indexValue = index.value();

        if(indexValue != null) {
            for (int i = 0; i < indexValue; i++) {
                missionRows.remove(0);
                removedLines++;
            }
        }

        if(missionPlan.containGimbalCommand()){
            MissionRow.MissionRowStub confirmNoGimbalRow = new MissionRow.MissionRowStub();
            RotateGimbal.RotateGimbalStub confirmGimbalWorkingTask = new RotateGimbal.RotateGimbalStub(new GimbalRequest(new GimbalState(0,-45,0),true,false,false),3);
            confirmNoGimbalRow.addPreCleanupCategory(TaskCategory.GIMBAL);
            confirmNoGimbalRow.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(confirmGimbalWorkingTask,true,false,null));
            missionRows.add(addedRows,confirmNoGimbalRow);
            addedRows++;

            MissionRow.MissionRowStub confirmNoGimbalRowSecond = new MissionRow.MissionRowStub();
            RotateGimbal.RotateGimbalStub confirmGimbalWorkingTaskSecond = new RotateGimbal.RotateGimbalStub(new GimbalRequest(new GimbalState(0,0,0),true,false,false),3);
            confirmNoGimbalRowSecond.addPreCleanupCategory(TaskCategory.GIMBAL);
            confirmNoGimbalRowSecond.addTask(TaskCategory.GIMBAL,new MissionTaskInfo(confirmGimbalWorkingTaskSecond,true,false,null));
            missionRows.add(addedRows,confirmNoGimbalRowSecond);
            addedRows++;
        }

        if(missionPlan.containCameraCommand()) {
            MissionRow.MissionRowStub confirmNoVideoRow = new MissionRow.MissionRowStub();
            confirmNoVideoRow.addTask(TaskCategory.CAMERA, new MissionTaskInfo(new StopRecording.StubStopRecording(), true, true, null));
            missionRows.add(addedRows, confirmNoVideoRow);
            addedRows++;

            MissionRow.MissionRowStub confirmNoShootingPhotoRow = new MissionRow.MissionRowStub();
            confirmNoShootingPhotoRow.addTask(TaskCategory.CAMERA, new MissionTaskInfo(new StopShootingPhotos.StubStopShootingPhotos(), true, true, null));
            missionRows.add(addedRows, confirmNoShootingPhotoRow);
            addedRows++;

            MissionRow.MissionRowStub confirmShootPhotoWorking = new MissionRow.MissionRowStub();
            confirmShootPhotoWorking .addTask(TaskCategory.CAMERA, new MissionTaskInfo(new TakePhoto.StubTakePhoto(), true, false, null));
            missionRows.add(addedRows, confirmShootPhotoWorking);
            addedRows++;
        }

        MissionRow.MissionRowStub takeOffRow = new MissionRow.MissionRowStub();
        TakeOff.TakeOffStub takeOffStub = new TakeOff.TakeOffStub(15);
        takeOffRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(takeOffStub,true,true,null));
        missionRows.add(addedRows,takeOffRow);
        addedRows++;

        GeneralDroneState lastKnownDroneState = lastKnownState.value();
        if(!index.isNull() && lastKnownDroneState != null){
            Location lastKnownLocation = lastKnownDroneState.getLocation();
            if(lastKnownLocation != null){

                AltitudeInfo lastKnownAltitudeInfo = lastKnownDroneState.getAboveSeaLevel() != null ? new AltitudeInfo(AltitudeType.ABOVE_SEA_LEVEL,lastKnownDroneState.getAboveSeaLevel()) :
                        new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION,lastKnownLocation.getAltitude());
                FlyToSafeAndFast.FlySafeAndFastToStub flyToSafeStub = new FlyToSafeAndFast.FlySafeAndFastToStub(lastKnownLocation,lastKnownAltitudeInfo);
                MissionRow.MissionRowStub flyToSafeRow = new MissionRow.MissionRowStub();
                flyToSafeRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(flyToSafeStub,true,false,null));
                missionRows.add(addedRows,flyToSafeRow);
                addedRows++;

                Double heading = lastKnownDroneState.getHeading();
                GeneralGimbalState generalGimbalState = lastKnownDroneState.getGimbalState();
                GimbalState gimbalState = generalGimbalState.getGimbalState();
                GeneralCameraState cameraState = lastKnownDroneState.getCameraState();

                if(heading != null){
                    MissionRow.MissionRowStub rotateHeadingRow = new MissionRow.MissionRowStub();
                    rotateHeadingRow.addTask(TaskCategory.FLIGHT,new MissionTaskInfo(new RotateHeading.RotateHeadnigStub(heading),true,false,null));
                    missionRows.add(addedRows,rotateHeadingRow);
                    addedRows++;
                }

                List<MissionRow> missionPlanRows = (List<MissionRow>)(List<?>)missionPlan.getMissionRows();
                Mission.MissionStub missionPlanMission = new Mission.MissionStub(missionPlanRows);
                HashMap<TaskCategory, List<EnumWithName>> planResources = missionPlanMission.resourcesRequired();

                if(planResources.get(TaskCategory.GIMBAL).size() > 0) {


                    if (gimbalState != null) {
                        MissionRow.MissionRowStub rotateGimbalRow = new MissionRow.MissionRowStub();
                        rotateGimbalRow.addTask(TaskCategory.GIMBAL, new MissionTaskInfo(new RotateGimbal.RotateGimbalStub(new GimbalRequest(gimbalState, true, false, true), null), true, false, null));
                        missionRows.add(addedRows, rotateGimbalRow);
                        addedRows++;
                    }

                    switch (generalGimbalState.getGimbalLockOptions()) {

                        case NONE:
                            break;
                        case ONLY_YAW:
                            MissionRow.MissionRowStub lockYawRow = new MissionRow.MissionRowStub();
                            lockYawRow.addTask(TaskCategory.GIMBAL, new MissionTaskInfo(new LockYawAtLocation.LockYawAtLocationStub(generalGimbalState.getLocationLocked(), generalGimbalState.getYawDegreeFromLocation()), false, false, null));
                            missionRows.add(addedRows, lockYawRow);
                            addedRows++;
                            break;
                        case PITCH_AND_YAW:
                            MissionRow.MissionRowStub lockLocationRow = new MissionRow.MissionRowStub();
                            lockLocationRow.addTask(TaskCategory.GIMBAL, new MissionTaskInfo(new LockGimbalAtLocation.LockGimbalAtLocationStub(generalGimbalState.getLocationLocked()), false, false, null));
                            missionRows.add(addedRows, lockLocationRow);
                            addedRows++;
                            break;
                    }
                }

                if(planResources.get(TaskCategory.CAMERA).size() > 0) {
                    if (cameraState.isRecording() != null && cameraState.isRecording()) {
                        MissionRow.MissionRowStub startRecordRow = new MissionRow.MissionRowStub();
                        startRecordRow.addTask(TaskCategory.CAMERA, new MissionTaskInfo(new StartRecording.StubStartRecording(), true, false, null));
                        missionRows.add(addedRows, startRecordRow);
                        addedRows++;
                    }

//                    if (cameraState.isShootingPhotoInInterval() != null && cameraState.isShootingPhotoInInterval()) {
//                        MissionRow.MissionRowStub startShootPhotoRow = new MissionRow.MissionRowStub();
//                        startShootPhotoRow.addTask(TaskCategory.CAMERA, new MissionTaskInfo(new TakePhotoInInterval.StubTakePhotoInInterval(255, cameraState.getShootPhotoInteralNumber()), true, false, null));
//                        missionRows.add(addedRows, startShootPhotoRow);
//                        addedRows++;
//                    }
                }
            }
        }
        else {
            Location firstMissionPlanLocation = (Location) missionPlan.getFlightPlanComponents().get(0).startLocation().value();

            if (firstMissionPlanLocation == null) {
                throw new DroneTaskException("Unknown first location for mission plan");
            }

            FlyToSafeAndFast.FlySafeAndFastToStub flyToSafeStub = new FlyToSafeAndFast.FlySafeAndFastToStub(firstMissionPlanLocation, (AltitudeInfo) missionPlan.getFlightPlanComponents().get(0).altitudeInfo().value());
            MissionRow.MissionRowStub flyToFirstLocationRow = new MissionRow.MissionRowStub();
            flyToFirstLocationRow.addTask(TaskCategory.FLIGHT, new MissionTaskInfo(flyToSafeStub, true, false, null));
            missionRows.add(addedRows, flyToFirstLocationRow);
            addedRows++;
        }

        return new GetMissionResult(new Mission.MissionStub((List)missionRows),addedRows,removedLines);
    }

    public double highestASLOnSession(DtmProvider provider,Location homeLocation,Location currentLocation) throws TerrainNotFoundException {

        double highestASL = missionPlan.highestASLOnPlan(provider,homeLocation);

        Location startLocation;

        if(index.isNull()){
            startLocation = (Location) missionPlan.getFlightPlanComponents().get(0).startLocation().value();
        }
        else {
            startLocation = lastKnownLocation.value();
        }

        if(startLocation == null){
            throw new TerrainNotFoundException("Unknown session start location");
        }

        double highestASLToFirstLocation = DtmProvider.DtmTools.highestDTMBetweenPoints(provider,currentLocation,startLocation) + FlyToSafeAndFast.MIN_AGL;
        highestASL = Math.max(highestASL,highestASLToFirstLocation);

        return highestASL;
    }

    public BooleanProperty getIsRunning() {
        return isRunning;
    }

    public Removable addToMap(final DroneController controller, final MapViewModel mapViewModel){
        RemovableCollection mapBindings = new RemovableCollection();

        final MapLine mapLine = new MapLine();
        mapLine.getDash().set(20D);
        mapLine.getGap().set(20D);
        mapLine.getColor().set(Color.BLUE);

        final RemovableCollection firstLineRemovable = new RemovableCollection();

        mapBindings.add(missionPlan.addToMap(mapViewModel));

        mapBindings.add(
                isRunning.observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {

                        firstLineRemovable.remove();

                        if(!newValue){
                            firstLineRemovable.add(mapLine.getStartPoint().bind(controller.telemetry().transform(new TelemetryLocation())));
//                            firstLineRemovable.add(missionPlan.getLastLocationBeforeMission().bind(controller.telemetry().transform(new TelemetryLocation())));
                            firstLineRemovable.add(missionPlan.getShowFirstLine().bind(isRunning.not().and(lastKnownLocation.notNull().not())));
                        }
                    }
                }).observeCurrentValue()
        );

        mapBindings.add(new Removable() {
            @Override
            public void remove() {
                firstLineRemovable.remove();
            }
        });
        mapBindings.add(mapLine.getVisibility().bind(isRunning.not()));
        mapBindings.add(mapLine.getEndPoint().bind(lastKnownLocation));
        mapViewModel.addMapItem(mapLine);
        mapBindings.add(new Removable() {
            @Override
            public void remove() {
                mapViewModel.removeMapItem(mapLine);
            }
        });

        return mapBindings;
    }

    public Property<GeneralDroneState> getLastKnownState() {
        return lastKnownState;
    }

    public void destroy(){
        firstLocationBeofreMissionBindings.remove();
    }

    public Property<Location> getFirstLocationBeforeSession() {
        return firstLocationBeforeSession;
    }

    public static MissionSession restoreFromSnapshot(MissionSessionSnapshot snapshot){
        MissionPlan missionPlan = new MissionPlan();
        missionPlan.restoreFromSnapshot(snapshot.getMissionPlanSnapshot());
        MissionSession missionSession = new MissionSession(missionPlan);
        missionSession.getIndex().set(snapshot.getIndex());
        missionSession.getLastKnownLocation().set(snapshot.getLastKnownLocation());
        missionSession.setSessionName(snapshot.getName());
        missionSession.getLastKnownState().set(snapshot.getLastKnownState());
        missionSession.getFirstLocationBeforeSession().set(snapshot.getFirstLocationBeforeSession());
        missionPlan.getLastLocationBeforeMission().set(snapshot.getFirstLocationBeforeSession());
        return missionSession;
    }
}
