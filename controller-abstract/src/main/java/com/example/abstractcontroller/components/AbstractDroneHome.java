package com.example.abstractcontroller.components;

import com.example.abstractcontroller.AbstractDroneController;
import com.example.abstractcontroller.taskmanager.GenericTaskManager;
import com.example.abstractcontroller.taskmanager.HomeTaskManager;

import eyesatop.controller.DroneController;
import eyesatop.controller.DroneHome;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.TaskCategory;
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
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 28/08/2017.
 */

public abstract class AbstractDroneHome extends GeneralDroneComponent<HomeTaskType,HomeTaskBlockerType> implements DroneHome {

    private final Property<Location> homeLocation;
    private final Property<Double> returnHomeAltitude;
    private final Property<Double> maxDistanceFromHome;
    private final Property<Double> maxAltitudeFromTakeOffLocation;
    private final BooleanProperty limitationActive;
    private final Property<Double> takeOffDTM;

    public AbstractDroneHome() {

        super(new HomeTaskManager());

        homeLocation                   = new Property<>();
        returnHomeAltitude             = new Property<>();
        maxDistanceFromHome            = new Property<>();
        maxAltitudeFromTakeOffLocation = new Property<>();
        takeOffDTM                     = new Property<>();
        limitationActive = new BooleanProperty();;
    }

    @Override
    public Property<Double> takeOffDTM() {
        return takeOffDTM;
    }

    @Override
    public ObservableValue<DroneTask<HomeTaskType>> currentTask() {
        return taskManager.currentTask();
    }

    @Override
    public ObservableList<HomeTaskBlockerType> taskBlockers() {
        return taskManager.getTasksBlockers();
    }

    @Override
    public Property<Location> homeLocation() {
        return homeLocation;
    }

    @Override
    public Property<Double> returnHomeAltitude() {
        return returnHomeAltitude;
    }

    @Override
    public Property<Double> maxDistanceFromHome() {
        return maxDistanceFromHome;
    }

    @Override
    public Property<Double> maxAltitudeFromTakeOffLocation() {
        return maxAltitudeFromTakeOffLocation;
    }

    @Override
    public BooleanProperty limitationActive() {
        return limitationActive;
    }

    @Override
    public SetReturnHomeAltitude setReturnHomeAltitude(final double altitude) throws DroneTaskException {

        SetReturnHomeAltitude.StubSetReturnHomeAltitude stubTask = new SetReturnHomeAltitude.StubSetReturnHomeAltitude() {
            @Override
            public double altitude() {
                return altitude;
            }
        };
        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public SetHomeLocation setHomeLocation(final Location newHomeLocation) throws DroneTaskException {

        SetHomeLocation.StubSetHomeLocation stubTask = new SetHomeLocation.StubSetHomeLocation() {
            @Override
            public Location location() {
                return newHomeLocation;
            }
        };

        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public SetLimitationEnabled setFlightLimitationEnabled(final boolean enabled) throws DroneTaskException {

        SetLimitationEnabled.StubSetLimitationEnabled stubTask = new SetLimitationEnabled.StubSetLimitationEnabled() {
            @Override
            public boolean enabled() {
                return enabled;
            }
        };

        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public SetMaxDistanceFromHome setMaxDistanceFromHome(final double maxDistanceFromHome) throws DroneTaskException {
        SetMaxDistanceFromHome.StubSetMaxDistanceFromHome stubTask = new SetMaxDistanceFromHome.StubSetMaxDistanceFromHome() {
            @Override
            public double maxDistanceFromHome() {
                return maxDistanceFromHome;
            }
        };

        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public SetMaxAltitudeFromHomeLocation setMaxAltitudeFromTakeOffLocation(final double maxAltitudeFromTakeOffLocation) throws DroneTaskException {
        SetMaxAltitudeFromHomeLocation.StubSetMaxAltitudeFromHomeLocation stubTask = new SetMaxAltitudeFromHomeLocation.StubSetMaxAltitudeFromHomeLocation() {
            @Override
            public double altitude() {
                return maxAltitudeFromTakeOffLocation;
            }
        };

        startStubTask(stubTask,false);
        return stubTask;
    }

    @Override
    public void clearData() {
        homeLocation.set(null);
        returnHomeAltitude.set(null);
        maxDistanceFromHome.set(null);
        maxAltitudeFromTakeOffLocation.set(null);
        limitationActive.set(null);
    }
}
