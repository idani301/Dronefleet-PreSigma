//package eyesatop.controller.djinew.tasks.flight;
//
//import java.util.concurrent.CountDownLatch;
//
//import eyesatop.controller.djinew.ControllerDjiNew;
//import eyesatop.controller.tasks.RunnableDroneTask;
//import eyesatop.controller.tasks.TaskProgressState;
//import eyesatop.controller.tasks.TaskStatus;
//import eyesatop.controller.tasks.exceptions.DroneTaskException;
//import eyesatop.controller.tasks.flight.FlightTaskType;
//import eyesatop.controller.tasks.flight.FlyTo;
//import eyesatop.util.Removable;
//import eyesatop.util.android.logs.LoggerTypes;
//import eyesatop.util.android.logs.MainLogger;
//import eyesatop.util.geo.Location;
//import eyesatop.util.geo.Telemetry;
//import eyesatop.util.model.Observation;
//import eyesatop.util.model.Observer;
//import eyesatop.util.model.Property;
//
///**
// * Created by einav on 06/03/2017.
// */
//
//public class DjiFlyToTask extends RunnableDroneTask<FlightTaskType> implements FlyTo {
//
//    private final ControllerDjiNew droneController;
//    private final Location targetLocation;
//
//    private Removable reachedPointRemovable = Removable.STUB;
//
//    public DjiFlyToTask(ControllerDjiNew droneController, Location targetLocation) {
//        this.droneController = droneController;
//        this.targetLocation = targetLocation;
//    }
//
//    @Override
//    protected void perform(Property<TaskProgressState> state) throws DroneTaskException,InterruptedException {
//
//        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,this.getClass().getName() + " Started");
//
//        Boolean limitationActive = droneController.droneHome().limitationActive().value();
//        if(limitationActive != null && limitationActive){
//
//            Location droneHomeLocation = droneController.droneHome().homeLocation().value();
//            Double maxDistance = droneController.droneHome().maxDistanceFromHome().value();
//
//            if(droneHomeLocation == null){
//                throw new DroneTaskException("Limitation is active, but no idea where home is.");
//            }
//
//            if(maxDistance == null){
//                throw new DroneTaskException("Limitation is active, but no idea what is the max distance");
//            }
//
//            double targetDistanceFromHome = droneHomeLocation.distance(targetLocation);
//
//            MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,this.getClass().getName() +
//                    " Flight Limitation Active. Target Distance from home : " + targetDistanceFromHome + ", Max Distance : " + maxDistance);
//
//            if(targetDistanceFromHome > maxDistance){
//                throw new DroneTaskException("Target is inside illegal area for drone. Target Distance from home : " + targetDistanceFromHome + ", Max Possible Distance : " + maxDistance);
//            }
//        }
//
//        droneController.getMovementManager().setGotoLocation(targetLocation);
//
//        final CountDownLatch gotoPointLatch = new CountDownLatch(1);
//
//        reachedPointRemovable = droneController.telemetry().observe(new Observer<Telemetry>() {
//            @Override
//            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
//
//                Location currentLocation;
//
//                try {
//
//                    currentLocation = droneController.telemetry().value().location();
//
//                    double altDistance = Math.abs(currentLocation.getAltitude() - targetLocation.getAltitude());
//
//                    if (targetLocation.distance(currentLocation) < 3 && altDistance < 1) {
//                        gotoPointLatch.countDown();
//                        observation.remove();
//                    }
//                }
//                catch (Exception e){
//                    observation.remove();
//                    gotoPointLatch.countDown();
//                }
//            }
//        });
//
//        MainLogger.logger.write_message(LoggerTypes.FLIGHT_TASKS,this.getClass().getName() + " Waiting for reaching point");
//        gotoPointLatch.await();
//    }
//
//    @Override
//    protected void cleanUp(TaskStatus exitStatus) throws Exception {
//        super.cleanUp(exitStatus);
//        reachedPointRemovable.remove();
//
//        switch (exitStatus){
//
//            case CANCELLED:
//                droneController.getMovementManager().setGotoLocation(null);
//                break;
//            case FINISHED:
//                droneController.getMovementManager().setGotoLocation(null);
//                break;
//            case ERROR:
//                droneController.getMovementManager().setGotoLocation(null);
//                break;
//        }
//    }
//
//    @Override
//    public FlightTaskType taskType() {
//        return FlightTaskType.GOTO_POINT;
//    }
//
//    @Override
//    public Location location() {
//        return targetLocation;
//    }
//}
