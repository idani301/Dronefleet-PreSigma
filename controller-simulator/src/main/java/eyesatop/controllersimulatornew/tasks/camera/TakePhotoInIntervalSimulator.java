package eyesatop.controllersimulatornew.tasks.camera;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.TakePhotoInInterval;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 09/10/2017.
 */

public class TakePhotoInIntervalSimulator extends RunnableDroneTask<CameraTaskType> implements TakePhotoInInterval {

    private final ControllerSimulator controller;
    private final int interval;
    private final int captureCount;

    public TakePhotoInIntervalSimulator(ControllerSimulator controller, int captureCount,int interval) {
        this.controller = controller;
        this.interval = interval;
        this.captureCount = captureCount;
    }

    @Override
    public int captureCount() {
        return captureCount;
    }

    @Override
    public int interval() {
        return interval;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.TAKE_PHOTO_INTERVAL;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Thread.sleep(2000);

        CameraMode currentMode = controller.camera().mode().value();
        if(currentMode == null || currentMode != CameraMode.STILLS){
            controller.camera().mode().set(CameraMode.STILLS);
        }

        Thread.sleep(2000);
        controller.camera().isShootingPhoto().set(true);
        controller.camera().shootPhotoIntervalValue().set(interval);
    }
}
