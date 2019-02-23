package eyesatop.controller_tcpip.remote.tcpipcontroller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.mock.MockDroneFlightTasks;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.controller.tasks.flight.FollowNavPlan;
import eyesatop.controller.tasks.flight.GoHome;
import eyesatop.controller.tasks.flight.Hover;
import eyesatop.controller.tasks.flight.LandAtLandingPad;
import eyesatop.controller.tasks.flight.LandAtLocation;
import eyesatop.controller.tasks.flight.LandInPlace;
import eyesatop.controller.tasks.flight.RotateHeading;
import eyesatop.controller.tasks.takeoff.TakeOff;
import eyesatop.controller_tcpip.common.tasks.flight.FlyInCircleTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.flight.FlyToUpdate;
import eyesatop.controller_tcpip.common.tasks.flight.FollowNavPlanTaskUpdate;
import eyesatop.controller_tcpip.common.tasks.flight.GoHomeUpdate;
import eyesatop.controller_tcpip.common.tasks.flight.TakeOffUpdate;
import eyesatop.controller_tcpip.common.tasksrequests.TaskResponse;
import eyesatop.controller_tcpip.common.tasksrequests.flight.ConfirmLandRequest;
import eyesatop.controller_tcpip.common.tasksrequests.flight.FlightTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.flight.FlyInCircleTaskRequest;
import eyesatop.controller_tcpip.common.tasksrequests.flight.FlyToRequest;
import eyesatop.controller_tcpip.common.tasksrequests.flight.FollowNavPlanRequest;
import eyesatop.controller_tcpip.common.tasksrequests.flight.GoHomeRequest;
import eyesatop.controller_tcpip.common.tasksrequests.flight.TakeOffRequest;
import eyesatop.controller_tcpip.remote.tasks.taskmanager.TaskManager;
import eyesatop.landingpad.LandingPad;
import eyesatop.util.connections.TimeoutInfo;
import eyesatop.util.connections.tcp.requestresponse.RequestResponseException;
import eyesatop.util.geo.Location;
import eyesatop.util.model.Property;

public class TCPFlightTasks extends MockDroneFlightTasks {

    private final TCPController controller;

    private final TaskManager<FlightTaskType> tasksManager;

    public TCPFlightTasks(TCPController controller) {
        this.controller = controller;
        this.tasksManager = new TaskManager<>(controller);
    }

    private synchronized UUID startTask(final FlightTaskRequest taskRequest) throws DroneTaskException {
        try {
            TaskResponse response = controller.getFlightTaskRequestsClient().sendMessage(taskRequest,3,TimeUnit.SECONDS);

            if(response == null){
                throw new DroneTaskException("Had no Response");
            }

            if(response.getTaskUUID() == null){
                throw new DroneTaskException(response.getError() == null ? "N/A" : response.getError());
            }

            return response.getTaskUUID();
        } catch (InterruptedException e) {
            throw new DroneTaskException("Interrupt");
        } catch (RequestResponseException e) {
            throw new DroneTaskException(e.getErrorMessage());
        }
    }

    public TaskManager<FlightTaskType> getTasksManager() {
        return tasksManager;
    }

    @Override
    public TakeOff takeOff(final double altitude) throws DroneTaskException {

        UUID uuid = startTask(new TakeOffRequest(altitude));

        TakeOffUpdate takeOffUpdate = new TakeOffUpdate(uuid,null, TaskStatus.NOT_STARTED,altitude);
        return (TakeOff) tasksManager.getTask(takeOffUpdate);
    }

    @Override
    public LandAtLandingPad land(LandingPad landingPad) throws DroneTaskException {
        return null;
    }

    @Override
    public LandAtLocation land(Location location) throws DroneTaskException {
        return null;
    }

    @Override
    public LandInPlace land() throws DroneTaskException {
        return null;
    }

    @Override
    public GoHome goHome() throws DroneTaskException {

        UUID uuid = startTask(new GoHomeRequest());
        GoHomeUpdate goHomeUpdate = new GoHomeUpdate(uuid,null,TaskStatus.NOT_STARTED);
        return (GoHome) tasksManager.getTask(goHomeUpdate);
    }

    @Override
    public RotateHeading rotateHeading(double angle) throws DroneTaskException {
        return null;
    }

    @Override
    public FlyTo flyTo(Location location, AltitudeInfo altitudeInfo, Double az, Double maxVelocity, Double radiusReached) throws DroneTaskException {

        UUID uuid = startTask(new FlyToRequest(location,altitudeInfo,az,maxVelocity,radiusReached));
        FlyToUpdate flyToUpdate = new FlyToUpdate(uuid,null,TaskStatus.NOT_STARTED,location,altitudeInfo,az,maxVelocity,radiusReached);
        return (FlyTo) tasksManager.getTask(flyToUpdate);
    }

    @Override
    public FlyToUsingDTM flyToUsingDTM(Location location, Double az, double agl, double underGrapPercent, double upperGapPercent, Double maxVelocity, Double radiusReached) throws DroneTaskException {
        return null;
    }

    @Override
    public FlyInCircle flyInCircle(Location center, double radius, RotationType rotationType, double degreesToCover, double startingDegree, AltitudeInfo altitudeInfo, double velocity) throws DroneTaskException {

        UUID uuid = startTask(new FlyInCircleTaskRequest(center,radius,rotationType,degreesToCover,startingDegree,altitudeInfo,velocity));
        FlyInCircleTaskUpdate flyInCircleTaskUpdate = new FlyInCircleTaskUpdate(uuid,null,TaskStatus.NOT_STARTED,center,radius,rotationType,degreesToCover,startingDegree,altitudeInfo,velocity);
        return (FlyInCircle) tasksManager.getTask(flyInCircleTaskUpdate);
    }

    @Override
    public FlyToSafeAndFast flySafeAndFastTo(Location targetLocation, AltitudeInfo altitudeInfo, Double minDistanceFromGround) throws DroneTaskException {
        return null;
    }

    @Override
    public FollowNavPlan followNavPlan(List<NavPlanPoint> navPlanPointList) throws DroneTaskException {
        UUID uuid = startTask(new FollowNavPlanRequest(navPlanPointList));
        FollowNavPlanTaskUpdate navPlanUpdate = new FollowNavPlanTaskUpdate(uuid,null,TaskStatus.NOT_STARTED,navPlanPointList);
        return (FollowNavPlan) tasksManager.getTask(navPlanUpdate);
    }

    @Override
    public Hover hover(Integer hoverTime) throws DroneTaskException {
        return null;
    }

    @Override
    public void confirmLand() throws DroneTaskException {
        startTask(new ConfirmLandRequest());
    }
}
