package eyesatop.controller.tasks.takeoff;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.stabs.StubDroneTask;

public interface TakeOff extends DroneTask<FlightTaskType> {
    double altitude();

    public class TakeOffStub extends StubDroneTask<FlightTaskType> implements TakeOff {

        private static final String ALTITUDE = "altitude";

        private final double altitude;

        @JsonCreator
        public TakeOffStub(@JsonProperty(ALTITUDE) double altitude) {
            this.altitude = altitude;
        }

        @Override
        public FlightTaskType taskType() {
            return FlightTaskType.TAKE_OFF;
        }

        @Override
        public double altitude() {
            return altitude;
        }

        @JsonProperty(ALTITUDE)
        public double getAltitude() {
            return altitude;
        }
    }
}
