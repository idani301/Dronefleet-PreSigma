//package eyesatop.controller.djinew.tasks.gimbal;
//
//import java.util.concurrent.CountDownLatch;
//
//import eyesatop.controller.GimbalBehaviorType;
//import eyesatop.controller.GimbalRequest;
//import eyesatop.controller.beans.GimbalBehavior;
//import eyesatop.util.geo.GimbalState;
//import eyesatop.controller.djinew.ControllerDjiNew;
//import eyesatop.controller.tasks.RunnableDroneTask;
//import eyesatop.controller.tasks.TaskProgressState;
//import eyesatop.controller.tasks.TaskStatus;
//import eyesatop.controller.tasks.exceptions.DroneTaskException;
//import eyesatop.controller.tasks.gimbal.GimbalTaskType;
//import eyesatop.controller.tasks.gimbal.RotateGimbal;
//import eyesatop.util.Removable;
//import eyesatop.util.geo.Location;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//
///**
// * Created by Idan on 20/09/2017.
// */
//
//public class DjiRotateGimbal extends RunnableDroneTask<GimbalTaskType> implements RotateGimbal {
//
//    private final GimbalRequest gimbalRequest;
//    private final ControllerDjiNew controller;
//
//    private Removable taskRemovable = Removable.STUB;
//
//    public DjiRotateGimbal(ControllerDjiNew controller, GimbalRequest gimbalRequest) {
//
//        GimbalRequest newGimbalRequest = null;
//        if(gimbalRequest.isPitchEnable()){
//            if(gimbalRequest.getGimbalState().getPitch() > 0){
//                newGimbalRequest = new GimbalRequest(gimbalRequest.getGimbalState().pitch(0),true,gimbalRequest.isYawEnable(),gimbalRequest.isRollEnable());
//            }
//            else if(gimbalRequest.getGimbalState().getPitch() < -90){
//                newGimbalRequest = new GimbalRequest(gimbalRequest.getGimbalState().pitch(-90),true,gimbalRequest.isYawEnable(),gimbalRequest.isRollEnable());
//            }
//        }
//        if(newGimbalRequest == null){
//            newGimbalRequest = gimbalRequest;
//        }
//
//        this.gimbalRequest = newGimbalRequest;
//        this.controller = controller;
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
//        final CountDownLatch taskLatch = new CountDownLatch(1);
//
//        taskRemovable = controller.gimbal().gimbalState().observe(new Observer<GimbalState>() {
//            @Override
//            public void observe(GimbalState oldValue, GimbalState newValue, Observation<GimbalState> observation) {
//
//                boolean isPitchReached = false;
//                if(gimbalRequest.isPitchEnable()){
//                    if(Location.angularDistance(newValue.getPitch(),gimbalRequest.getGimbalState().getPitch()) < 0.5){
//                        isPitchReached = true;
//                    }
//                }
//                else{
//                    isPitchReached = true;
//                }
//
//                boolean isYawReached = false;
//                if(gimbalRequest.isYawEnable()){
//                    if(Location.angularDistance(newValue.getYaw(),gimbalRequest.getGimbalState().getYaw()) < 1){
//                        isYawReached = true;
//                    }
//                }
//                else{
//                    isYawReached = true;
//                }
//
//                if(isPitchReached && isYawReached){
//                    try {
//                        taskLatch.countDown();
//                        observation.remove();
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).observeCurrentValue();
//
//        taskLatch.await();
//    }
//
//    @Override
//    protected void cleanUp(TaskStatus exitStatus) throws Exception {
//        taskRemovable.remove();
//    }
//}
