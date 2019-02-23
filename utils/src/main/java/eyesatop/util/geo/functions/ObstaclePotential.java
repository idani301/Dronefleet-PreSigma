package eyesatop.util.geo.functions;

import java.util.UUID;

import eyesatop.math.Geometry.Point3D;
import eyesatop.util.geo.ObstacleObject;
import eyesatop.util.geo.Shape3D;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by Einav on 01/11/2017.
 */

public class ObstaclePotential extends ObstacleObject{

    private static final double MOTORS_ON_RADIUS = 1/2.5;
    private static final Shape3D MOTORS_ON_SHAPE = new LinearGoToPointShape3D(MOTORS_ON_RADIUS);
    private static final Shape3D MOTORS_OFF_SHAPE = new Shape3D() {
        @Override
        public Point3D influence(Telemetry myCenter, Telemetry location) {
            return Point3D.zero();
        }
    };


    private final ObservableValue<Boolean> motorsOn;
    private final Property<Shape3D> currentShape = new Property<Shape3D>();

    public ObstaclePotential(UUID obstacleUUID, ObservableValue<Telemetry> telemetry, ObservableValue<Boolean> motorsOn) {
        super(obstacleUUID, telemetry);
        this.motorsOn = motorsOn;

        motorsOn.observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {

                if(newValue == null){
                    currentShape.set(MOTORS_ON_SHAPE);
                }
                else{
                    currentShape.set(newValue ? MOTORS_ON_SHAPE : MOTORS_OFF_SHAPE);
                }
            }
        }).observeCurrentValue();
    }

    @Override
    public Point3D getInfluence(Telemetry myTelemetry) {
        return currentShape.value().influence(myTelemetry,getTelemetry().value());
    }
}