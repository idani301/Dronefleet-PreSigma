//package eyesatop.controller.djinew.tasks.flight;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import eyesatop.controller.beans.RotationType;
//import eyesatop.controller.djinew.ControllerDjiNew;
//import eyesatop.controller.tasks.RunnableDroneTask;
//import eyesatop.controller.tasks.TaskProgressState;
//import eyesatop.controller.tasks.TaskStatus;
//import eyesatop.controller.tasks.exceptions.DroneTaskException;
//import eyesatop.controller.tasks.flight.FlightTaskType;
//import eyesatop.controller.tasks.flight.FlyInCircle;
//import eyesatop.util.Predicate;
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
// * Created by Idan on 09/10/2017.
// */
//
//public class DjiFlyInCircle extends RunnableDroneTask<FlightTaskType> implements FlyInCircle {
//
//    private final ControllerDjiNew controller;
//    private final Location center;
//    private final double radiusReached;
//    private final RotationType rotationType;
//    private final double degreesToCover;
//    private final double startingDegree;
//    private final double altitudeFromGround;
//    private DjiFlyToTask flyToTask;
//    private final ExecutorService flyToExecutor = Executors.newSingleThreadExecutor();
//
//    private Removable observer = Removable.STUB;
//
//    public DjiFlyInCircle(ControllerDjiNew controller, Location center, double radiusReached, RotationType rotationType, double degreesToCover, double startingDegree, double altitudeFromGround) {
//        this.controller = controller;
//        this.center = center;
//        this.radiusReached = radiusReached;
//        this.rotationType = rotationType;
//        this.degreesToCover = degreesToCover;
//        this.startingDegree = startingDegree;
//        this.altitudeFromGround = altitudeFromGround;
//    }
//
//    @Override
//    public Location center() {
//        return center;
//    }
//
//    @Override
//    public double radiusReached() {
//        return radiusReached;
//    }
//
//    @Override
//    public RotationType rotationType() {
//        return rotationType;
//    }
//
//    @Override
//    public double degreesToCover() {
//        return degreesToCover;
//    }
//
//    @Override
//    public double startingDegree() {
//        return startingDegree;
//    }
//
//    @Override
//    public double altitudeFromGround() {
//        return altitudeFromGround;
//    }
//
//    @Override
//    public double velocity() {
//        return 0;
//    }
//
//    @Override
//    public FlightTaskType taskType() {
//        return FlightTaskType.FLY_IN_CIRCLE;
//    }
//
//    @Override
//    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {
//
//        final Property<Double> degreesCovered = new Property<Double>(0D);
//
//        Location currentLocation = Telemetry.telemetryToLocation(controller.telemetry().value());
//        if(currentLocation == null){
//            throw new DroneTaskException("Unknown controller position");
//        }
//
//        double startingDegree;
//        if(this.startingDegree != -1){
//            startingDegree = this.startingDegree;
//        }
//        else{
//            startingDegree = center.az(currentLocation);
//        }
//
//        Location newLocation = center.getLocationFromAzAndDistance(radiusReached,startingDegree);
//        flyToTask = new DjiFlyToTask(controller,newLocation.altitude(altitudeFromGround));
//        flyToExecutor.submit(flyToTask);
//        flyToTask.status().await(new Predicate<TaskStatus>() {
//            @Override
//            public boolean test(TaskStatus subject) {
//                return subject.isTaskDone();
//            }
//        });
//        if(flyToTask.status().value() != TaskStatus.FINISHED){
//            throw new DroneTaskException("Fly to failed");
//        }
//
//        final CountDownLatch taskLatch = new CountDownLatch(1);
//        observer = controller.telemetry().observe(new Observer<Telemetry>() {
//            @Override
//            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
//
//                if(oldValue == null || oldValue.location() == null || newValue == null || newValue.location() == null){
//                    return;
//                }
//
//                double degreesDifference = 0;
//                try {
//                    degreesDifference = Location.degreesCovered(center.az(oldValue.location()), center.az(newValue.location()));
//                } catch (IllegalArgumentException e) {
//                    e.printStackTrace();
//                    MainLogger.logger.write_message(LoggerTypes.ERROR,"Error inside degrees covered : " +
//                    oldValue.toString() + "," + newValue.toString() +
//                    MainLogger.TAB + "Center az to old value : " + center.az(oldValue.location()) +
//                                    MainLogger.TAB + "Center az to new value : " + center.az(newValue.location()));
//                }
//                degreesCovered.set(degreesCovered.value() + degreesDifference);
//
//                if (Math.abs(Math.abs(degreesCovered.value()) - degreesToCover) < 5) {
//                    observer.remove();
//                    observer = Removable.STUB;
//                    taskLatch.countDown();
//                    return;
//                }
//            }
//        });
//        taskLatch.await();
//    }
//
//    @Override
//    protected void cleanUp(TaskStatus exitStatus) throws Exception {
//
//        if(flyToTask != null){
//
//            if(!flyToTask.status().value().isTaskDone()) {
//                flyToTask.cancel();
//            }
//            flyToTask.status().await(new Predicate<TaskStatus>() {
//                @Override
//                public boolean test(TaskStatus subject) {
//                    return subject.isTaskDone();
//                }
//            });
//        }
//
//        observer.remove();
//        flyToExecutor.shutdown();
//    }
//}
