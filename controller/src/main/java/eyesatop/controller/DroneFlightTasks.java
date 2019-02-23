package eyesatop.controller;

import java.util.List;

import eyesatop.controller.beans.AltitudeInfo;
import eyesatop.controller.beans.NavPlanPoint;
import eyesatop.controller.beans.RotationType;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyInCircle;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.controller.tasks.flight.FlyTo;
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
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;

public interface DroneFlightTasks {

    ObservableValue<DroneTask<FlightTaskType>> current();
    ObservableList<FlightTaskBlockerType> tasksBlockers();

    TakeOff takeOff(double altitude)             throws DroneTaskException;
    LandAtLandingPad land(LandingPad landingPad) throws DroneTaskException;
    LandAtLocation land(Location location)       throws DroneTaskException;
    LandInPlace land()                           throws DroneTaskException;
    GoHome goHome()                              throws DroneTaskException;

    RotateHeading rotateHeading(double angle) throws DroneTaskException;

    FlyToSafeAndFast flySafeAndFastTo(Location targetLocation, AltitudeInfo altitudeInfo, Double minDistanceFromGround) throws DroneTaskException;
    FlyTo flyTo(Location location, AltitudeInfo altitudeInfo,Double az,Double maxVelocity,Double radiusReached) throws DroneTaskException;
    FlyToUsingDTM flyToUsingDTM(Location location,
                                Double az,
                                double agl,
                                double underGrapPercent,
                                double upperGapPercent,
                                Double maxVelocity,Double radiusReached) throws DroneTaskException;


    FlyInCircle flyInCircle(Location center,
                            double radius,
                            RotationType rotationType,
                            double degreesToCover,
                            double startingDegree,
                            AltitudeInfo altitudeInfo,
                            double velocity) throws DroneTaskException;

    FollowNavPlan followNavPlan(List<NavPlanPoint> navPlanPoints) throws DroneTaskException;

    Hover hover(Integer hoverTime) throws DroneTaskException;

    ObservableBoolean confirmLandRequire();
    void confirmLand() throws DroneTaskException;
}

