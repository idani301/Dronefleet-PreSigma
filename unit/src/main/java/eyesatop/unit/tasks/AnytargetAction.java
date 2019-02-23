package eyesatop.unit.tasks;

import java.util.UUID;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableValue;

public interface AnytargetAction extends DroneTask<UnitTaskType>{

    DroneController holderController();
    DroneController explorerController();

    ObservableValue<Location> holderBodyDetections();
    ObservableValue<Location> explorerBodyDetections();
    ObservableBoolean explorerFaceRecognition();
}
