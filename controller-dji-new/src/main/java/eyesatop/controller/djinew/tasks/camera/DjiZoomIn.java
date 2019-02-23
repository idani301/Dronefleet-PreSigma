package eyesatop.controller.djinew.tasks.camera;

import java.util.concurrent.CountDownLatch;

import dji.common.camera.SettingsDefinitions;
import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.ZoomIn;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Property;
import logs.LoggerTypes;

public class DjiZoomIn extends RunnableDroneTask<CameraTaskType> implements ZoomIn {

    private final ControllerDjiNew controller;

    public DjiZoomIn(ControllerDjiNew controller) {
        this.controller = controller;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        ZoomInfo currentZoomInfo = controller.camera().zoomInfo().value();
        Integer maxDigitalFactor = controller.camera().getMaxDigitalFactor().value();

        final Integer currentFocalLength = controller.camera().getCurrentFocalLength().value();
        final SettingsDefinitions.OpticalZoomSpec spec = controller.camera().getOpticalSpec().value();

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final Property<DJIError> djiErrorProperty = new Property<>();

        if(currentFocalLength != null && spec != null && currentFocalLength < spec.getMaxFocalLength()){



//            int numOfTotalSteps = (spec.getMaxFocalLength()-spec.getMinFocalLength())/spec.getFocalLengthStep();
//            int numOfStepsToDo = numOfTotalSteps /4;

            int twiceFocalLength;
            if(currentFocalLength % spec.getFocalLengthStep() == 0){
                twiceFocalLength = currentFocalLength * 2;
            }
            else{
                int lowerFocalLength = currentFocalLength + (currentFocalLength/spec.getFocalLengthStep())*spec.getFocalLengthStep();
                int higherFocalLength = currentFocalLength + ((currentFocalLength/spec.getFocalLengthStep())+1)*spec.getFocalLengthStep();
                if(Math.abs(higherFocalLength - currentFocalLength*2) > Math.abs(lowerFocalLength - currentFocalLength*2)){
                    twiceFocalLength = lowerFocalLength;
                }
                else{
                    twiceFocalLength = higherFocalLength;
                }
            }

            int targetFocalLength = Math.min(twiceFocalLength,spec.getMaxFocalLength());

            if(spec.getMaxFocalLength() == 129){
                targetFocalLength = 128;
            }

            MainLogger.logger.write_message(LoggerTypes.ZOOM,"Target focal length : " + targetFocalLength +
                    MainLogger.TAB + "Focal Step     : " + spec.getFocalLengthStep() +
                    MainLogger.TAB + "Min Focal      : " + spec.getMinFocalLength() +
                    MainLogger.TAB + "Max Focal      : " + spec.getMaxFocalLength() +
                    MainLogger.TAB + "Current Focal  : " + currentFocalLength
            );
            controller.getHardwareManager().getDjiCamera().setOpticalZoomFocalLength(targetFocalLength, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {

//                    MainLogger.logger.write_message(LoggerTypes.ZOOM,"Target focal length : " + targetFocalLength +
//                            MainLogger.TAB + "Focal Step     : " + spec.getFocalLengthStep() +
//                            MainLogger.TAB + "Min Focal      : " + spec.getMinFocalLength() +
//                            MainLogger.TAB + "Max Focal      : " + spec.getFocalLengthStep() +
//                            MainLogger.TAB + "Current Focal  : " + currentFocalLength
//                    );
                    djiErrorProperty.set(djiError);
                    taskLatch.countDown();
                }
            });

            taskLatch.await();
            confirmDjiError(djiErrorProperty.value());
        }
        else{
            if(currentZoomInfo != null && maxDigitalFactor != null && currentZoomInfo.getDigitalZoomFactor() < maxDigitalFactor){
                MainLogger.logger.write_message(LoggerTypes.ZOOM,"Target Digital Zoom : " + 2 +
                        MainLogger.TAB + "Focal Step     : " + spec.getFocalLengthStep() +
                        MainLogger.TAB + "Min Focal      : " + spec.getMinFocalLength() +
                        MainLogger.TAB + "Max Focal      : " + spec.getMaxFocalLength() +
                        MainLogger.TAB + "Current Focal  : " + currentFocalLength
                );

                controller.getHardwareManager().getDjiCamera().setDigitalZoomFactor(2F, new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        djiErrorProperty.set(djiError);
                        taskLatch.countDown();
                    }
                });
                taskLatch.await();
                confirmDjiError(djiErrorProperty.value());
            }
        }
    }

    private void confirmDjiError(DJIError djiError)throws DroneTaskException {
        if(djiError != null){
            throw new DroneTaskException("Internal Dji Error : " + djiError.getDescription());
        }
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.ZOOM_IN;
    }
}
