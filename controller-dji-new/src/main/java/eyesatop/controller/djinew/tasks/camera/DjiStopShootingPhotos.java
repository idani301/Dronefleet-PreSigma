package eyesatop.controller.djinew.tasks.camera;

import java.util.concurrent.CountDownLatch;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.StopShootingPhotos;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 13/10/2017.
 */

public class DjiStopShootingPhotos extends RunnableDroneTask<CameraTaskType> implements StopShootingPhotos{

    private final ControllerDjiNew contrller;
    private DJIError taskDjiError;

    public DjiStopShootingPhotos(ControllerDjiNew contrller) {
        this.contrller = contrller;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.STOP_SHOOTING_PHOTOS;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Started");

        Camera djiCamera = contrller.getHardwareManager().getDjiCamera();

        if(djiCamera == null || !djiCamera.isConnected()){
            MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Cannot start, has no dji camera");
            throw new DroneTaskException("Cannot start, has no dji camera");
        }

        if(contrller.camera().mode().isNull() || contrller.camera().mode().value() != CameraMode.STILLS){
            MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Can't Stop Record, wrong camera mode.");
            throw new DroneTaskException("Cannot Stop Record, camera mode should be Video, but found : " +
                    (contrller.camera().mode().isNull() ? "NULL" : contrller.camera().mode().value().name()));
        }

        final CountDownLatch taskLatch = new CountDownLatch(1);

        djiCamera.stopShootPhoto(new CommonCallbacks.CompletionCallback() {

            @Override
            public void onResult(DJIError djiError) {
                taskDjiError = djiError;
                taskLatch.countDown();
            }
        });

        MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() + " Sent to Dji, waiting for complete");
        taskLatch.await();
        MainLogger.logger.write_message(LoggerTypes.CAMERA_TASKS,this.getClass().getName() +
                " Dji done. status : " + (taskDjiError == null ? "Success" : "Failed, " + taskDjiError.getDescription()));

        if(taskDjiError != null){
            throw new DroneTaskException("Internal Dji Error : " + taskDjiError.getDescription());
        }
    }
}
