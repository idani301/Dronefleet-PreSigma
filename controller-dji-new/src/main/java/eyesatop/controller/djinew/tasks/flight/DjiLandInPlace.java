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
import eyesatop.controller.tasks.flight.LandInPlace;
import eyesatop.util.Predicate;
import eyesatop.util.Removable;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;
import logs.LoggerTypes;

public class DjiLandInPlace extends RunnableDroneTask<FlightTaskType> implements LandInPlace {

    private final ControllerDjiNew droneController;

    private DJIError landInPlaceDjiError;
    private Removable flightModeObserver = Removable.STUB;

    public DjiLandInPlace(ControllerDjiNew droneController) {
        this.droneController = droneController;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        FlightController djiFlightController = droneController.getHardwareManager().getDjiFlightController();

        if(djiFlightController == null || !djiFlightController.isConnected()){
            MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Drone is not connected, can't Land");
            throw new DroneTaskException("Drone is not connected, can't Land");
        }

        final CountDownLatch djiTaskLatch = new CountDownLatch(1);

        droneController.getHardwareManager().getDjiFlightController().startLanding(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                landInPlaceDjiError = djiError;
                djiTaskLatch.countDown();
            }
        });

        djiTaskLatch.await();

        if(landInPlaceDjiError != null){
            MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Internal Dji Error : " + landInPlaceDjiError.getDescription());
            throw new DroneTaskException("Land Internal Error : " + landInPlaceDjiError.getDescription());
        }

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Land : Waiting for Auto Land");
        droneController.flightMode().await(new Predicate<FlightMode>() {
            @Override
            public boolean test(FlightMode subject) {
                return subject == FlightMode.AUTO_GO_HOME;
            }
        });

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Land : Done Waiting for Auto Land, Waiting for Land to be done");

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

        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Done Land. Motors On : " + (droneController.motorsOn().isNull() ? "NULL" : droneController.motorsOn().value()));

        if(droneController.motorsOn().isNull() || droneController.motorsOn().value()){
            throw new DroneTaskException("Ended Land without success, motors are still on.");
        }
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {

        flightModeObserver.remove();

        if(!droneController.flightMode().isNull() && droneController.flightMode().value() == FlightMode.AUTO_GO_HOME){
            if(droneController.getHardwareManager().getDjiFlightController() != null && droneController.getHardwareManager().getDjiFlightController().isConnected()){
                droneController.getHardwareManager().getDjiFlightController().cancelLanding(new CommonCallbacks.CompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Done Cancel Land inside cleanup. reasult : " + (djiError == null ? "Success" : "Failed : " + djiError.getDescription()));
                    }
                });
            }
        }
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.LAND_IN_PLACE;
    }
}
