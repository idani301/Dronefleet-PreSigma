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
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Property;

/**
 * Created by einav on 02/05/2017.
 */
public class DjiSetMaxAltitudeFromHomeLocation extends RunnableDroneTask<HomeTaskType> implements SetMaxAltitudeFromHomeLocation {

    private final ControllerDjiNew droneController;
    private final double maxAltitude;
    private DJIError taskDjiError = null;

    public DjiSetMaxAltitudeFromHomeLocation(ControllerDjiNew droneController, double maxAltitude) {
        this.droneController = droneController;
        this.maxAltitude = maxAltitude;
    }

    @Override
    public double altitude() {
        return maxAltitude;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
        FlightController djiFlightController = droneController.getHardwareManager().getDjiFlightController();

        final CountDownLatch taskLatch = new CountDownLatch(1);

        try {
            djiFlightController.setMaxFlightHeight((int)maxAltitude, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    taskDjiError = djiError;
                    taskLatch.countDown();
                }
            });
        }
        catch (Exception e){
            taskLatch.countDown();
            e.printStackTrace();
        }

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
        return HomeTaskType.SET_MAX_ALT_FROM_TAKE_OFF_ALT;
    }
}
