package eyesatop.controller.mission;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableValue;

/**
 * Created by Idan on 03/09/2017.
 */

public interface MissionManager {

    ObservableValue<DroneTask<MissionTaskType>> currentTask();
    ObservableCollection<MissionTaskBlockerType> tasksBlockers();

    public void startMission(Mission.MissionStub mission) throws DroneTaskException;
}
