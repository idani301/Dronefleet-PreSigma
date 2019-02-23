package eyesatop.util.geo;

import java.util.UUID;

import eyesatop.math.Geometry.Point3D;
import eyesatop.util.model.ObservableValue;

/**
 * Created by Idan on 17/10/2017.
 */

public abstract class ObstacleObject {

    private final UUID obstacleUUID;
    private final ObservableValue<Telemetry> telemetry;

    public ObstacleObject(UUID obstacleUUID,ObservableValue<Telemetry> telemetry) {
        this.obstacleUUID = obstacleUUID;
        this.telemetry = telemetry;
    }

    public ObservableValue<Telemetry> getTelemetry() {
        return telemetry;
    }

    public UUID getObstacleUUID() {
        return obstacleUUID;
    }

    public abstract Point3D getInfluence(Telemetry myTelemetry);
}
