//package eyesatop.controller.djinew.tasks.flight;
//
//import java.util.concurrent.CountDownLatch;
//
//import dji.common.error.DJIError;
//import dji.common.util.CommonCallbacks;
//import dji.sdk.flightcontroller.FlightController;
//import eyesatop.controller.beans.TakeOffState;
//import eyesatop.controller.djinew.ControllerDjiNew;
//import eyesatop.controller.djinew.DjiMicroMovementManager;
//import eyesatop.controller.tasks.ConfirmationsType;
//import eyesatop.controller.tasks.RunnableDroneTask;
//import eyesatop.controller.tasks.TaskProgressState;
//import eyesatop.controller.tasks.TaskStatus;
//import eyesatop.controller.tasks.exceptions.DroneTaskException;
//import eyesatop.controller.tasks.flight.FlightTaskType;
//import eyesatop.controller.tasks.takeoff.TakeOff;
//import eyesatop.util.Predicate;
//import eyesatop.util.Removable;
//import eyesatop.util.android.logs.LoggerTypes;
//import eyesatop.util.android.logs.MainLogger;
//import eyesatop.util.geo.Telemetry;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//
///**
// * Created by einav on 24/01/2017.
// */
//
//public class DjiTakeOff extends RunnableDroneTask<FlightTaskType> implements TakeOff {
//
//    private double altitude;
//    private ControllerDjiNew droneController;
//
//    private DJIError takeOffDjiError;
//
//    private Removable telemetryObserver = Removable.STUB;
//
//    public DjiTakeOff(ControllerDjiNew droneController, double altitude) {
//        this.altitude = altitude;
//        this.droneController = droneController;
//    }
//
//    private FlightController getDjiFlightController(){
//        return droneController.getHardwareManager().getDjiFlightController();
//    }
//
//    @Override
//    public double altitude() {
//        return altitude;
//    }
//
//    @Override
//    protected void perform(final Property<TaskProgressState> state) throws DroneTaskException,InterruptedException {
//
//        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "Starting Take Off DroneTask,waiting for confirmation");
//
//        final CountDownLatch takeoffLatch = new CountDownLatch(1);
//
//        getDjiFlightController().startTakeoff(new CommonCallbacks.CompletionCallback() {
//            @Override
//            public void onResult(DJIError djiError) {
//
//                takeOffDjiError = djiError;
//                MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "DJI Done with take off. result : " + (djiError == null ? "NULL" : djiError.getDescription()));
//                takeoffLatch.countDown();
//            }
//        });
//
//        takeoffLatch.await();
//
//        if(takeOffDjiError != null && droneController.flying().value() != true){
//            throw new DroneTaskException("Dji Internal Error : " + takeOffDjiError.getDescription());
//        }
//
//        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Waiting for enable speeds");
//        droneController.getEnableSticks().getSticksEnabled().awaitTrue();
//
//        state.set(new TaskProgressState(70, TakeOffState.APPROCHING_TARGET_HEIGHT.name()));
//
//        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "Sending speeds commadns, waiting for reaching target alt.");
//        telemetryObserver = droneController.telemetry().observe(new Observer<Telemetry>() {
//
//            @Override
//            public void observe(Telemetry oldValue, Telemetry newValue, Observation observer) {
//                if(newValue != null && newValue.location() != null) {
//                    sendSpeedCommand(altitude, newValue.location().getAltitude());
//                }
//                else{
//                    MainLogger.logger.write_message(LoggerTypes.ERROR,"Null inside take off telemetery observer.");
//                }
//            }
//        });
//
//        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "Current Alt : " + droneController.telemetry().value().location().getAltitude() + ", Target Alt : " + altitude);
//        droneController.telemetry().await(new Predicate<Telemetry>() {
//            @Override
//            public boolean test(Telemetry subject) {
//
//                if(subject == null || subject.location() == null || subject.velocities() == null){
//                    return false;
//                }
//
//                return (Math.abs(subject.location().getAltitude() - altitude) < 0.5 && Math.abs(subject.velocities().getZ()) < 0.7);
//            }
//        });
//
//        telemetryObserver.remove();
//        telemetryObserver = Removable.STUB;
//
//        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS, "Reached target alt");
//
//        state.set(new TaskProgressState(100, TakeOffState.DONE.name()));
//
//    }
//
//    public void sendSpeedCommand(double targetHeight,double currentHeight){
//        double speedSign = Math.signum(targetHeight - currentHeight);
//        double distanceFromTarget = Math.abs(targetHeight - currentHeight);
//        double speedValue = 0;
//        double slowDistance = 10D;
//
//
//        if(distanceFromTarget > slowDistance){
//            speedValue = DjiMicroMovementManager.MAX_HEIGHT_VELOCITY;
//        }
//        else if(distanceFromTarget > 1 && distanceFromTarget <= slowDistance){
//            speedValue = distanceFromTarget*(DjiMicroMovementManager.MAX_HEIGHT_VELOCITY/slowDistance);
//        }
//        else{
//            speedValue = 0.4F;
//        }
//
//        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,"Send Speed Command : " +
//                MainLogger.TAB + "Current Height : " + currentHeight +
//                MainLogger.TAB + "Speed Sign : " + speedSign +
//                MainLogger.TAB + "Distance From target : " + distanceFromTarget +
//                MainLogger.TAB + "SpeedValue : " + speedValue);
//
//        droneController.getMovementManager().sendSpeedsMessage(0,0,0, (float) (speedSign * speedValue));
//    }
//
//    @Override
//    public FlightTaskType taskType() {
//        return FlightTaskType.TAKE_OFF;
//    }
//
//    @Override
//    protected void cleanUp(TaskStatus exitStatus) throws Exception {
//        telemetryObserver.remove();
//    }
//}
