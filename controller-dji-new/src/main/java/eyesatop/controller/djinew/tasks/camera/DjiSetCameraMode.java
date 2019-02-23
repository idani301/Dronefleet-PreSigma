package eyesatop.controller.djinew.tasks.camera;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetCameraMode;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Property;

/**
 * Created by einav on 03/05/2017.
 */

public class DjiSetCameraMode extends RunnableDroneTask<CameraTaskType> implements SetCameraMode {

    private final ControllerDjiNew droneController;
    private final CameraMode mode;

    private DJIError taskDjiError = null;

    public DjiSetCameraMode(ControllerDjiNew droneController, CameraMode mode) {
        this.droneController = droneController;
        this.mode = mode;
    }

    @Override
    public CameraMode mode() {
        return mode;
    }

    @Override
    protected void perform(final Property<TaskProgressState> state) throws DroneTaskException,InterruptedException {

        MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Started");

        SettingsDefinitions.CameraMode djiCameraMode;
        switch (mode) {

            case VIDEO:
                djiCameraMode = SettingsDefinitions.CameraMode.RECORD_VIDEO;
                break;
            case STILLS:
                djiCameraMode = SettingsDefinitions.CameraMode.SHOOT_PHOTO;
                break;
            case UNKNOWN:
                throw new DroneTaskException("Illegal camera mode : " + mode);
            default:
                throw new DroneTaskException("Illegal camera mode : " + mode);
        }

        DjiCameraTasksCommon.setCameraModeDji(droneController,djiCameraMode);
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.CHANGE_MODE;
    }
}
