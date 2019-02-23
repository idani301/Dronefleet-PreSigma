package eyesatop.controller.mock;

import java.util.UUID;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.beans.RCFlightModeSwitchPosition;
import eyesatop.controller.beans.SticksPosition;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Property;

public abstract class MockController implements DroneController {

    public static class Stub extends MockController {

        public static Stub STUB = new Stub();

        public Stub() {
            this(UUID.randomUUID());
        }

        public Stub(UUID uuid) {
            super(uuid);
        }

        @Override
        protected MockDroneCamera createDroneCamera() {
            return new MockDroneCamera.Stub();
        }

        @Override
        protected MockDroneGimbal createDroneGimbal() {
            return new MockDroneGimbal.Stub();
        }

        @Override
        protected MockDroneFlightTasks createDroneFlightTasks() {
            return new MockDroneFlightTasks.Stub();
        }

        @Override
        protected MockDroneHome createDroneHome() {
            return new MockDroneHome.Stub();
        }

        @Override
        public void setVirtualSticksPosition(SticksPosition position) {

        }

        @Override public void close() {}
    }
    
    private final UUID uuid;
    
    private final Property<DroneModel> model;
    private final Property<DroneConnectivity> connectivity;
    private final Property<Telemetry> telemetry;
    private final Property<BatteryState> droneBattery;
    private final Property<BatteryState> rcBattery;
    private final BooleanProperty flying;
    private final BooleanProperty motorsOn;
    private final Property<GpsState> gps;
    private final Property<Location> rcLocation;
    private final Property<Location> lastKnownLocation;
    private final Property<RCFlightModeSwitchPosition> rcFlightModeSwitchPosition;
    private final BooleanProperty rcInFunctionMode;
    private final Property<FlightMode> flightMode;
    private final Property<Integer> rcSignalStrengthPercent;
    private final Property<Location> lookAtLocation;
    private final Property<Double> aboveGroundAltitude = new Property<>();
    private final Property<Double> aboveSeaAltitude = new Property<>();
    private final Property<SticksPosition> virtualSticksPosition = new Property<>();

//    private final Property<Location> rtk3DLocation = new Property<>();
//    private final Property<Location> rtkBaseStationLocation = new Property<>();

    private final MockDroneHome droneHome;
    private final MockDroneCamera droneCamera;
    private final MockDroneFlightTasks flightTasks;
    private final MockDroneGimbal gimbal;

    public MockController(
            UUID uuid) {
        this.uuid = uuid;
        this.droneHome = createDroneHome();
        this.droneCamera = createDroneCamera();
        this.flightTasks = createDroneFlightTasks();
        this.gimbal = createDroneGimbal();
        model = new Property<>();
        connectivity = new Property<>();
        telemetry = new Property<>();
        droneBattery = new Property<>();
        rcBattery = new Property<>();
        flying = new BooleanProperty();
        motorsOn = new BooleanProperty();
        gps = new Property<>();
        rcLocation = new Property<>();
        lastKnownLocation = new Property<>();
        rcFlightModeSwitchPosition = new Property<>();
        rcInFunctionMode = new BooleanProperty();
        flightMode = new Property<>();
        rcSignalStrengthPercent = new Property<>();
        lookAtLocation = new Property<>();
    }

    protected abstract MockDroneCamera createDroneCamera();
    protected abstract MockDroneGimbal createDroneGimbal();
    protected abstract MockDroneFlightTasks createDroneFlightTasks();
    protected abstract MockDroneHome createDroneHome();


    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override public Property<DroneModel> model() {return model;}
    @Override public Property<DroneConnectivity> connectivity() {return connectivity;}
    @Override public Property<Telemetry> telemetry() {return telemetry;}
    @Override public Property<BatteryState> droneBattery() {return droneBattery;}
    @Override public BooleanProperty flying() {return flying;}
    @Override public BooleanProperty motorsOn() {return motorsOn;}
    @Override public Property<BatteryState> rcBattery() {return rcBattery;}
    @Override public Property<GpsState> gps() {return gps;}
    @Override public Property<Location> rcLocation() {return rcLocation;}
    @Override public Property<Location> lastKnownLocation() {return lastKnownLocation;}
    @Override public Property<RCFlightModeSwitchPosition> rcFlightModeSwitchPosition() {return rcFlightModeSwitchPosition;}
    @Override public BooleanProperty rcInFunctionMode() {return rcInFunctionMode;}
    @Override public Property<FlightMode> flightMode() {return flightMode;}
    @Override public Property<Integer> rcSignalStrengthPercent() {return rcSignalStrengthPercent;}
    @Override public MockDroneHome droneHome() {return droneHome;}
    @Override public MockDroneCamera camera() {return droneCamera;}
    @Override public MockDroneFlightTasks flightTasks() {return flightTasks;}
    @Override public MockDroneGimbal gimbal() {return gimbal;}

//    @Override
//    public ObservableValue<Location> rtk3DLocation() {
//        return rtk3DLocation;
//    }
//
//    @Override
//    public ObservableValue<Location> rtkBaseStationLocation() {
//        return rtkBaseStationLocation;
//    }


    @Override
    public Property<SticksPosition> virtualSticksPosition() {
        return virtualSticksPosition;
    }

    @Override
    public Property<Double> aboveGroundAltitude() {
        return aboveGroundAltitude;
    }

    @Override
    public Property<Double> aboveSeaAltitude() {
        return aboveSeaAltitude;
    }

    @Override
    public Property<Location> lookAtLocation() {
        return lookAtLocation;
    }

    @Override public abstract void close();
}
