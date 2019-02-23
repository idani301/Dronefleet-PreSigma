package eyesatop.controller.mock;

import eyesatop.controller.DroneHome;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.controller.tasks.home.SetHomeLocation;
import eyesatop.controller.tasks.home.SetLimitationEnabled;
import eyesatop.controller.tasks.home.SetMaxAltitudeFromHomeLocation;
import eyesatop.controller.tasks.home.SetMaxDistanceFromHome;
import eyesatop.controller.tasks.home.SetReturnHomeAltitude;
import eyesatop.util.geo.Location;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.Property;

public abstract class MockDroneHome implements DroneHome {

    public static class Stub extends MockDroneHome {

        @Override public SetReturnHomeAltitude setReturnHomeAltitude(double altitude) throws DroneTaskException {return null;}
        @Override public SetHomeLocation setHomeLocation(Location newHomeLocation) throws DroneTaskException {return null;}
        @Override public SetLimitationEnabled setFlightLimitationEnabled(boolean enabled) throws DroneTaskException {return null;}
        @Override public SetMaxDistanceFromHome setMaxDistanceFromHome(double maxDistanceFromHome) throws DroneTaskException {return null;}
        @Override public SetMaxAltitudeFromHomeLocation setMaxAltitudeFromTakeOffLocation(double maxAltitudeFromTakeOffLocation) throws DroneTaskException {return null;}
    }

    private final Property<DroneTask<HomeTaskType>> currentTask;
    private final ObservableList<HomeTaskBlockerType> taskBlockers;

    private final Property<Location> homeLocation;
    private final Property<Double> returnHomeAltitude;
    private final Property<Double> maxDistanceFromHome;
    private final Property<Double> maxAltitudeFromTakeOffLocation;
    private final BooleanProperty limitationActive;
    private final Property<Double> takeOffDTM;

    public MockDroneHome() {
        currentTask = new Property<>();
        taskBlockers = new ObservableList<>();
        homeLocation                   = new Property<>();
        returnHomeAltitude             = new Property<>();
        maxDistanceFromHome            = new Property<>();
        maxAltitudeFromTakeOffLocation = new Property<>();
        limitationActive = new BooleanProperty();
        takeOffDTM = new Property<>();
    }

    @Override public Property<DroneTask<HomeTaskType>> currentTask() {return currentTask;}
    @Override public ObservableList<HomeTaskBlockerType> taskBlockers() {return taskBlockers;}
    @Override public Property<Location> homeLocation() {return homeLocation;}
    @Override public Property<Double> returnHomeAltitude() {
        return returnHomeAltitude;
    }
    @Override public Property<Double> maxDistanceFromHome() {return maxDistanceFromHome;}
    @Override public Property<Double> maxAltitudeFromTakeOffLocation() {return maxAltitudeFromTakeOffLocation;}
    @Override public BooleanProperty limitationActive() {return limitationActive;}

    @Override
    public Property<Double> takeOffDTM() {
        return takeOffDTM;
    }

    @Override public abstract SetReturnHomeAltitude setReturnHomeAltitude(double altitude)                                            throws DroneTaskException;
    @Override public abstract SetHomeLocation setHomeLocation(Location newHomeLocation)                                               throws DroneTaskException;
    @Override public abstract SetLimitationEnabled setFlightLimitationEnabled(boolean enabled)                                        throws DroneTaskException;
    @Override public abstract SetMaxDistanceFromHome setMaxDistanceFromHome(double maxDistanceFromHome)                               throws DroneTaskException;
    @Override public abstract SetMaxAltitudeFromHomeLocation setMaxAltitudeFromTakeOffLocation(double maxAltitudeFromTakeOffLocation) throws DroneTaskException;
}
