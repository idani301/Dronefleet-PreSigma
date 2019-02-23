package eyesatop.unit.ui.utils;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.ArrayList;
import java.util.List;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.GeneralCameraState;
import eyesatop.controller.beans.GeneralDroneState;
import eyesatop.controller.beans.GeneralGimbalState;
import eyesatop.controller.beans.GimbalLockOptions;
import eyesatop.controller.beans.SticksPosition;
import eyesatop.controller.mission.Mission;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.MissionTaskType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.TaskBlocker;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 07/05/2018.
 */

public class ControllerUtils {

    public static GeneralDroneState generateState(DroneController controller,DtmProvider dtmProvider){

        if(controller == null){
            return null;
        }

        DroneTask<GimbalTaskType> currentGimbalTask = controller.gimbal().currentTask().value();
        GimbalState currentGimbalState = controller.gimbal().gimbalState().value();

        GimbalLockOptions gimbalLockOptions;
        Location locationLocked = null;
        Double yawDegree = null;

        if(currentGimbalTask == null){
            gimbalLockOptions = GimbalLockOptions.NONE;
        }
        else{
            switch (currentGimbalTask.taskType()){

                case LOCK_LOOK_AT_LOCATION:
                    gimbalLockOptions = GimbalLockOptions.PITCH_AND_YAW;
                    locationLocked = ((LockGimbalAtLocation)currentGimbalTask).location();
                    break;
                case LOCK_YAW_AT_LOCATION:
                    gimbalLockOptions = GimbalLockOptions.ONLY_YAW;
                    locationLocked = ((LockYawAtLocation)currentGimbalTask).location();
                    yawDegree = ((LockYawAtLocation)currentGimbalTask).degreeShiftFromLocation();
                    break;
                default:
                    gimbalLockOptions = GimbalLockOptions.NONE;
            }
        }

        GeneralGimbalState gimbalState = new GeneralGimbalState(currentGimbalState,gimbalLockOptions,locationLocked,yawDegree);
        GeneralCameraState cameraState = new GeneralCameraState(controller.camera().recording().value(),controller.camera().isShootingPhoto().value(),controller.camera().shootPhotoIntervalValue().value());

        Telemetry currentTelemetry = controller.telemetry().value();

        return new GeneralDroneState(currentTelemetry == null ? null : currentTelemetry.location(), controller.aboveSeaAltitude().value() - dtmProvider.dtmRaiseValue().value(), cameraState,gimbalState, currentTelemetry == null ? null : currentTelemetry.heading());
    }

    public static MissionInfoResult missionString(AbstractDroneController controller){
        final Property<String> missionInfo = new Property<>();

        RemovableCollection bindings = new RemovableCollection();
        final RemovableCollection missionBindings = new RemovableCollection();
        final RemovableCollection indexBindings = new RemovableCollection();

        bindings.add(missionBindings);
        bindings.add(indexBindings);

        bindings.add(
                controller.getMissionManager().currentTask().observe(new Observer<DroneTask<MissionTaskType>>() {
                    @Override
                    public void observe(DroneTask<MissionTaskType> oldValue, DroneTask<MissionTaskType> newValue, Observation<DroneTask<MissionTaskType>> observation) {

                        missionBindings.remove();

                        if(newValue != null){
                            final Mission currentMission = (Mission) newValue;
                            missionBindings.add(((Mission) newValue).getCurrentIndex().observe(new Observer<Integer>() {
                                @Override
                                public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {

                                    indexBindings.remove();

                                    if(newValue >= currentMission.getRows().size() ){
                                        return;
                                    }

                                    final MissionRow currentRow = currentMission.getRows().get(newValue);

                                    missionInfo.set(currentRow.toString());

                                    for(TaskCategory category : currentRow.getTasksMap().keySet()){
                                        indexBindings.add(currentRow.getTasksMap().get(category).getStubDroneTask().status().observe(new Observer() {
                                            @Override
                                            public void observe(Object oldValue, Object newValue, Observation observation) {
                                                missionInfo.set(currentRow.toString());
                                            }
                                        }));
                                    }
                                }
                            }).observeCurrentValue());
                        }
//                        else{
//                            missionInfo.set("");
//                        }
                    }
                }).observeCurrentValue()
        );
        return new MissionInfoResult(missionInfo,bindings);
    }

    public static class MissionInfoResult{
        private final ObservableValue<String> missionInfo;
        private final Removable bindings;

        public MissionInfoResult(ObservableValue<String> missionInfo, Removable bindings) {
            this.missionInfo = missionInfo;
            this.bindings = bindings;
        }

        public ObservableValue<String> getMissionInfo() {
            return missionInfo;
        }

        public Removable getBindings() {
            return bindings;
        }
    }

    public static List<TaskBlocker> taskBlockers(DroneController controller, TaskCategory category, EnumWithName taskType){

        ArrayList<TaskBlocker> finalList = new ArrayList<>();

        int i;
        switch (category){

            case CAMERA:
                for(i=0; i < controller.camera().tasksBlockers().size(); i++){
                    try{
                        CameraTaskBlockerType cameraBlocker = controller.camera().tasksBlockers().get(i);
                        if(cameraBlocker.affectedTasks().contains(taskType)){

                            if(!finalList.contains(cameraBlocker)){
                                finalList.add(cameraBlocker);
                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case FLIGHT:
                for(i=0; i < controller.flightTasks().tasksBlockers().size(); i++){
                    try{
                        FlightTaskBlockerType flightBlocker = controller.flightTasks().tasksBlockers().get(i);
                        if(flightBlocker.affectedTasks().contains(taskType)){

                            if(!finalList.contains(flightBlocker)){
                                finalList.add(flightBlocker);
                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case GIMBAL:
                for(i=0; i < controller.gimbal().tasksBlockers().size(); i++){
                    try{
                        GimbalTaskBlockerType gimbalBlocker = controller.gimbal().tasksBlockers().get(i);
                        if(gimbalBlocker.affectedTasks().contains(taskType) && !gimbalBlocker.isBusy()){

                            if(!finalList.contains(gimbalBlocker)){
                                finalList.add(gimbalBlocker);
                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case HOME:
                for(i=0; i < controller.droneHome().taskBlockers().size(); i++){
                    try{
                        HomeTaskBlockerType homeBlocker = controller.droneHome().taskBlockers().get(i);
                        if(homeBlocker.affectedTasks().contains(taskType)){

                            if(!finalList.contains(homeBlocker)){
                                finalList.add(homeBlocker);
                            }
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case BATTERY:
                break;
            case AIR_LINK:
                break;
            case REMOTE_CONTROLLER:
                break;
            case MISSION:
                break;
        }

        return finalList;
    }
}
