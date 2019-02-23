package eyesatop.controller.djinew.tasks.camera;

import java.util.concurrent.CountDownLatch;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.TakePhotoInInterval;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 09/10/2017.
 */

public class DjiTakePhotoInInterval extends RunnableDroneTask<CameraTaskType> implements TakePhotoInInterval {

    private final ControllerDjiNew controller;
    private final int captureCount;
    private final int interval;

    public DjiTakePhotoInInterval(ControllerDjiNew controller, int captureCount, int interval) {
        this.controller = controller;
        this.captureCount = captureCount;
        this.interval = interval;
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
        Camera camera = controller.getHardwareManager().getDjiCamera();

        try{
            if(camera == null || !camera.isConnected()){
                throw new DroneTaskException("No Camera");
            }

            final CountDownLatch latch = new CountDownLatch(1);
            final Property<DJIError> djiErrorProperty = new Property<>();

            camera.setPhotoTimeIntervalSettings(new SettingsDefinitions.PhotoTimeIntervalSettings(captureCount,interval), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    djiErrorProperty.set(djiError);
                    latch.countDown();
                }
            });
            latch.await();

            camera.getPhotoTimeIntervalSettings(new CommonCallbacks.CompletionCallbackWith<SettingsDefinitions.PhotoTimeIntervalSettings>() {
                @Override
                public void onSuccess(SettingsDefinitions.PhotoTimeIntervalSettings photoTimeIntervalSettings) {
                    controller.camera().shootPhotoIntervalValue().set(photoTimeIntervalSettings.getTimeIntervalInSeconds());
                }

                @Override
                public void onFailure(DJIError djiError) {

                }
            });

            if(djiErrorProperty.value() != null && djiErrorProperty.value().getDescription() != null){

                DroneModel model = controller.model().value();

                if(model != null && model != DroneModel.PHANTOM_3) {
                    throw new DroneTaskException("setPhotoTimeIntervalSettings Internal Dji Error : " + djiErrorProperty.value().getDescription());
                }
            }
            DjiCameraTasksCommon.setCameraModeDji(controller, SettingsDefinitions.CameraMode.SHOOT_PHOTO);
            DjiCameraTasksCommon.setCameraShootPhotoMode(controller, SettingsDefinitions.ShootPhotoMode.INTERVAL);
            DjiCameraTasksCommon.startShootingPhoto(controller);
        }
        catch (DroneTaskException e){
            throw new DroneTaskException("Error with taking photo in interval : " + e.getErrorString());
        }
        catch (Exception e){
            throw new DroneTaskException("Error with taking photo in interval : " + e.getMessage());
        }
    }
}
