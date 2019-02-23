package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StopRecording;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 19/07/2017.
 */

public class StopRecordSimulator extends RunnableDroneTask<CameraTaskType> implements StopRecording {

    private final ControllerSimulator controller;

    public StopRecordSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.STOP_RECORD;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(2000);
        controller.camera().recording().set(false);
    }
}
