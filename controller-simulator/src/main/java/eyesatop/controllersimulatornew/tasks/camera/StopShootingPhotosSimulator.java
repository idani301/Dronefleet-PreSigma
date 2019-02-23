package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StopShootingPhotos;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 13/10/2017.
 */

public class StopShootingPhotosSimulator extends RunnableDroneTask<CameraTaskType> implements StopShootingPhotos{

    private final ControllerSimulator controller;

    public StopShootingPhotosSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.STOP_SHOOTING_PHOTOS;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        Thread.sleep(500);
        controller.camera().isShootingPhoto().set(false);
    }
}
