package eyesatop.unit;

import java.util.UUID;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.landingpad.LandingPad;
import eyesatop.unit.exceptions.ComponentNotFoundException;
import eyesatop.unit.tasks.UnitTaskType;
import eyesatop.util.geo.ObstacleProvider;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableCollection;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;

public interface DroneUnit {

    ObservableBoolean isRCConnected();

    ObservableList<DroneController> controllers();

    void addController(DroneController controller);
    void removeController(DroneController controller);

    void setSelectedDrone(DroneController controller);

    ObservableValue<DroneTask<UnitTaskType>> currentTask(UUID uuid);

    DroneController getControllerByUUID(UUID uuid) throws ComponentNotFoundException;

    UnitFlightTasks flightTasks();
}
