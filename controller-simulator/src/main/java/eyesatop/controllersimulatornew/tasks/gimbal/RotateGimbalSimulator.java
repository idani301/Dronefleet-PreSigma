//package eyesatop.controllersimulatornew.tasks.gimbal;
//
//import eyesatop.controller.GimbalRequest;
//import eyesatop.controller.tasks.RunnableDroneTask;
//import eyesatop.controller.tasks.TaskProgressState;
//import eyesatop.controller.tasks.exceptions.DroneTaskException;
//import eyesatop.controller.tasks.gimbal.GimbalTaskType;
//import eyesatop.controller.tasks.gimbal.RotateGimbal;
//import eyesatop.controllersimulatornew.ControllerSimulator;
//import eyesatop.util.model.Property;
//
///**
// * Created by Idan on 20/09/2017.
// */
//
//public class RotateGimbalSimulator extends RunnableDroneTask<GimbalTaskType> implements RotateGimbal {
//
//    private final ControllerSimulator controller;
//    private final GimbalRequest gimbalRequest;
//
//    public RotateGimbalSimulator(ControllerSimulator controller, GimbalRequest gimbalRequest) {
//        this.controller = controller;
//        this.gimbalRequest = gimbalRequest;
//    }
//
//    @Override
//    public GimbalRequest rotationRequest() {
//        return gimbalRequest;
//    }
//
//    @Override
//    public GimbalTaskType taskType() {
//        return GimbalTaskType.ROTATE_GIMBAL;
//    }
//
//    @Override
//    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
//
//        Thread.sleep(1000);
//
//        controller.gimbal().addGimbalRequest(gimbalRequest);
//    }
//}
