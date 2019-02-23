package eyesatop.controller.djinew.tasks.camera;

import java.util.concurrent.CountDownLatch;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StartRecording;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Property;

/**
 * Created by einav on 03/05/2017.
 */

public class DjiStartRecording extends RunnableDroneTask<CameraTaskType> implements StartRecording {

    private final ControllerDjiNew droneController;
    private DJIError taskDjiError = null;

    public DjiStartRecording(ControllerDjiNew droneController) {
        this.droneController = droneController;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Started");

        final Camera djiCamera = droneController.getHardwareManager().getDjiCamera();

        if(djiCamera == null || !djiCamera.isConnected()){
            MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Cannot start, has no dji camera");
            throw new DroneTaskException("Cannot start, has no dji camera");
        }

//        if(droneController.camera().mode().isNull() || droneController.camera().mode().value() != CameraMode.VIDEO){
//            MainLoggerJava.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Can't Start Record, wrong camera mode.");
//            throw new DroneTaskException("Cannot Start Record, camera mode should be Video, but found : " +
//                    (droneController.camera().mode().isNull() ? "NULL" : droneController.camera().mode().value().name()));
//        }

        try {
            DjiCameraTasksCommon.setCameraModeDji(droneController, SettingsDefinitions.CameraMode.RECORD_VIDEO);
        }
        catch (DroneTaskException e){
            Thread.sleep(3000);
            DjiCameraTasksCommon.setCameraModeDji(droneController, SettingsDefinitions.CameraMode.RECORD_VIDEO);
        }
        try {
            DjiCameraTasksCommon.startRecord(droneController);
        }
        catch (DroneTaskException e){
            Thread.sleep(3000);
            DjiCameraTasksCommon.startRecord(droneController);
        }
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.START_RECORD;
    }
}
