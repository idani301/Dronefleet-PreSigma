package eyesatop.controller.djinew.tasks.flight;

import java.util.concurrent.CountDownLatch;

import dji.common.error.DJIError;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.djinew.ControllerDjiNew;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.GoHome;
import eyesatop.util.Predicate;
import eyesatop.util.Removable;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by einav on 13/05/2017.
 */

public class DjiGoHome extends RunnableDroneTask<FlightTaskType> implements GoHome {

    private final ControllerDjiNew droneController;

    private DJIError goHomeDjiError;
    private Removable flightModeObserver = Removable.STUB;

    public DjiGoHome(ControllerDjiNew droneController) {
        this.droneController = droneController;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        Boolean isControllerFlying = droneController.flying().value();

        if(isControllerFlying != null && !isControllerFlying){
            throw new DroneTaskException("Drone is not flying, Go Home Fail");
        }

        FlightController djiFlightController = droneController.getHardwareManager().getDjiFlightController();

        if(djiFlightController == null || !djiFlightController.isConnected()){
            MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Drone is not connected, can't go home");
            throw new DroneTaskException("Drone is not connected, can't go home");
        }

        final CountDownLatch djiTaskLatch = new CountDownLatch(1);

        droneController.getHardwareManager().getDjiFlightController().startGoHome(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                goHomeDjiError = djiError;
                djiTaskLatch.countDown();
            }
        });

        djiTaskLatch.await();

        if(goHomeDjiError != null){
            MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Internal Dji Error : " + goHomeDjiError.getDescription());
            throw new DroneTaskException("Go Home Internal Error : " + goHomeDjiError.getDescription());
        }

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Go Home : Waiting for Auto go home");
        droneController.flightMode().await(new Predicate<FlightMode>() {
            @Override
            public boolean test(FlightMode subject) {
                return subject == FlightMode.AUTO_GO_HOME;
            }
        });

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Go Home : Done Waiting for Auto go home, Waiting for go home to be done");

        final CountDownLatch goHomeLatch = new CountDownLatch(1);

        flightModeObserver = droneController.flightMode().observe(new Observer<FlightMode>() {
            @Override
            public void observe(FlightMode oldValue, FlightMode newValue, Observation<FlightMode> observation) {
                if((newValue == null || newValue != FlightMode.AUTO_GO_HOME)){
                    goHomeLatch.countDown();
                }
            }
        });

        goHomeLatch.await();

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Done Going home. Motors On : " + (droneController.motorsOn().isNull() ? "NULL" : droneController.motorsOn().value()));

        if(droneController.motorsOn().isNull() || droneController.motorsOn().value()){
            throw new DroneTaskException("Ended go home without success, motors are still on.");
        }
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {

        flightModeObserver.remove();

        if(!droneController.flightMode().isNull() && droneController.flightMode().value() == FlightMode.AUTO_GO_HOME){
            if(droneController.getHardwareManager().getDjiFlightController() != null && droneController.getHardwareManager().getDjiFlightController().isConnected()){
                droneController.getHardwareManager().getDjiFlightController().cancelGoHome(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Done Cancel going home inside cleanup. reasult : " + (djiError == null ? "Success" : "Failed : " + djiError.getDescription()));
                    }
                });
            }
        }
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.GO_HOME;
    }
}
