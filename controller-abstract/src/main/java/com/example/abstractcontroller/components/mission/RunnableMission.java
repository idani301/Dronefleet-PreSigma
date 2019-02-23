package com.example.abstractcontroller.components.mission;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.mission.Mission;
import eyesatop.controller.mission.MissionIterator;
import eyesatop.controller.mission.MissionRow;
import eyesatop.controller.mission.MissionTaskType;
import eyesatop.controller.mission.exceptions.IteratorException;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.Removable;
import logs.LoggerTypes;
//import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 03/09/2017.
 */

public class RunnableMission extends RunnableDroneTask<MissionTaskType> implements Mission {

    private Property<Integer> currentIndex = new Property<>();
    private final List<MissionRow.MissionRowStub> rows;
    private final MissionIterator iterator;

    private ExecutorService currentRowExecutor;
    private AbstractMissionRow currentRowExecuted;
    private Removable rowMissionDoneRemovable = Removable.STUB;
    private AbstractDroneController controller;

    public RunnableMission(int startIndex, List<MissionRow.MissionRowStub> rows) {

        this.currentIndex.set(startIndex);
        this.rows = rows;
        this.iterator = new MissionIterator();
    }

    public void setController(AbstractDroneController controller){
        this.controller = controller;
    }

    @Override
    public ObservableValue<Integer> getCurrentIndex() {
        return this.currentIndex;
    }

    @Override
    public List<MissionRow> getRows() {
        return (List<MissionRow>)(List<?>)rows;
    }

    @Override
    public HashMap<TaskCategory, List<EnumWithName>> resourcesRequired() {
        return null;
    }

    @Override
    public MissionTaskType taskType() {
        return MissionTaskType.NORMAL;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        currentRowExecutor = Executors.newSingleThreadExecutor();

        while(currentIndex.value() < rows.size() && currentIndex.value() >= 0){

//            MainLogger.logger.write_message(LoggerTypes.MISSION,"Mission : Executing row number " + currentIndex.value() + " Out of " + rows.size());

            MissionRow.MissionRowStub stubRow = rows.get(currentIndex.value());
            currentRowExecuted = new AbstractMissionRow(stubRow.getPreRunCleanUpList(),stubRow.getTasksMap(),stubRow.getPostRunCleanUpList(),stubRow.getIteratorUpdate());
            currentRowExecuted.setController(controller);
            stubRow.bindToTask(currentRowExecuted);
            currentRowExecutor.submit(currentRowExecuted);

            final CountDownLatch latch = new CountDownLatch(1);
            rowMissionDoneRemovable = currentRowExecuted.status().observe(new Observer<TaskStatus>() {
                @Override
                public void observe(TaskStatus oldValue, TaskStatus newValue, Observation<TaskStatus> observation) {

//                    MainLogger.logger.write_message(LoggerTypes.MISSION,"Mission : Change in the status of row number " + currentIndex.value() +
//                            MainLogger.TAB + "New Status : " + newValue);

                    if(newValue.isTaskDone()) {

                        if(newValue == TaskStatus.ERROR || newValue == TaskStatus.CANCELLED){
                            cancel();
                        }
                        else{
                            rowMissionDoneRemovable.remove();
                            rowMissionDoneRemovable = Removable.STUB;
                            latch.countDown();
                        }

                    }
                }
            }).observeCurrentValue();

//            MainLogger.logger.write_message(LoggerTypes.MISSION,"Mission : Waiting for row " + currentIndex.value() + " to be finished");

            latch.await();
//            MainLogger.logger.write_message(LoggerTypes.MISSION,"Mission : Done Waiting for row " + currentIndex.value() + " to be finished");
            iterator.applyIteratorCommand(currentRowExecuted.getIteratorUpdate());
            try {
                currentIndex.set(iterator.calcNextIndex(currentIndex.value(),rows.size()));
            } catch (IteratorException e) {
                cancel();
//                MainLogger.logger.write_message(LoggerTypes.ERROR,"Error inside calc next index");
                e.printStackTrace();
            }
        }
//        MainLogger.logger.write_message(LoggerTypes.MISSION,"Done Mission");
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        rowMissionDoneRemovable.remove();
        if(currentRowExecuted != null){
            currentRowExecuted.cancel();
        }

        if(currentRowExecutor != null){
            currentRowExecutor.shutdownNow();
        }
    }
}
