package eyesatop.controller.mission.flightplans;

/**
 * Created by Idan on 08/04/2018.
 */

public enum FlightPlanComponentType {
    CIRCLE("Circle"),
    WAYPOINTS("Waypoints"),
    RADIATOR("Radiator");

    private final String name;

    FlightPlanComponentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
