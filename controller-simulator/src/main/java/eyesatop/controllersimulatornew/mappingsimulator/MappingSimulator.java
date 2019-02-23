package eyesatop.controllersimulatornew.mappingsimulator;

import java.util.concurrent.CountDownLatch;

import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controllersimulatornew.TestingSimulator;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.ObstacleObject;
import eyesatop.util.geo.ObstacleProvider;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.Velocities;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Einav on 15/11/2017.
 */

public class MappingSimulator {

    public static void main(String[] args){

        final TestingSimulator controller = new TestingSimulator(new ObstacleProvider() {
            @Override
            public DtmProvider dtmProvider() {
                return null;
            }

            @Override
            public ObservableList<ObstacleObject> obstacleObjects() {
                return null;
            }
        });

        controller.togglePower(new Telemetry(new Location(0,0,0),new Velocities(0,0,0),0 ), new GimbalState(0,0,0));
//        try {
//            controller.flightTasksTesting().flyInCircle(
//                    new Location(0,0,0).getLocationFromAzAndDistance(100,0).altitude(50),
//                    100,
//                    RotationType.CLOCKWISE,
//                    360,
//                    0,
//                    50,
//                    5);
//        } catch (DroneTaskException e) {
//            e.printStackTrace();
//        }

//        try {
//            controller.flightTasksTesting().flyTo(new Location(0,0,0).getLocationFromAzAndDistance(500,0).altitude(10));
//        } catch (DroneTaskException e) {
//            e.printStackTrace();
//        }
//        controller.flightTasks().current().observe(new Observer<DroneTask<FlightTaskType>>() {
//            @Override
//            public void observe(DroneTask<FlightTaskType> oldValue, DroneTask<FlightTaskType> newValue, Observation<DroneTask<FlightTaskType>> observation) {
//                if (newValue != null) {
//                    if (newValue.status().value() != TaskStatus.FINISHED) {
//                        System.out.println("finish");
//                        System.exit(1);
//                    }
//                }
//            }
//        });
//        controller.startCameraShoot(2000);
        controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                System.out.println(newValue.location().distance3D(new Location(0,0,0).getLocationFromAzAndDistance(500,0).altitude(10)));
                if (newValue.location().distance3D(new Location(0,0,0).getLocationFromAzAndDistance(500,0).altitude(10)) < 5) {
                    System.out.println("images: " + controller.getImageInfos().size());
                    System.out.println("time to point: " + controller.getTimeInFlightTask()/1000 + "s");
                }
            }
        });

        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
