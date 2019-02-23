package eyesatop.controller.djinew.tasks.camera;

import java.util.concurrent.CountDownLatch;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetOpticalZoomLevel;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.Property;

public class DjiSetOpticalZoomLevel extends RunnableDroneTask<CameraTaskType> implements SetOpticalZoomLevel {

    private DJIError taskDjiError = null;
    private final ControllerDjiNew controller;
    private final double zoomLevel;

    public DjiSetOpticalZoomLevel(ControllerDjiNew controller, double zoomLevel) {
        this.controller = controller;
        this.zoomLevel = zoomLevel;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        if(zoomLevel > 30 || zoomLevel < 1){
            throw new DroneTaskException("Zoom level not in range.");
        }

        Double currentZoomLevel = controller.camera().zoomLevel().value();
        if(currentZoomLevel != null && zoomLevel == currentZoomLevel){
            return;
        }

        final CountDownLatch taskLatch = new CountDownLatch(1);

        try {

            controller.getHardwareManager().getDjiCamera().setDigitalZoomFactor((float) zoomLevel, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    try {
                        taskDjiError = djiError;
                        taskLatch.countDown();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

        taskLatch.await();

        controller.camera().refreshZoomLevelData();

        if(taskDjiError != null){
            throw new DroneTaskException("Dji Internal Task Fail : " + taskDjiError.getDescription());
        }

    }

    @Override
    public double zoomLevel() {
        return zoomLevel;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.SET_OPTICAL_ZOOM_LEVEL;
    }
}
