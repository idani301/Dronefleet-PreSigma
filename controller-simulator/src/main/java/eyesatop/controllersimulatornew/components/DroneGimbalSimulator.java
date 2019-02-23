package eyesatop.controllersimulatornew.components;

import com.example.abstractcontroller.components.AbstractDroneGimbal;
import com.example.abstractcontroller.tasks.gimbal.ExploreAbstract;
import com.example.abstractcontroller.tasks.gimbal.LockGimbalAtLocationAbstract;
import com.example.abstractcontroller.tasks.gimbal.LockYawAtLocationAbstract;
import com.example.abstractcontroller.tasks.gimbal.LookAtPointAbstract;
import com.example.abstractcontroller.tasks.gimbal.RotateGimbalAbstract;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.GimbalRequest;
import eyesatop.util.geo.GimbalState;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.controllersimulatornew.tasks.gimbal.LockGimbalAtFlightDirectionSimulator;
import eyesatop.util.geo.Location;

/**
 * Created by Idan on 29/08/2017.
 */

public class DroneGimbalSimulator extends AbstractDroneGimbal {

    private final ControllerSimulator controller;
    private ExecutorService gimbalInjector = Executors.newSingleThreadExecutor();

    public DroneGimbalSimulator(ControllerSimulator controller) {
        this.controller = controller;
        fullGimbalSupported().set(true);
        startGimbalSimulator();
    }

    private void startGimbalSimulator(){
        gimbalInjector.execute(new Runnable() {
            @Override
            public void run() {
                while(true){

                    if(currentTask().value() != null){
                        gimbalState().set(gimbalState().value());
                    }

                    try {
                        Thread.sleep(70);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    public void addGimbalRequest(GimbalRequest gimbalRequest) throws DroneTaskException {
        double newPitch;
        double newYaw;
        double newRoll;

        if(controller.gimbal().gimbalState().isNull()){
            newPitch = 0;
            newRoll = 0;
            newYaw = 0;
        }
        else{
            GimbalState currnetGimbalState = controller.gimbal().gimbalState().value();
            newPitch = currnetGimbalState.getPitch();
            newYaw = currnetGimbalState.getYaw();
            newRoll = currnetGimbalState.getRoll();
        }

        if(gimbalRequest.isPitchEnable()){
            newPitch = gimbalRequest.getGimbalState().getPitch();
        }

        if(gimbalRequest.isRollEnable()){
            newRoll = Location.degreeBetween0To360(gimbalRequest.getGimbalState().getRoll());
        }

        if(gimbalRequest.isYawEnable()){
            newYaw = Location.degreeBetween0To360(gimbalRequest.getGimbalState().getYaw());
        }

        controller.gimbal().gimbalState().set(new GimbalState(newRoll,newPitch,newYaw));
    }

    @Override
    public void internalGimbalRotation(GimbalRequest request) throws DroneTaskException {

        addGimbalRequest(request);
    }

    @Override
    protected RunnableDroneTask<GimbalTaskType> stubToRunnable(StubDroneTask<GimbalTaskType> stubDroneTask) throws DroneTaskException {

        switch (stubDroneTask.taskType()){

            case LOOK_AT_POINT:
                return new LookAtPointAbstract(controller,((LookAtPoint)stubDroneTask).location());
            case LOCK_LOOK_AT_LOCATION:
                return new LockGimbalAtLocationAbstract(((LockGimbalAtLocation)stubDroneTask).location(), controller);
            case LOCK_YAW_AT_LOCATION:
                LockYawAtLocation task = (LockYawAtLocation)stubDroneTask;
                return new LockYawAtLocationAbstract(task.location(),task.degreeShiftFromLocation());
            case LOCK_TO_FLIGHT_DIRECTION:
                return new LockGimbalAtFlightDirectionSimulator();
            case EXPLORE:
                return new ExploreAbstract();
            case ROTATE_GIMBAL:
                RotateGimbal rotateGimbal = (RotateGimbal)stubDroneTask;
                return new RotateGimbalAbstract(controller,rotateGimbal.rotationRequest(), rotateGimbal.timeoutInSeconds());
            default:
                throw new DroneTaskException("Didn't implement stubToRunnable for : " + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {

    }

    @Override
    public void onComponentConnected() {

    }
}
