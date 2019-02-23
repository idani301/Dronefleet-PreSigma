package eyesatop.controller_tcpip.local;

import java.util.concurrent.BlockingQueue;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.beans.StreamState;
import eyesatop.controller_tcpip.common.telemetryupdate.StreamStateUpdate;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;
import eyesatop.controller_tcpip.common.ControllerTcpIPCommon;
import eyesatop.controller_tcpip.common.telemetryupdate.AboveGroundAltitudeUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.AboveSeaAltitudeUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.BatteryStateUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.CameraBlockersUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.CameraModeUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.ConfirmLandRequireUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.ConnectivityUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.ControllerUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.DroneModelUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.FlightBlockersUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.FlightModeUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.GimbalBlockerUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.GimbalStateUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.GpsStateUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.HomeBlockersUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.HomeLocationUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.IsFlyingUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.IsRecordingUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.IsShootingPhotoUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.IsZoomSupportedUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.LimitationActiveUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.LookAtLocationUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.MaxAltitudeUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.MaxDistanceFromHomeUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.MediaStorageUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.MotorsOnUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.RcBatteryUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.RcInFunctionModeUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.RcSignalStrengthUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.RecordTimeInSecUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.ReturnHomeAltitudeUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.ShootPhotoIntervalValueUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.TakeOffDTMUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.TelemetryUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.ZoomInfoUpdate;
import eyesatop.controller_tcpip.common.telemetryupdate.ZoomLevelUpdate;
import eyesatop.util.RemovableCollection;
import eyesatop.util.connections.tcp.oneway.OneWayStreamTCPClient;
import eyesatop.util.connections.tcp.oneway.OneWayStreamCallback;
import eyesatop.util.connections.tcp.oneway.OneWayStreamConnectionInfo;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public class TCPTelemetryClientManager {

    private final OneWayStreamTCPClient<ControllerUpdate> telemetryClient = new OneWayStreamTCPClient<>();
    private final DroneController controller;
    private final int port;
    private final ObservableValue<String> remoteIP;
    private final RemovableCollection telemetryBindings = new RemovableCollection();

    public TCPTelemetryClientManager(int droneID,DroneController controller, ObservableValue<String> remoteIP) {
        this.controller = controller;
        this.remoteIP = remoteIP;
        this.port = ControllerTcpIPCommon.getTelemetryPortMap(droneID);

        remoteIP.observe(new Observer<String>() {
            @Override
            public void observe(String oldValue, String newValue, Observation<String> observation) {
                telemetryClient.connect(newValue == null ? null : new OneWayStreamConnectionInfo<ControllerUpdate>(
                        newValue, port, new OneWayStreamCallback<ControllerUpdate>() {
                    @Override
                    public void onConnectionActive(BlockingQueue<ControllerUpdate> messagesToSend) {
                        doBindings(messagesToSend);
                    }

                    @Override
                    public void onConnectionLost(BlockingQueue<ControllerUpdate> messagesToSend) {
                        telemetryBindings.remove();
                        messagesToSend.clear();
                    }
                }
                ));
            }
        });
    }

    private void doBindings(final BlockingQueue<ControllerUpdate> updates){

        telemetryBindings.remove();

        updates.add(new FlightBlockersUpdate(controller.flightTasks().tasksBlockers()));
        updates.add(new GimbalBlockerUpdate(controller.gimbal().tasksBlockers()));
        updates.add(new CameraBlockersUpdate(controller.camera().tasksBlockers()));
        updates.add(new HomeBlockersUpdate(controller.droneHome().taskBlockers()));

        telemetryBindings.add(
                controller.flightTasks().tasksBlockers().observe(new CollectionObserver<FlightTaskBlockerType>(){
                    @Override
                    public void added(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
                        updates.add(new FlightBlockersUpdate(controller.flightTasks().tasksBlockers()));
                    }

                    @Override
                    public void removed(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
                        updates.add(new FlightBlockersUpdate(controller.flightTasks().tasksBlockers()));
                    }
                })
        );

        telemetryBindings.add(
                controller.camera().tasksBlockers().observe(new CollectionObserver<CameraTaskBlockerType>(){
                    @Override
                    public void added(CameraTaskBlockerType value, Observation<CameraTaskBlockerType> observation) {
                        updates.add(new CameraBlockersUpdate(controller.camera().tasksBlockers()));
                    }

                    @Override
                    public void removed(CameraTaskBlockerType value, Observation<CameraTaskBlockerType> observation) {
                        updates.add(new CameraBlockersUpdate(controller.camera().tasksBlockers()));
                    }
                })
        );

        telemetryBindings.add(
                controller.gimbal().tasksBlockers().observe(new CollectionObserver<GimbalTaskBlockerType>(){
                    @Override
                    public void added(GimbalTaskBlockerType value, Observation<GimbalTaskBlockerType> observation) {
                        updates.add(new GimbalBlockerUpdate(controller.gimbal().tasksBlockers()));
                    }

                    @Override
                    public void removed(GimbalTaskBlockerType value, Observation<GimbalTaskBlockerType> observation) {
                        updates.add(new GimbalBlockerUpdate(controller.gimbal().tasksBlockers()));
                    }
                })
        );

        telemetryBindings.add(
                controller.droneHome().taskBlockers().observe(new CollectionObserver<HomeTaskBlockerType>(){
                    @Override
                    public void added(HomeTaskBlockerType value, Observation<HomeTaskBlockerType> observation) {
                        updates.add(new HomeBlockersUpdate(controller.droneHome().taskBlockers()));
                    }

                    @Override
                    public void removed(HomeTaskBlockerType value, Observation<HomeTaskBlockerType> observation) {
                        updates.add(new HomeBlockersUpdate(controller.droneHome().taskBlockers()));
                    }
                })
        );

        telemetryBindings.add(
                controller.telemetry().observe(new Observer<Telemetry>() {
                    @Override
                    public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                        updates.add(new TelemetryUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.telemetry().observe(new Observer<Telemetry>() {
                    @Override
                    public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                        updates.add(new GimbalStateUpdate(controller.gimbal().gimbalState().value()));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.lookAtLocation().observe(new Observer<Location>() {
                    @Override
                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                        updates.add(new LookAtLocationUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.aboveGroundAltitude().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        updates.add(new AboveGroundAltitudeUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.aboveSeaAltitude().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        updates.add(new AboveSeaAltitudeUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.rcSignalStrengthPercent().observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                        updates.add(new RcSignalStrengthUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.telemetry().observe(new Observer<Telemetry>() {
                    @Override
                    public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                        updates.add(new MediaStorageUpdate(controller.camera().mediaStorage().value()));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.model().observe(new Observer<DroneModel>() {
                    @Override
                    public void observe(DroneModel oldValue, DroneModel newValue, Observation<DroneModel> observation) {
                        updates.add(new DroneModelUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.droneBattery().observe(new Observer<BatteryState>() {
                    @Override
                    public void observe(BatteryState oldValue, BatteryState newValue, Observation<BatteryState> observation) {
                        updates.add(new BatteryStateUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().mode().observe(new Observer<CameraMode>() {
                    @Override
                    public void observe(CameraMode oldValue, CameraMode newValue, Observation<CameraMode> observation) {
                        updates.add(new CameraModeUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.connectivity().observe(new Observer<DroneConnectivity>() {
                    @Override
                    public void observe(DroneConnectivity oldValue, DroneConnectivity newValue, Observation<DroneConnectivity> observation) {
                        updates.add(new ConnectivityUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.flightMode().observe(new Observer<FlightMode>() {
                    @Override
                    public void observe(FlightMode oldValue, FlightMode newValue, Observation<FlightMode> observation) {
                        updates.add(new FlightModeUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.gps().observe(new Observer<GpsState>() {
                    @Override
                    public void observe(GpsState oldValue, GpsState newValue, Observation<GpsState> observation) {
                        updates.add(new GpsStateUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.droneHome().homeLocation().observe(new Observer<Location>() {
                    @Override
                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                        updates.add(new HomeLocationUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.flying().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updates.add(new IsFlyingUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().recording().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updates.add(new IsRecordingUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().isShootingPhoto().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updates.add(new IsShootingPhotoUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().isZoomSupported().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updates.add(new IsZoomSupportedUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.droneHome().limitationActive().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updates.add(new LimitationActiveUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.droneHome().maxAltitudeFromTakeOffLocation().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        updates.add(new MaxAltitudeUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.droneHome().maxDistanceFromHome().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        updates.add(new MaxDistanceFromHomeUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.motorsOn().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updates.add(new MotorsOnUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.rcBattery().observe(new Observer<BatteryState>() {
                    @Override
                    public void observe(BatteryState oldValue, BatteryState newValue, Observation<BatteryState> observation) {
                        updates.add(new RcBatteryUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.rcInFunctionMode().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updates.add(new RcInFunctionModeUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().recordTimeInSeconds().observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                        updates.add(new RecordTimeInSecUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.droneHome().returnHomeAltitude().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        updates.add(new ReturnHomeAltitudeUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().shootPhotoIntervalValue().observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                        updates.add(new ShootPhotoIntervalValueUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.droneHome().takeOffDTM().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        updates.add(new TakeOffDTMUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.flightTasks().confirmLandRequire().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        updates.add(new ConfirmLandRequireUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().zoomInfo().observe(new Observer<ZoomInfo>() {
                    @Override
                    public void observe(ZoomInfo oldValue, ZoomInfo newValue, Observation<ZoomInfo> observation) {
                        updates.add(new ZoomInfoUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().zoomLevel().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        updates.add(new ZoomLevelUpdate(newValue));
                    }
                }).observeCurrentValue()
        );

        telemetryBindings.add(
                controller.camera().streamState().observe(new Observer<StreamState>() {
                    @Override
                    public void observe(StreamState oldValue, StreamState newValue, Observation<StreamState> observation) {
                        updates.add(new StreamStateUpdate(newValue));
                    }
                }).observeCurrentValue()
        );
    }
}
