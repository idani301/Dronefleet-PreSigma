package eyesatop.controller.djinew.tasks.home;

import java.util.concurrent.CountDownLatch;

import dji.common.error.DJIError;
import dji.common.model.LocationCoordinate2D;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

/**
 * Created by einav on 01/05/2017.
 */

public class DjiSetHomeLocation extends RunnableDroneTask<HomeTaskType> implements SetHomeLocation {

    private final ControllerDjiNew droneController;
    private final Location location;
    private DJIError taskDjiError = null;

    public DjiSetHomeLocation(ControllerDjiNew droneController,Location location){
        this.droneController = droneController;
        this.location = location;
    }

    @Override
    public Location location() {
        return location;
    }

    @Override
    protected void perform(final Property<TaskProgressState> state) throws DroneTaskException,InterruptedException {

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Starting set home location task. Location : " + (location == null ? "NULL" : location.toString()));

        FlightController djiFlightController = droneController.getHardwareManager().getDjiFlightController();

        if(djiFlightController == null || djiFlightController.isConnected() == false){
            MainLogger.logger.write_message(LoggerTypes.HOME_TASKS,this.getClass().getName() + " ,dji flight controller is null.");
            throw new DroneTaskException(this.getClass().getName() + " ,dji flight controller is null.");
        }

        final CountDownLatch setHomeLatch = new CountDownLatch(1);

        if(location == null){
            djiFlightController.setHomeLocationUsingAircraftCurrentLocation(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    taskDjiError = djiError;
                    setHomeLatch.countDown();
                }
            });
        }
        else{
            djiFlightController.setHomeLocation(new LocationCoordinate2D(location.getLatitude(), location.getLongitude()), new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    taskDjiError = djiError;
                    setHomeLatch.countDown();
                }
            });
        }

        MainLogger.logger.write_message(LoggerTypes.HOME_TASKS,this.getClass().getName() + " Sent to DJI, waiting for complete.");
        setHomeLatch.await();
        MainLogger.logger.write_message(LoggerTypes.HOME_TASKS,this.getClass().getName() + " Completed, Status: " + (taskDjiError == null ? "Success" : "Failed, " + taskDjiError.getDescription()));

        if(taskDjiError != null) {
            throw new DroneTaskException(this.getClass().getName() + "Set Home Failed, Internal error inside DJI. Reason: " + taskDjiError.getDescription());
        }
    }

    @Override
    public HomeTaskType taskType() {
        return HomeTaskType.SET_HOME_LOCATION;
    }
}
