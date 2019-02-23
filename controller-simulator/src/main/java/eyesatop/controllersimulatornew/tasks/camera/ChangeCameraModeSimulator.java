package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetCameraMode;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 19/07/2017.
 */

public class ChangeCameraModeSimulator extends RunnableDroneTask<CameraTaskType> implements SetCameraMode {

    private final CameraMode mode;
    private final ControllerSimulator controller;

    public ChangeCameraModeSimulator(CameraMode mode, ControllerSimulator controller) {
        this.mode = mode;
        this.controller = controller;
    }

    @Override
    public CameraMode mode() {
        return mode;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.CHANGE_MODE;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(2000);
        controller.camera().mode().set(mode);
    }
}
