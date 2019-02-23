package eyesatop.controller.beans;

import eyesatop.util.geo.Location;

/**
 * Created by einav on 24/01/2017.
 */

public class GimbalInstruction {
    private final Location point;
    private final GimbalStrategy strategy;

    public GimbalInstruction(Location point, GimbalStrategy strategy) {
        this.point = point;
        this.strategy = strategy;
    }
}
