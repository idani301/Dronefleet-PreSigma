package com.example.abstractcontroller.components.mission;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.components.GeneralDroneComponent;
import com.example.abstractcontroller.taskmanager.GenericTaskManager;

import java.util.List;

import eyesatop.controller.mission.Mission;
import eyesatop.controller.mission.MissionManager;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.MissionTaskBlockerType;
import eyesatop.controller.mission.MissionTaskType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 03/09/2017.
 */

public class AbstractMissionManager extends GeneralDroneComponent<MissionTaskType,MissionTaskBlockerType> implements MissionManager{

    private final ObservableList<TaskCategory> takenResources = new ObservableList<>();
    private final AbstractDroneController controller;

    public AbstractMissionManager(GenericTaskManager<MissionTaskType, MissionTaskBlockerType> taskManager, AbstractDroneController controller) {
        super(taskManager);
        this.controller = controller;

        currentTask().observe(new Observer<DroneTask<MissionTaskType>>() {
            @Override
            public void observe(DroneTask<MissionTaskType> oldValue, DroneTask<MissionTaskType> newValue, Observation<DroneTask<MissionTaskType>> observation) {
                if(newValue == null || newValue.status().value().isTaskDone()){
                    takenResources.clear();
                }
            }
        }).observeCurrentValue();
    }

    @Override
    protected RunnableDroneTask<MissionTaskType> stubToRunnable(StubDroneTask<MissionTaskType> stubDroneTask) throws DroneTaskException {

        Mission.MissionStub task = (Mission.MissionStub) stubDroneTask;
        RunnableMission newMission = new RunnableMission(task.getCurrentIndex().value(),(List<MissionRow.MissionRowStub>)(List<?>)task.getRows());
        newMission.setController(controller);
        return newMission;
    }

    @Override
    public void onComponentAvailable() {

    }

    @Override
    public void onComponentConnected() {

    }

    @Override
    public void clearData() {

    }

    @Override
    public ObservableValue<DroneTask<MissionTaskType>> currentTask() {
        return taskManager.currentTask();
    }

    @Override
    public ObservableList<MissionTaskBlockerType> tasksBlockers() {
        return taskManager.getTasksBlockers();
    }

    @Override
    public synchronized void startMission(Mission.MissionStub mission) throws DroneTaskException {

        if(mission.getCurrentIndex().isNull()){
            throw new DroneTaskException("Unknown mission index, can't start");
        }

        for(MissionRow row : mission.getRows()){
            for(TaskCategory taskCategory : row.getTasksMap().keySet()){
                switch (taskCategory){

                    case CAMERA:
                        if(controller.camera().currentTask().value() != null){
                            throw new DroneTaskException("Camera is Busy, cannot start mission");
                        }
                        break;
                    case FLIGHT:
                        if(controller.flightTasks().current().value() != null){
                            throw new DroneTaskException("Flight Tasks is Busy, cannot start mission");
                        }
                        break;
                    case HOME:
                        if(controller.droneHome().currentTask().value() != null){
                            throw new DroneTaskException("Home Tasks is Busy, cannot start mission");
                        }
                        break;
                    case GIMBAL:
//                        if(controller.gimbal().currentTask().value() != null){
//                            throw new DroneTaskException("Gimbal is Busy, cannot start mission");
//                        }
                        break;
                    case MISSION:
                        break;
                }
            }
        }

        startStubTask(mission,false);

        for(MissionRow row : ((Mission)mission).getRows()){
            for(TaskCategory taskCategory : row.getTasksMap().keySet()){
                if(!takenResources.contains(taskCategory)){
                    takenResources.add(taskCategory);
                }
            }
        }
    }

    public ObservableList<TaskCategory> getTakenResources() {
        return takenResources;
    }
}
