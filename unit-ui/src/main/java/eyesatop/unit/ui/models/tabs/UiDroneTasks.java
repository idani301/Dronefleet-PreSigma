package eyesatop.unit.ui.models.tabs;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.DroneController;
import eyesatop.controller.GimbalRequest;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.AltitudeType;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.mission.Mission;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.gimbal.RotateGimbal;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Einav on 06/07/2017.
 */

public class UiDroneTasks {

    private final LittleMessageViewModel littleMessageViewModel;
    private final DroneController droneController;
    private final ExecutorService droneControllerTasksExecutor = Executors.newSingleThreadExecutor();

    private final ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService gimbalExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService homeExecutor = Executors.newSingleThreadExecutor();

    public UiDroneTasks(DroneController droneController,LittleMessageViewModel littleMessageViewModel) {
        this.droneController = droneController;
        this.littleMessageViewModel = littleMessageViewModel;
    }

    public void takeOff(final double altitude){

        droneControllerTasksExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final TakeOff x = droneController.flightTasks().takeOff(altitude);
                    trackTask(x);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Take Off : " + e.getErrorString());
                }
            }
        });
    }

    public void flyTo(final Location location){
        droneControllerTasksExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
//                    System.out.println("Starting Fly to task");
                    DroneTask task = droneController.flightTasks().flyTo(location,new AltitudeInfo(AltitudeType.FROM_TAKE_OFF_LOCATION,location.getAltitude()),null,null,5D);
//                    System.out.println("Started fly to task, got UUID : " + task.uuid());
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Fly to point : " + e.getErrorString());
                }
            }
        });
    }

    //
//    public void flyToUsingDTM(final Location location){
//        droneControllerTasksExecutor.submit(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    DroneTask task = droneController.flightTasks().flyToUsingDTM(location,30,20,20,10D);
//                    trackTask(task);
//                } catch (DroneTaskException e) {
//                    e.printStackTrace();
//                    littleMessageViewModel.addNewMessage("Can't Fly to point using DTM: " + e.getErrorString());
//                }
//            }
//        });
//    }

    public void lookAtPoint(final Location location){

        gimbalExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    DroneTask task = droneController.gimbal().lookAtPoint(location);
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Look at point : " + e.getErrorString());
                }
            }
        });
    }


    public void explore(){
        gimbalExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    DroneTask task = droneController.gimbal().explore();
                    trackTask(task);
                }
                catch (DroneTaskException e){
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Explore : " + e.getErrorString());
                }
            }
        });
    }

    public void lockGimbalAtLocation(final Location location){
        gimbalExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    DroneTask task = droneController.gimbal().lockGimbalAtLocation(location);
                    trackTask(task);
                }
                catch (DroneTaskException e){
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Lock Gimbal : " + e.getErrorString());
                }
            }
        });
    }

    public void goHome(){

        droneControllerTasksExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DroneTask task = droneController.flightTasks().goHome();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Go Home : " + e.getErrorString());
                }
            }
        });
    };

    public void zoomIn(){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DroneTask task = droneController.camera().zoomIn();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Zoom In : " + e.getErrorString());
                }
            }
        });
    }


    public void zoomOut(){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DroneTask task = droneController.camera().zoomOut();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Zoom In : " + e.getErrorString());
                }
            }
        });
    }

    public void stopShootingPhotos(){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DroneTask task = droneController.camera().stopShootingPhotos();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Stop Shooting Photos : " + e.getErrorString());
                }
            }
        });
    }

    public void startRecord(){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    DroneTask task = droneController.camera().startRecording();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Start Record : " + e.getErrorString());
                }
            }
        });
    }


    public void stopLiveStream(){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    DroneTask task = droneController.camera().stopLiveStream();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Stop live Stream : " + e.getErrorString());
                }
            }
        });
    }

    public void startLiveStream(final String url){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    DroneTask task = droneController.camera().startLiveStream(url);
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Start live Stream : " + e.getErrorString());
                }
            }
        });
    }

    public void formatSDCard(){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    DroneTask task = droneController.camera().formatSDCard();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Format SD Card : " + e.getErrorString());
                }
            }
        });
    }


    public void stopRecord(){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    DroneTask task = droneController.camera().stopRecording();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Stop Record : " + e.getErrorString());
                }
            }
        });
    }


    public void takePhoto(){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    DroneTask task = droneController.camera().takePhoto();
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Take Photo : " + e.getErrorString());
                }
            }
        });
    }


    public void changeMode(final CameraMode newMode){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    DroneTask task = droneController.camera().setMode(newMode);
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Change Camera Mode : " + e.getErrorString());
                }
            }
        });
    }

    public void setMaxAltitude(final double maxAltitude){

        if(maxAltitude < 20 || maxAltitude > 500){
            littleMessageViewModel.addNewMessage("Can't set max Altitude : altitude is out of bounds");
            return;
        }

        homeExecutor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    final SetMaxAltitudeFromHomeLocation maxAltitudeFromHomeLocation = droneController.droneHome().setMaxAltitudeFromTakeOffLocation(maxAltitude);
                    trackTask(maxAltitudeFromHomeLocation);
                } catch (DroneTaskException e) {
                    littleMessageViewModel.addNewMessage("Can't Set Max Altitude : " + e.getErrorString());
                    e.printStackTrace();
                }
            }
        });
    }

    public void rotateGimbal(final GimbalRequest request){
        droneControllerTasksExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try{
                    final RotateGimbal rotateGimbal = droneController.gimbal().rotateGimbal(request,3);
//                    trackTask(rotateGimbal);
                }
                catch (DroneTaskException e){
                    littleMessageViewModel.addNewMessage("Can't rotate gimbal : " + e.getErrorString());
                    e.printStackTrace();
                }
            }
        });
    }

    public void setReturnHomeAltitude(final double altitude){

        if(altitude < 20 || altitude > 500){
            littleMessageViewModel.addNewMessage("Can't set Return Home Altitude : altitude is out of bounds");
            return;
        }

        homeExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final DroneTask task = droneController.droneHome().setReturnHomeAltitude(altitude);
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Set Return Home altitude : " + e.getErrorString());
                }
            }
        });
    }

    public void setMaxDistance(final double maxDistance){

        if(maxDistance < 15 || maxDistance > 8000){
            littleMessageViewModel.addNewMessage("Can't Set Max Distance : Max Distance out of bounds");
            return;
        }

        homeExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final DroneTask task = droneController.droneHome().setMaxDistanceFromHome(maxDistance);
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Set Max Distance : " + e.getErrorString());
                }
            }
        });
    }

    public void setLimitationActive(final boolean isActive){
        homeExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    DroneTask task = droneController.droneHome().setFlightLimitationEnabled(isActive);
                    trackTask(task);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                    littleMessageViewModel.addNewMessage("Can't Set Flight Limitation Enabled to " + isActive + ":" + e.getErrorString());
                }
            }
        });
    }

    public void setZoomLevel(final double zoomLevel){
        cameraExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    droneController.camera().setZoomLevel(zoomLevel);
                } catch (DroneTaskException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void shutdown(){
        droneControllerTasksExecutor.shutdownNow();
    }

    public void startMission(final Mission.MissionStub mission){
        droneControllerTasksExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ((AbstractDroneController)droneController).getMissionManager().startMission(mission);
                    trackTask(mission);
                } catch (DroneTaskException e) {
                    littleMessageViewModel.addNewMessage("Can't Start Mission ");
                    e.printStackTrace();
                }
            }
        });
    }

    public void trackTask(final DroneTask task){

//        System.out.println("Starting to track task with uuid : " + task.uuid());

        task.status().observe(new Observer<TaskStatus>() {
            @Override
            public void observe(TaskStatus oldValue, TaskStatus newValue, Observation observation) {

//                System.out.println("Track UUID " + task.uuid() + "status changed to : " + newValue);

                if(newValue != null && newValue.isTaskDone()){

                    observation.remove();

                    switch (newValue){

                        case NOT_STARTED:
                            littleMessageViewModel.addNewMessage(task.taskType().getName() + ": Illegal Exit Status");
                            break;
                        case RUNNING:
                            littleMessageViewModel.addNewMessage(task.taskType().getName() + ": Illegal Exit Status");
                            break;
                        case CANCELLED:
                            littleMessageViewModel.addNewMessage(task.taskType().getName() + ": Cancelled");
                            break;
                        case FINISHED:
                            littleMessageViewModel.addNewMessage(task.taskType().getName() + ": Completed");
                            break;
                        case ERROR:
                            String errorString = task.error().isNull() ? "" : ((DroneTaskException)task.error().value()).getErrorString();
                            littleMessageViewModel.addNewMessage(task.taskType().getName() + " : Failed, Reason:  " + errorString);
                            break;
                    }
                }
            }
        }).observeCurrentValue();
    }
}
