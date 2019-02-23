package eyesatop.util.geo;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by Idan on 17/10/2017.
 */

public interface Shape3D {
    Point3D influence(Telemetry myCenter, Telemetry location);
}
