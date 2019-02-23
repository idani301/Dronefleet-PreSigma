package eyesatop.controller.djinew.tasks.camera;

import java.util.concurrent.CountDownLatch;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.camera.Camera;
import eyesatop.controller.DroneController;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 11/10/2017.
 */

public class DjiCameraTasksCommon {

    public static void startRecord(ControllerDjiNew controller) throws DroneTaskException {
        Camera camera = controller.getHardwareManager().getDjiCamera();

        try{
            if(camera == null || !camera.isConnected()){
                throw new DroneTaskException("No Camera, can't change mode");
            }

            final CountDownLatch latch = new CountDownLatch(1);
            final Property<DJIError> djiErrorProperty = new Property<>();

            camera.startRecordVideo(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    djiErrorProperty.set(djiError);
                    latch.countDown();
                }
            });
            latch.await();
            DJIError result = djiErrorProperty.value();
            if(result != null && result.getDescription() != null){
                throw new DroneTaskException("Internal Dji Error : " + result.getDescription());
            }
        }
        catch (Exception e){
            throw new DroneTaskException("Error with Start Shooting photo : " + e.getMessage());
        }
    }

    public static void startShootingPhoto(ControllerDjiNew controller) throws DroneTaskException {
        Camera camera = controller.getHardwareManager().getDjiCamera();

        try{
            if(camera == null || !camera.isConnected()){
                throw new DroneTaskException("No Camera, can't change mode");
            }

            final CountDownLatch latch = new CountDownLatch(1);
            final Property<DJIError> djiErrorProperty = new Property<>();

            camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    djiErrorProperty.set(djiError);
                    latch.countDown();
                }
            });
            latch.await();
            DJIError result = djiErrorProperty.value();
            if(result != null && result.getDescription() != null){
                throw new DroneTaskException("Internal Dji Error : " + result.getDescription());
            }
        }
        catch (Exception e){
            throw new DroneTaskException("Error with Start Shooting photo : " + e.getMessage());
        }
    }

    public static void setCameraShootPhotoMode(final ControllerDjiNew controller, SettingsDefinitions.ShootPhotoMode cameraMode) throws DroneTaskException{

        Camera camera = controller.getHardwareManager().getDjiCamera();

        try{
            if(camera == null || !camera.isConnected()){
                throw new DroneTaskException("No Camera, can't change mode");
            }
            SettingsDefinitions.ShootPhotoMode currentMode = controller.camera().getCurrentShootPhotoMode().value();

            if(currentMode != cameraMode){

                final CountDownLatch latch = new CountDownLatch(1);
                final Property<DJIError> djiErrorProperty = new Property<>();

                camera.setShootPhotoMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        controller.camera().refreshShootPhotoMode();
                        djiErrorProperty.set(djiError);
                        latch.countDown();
                    }
                });
                latch.await();
                DJIError result = djiErrorProperty.value();
                if(result != null && result.getDescription() != null){
                    throw new DroneTaskException("Internal Dji Error : " + result.getDescription());
                }
            }
        }
        catch (Exception e){
            throw new DroneTaskException("Error with changing Shooting Camera Mode : " + e.getMessage());
        }
    }


    public static void setCameraModeDji(ControllerDjiNew controller, SettingsDefinitions.CameraMode cameraMode) throws DroneTaskException{

        final Camera camera = controller.getHardwareManager().getDjiCamera();

        try{
            if(camera == null || !camera.isConnected()){
                throw new DroneTaskException("No Camera, can't change mode");
            }
            SettingsDefinitions.CameraMode currentMode = getCurrentmodeAsDjiMode(controller);
            if(currentMode != cameraMode){

                final CountDownLatch latch = new CountDownLatch(1);
                final Property<DJIError> djiErrorProperty = new Property<>();

                camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        djiErrorProperty.set(djiError);
                        latch.countDown();
                    }
                });
                latch.await();
                DJIError result = djiErrorProperty.value();
                if(result != null && result.getDescription() != null){
                    throw new DroneTaskException("Internal Dji Error : " + result.getDescription());
                }
            }
        }
        catch (Exception e){
            throw new DroneTaskException("Error with changing Camera Mode : " + e.getMessage());
        }
    }

    private static SettingsDefinitions.CameraMode getCurrentmodeAsDjiMode(DroneController controller){
        CameraMode currentMode = controller.camera().mode().value();

        if(currentMode == null){
            return SettingsDefinitions.CameraMode.UNKNOWN;
        }
        switch (currentMode){

            case VIDEO:
                return SettingsDefinitions.CameraMode.RECORD_VIDEO;
            case STILLS:
                return SettingsDefinitions.CameraMode.SHOOT_PHOTO;
            default:
                return SettingsDefinitions.CameraMode.UNKNOWN;
        }
    }
}
