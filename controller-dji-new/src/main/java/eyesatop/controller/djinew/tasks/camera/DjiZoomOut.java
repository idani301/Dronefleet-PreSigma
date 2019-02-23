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
import eyesatop.controller.tasks.camera.ZoomOut;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.Property;

public class DjiZoomOut extends RunnableDroneTask<CameraTaskType> implements ZoomOut {

    private final ControllerDjiNew controller;

    public DjiZoomOut(ControllerDjiNew controller) {
        this.controller = controller;
    }


    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        ZoomInfo currentZoomInfo = controller.camera().zoomInfo().value();
        Integer maxDigitalFactor = controller.camera().getMaxDigitalFactor().value();

        Integer currentFocalLength = controller.camera().getCurrentFocalLength().value();
        SettingsDefinitions.OpticalZoomSpec spec = controller.camera().getOpticalSpec().value();

        final CountDownLatch taskLatch = new CountDownLatch(1);
        final Property<DJIError> djiErrorProperty = new Property<>();

        if(currentZoomInfo != null && currentZoomInfo.getDigitalZoomFactor() > 1){
            controller.getHardwareManager().getDjiCamera().setDigitalZoomFactor(1, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    djiErrorProperty.set(djiError);
                    taskLatch.countDown();
                }
            });
            taskLatch.await();
            confirmDjiError(djiErrorProperty.value());
        }
        else if(currentFocalLength != null && spec != null && currentFocalLength > spec.getMinFocalLength()){

//            int numOfTotalSteps = (spec.getMaxFocalLength()-spec.getMinFocalLength())/spec.getFocalLengthStep();
//            int numOfStepsToDo = numOfTotalSteps /4;

            int targetFocalLength = Math.max(spec.getMinFocalLength(),currentFocalLength/2);


            if(spec.getMinFocalLength() == 41){
                targetFocalLength = 42;
            }

            controller.getHardwareManager().getDjiCamera().setOpticalZoomFocalLength(targetFocalLength, new CommonCallbacks.CompletionCallback() {
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

    private void confirmDjiError(DJIError djiError)throws DroneTaskException {
        if(djiError != null){
            throw new DroneTaskException("Internal Dji Error : " + djiError.getDescription());
        }
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.ZOOM_OUT;
    }
}
