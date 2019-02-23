package eyesatop.controller.mission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 03/09/2017.
 */

public interface MissionRow {

    List<TaskCategory> getPreRunCleanUpList();

    HashMap<TaskCategory,MissionTaskInfo> getTasksMap();

    List<TaskCategory> getPostRunCleanUpList();

    IteratorCommandInfo getIteratorUpdate();

    public class MissionRowStub extends StubDroneTask<MissionTaskType> implements MissionRow{

        private final ArrayList<TaskCategory> preRunCleanup = new ArrayList<>();
        private final HashMap<TaskCategory,MissionTaskInfo> tasksMap = new HashMap<>();
        private final ArrayList<TaskCategory> postRunCleanup = new ArrayList<>();
        private IteratorCommandInfo iteratorCommand = new IteratorCommandInfo(MissionIteratorType.INCREASE,1);

        public void addPreCleanupCategory(TaskCategory category){
            if(!preRunCleanup.contains(category)) {
                preRunCleanup.add(category);
            }
        }

        public void addPostCleanupCategory(TaskCategory category){
            if(!postRunCleanup.contains(category)) {
                postRunCleanup.add(category);
            }
        }

        public void addTask(TaskCategory category,MissionTaskInfo taskInfo){
            tasksMap.put(category,taskInfo);
        }

        public void setIteratorCommand(IteratorCommandInfo iteratorCommand) {
            this.iteratorCommand = iteratorCommand;
        }

        @Override
        public List<TaskCategory> getPreRunCleanUpList() {
            return preRunCleanup;
        }

        @Override
        public HashMap<TaskCategory, MissionTaskInfo> getTasksMap() {
            return tasksMap;
        }

        @Override
        public List<TaskCategory> getPostRunCleanUpList() {
            return postRunCleanup;
        }

        @Override
        public IteratorCommandInfo getIteratorUpdate() {
            return iteratorCommand;
        }

        @Override
        public MissionTaskType taskType() {
            return MissionTaskType.NORMAL;
        }

        public String toString(){
            String finalString = "";
            for(TaskCategory category : tasksMap.keySet()){

                StubDroneTask task = tasksMap.get(category).getStubDroneTask();
                TaskStatus status = (TaskStatus) task.status().value();

                if(status == TaskStatus.ERROR){

                    DroneTaskException taskError = (DroneTaskException) task.error().value();
                    String errorString = taskError == null ? "N/A" : taskError.getErrorString();

                    finalString = category + " : " + task.taskType().getName() + " - " + status + ":" + errorString + "\n";
                }
                else {
                    finalString += category + " : " + tasksMap.get(category).getStubDroneTask().taskType().getName() + " - " + tasksMap.get(category).getStubDroneTask().status().value() + "\n";
                }
            }

            return finalString;
        }
    }
}
