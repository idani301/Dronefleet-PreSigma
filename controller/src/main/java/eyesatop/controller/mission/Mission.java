package eyesatop.controller.mission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.EnumWithName;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskCategory;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 03/09/2017.
 */

public interface Mission extends DroneTask<MissionTaskType>{

    ObservableValue<Integer> getCurrentIndex();
    List<MissionRow> getRows();
    HashMap<TaskCategory,List<EnumWithName>> resourcesRequired();

    public class MissionStub extends StubDroneTask<MissionTaskType> implements Mission{

        private final Property<Integer> currentIndex = new Property<>();
        private final List<MissionRow> missionRows;

        public MissionStub(List<MissionRow> missionRows) {

            this.currentIndex.set(0);
            this.missionRows = missionRows;
        }

        public MissionStub(List<MissionRow> missionRows,int startIndex) {
            this.missionRows = missionRows;
            currentIndex.set(startIndex);
        }

        @Override
        public MissionTaskType taskType() {
            return MissionTaskType.NORMAL;
        }

        @Override
        public ObservableValue<Integer> getCurrentIndex() {
            return currentIndex;
        }

        @Override
        public List<MissionRow> getRows() {
            return missionRows;
        }

        @Override
        public HashMap<TaskCategory, List<EnumWithName>> resourcesRequired() {

            HashMap<TaskCategory,List<EnumWithName>> resourcesMap = new HashMap<>();

            for(TaskCategory category : TaskCategory.values()){
                resourcesMap.put(category,new ArrayList<EnumWithName>());
            }

            if(missionRows == null){
                return resourcesMap;
            }

            for(MissionRow row : missionRows){
                for(TaskCategory category : row.getTasksMap().keySet()){

                    if(row.getTasksMap().get(category).isAllowedToCrush()){
                        continue;
                    }

                    List<EnumWithName> relevantList = resourcesMap.get(category);
                    if(!relevantList.contains(row.getTasksMap().get(category).getStubDroneTask().taskType())){
                        relevantList.add(row.getTasksMap().get(category).getStubDroneTask().taskType());
                    }
                }
            }
            return resourcesMap;
        }

        @Override
        public void bindToTask(DroneTask<MissionTaskType> task) {
            super.bindToTask(task);
            getBindings().add(currentIndex.bind(((Mission)task).getCurrentIndex()));
        }
    }
}
