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
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Property;

/**
 * Created by einav on 02/05/2017.
 */
public class DjiSetMaxDistanceFromHome extends RunnableDroneTask<HomeTaskType> implements SetMaxDistanceFromHome{

    private final ControllerDjiNew droneController;
    private final double maxDistanceFromHome;
    private DJIError taskDjiError = null;

    public DjiSetMaxDistanceFromHome(ControllerDjiNew droneController, double maxDistanceFromHome) {
        this.droneController = droneController;
        this.maxDistanceFromHome = maxDistanceFromHome;
    }

    @Override
    public double maxDistanceFromHome() {
        return maxDistanceFromHome;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        FlightController djiFlightController = droneController.getHardwareManager().getDjiFlightController();

        if(djiFlightController == null || djiFlightController.isConnected() == false){
            MainLogger.logger.write_message(LoggerTypes.HOME_TASKS,this.getClass().getName() + " dji flight controller is null.");
            throw new DroneTaskException(this.getClass().getName() + " dji flight controller is null.");
        }

        final CountDownLatch taskLatch = new CountDownLatch(1);

        djiFlightController.setMaxFlightRadius((int)maxDistanceFromHome,new CommonCallbacks.CompletionCallback() {
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
        return HomeTaskType.SET_MAX_DISTANCE_FROM_HOME;
    }
}
