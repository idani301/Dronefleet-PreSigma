package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StartRecording;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 19/07/2017.
 */

public class StartRecordSimulator extends RunnableDroneTask<CameraTaskType> implements StartRecording{

    private final ControllerSimulator controller;

    public StartRecordSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.START_RECORD;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(2000);
        controller.camera().mode().set(CameraMode.VIDEO);
        Thread.sleep(2000);
        controller.camera().recording().set(true);
    }
}
