package eyesatop.controller_tcpip.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.CameraMode;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.beans.MediaStorage;
import eyesatop.controller.beans.ZoomInfo;
import eyesatop.controller.tasks.camera.CameraTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.gimbal.GimbalTaskBlockerType;
import eyesatop.controller.tasks.home.HomeTaskBlockerType;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;

public class TelemetryInfo {

    private static final String ABOVE_GROUND_ALTITUDE = "aboveGroundAltitude";
    private static final String ABOVE_SEA_ALTITUDE = "aboveSeaAltitude";
    private static final String LOOK_AT_LOCATION = "lookAtLocation";
    private static final String MODEL = "model";
    private static final String CONNECTIVITY = "connectivity";
    private static final String TELEMETRY = "telemetry";
    private static final String BATTERY_STATE = "batteryState";
    private static final String IS_FLYING = "isFlying";
    private static final String IS_MOTORS_ON = "isMotorsOn";
    private static final String RC_BATTERY = "rcBattery";
    private static final String GPS_STATE = "gpsState";
    private static final String RC_IN_FUNCTION_MODE = "rcInFunctionMode";
    private static final String FLIGHT_MODE = "flightMode";
    private static final String RC_SIGNAL_STRENGTH = "rcSignalStrengthPercent";
    private static final String CURRENT_GIMBAL_UUID = "currentGimbalTaskUUID";
    private static final String GIMBAL_BLOCKERS = "gimbalBlockers";
    private static final String GIMBAL_STATE = "gimbalState";
    private static final String CURRENT_DRONE_HOME_TASK_UUID = "currentDroneHomeTaskUUID";
    private static final String HOME_BLOCKERS = "homeBlockers";
    private static final String TAKE_OFF_DTM = "takeOffDTM";
    private static final String HOME_LOCATION = "homeLocation";
    private static final String RETURN_HOME_ALTITUDE = "returnHomeAltitude";
    private static final String MAX_DISTANCE_FROM_HOME = "maxDistanceFromHome";
    private static final String MAX_ALTITUDE_FROM_TAKE_OFF = "maxAltitudeFromTakeOffLocation";
    private static final String LIMITATION_ACTIVE = "limitationActive";
    private static final String CAMERA_CURRENT_TASK = "cameraCurrentTaskUUID";
    private static final String CAMERA_BLOCKERS = "cameraBlockers";
    private static final String CAMERA_MODE = "cameraMode";
    private static final String IS_RECORDING = "isRecording";
    private static final String IS_SHOOTING_PHOTO = "isShootingPhoto";
    private static final String RECORD_TIME_IN_SECONDS = "recordTimeInSeconds";
    private static final String SHOOT_PHOTO_INTERVAL_VALUE = "shootPhotoIntervalValue";
    private static final String MEDIA_STORAGE = "mediaStorage";
    private static final String ZOOM_INFO = "zoomInfo";
    private static final String ZOOM_LEVEL = "zoomLevel";
    private static final String IS_ZOOM_SUPPORTED = "isZoomSupported";
    private static final String FLIGHT_CURRENT_TASK_UUID = "flightCurrentTaskUUID";
    private static final String FLIGHT_BLOCKER_LIST = "flightBlockerList";

    private final Double aboveGroundAltitude;
    private final Double aboveSeaAltitude;
    private final Location lookAtLocation;
    private final DroneModel model;
    private final DroneConnectivity connectivity;
    private final Telemetry telemetry;
    private final BatteryState batteryState;
    private final Boolean isFlying;
    private final Boolean isMotorsOn;
    private final BatteryState rcBattery;
    private final GpsState gpsState;
    private final Boolean rcInFunctionMode;
    private final FlightMode flightMode;
    private final Integer rcSignalStrengthPercent;

    private final UUID currentGimbalTaskUUID;
    private final List<GimbalTaskBlockerType> gimbalBlockers;
    private final GimbalState gimbalState;

    private final UUID currentDroneHomeTaskUUID;
    private final List<HomeTaskBlockerType> homeBlockers;
    private final Double takeOffDTM;
    private final Location homeLocation;
    private final Double returnHomeAltitude;
    private final Double maxDistanceFromHome;
    private final Double maxAltitudeFromTakeOffLocation;
    private final Boolean limitationActive;

    private final UUID cameraCurrentTaskUUID;
    private final List<CameraTaskBlockerType> cameraBlockers;
    private final CameraMode cameraMode;
    private final Boolean isRecording;
    private final Boolean isShootingPhoto;
    private final Integer recordTimeInSeconds;
    private final Integer shootPhotoIntervalValue;
    private final MediaStorage mediaStorage;
    private final ZoomInfo zoomInfo;
    private final Double zoomLevel;
    private final Boolean isZoomSupported;

    private final UUID flightCurrentTaskUUID;
    private final List<FlightTaskBlockerType> flightBlockerList;

    @JsonCreator
    public TelemetryInfo(@JsonProperty(ABOVE_GROUND_ALTITUDE) Double aboveGroundAltitude,
                         @JsonProperty(ABOVE_SEA_ALTITUDE) Double aboveSeaAltitude,
                         @JsonProperty(LOOK_AT_LOCATION) Location lookAtLocation,
                         @JsonProperty(MODEL) DroneModel model,
                         @JsonProperty(CONNECTIVITY) DroneConnectivity connectivity,
                         @JsonProperty(TELEMETRY) Telemetry telemetry,
                         @JsonProperty(BATTERY_STATE) BatteryState batteryState,
                         @JsonProperty(IS_FLYING) Boolean isFlying,
                         @JsonProperty(IS_MOTORS_ON) Boolean isMotorsOn,
                         @JsonProperty(RC_BATTERY) BatteryState rcBattery,
                         @JsonProperty(GPS_STATE) GpsState gpsState,
                         @JsonProperty(RC_IN_FUNCTION_MODE) Boolean rcInFunctionMode,
                         @JsonProperty(FLIGHT_MODE) FlightMode flightMode,
                         @JsonProperty(RC_SIGNAL_STRENGTH) Integer rcSignalStrengthPercent,
                         @JsonProperty(CURRENT_GIMBAL_UUID) UUID currentGimbalTaskUUID,
                         @JsonProperty(GIMBAL_BLOCKERS) List<GimbalTaskBlockerType> gimbalBlockers,
                         @JsonProperty(GIMBAL_STATE) GimbalState gimbalState,
                         @JsonProperty(CURRENT_DRONE_HOME_TASK_UUID) UUID currentDroneHomeTaskUUID,
                         @JsonProperty(HOME_BLOCKERS) List<HomeTaskBlockerType> homeBlockers,
                         @JsonProperty(TAKE_OFF_DTM) Double takeOffDTM,
                         @JsonProperty(HOME_LOCATION) Location homeLocation,
                         @JsonProperty(RETURN_HOME_ALTITUDE) Double returnHomeAltitude,
                         @JsonProperty(MAX_DISTANCE_FROM_HOME) Double maxDistanceFromHome,
                         @JsonProperty(MAX_ALTITUDE_FROM_TAKE_OFF) Double maxAltitudeFromTakeOffLocation,
                         @JsonProperty(LIMITATION_ACTIVE) Boolean limitationActive,
                         @JsonProperty(CAMERA_CURRENT_TASK) UUID cameraCurrentTaskUUID,
                         @JsonProperty(CAMERA_BLOCKERS) List<CameraTaskBlockerType> cameraBlockers,
                         @JsonProperty(CAMERA_MODE) CameraMode cameraMode,
                         @JsonProperty(IS_RECORDING) Boolean isRecording,
                         @JsonProperty(IS_SHOOTING_PHOTO) Boolean isShootingPhoto,
                         @JsonProperty(RECORD_TIME_IN_SECONDS) Integer recordTimeInSeconds,
                         @JsonProperty(SHOOT_PHOTO_INTERVAL_VALUE) Integer shootPhotoIntervalValue,
                         @JsonProperty(MEDIA_STORAGE) MediaStorage mediaStorage,
                         @JsonProperty(ZOOM_INFO) ZoomInfo zoomInfo,
                         @JsonProperty(ZOOM_LEVEL) Double zoomLevel,
                         @JsonProperty(IS_ZOOM_SUPPORTED) Boolean isZoomSupported,
                         @JsonProperty(FLIGHT_CURRENT_TASK_UUID) UUID flightCurrentTaskUUID,
                         @JsonProperty(FLIGHT_BLOCKER_LIST) List<FlightTaskBlockerType> flightBlockerList) {
        this.aboveGroundAltitude = aboveGroundAltitude;
        this.aboveSeaAltitude = aboveSeaAltitude;
        this.lookAtLocation = lookAtLocation;
        this.model = model;
        this.connectivity = connectivity;
        this.telemetry = telemetry;
        this.batteryState = batteryState;
        this.isFlying = isFlying;
        this.isMotorsOn = isMotorsOn;
        this.rcBattery = rcBattery;
        this.gpsState = gpsState;
        this.rcInFunctionMode = rcInFunctionMode;
        this.flightMode = flightMode;
        this.rcSignalStrengthPercent = rcSignalStrengthPercent;
        this.currentGimbalTaskUUID = currentGimbalTaskUUID;
        this.gimbalBlockers = gimbalBlockers;
        this.gimbalState = gimbalState;
        this.currentDroneHomeTaskUUID = currentDroneHomeTaskUUID;
        this.homeBlockers = homeBlockers;
        this.takeOffDTM = takeOffDTM;
        this.homeLocation = homeLocation;
        this.returnHomeAltitude = returnHomeAltitude;
        this.maxDistanceFromHome = maxDistanceFromHome;
        this.maxAltitudeFromTakeOffLocation = maxAltitudeFromTakeOffLocation;
        this.limitationActive = limitationActive;
        this.cameraCurrentTaskUUID = cameraCurrentTaskUUID;
        this.cameraBlockers = cameraBlockers;
        this.cameraMode = cameraMode;
        this.isRecording = isRecording;
        this.isShootingPhoto = isShootingPhoto;
        this.recordTimeInSeconds = recordTimeInSeconds;
        this.shootPhotoIntervalValue = shootPhotoIntervalValue;
        this.mediaStorage = mediaStorage;
        this.zoomInfo = zoomInfo;
        this.zoomLevel = zoomLevel;
        this.isZoomSupported = isZoomSupported;
        this.flightCurrentTaskUUID = flightCurrentTaskUUID;
        this.flightBlockerList = flightBlockerList;
    }

    @JsonProperty(ABOVE_GROUND_ALTITUDE)
    public Double getAboveGroundAltitude() {
        return aboveGroundAltitude;
    }

    @JsonProperty(ABOVE_SEA_ALTITUDE)
    public Double getAboveSeaAltitude() {
        return aboveSeaAltitude;
    }

    @JsonProperty(LOOK_AT_LOCATION)
    public Location getLookAtLocation() {
        return lookAtLocation;
    }

    @JsonProperty(MODEL)
    public DroneModel getModel() {
        return model;
    }

    @JsonProperty(CONNECTIVITY)
    public DroneConnectivity getConnectivity() {
        return connectivity;
    }

    @JsonProperty(TELEMETRY)
    public Telemetry getTelemetry() {
        return telemetry;
    }

    @JsonProperty(BATTERY_STATE)
    public BatteryState getBatteryState() {
        return batteryState;
    }

    @JsonProperty(IS_FLYING)
    public Boolean getFlying() {
        return isFlying;
    }

    @JsonProperty(IS_MOTORS_ON)
    public Boolean getMotorsOn() {
        return isMotorsOn;
    }

    @JsonProperty(RC_BATTERY)
    public BatteryState getRcBattery() {
        return rcBattery;
    }

    @JsonProperty(GPS_STATE)
    public GpsState getGpsState() {
        return gpsState;
    }

    @JsonProperty(RC_IN_FUNCTION_MODE)
    public Boolean getRcInFunctionMode() {
        return rcInFunctionMode;
    }

    @JsonProperty(FLIGHT_MODE)
    public FlightMode getFlightMode() {
        return flightMode;
    }

    @JsonProperty(RC_SIGNAL_STRENGTH)
    public Integer getRcSignalStrengthPercent() {
        return rcSignalStrengthPercent;
    }

    @JsonProperty(CURRENT_GIMBAL_UUID)
    public UUID getCurrentGimbalTaskUUID() {
        return currentGimbalTaskUUID;
    }

    @JsonProperty(GIMBAL_BLOCKERS)
    public List<GimbalTaskBlockerType> getGimbalBlockers() {
        return gimbalBlockers;
    }

    @JsonProperty(GIMBAL_STATE)
    public GimbalState getGimbalState() {
        return gimbalState;
    }

    @JsonProperty(CURRENT_DRONE_HOME_TASK_UUID)
    public UUID getCurrentDroneHomeTaskUUID() {
        return currentDroneHomeTaskUUID;
    }

    @JsonProperty(HOME_BLOCKERS)
    public List<HomeTaskBlockerType> getHomeBlockers() {
        return homeBlockers;
    }

    @JsonProperty(TAKE_OFF_DTM)
    public Double getTakeOffDTM() {
        return takeOffDTM;
    }

    @JsonProperty(HOME_LOCATION)
    public Location getHomeLocation() {
        return homeLocation;
    }

    @JsonProperty(RETURN_HOME_ALTITUDE)
    public Double getReturnHomeAltitude() {
        return returnHomeAltitude;
    }

    @JsonProperty(MAX_DISTANCE_FROM_HOME)
    public Double getMaxDistanceFromHome() {
        return maxDistanceFromHome;
    }

    @JsonProperty(MAX_ALTITUDE_FROM_TAKE_OFF)
    public Double getMaxAltitudeFromTakeOffLocation() {
        return maxAltitudeFromTakeOffLocation;
    }

    @JsonProperty(LIMITATION_ACTIVE)
    public Boolean getLimitationActive() {
        return limitationActive;
    }

    @JsonProperty(CAMERA_CURRENT_TASK)
    public UUID getCameraCurrentTaskUUID() {
        return cameraCurrentTaskUUID;
    }

    @JsonProperty(CAMERA_BLOCKERS)
    public List<CameraTaskBlockerType> getCameraBlockers() {
        return cameraBlockers;
    }

    @JsonProperty(CAMERA_MODE)
    public CameraMode getCameraMode() {
        return cameraMode;
    }

    @JsonProperty(IS_RECORDING)
    public Boolean getRecording() {
        return isRecording;
    }

    @JsonProperty(IS_SHOOTING_PHOTO)
    public Boolean getShootingPhoto() {
        return isShootingPhoto;
    }

    @JsonProperty(RECORD_TIME_IN_SECONDS)
    public Integer getRecordTimeInSeconds() {
        return recordTimeInSeconds;
    }

    @JsonProperty(SHOOT_PHOTO_INTERVAL_VALUE)
    public Integer getShootPhotoIntervalValue() {
        return shootPhotoIntervalValue;
    }

    @JsonProperty(MEDIA_STORAGE)
    public MediaStorage getMediaStorage() {
        return mediaStorage;
    }

    @JsonProperty(ZOOM_INFO)
    public ZoomInfo getZoomInfo() {
        return zoomInfo;
    }

    @JsonProperty(ZOOM_LEVEL)
    public Double getZoomLevel() {
        return zoomLevel;
    }

    @JsonProperty(IS_ZOOM_SUPPORTED)
    public Boolean getZoomSupported() {
        return isZoomSupported;
    }

    @JsonProperty(FLIGHT_CURRENT_TASK_UUID)
    public UUID getFlightCurrentTaskUUID() {
        return flightCurrentTaskUUID;
    }

    @JsonProperty(FLIGHT_BLOCKER_LIST)
    public List<FlightTaskBlockerType> getFlightBlockerList() {
        return flightBlockerList;
    }
}
