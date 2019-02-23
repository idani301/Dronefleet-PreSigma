package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.TakePhoto;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by einav on 19/07/2017.
 */

public class TakePhotoSimulator extends RunnableDroneTask<CameraTaskType> implements TakePhoto {

    private final ControllerSimulator controller;

    public TakePhotoSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.TAKE_PHOTO;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        controller.camera().isShootingPhoto().set(true);
        Thread.sleep(2000);
        controller.camera().isShootingPhoto().set(false);

    }
}
