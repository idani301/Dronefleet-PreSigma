package eyesatop.controller;


import eyesatop.controller.beans.FlightLimitations;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import eyesatop.controller.tasks.home.SetLimitationEnabled;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;

public interface DroneHome {

    ObservableValue<DroneTask<HomeTaskType>> currentTask();

    ObservableList<HomeTaskBlockerType> taskBlockers();

    ObservableValue<Double> takeOffDTM();
    ObservableValue<Location> homeLocation();
    ObservableValue<Double> returnHomeAltitude();
    ObservableValue<Double> maxDistanceFromHome();
    ObservableValue<Double> maxAltitudeFromTakeOffLocation();
    ObservableBoolean limitationActive();

    SetReturnHomeAltitude setReturnHomeAltitude(double altitude)                                               throws DroneTaskException;
    SetHomeLocation setHomeLocation(Location newHomeLocation)                                                  throws DroneTaskException;
    SetLimitationEnabled setFlightLimitationEnabled(boolean enabled)                                           throws DroneTaskException;
    SetMaxDistanceFromHome setMaxDistanceFromHome(double maxDistanceFromHome)                                  throws  DroneTaskException;
    SetMaxAltitudeFromHomeLocation setMaxAltitudeFromTakeOffLocation(double maxAltitudeFromTakeOffLocation)    throws DroneTaskException;
}
