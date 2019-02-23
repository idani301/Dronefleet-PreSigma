package eyesatop.controller.djinew.tasks.camera;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.TakePhoto;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Property;

/**
 * Created by einav on 03/05/2017.
 */

public class DjiTakePhoto extends RunnableDroneTask<CameraTaskType> implements TakePhoto {

    private final ControllerDjiNew droneController;

    public DjiTakePhoto(ControllerDjiNew droneController) {
        this.droneController = droneController;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Started");

        DjiCameraTasksCommon.setCameraShootPhotoMode(droneController, SettingsDefinitions.ShootPhotoMode.SINGLE);
        DjiCameraTasksCommon.setCameraModeDji(droneController, SettingsDefinitions.CameraMode.SHOOT_PHOTO);
        try {
            DjiCameraTasksCommon.startShootingPhoto(droneController);
        }
        catch (DroneTaskException e){
            Thread.sleep(3000);
            DjiCameraTasksCommon.startShootingPhoto(droneController);
        }
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.TAKE_PHOTO;
    }
}
