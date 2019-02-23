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
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;
import eyesatop.util.model.Property;

/**
 * Created by einav on 22/07/2017.
 */
public class DjiSetReturnHomeAltitude extends RunnableDroneTask<HomeTaskType> implements SetReturnHomeAltitude {

    private final ControllerDjiNew controller;
    private final double altitude;

    private DJIError taskError = null;

    public DjiSetReturnHomeAltitude(ControllerDjiNew controller, double altitude) {
        this.controller = controller;
        this.altitude = altitude;
    }

    @Override
    public double altitude() {
        return altitude;
    }

    @Override
    public HomeTaskType taskType() {
        return HomeTaskType.SET_RETURN_HOME_ALT;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        FlightController flightController = controller.getHardwareManager().getDjiFlightController();

        final CountDownLatch taskLatch = new CountDownLatch(1);

        try {
            flightController.setGoHomeHeightInMeters((int) altitude, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    taskError = djiError;
                    taskLatch.countDown();
                }
            });
            taskLatch.await();
        }
        catch (Exception e){
            taskLatch.countDown();
            e.printStackTrace();
        }

        if(taskError != null){
            throw new DroneTaskException(taskType().getName() + " ,Failed. Reason : " + taskError.getDescription());
        }

        controller.droneHome().onComponentConnected();
    }
}
