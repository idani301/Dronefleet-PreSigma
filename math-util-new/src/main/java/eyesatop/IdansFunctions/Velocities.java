package eyesatop.IdansFunctions;

import eyesatop.math.Geometry.Point3D;

/**
 * Created by Einav on 15/05/2017.
 */

public class Velocities {

    private final Point3D velocity;

    public Velocities(Point3D velocity) {
        this.velocity = velocity;
    }

    public Point3D getVelocity() {
        return velocity;
    }
}
