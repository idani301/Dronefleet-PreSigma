package eyesatop.controller.djinew.tasks.home;

import java.util.concurrent.CountDownLatch;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetLimitationEnabled;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Property;

/**
 * Created by einav on 02/05/2017.
 */
public class DjiSetLimitationEnabled extends RunnableDroneTask<HomeTaskType> implements SetLimitationEnabled {

    private final ControllerDjiNew droneController;
    private final boolean enabled;
    private DJIError taskDjiError = null;

    public DjiSetLimitationEnabled(ControllerDjiNew droneController, boolean enabled) {
        this.droneController = droneController;
        this.enabled = enabled;
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        FlightController djiFlightController = droneController.getHardwareManager().getDjiFlightController();

        if(djiFlightController == null || djiFlightController.isConnected() == false){
            MainLogger.logger.write_message(LoggerTypes.HOME_TASKS,this.getClass().getName() + " dji flight controller is null.");
            throw new DroneTaskException(this.getClass().getName() + " dji flight controller is null.");
        }

        final CountDownLatch taskLatch = new CountDownLatch(1);

        djiFlightController.setMaxFlightRadiusLimitationEnabled(enabled,new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                taskDjiError = djiError;
                taskLatch.countDown();
            }
        });

        MainLogger.logger.write_message(LoggerTypes.HOME_TASKS,this.getClass().getName() + " Sent to DJI, waiting for complete.");
        taskLatch.await();
        MainLogger.logger.write_message(LoggerTypes.HOME_TASKS,this.getClass().getName()+ " Completed, Status: " + (taskDjiError == null ? "Success" : "Failed, " + taskDjiError.getDescription()));

        if(taskDjiError != null) {
            throw new DroneTaskException(this.getClass().getName() + " Failed, Internal error inside DJI. Reason: " + taskDjiError.getDescription());
        }

        droneController.droneHome().onComponentConnected();
    }

    @Override
    public HomeTaskType taskType() {
        return HomeTaskType.SET_LIMITATION_ENABLED;
    }

}
