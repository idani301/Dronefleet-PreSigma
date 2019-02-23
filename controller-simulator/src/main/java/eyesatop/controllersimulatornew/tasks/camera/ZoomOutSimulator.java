package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.ZoomOut;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.controllersimulatornew.beans.SimulatorZoomSpec;
import eyesatop.util.model.Property;

public class ZoomOutSimulator extends RunnableDroneTask<CameraTaskType> implements ZoomOut {

    private final ControllerSimulator controller;

    public ZoomOutSimulator(ControllerSimulator controller) {
        this.controller = controller;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        SimulatorZoomSpec simulatorZoomSpec = controller.camera().getZoomSpec().value();
        ZoomInfo currentZoomInfo = controller.camera().zoomInfo().value();

        if(simulatorZoomSpec == null){
            throw new DroneTaskException("Unknown simulator zoom spec");
        }

        if(currentZoomInfo == null){
            throw new DroneTaskException("Unknown current zoom info");
        }

        Thread.sleep(1000);

        if(currentZoomInfo.getDigitalZoomFactor() > 1){
            controller.camera().zoomInfo().set(new ZoomInfo(currentZoomInfo.getOpticalZoomFactor(),currentZoomInfo.getDigitalZoomFactor() - simulatorZoomSpec.getDigitalZoomStep()));
        }
        else{
            if(currentZoomInfo.getOpticalZoomFactor() > 1){
                controller.camera().zoomInfo().set(new ZoomInfo(currentZoomInfo.getOpticalZoomFactor() - simulatorZoomSpec.getOpticalZoomStep(),currentZoomInfo.getDigitalZoomFactor()));
            }
        }
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.ZOOM_OUT;
    }
}
