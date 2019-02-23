package eyesatop.controller.djinew.components;

import com.example.abstractcontroller.components.AbstractDroneBattery;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.battery.BatteryTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;

/**
 * Created by Idan on 12/09/2017.
 */

public class DroneBatteryDji extends AbstractDroneBattery {

    private final ControllerDjiNew controller;
    private boolean isBatteryStateCallbackInit = false;

    public DroneBatteryDji(ControllerDjiNew controller) {
        this.controller = controller;
    }

    @Override
    protected RunnableDroneTask<BatteryTaskType> stubToRunnable(StubDroneTask<BatteryTaskType> stubDroneTask) throws DroneTaskException {
        switch (stubDroneTask.taskType()){
            default:
                throw new DroneTaskException("Not implemented : " + stubDroneTask.taskType());
        }
    }

    @Override
    public void onComponentAvailable() {
        startBatteryCallback();
    }

    private void startBatteryCallback(){
        if(isBatteryStateCallbackInit){
            return;
        }
        isBatteryStateCallbackInit = true;

        controller.getHardwareManager().getDjiBattery().setStateCallback(new dji.common.battery.BatteryState.Callback() {
            @Override
            public void onUpdate(dji.common.battery.BatteryState batteryState) {

                BatteryState newDroneBatteryState = new BatteryState(
                        batteryState.getFullChargeCapacity(),
                        batteryState.getChargeRemaining(),
                        batteryState.getTemperature(),
                        batteryState.getLifetimeRemaining());

                getDroneBattery().setIfNew(newDroneBatteryState);
            }
        });
    }

    @Override
    public void onComponentConnected() {
        startBatteryCallback();
    }

    @Override
    public void clearData() {

        if(controller.getHardwareManager().getDjiBattery() != null){
            controller.getHardwareManager().getDjiBattery().setStateCallback(null);
            isBatteryStateCallbackInit = false;
        }

        if(controller.getHardwareManager().getDjiBattery() == null){
            isBatteryStateCallbackInit = false;
        }

        super.clearData();
    }
}

