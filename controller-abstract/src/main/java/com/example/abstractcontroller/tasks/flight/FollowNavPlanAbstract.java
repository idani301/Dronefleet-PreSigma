package com.example.abstractcontroller.tasks.flight;

import com.example.abstractcontroller.AbstractDroneController;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.tasks.RunnableDroneTask;
import eyesatop.controller.tasks.TaskProgressState;
import eyesatop.controller.tasks.TaskStatus;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FollowNavPlan;
import eyesatop.util.Removable;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

public class FollowNavPlanAbstract extends RunnableDroneTask<FlightTaskType> implements FollowNavPlan {

    private final AbstractDroneController controller;
    private final List<NavPlanPoint> navPlanPoints;
    private Integer currentIndex = 0;
    Removable telemeteryObserver = Removable.STUB;

    public FollowNavPlanAbstract(AbstractDroneController controller, List<NavPlanPoint> navPlanPoints) {
        this.controller = controller;
        this.navPlanPoints = navPlanPoints;
    }

    @Override
    protected void perform(Property<TaskProgressState> state) throws DroneTaskException, InterruptedException {

        while(currentIndex != -1){

            NavPlanPoint currentNavPlanPoint = navPlanPoints.get(currentIndex);
            final Location location = currentNavPlanPoint.getLocation();
            final double radiusReached = currentNavPlanPoint.getRadiusReached();
            final AltitudeInfo altitudeInfo = currentNavPlanPoint.getAltitudeInfo();

            final CountDownLatch taskLatch = new CountDownLatch(1);

            telemeteryObserver = controller.telemetry().observe(new Observer<Telemetry>() {
                @Override
                public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {

                    if(newValue == null || newValue.location() == null){
                        return;
                    }

                    Location currentLocation = newValue.location();

                    if (location.distance(currentLocation) < radiusReached) {

                        double altDistance = 0;
                        if(!altitudeInfo.isNull()){
                            switch (altitudeInfo.getAltitudeType()){

                                case ABOVE_GROUND_LEVEL:
                                    Double currentAboveGroundLevel = controller.aboveGroundAltitude().value();
                                    if(currentAboveGroundLevel != null) {
                                        altDistance = Math.abs(currentAboveGroundLevel - altitudeInfo.getValueInMeters());
                                    }
                                    break;
                                case ABOVE_SEA_LEVEL:
                                    Double currentAboveSeaLevel = controller.aboveSeaAltitude().value();
                                    if(currentAboveSeaLevel != null) {
                                        altDistance = Math.abs(currentAboveSeaLevel - altitudeInfo.getValueInMeters());
                                    }
                                    break;
                                case FROM_TAKE_OFF_LOCATION:
                                    altDistance = Math.abs(currentLocation.getAltitude() - altitudeInfo.getValueInMeters());
                                    break;
                            }
                        }

                        if(altDistance < 1) {
                            telemeteryObserver.remove();
                            telemeteryObserver = Removable.STUB;
                            taskLatch.countDown();
                        }
                    }
                }
            }).observeCurrentValue();

            taskLatch.await();
            currentIndex = currentIndex == (navPlanPoints.size() -1) ? -1 : (currentIndex + 1);
        }
    }

    public int currentIndex(){
        return currentIndex;
    }

    @Override
    protected void cleanUp(TaskStatus exitStatus) throws Exception {
        try {
            telemeteryObserver.remove();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<NavPlanPoint> navPlanPoints() {
        return navPlanPoints;
    }

    @Override
    public FlightTaskType taskType() {
        return FlightTaskType.FOLLOW_NAV_PLAN;
    }
}
