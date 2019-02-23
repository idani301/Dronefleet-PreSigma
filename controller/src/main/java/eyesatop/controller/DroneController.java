package eyesatop.controller;

import java.io.Serializable;
import java.util.UUID;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.RCFlightModeSwitchPosition;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.beans.SticksPosition;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableValue;

public interface DroneController extends Serializable{

    UUID uuid();

    ObservableValue<Double> aboveGroundAltitude();

    ObservableValue<Double> aboveSeaAltitude();

    ObservableValue<Location> lookAtLocation();

    ObservableValue<DroneModel> model();

    ObservableValue<DroneConnectivity> connectivity();

    ObservableValue<Telemetry> telemetry();

    ObservableValue<BatteryState> droneBattery();

    ObservableValue<Boolean> flying();

    ObservableValue<Boolean> motorsOn();

    ObservableValue<BatteryState> rcBattery();

    ObservableValue<GpsState> gps();

//    ObservableValue<Location> rtk3DLocation();
//    ObservableValue<Location> rtkBaseStationLocation();

    DroneHome droneHome();

    DroneCamera camera();

    DroneFlightTasks flightTasks();

    DroneGimbal gimbal();

    ObservableValue<Location> rcLocation();

    ObservableValue<Location> lastKnownLocation();

    ObservableValue<RCFlightModeSwitchPosition> rcFlightModeSwitchPosition();

    ObservableBoolean rcInFunctionMode();

    ObservableValue<FlightMode> flightMode();

    ObservableValue<Integer> rcSignalStrengthPercent();

    ObservableValue<SticksPosition> virtualSticksPosition();
    void setVirtualSticksPosition(SticksPosition position);

    void close();
}
