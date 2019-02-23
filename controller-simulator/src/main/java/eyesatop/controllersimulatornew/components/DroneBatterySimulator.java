package eyesatop.controllersimulatornew.components;

import com.example.abstractcontroller.components.AbstractDroneBattery;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.battery.BatteryTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.controllersimulatornew.ControllerSimulator;
import eyesatop.util.RemovableCollection;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public class DroneBatterySimulator extends AbstractDroneBattery {

    private ExecutorService batteryExecutor = null;
    private final int reductionIntervalInSeconds = 5;
    private final ControllerSimulator controller;
    private final RemovableCollection simulatorBindings = new RemovableCollection();
    private int batteryNextStep = 0;

    public DroneBatterySimulator(final ControllerSimulator controller){
        this.controller = controller;
    }

    public void startBatterySimulator(){
        simulatorBindings.remove();
        if(batteryExecutor != null){
            batteryExecutor.shutdownNow();
            batteryExecutor = null;
        }

        simulatorBindings.add(
                controller.flying().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        if(newValue == null || !newValue){
                            batteryNextStep = 10;
                        }
                        else{
                            batteryNextStep = -3;
                        }
                    }
                })
        );

        batteryExecutor = Executors.newSingleThreadExecutor();

        batteryExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(reductionIntervalInSeconds * 1000);
                    } catch (InterruptedException e) {
                        return;
                    }

                    BatteryState currentState = controller.droneBattery().value();
                    if(currentState == null){
                        continue;
                    }

                    int newBatteryPercent = currentState.getCurrent() + batteryNextStep;
                    if(newBatteryPercent >= 100){
                        newBatteryPercent = 100;
                    }
                    else if(newBatteryPercent <=0){
                        newBatteryPercent = 0;
                    }

                    BatteryState newState = currentState.current(newBatteryPercent);
//                    System.out.println("Doing battery step : \n");
//                    System.out.println("Current State : " + currentState.toString() + "\n");
//                    System.out.println("New Step : " + newState.toString() + "\n");
//                    System.out.println("Battery next step : " + batteryNextStep + "\n");
                    controller.droneBattery().set(newState);
                }
            }
        });
    }

    public void stopBatterySimulator(){
        simulatorBindings.remove();
        if(batteryExecutor != null){
            batteryExecutor.shutdownNow();
            batteryExecutor = null;
        }
    }

    @Override
    protected RunnableDroneTask<BatteryTaskType> stubToRunnable(StubDroneTask<BatteryTaskType> stubDroneTask) throws DroneTaskException {
        return null;
    }

    @Override
    public void onComponentAvailable() {

    }

    @Override
    public void onComponentConnected() {

    }
}
