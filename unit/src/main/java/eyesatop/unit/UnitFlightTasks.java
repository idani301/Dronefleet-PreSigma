package eyesatop.unit;

import java.util.HashMap;
import java.util.UUID;

import eyesatop.controller.DroneController;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.camera.CameraTaskType;
import eyesatop.controller.tasks.exceptions.DroneTaskException;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.stabs.StubDroneTask;
import eyesatop.imageprocess.DetectionData;
import eyesatop.unit.tasks.AnytargetAction;
import eyesatop.unit.tasks.Swap;
import eyesatop.unit.exceptions.UnitFlightTaskException;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;
import eyesatop.util.videoclicks.VideoClickInfo;

/**
 * Created by einav on 13/07/2017.
 */

public interface UnitFlightTasks {

    ObservableList<UUID> busyList();

    Swap swap(DroneController controller) throws DroneTaskException;

    AnytargetAction anytargetAction(final DroneController holderController,
                                    final HashMap<DroneController,Property<VideoClickInfo>> videoClickedLocations,
                                    ObservableValue<DetectionData> faceRecognitionDetections) throws DroneTaskException;
}
