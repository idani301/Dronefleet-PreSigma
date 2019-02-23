package eyesatop.unit.ui.missions;

import eyesatop.controller.beans.RotationType;
import eyesatop.util.geo.Location;

/**
 * Created by Idan on 21/12/2017.
 */
public class CircleFlightPlanSnapshot {

    private final String name;

    private final Double radius;
    private final Location centerLocation;
    private final Boolean visibility;

    private final Double agl;
    private final Double velocity;

    private final RotationType rotationType;

    private final Double gotoCircleAGL;

    private final Double gimbalPitch;
    private final Boolean lookToMid;

    CircleFlightPlanSnapshot(
            String name,
            Double radius,
            Location centerLocation,
            Boolean visibility,
            Double agl,
            Double velocity,
            RotationType rotationType,
            Double gotoCircleAGL,
            Double gimbalPitch,
            Boolean lookToMid) {
        this.name = name;
        this.radius = radius;
        this.centerLocation = centerLocation;
        this.visibility = visibility;
        this.agl = agl;
        this.velocity = velocity;
        this.rotationType = rotationType;
        this.gotoCircleAGL = gotoCircleAGL;
        this.gimbalPitch = gimbalPitch;
        this.lookToMid = lookToMid;
    }

    public Double getRadius() {
        return radius;
    }

    public Location getCenterLocation() {
        return centerLocation;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public Double getAgl() {
        return agl;
    }

    public Double getVelocity() {
        return velocity;
    }

    public RotationType getRotationType() {
        return rotationType;
    }

    public Double getGotoCircleAGL() {
        return gotoCircleAGL;
    }

    public Double getGimbalPitch() {
        return gimbalPitch;
    }

    public Boolean getLookToMid() {
        return lookToMid;
    }

    public String getName() {
        return name;
    }
}
