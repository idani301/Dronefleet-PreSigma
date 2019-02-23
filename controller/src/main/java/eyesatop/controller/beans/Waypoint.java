package eyesatop.controller.beans;

import eyesatop.util.geo.Location;

public class Waypoint {
    private final Location location;
    private final GimbalInstruction gimbalInstruction;


    public Waypoint(Location location, GimbalInstruction gimbalInstruction) {
        this.location = location;
        this.gimbalInstruction = gimbalInstruction;
    }

    public Location getLocation() {
        return location;
    }

    public GimbalInstruction getGimbalInstruction() {
        return gimbalInstruction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Waypoint waypoint = (Waypoint) o;

        return location != null ? location.equals(waypoint.location) : waypoint.location == null;
    }

    @Override
    public int hashCode() {
        return location != null ? location.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Waypoint{" +
                "location=" + location +
                '}';
    }
}
