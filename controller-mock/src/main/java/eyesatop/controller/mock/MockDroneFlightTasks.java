package eyesatop.controller.mock;

import java.util.List;

import eyesatop.controller.DroneFlightTasks;
import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
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
import eyesatop.landingpad.LandingPad;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.Property;

public abstract class MockDroneFlightTasks implements DroneFlightTasks {

    public static class Stub extends MockDroneFlightTasks {
        @Override public TakeOff takeOff(double altitude) throws DroneTaskException {return null;}
        @Override public LandAtLandingPad land(LandingPad landingPad) throws DroneTaskException {return null;}
        @Override public LandAtLocation land(Location location) throws DroneTaskException {return null;}
        @Override public LandInPlace land() throws DroneTaskException {return null;}
        @Override public GoHome goHome() throws DroneTaskException {return null;}

        @Override
        public FlyTo flyTo(Location location, AltitudeInfo altitudeInfo, Double az, Double maxVelocity,Double radiusReached) throws DroneTaskException {
            return null;
        }

        @Override
        public FlyToSafeAndFast flySafeAndFastTo(Location targetLocation, AltitudeInfo altitudeInfo, Double minDistanceFromGround) throws DroneTaskException {
            return null;
        }

        @Override
        public FollowNavPlan followNavPlan(List<NavPlanPoint> navPlanPointList) throws DroneTaskException {
            return null;
        }

        @Override
        public Hover hover(Integer hoverTime) throws DroneTaskException {
            return null;
        }

        @Override
        public void confirmLand() throws DroneTaskException {

        }

        @Override
        public RotateHeading rotateHeading(double angle) throws DroneTaskException {
            return null;
        }

        @Override
        public FlyToUsingDTM flyToUsingDTM(Location location, Double az, double agl, double underGrapPercent, double upperGapPercent, Double maxVelocity,Double radiusReached) throws DroneTaskException {
            return null;
        }

        @Override
        public FlyInCircle flyInCircle(
                Location center,
                double radius,
                RotationType rotationType,
                double degreesToCover,
                double startingDegree,
                AltitudeInfo altitudeInfo,
                double velocity) throws DroneTaskException {
            return null;
        }
    }

    private final Property<DroneTask<FlightTaskType>> current;
    private final ObservableList<FlightTaskBlockerType> tasksBlockers;
    private final BooleanProperty confirmLandRequire = new BooleanProperty();

    protected MockDroneFlightTasks() {
        current = new Property<>();
        tasksBlockers = new ObservableList<>();
    }

    @Override public Property<DroneTask<FlightTaskType>> current() {return current;}
    @Override public ObservableList<FlightTaskBlockerType> tasksBlockers() {return tasksBlockers;}

    @Override public abstract TakeOff takeOff(double altitude) throws DroneTaskException;
    @Override public abstract LandAtLandingPad land(LandingPad landingPad) throws DroneTaskException;
    @Override public abstract LandAtLocation land(Location location) throws DroneTaskException;
    @Override public abstract LandInPlace land() throws DroneTaskException;
    @Override public abstract GoHome goHome() throws DroneTaskException;
    @Override public abstract FlyTo flyTo(Location location, AltitudeInfo altitudeInfo,Double az,Double maxVelocity,Double radiusReached) throws DroneTaskException;

    @Override
    public abstract FlyToSafeAndFast flySafeAndFastTo(Location targetLocation, AltitudeInfo altitudeInfo, Double minDistanceFromGround) throws DroneTaskException;

    public abstract FollowNavPlan followNavPlan(List<NavPlanPoint> navPlanPointList) throws DroneTaskException;

    @Override
    public BooleanProperty confirmLandRequire() {
        return confirmLandRequire;
    }

    @Override
    public abstract Hover hover(Integer hoverTime) throws DroneTaskException;
}
