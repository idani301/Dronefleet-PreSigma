package eyesatop.controller.djinew.tasks.camera;

import java.util.concurrent.CountDownLatch;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.SetZoomLevel;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 22/09/2017.
 */

public class DjiSetZoomLevel extends RunnableDroneTask<CameraTaskType> implements SetZoomLevel {

    private final ControllerDjiNew controller;
    private final double zoomLevel;

    private DJIError taskDjiError;

    public DjiSetZoomLevel(ControllerDjiNew controller, double zoomLevel) {
        this.controller = controller;
        this.zoomLevel = zoomLevel;
    }

    @Override
    public double zoomLevel() {
        return zoomLevel;
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.SET_ZOOM_LEVEL;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        final CountDownLatch taskLatch = new CountDownLatch(1);

        if(zoomLevel > 2 || zoomLevel < 1){
            throw new DroneTaskException("Zoom level not in range.");
        }

        Double currentZoomLevel = controller.camera().zoomLevel().value();
        if(currentZoomLevel != null && zoomLevel == currentZoomLevel){
            throw new DroneTaskException("Zoom level request is same like current.");
        }

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
        controller.camera().refreshZoomInfo();

        if(taskDjiError != null){
            throw new DroneTaskException("Dji Internal Task Fail : " + taskDjiError.getDescription());
        }

    }
}
