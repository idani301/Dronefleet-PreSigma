package eyesatop.controllersimulatornew.tasks.camera;

import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.camera.ZoomIn;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.controllersimulatornew.beans.SimulatorZoomSpec;
import eyesatop.util.model.Property;

public class ZoomInSimulator extends RunnableDroneTask<CameraTaskType> implements ZoomIn {

    private final ControllerSimulator controller;

    public ZoomInSimulator(ControllerSimulator controller) {
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

        if(currentZoomInfo.getOpticalZoomFactor() < simulatorZoomSpec.getMaxOpticalZoomFactor()){
            controller.camera().zoomInfo().set(new ZoomInfo(currentZoomInfo.getOpticalZoomFactor() + simulatorZoomSpec.getOpticalZoomStep(),currentZoomInfo.getDigitalZoomFactor()));
        }
        else{
            if(currentZoomInfo.getDigitalZoomFactor() < simulatorZoomSpec.getMaxDigitalZoomFactor()){
                controller.camera().zoomInfo().set(new ZoomInfo(currentZoomInfo.getOpticalZoomFactor(),currentZoomInfo.getDigitalZoomFactor() + simulatorZoomSpec.getDigitalZoomStep()));
            }
        }
    }

    @Override
    public CameraTaskType taskType() {
        return CameraTaskType.ZOOM_IN;
    }
}
