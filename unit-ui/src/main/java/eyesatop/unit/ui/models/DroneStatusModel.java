package eyesatop.unit.ui.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.beans.FlightMode;
import eyesatop.controller.beans.GpsSignalLevel;
import eyesatop.controller.beans.GpsState;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller_tcpip.remote.tcpipcontroller.TCPController;
import eyesatop.unit.DroneUnit;
import eyesatop.unit.ui.Colour;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.activities.EyesatopAppConfiguration;
import eyesatop.unit.ui.functions.BatteryStateToColor;
import eyesatop.unit.ui.functions.RCBatteryStateToDrawable;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.DistanceUnitType;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.geo.TerrainNotFoundException;
import eyesatop.util.geo.Velocities;
import eyesatop.util.geo.dtm.DtmProviderWrapper;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

import static eyesatop.unit.ui.R.drawable.ic_topbar_signal_level_0;
import static eyesatop.unit.ui.R.drawable.location;
import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

public class DroneStatusModel {

    public enum DroneStatusObjectsType {
        HEADNG,
        GIMBAL_PITCH,
        VELOCITY,
        AGL,
        DISTANCE_FROM_HOME,
        GPS,
        RC_SIGNAL,
        RC_BATTERY,
        ASC_DTM,
        CROSSHAIR_DTM,
        WIFI,
        ATL,
        VIRTUAL_RC,
        BATTERY
    }

    public static final String DEGREE = "\u00b0";

    private static int BATTERY_PIVOT = 50;
    private static int BATTERY_MED_PIVOT = 70;

    private final ImageViewModel appLogo;

    private final ImageViewModel batteryPercentImageView;
    private final ImageViewModel wifiImageView;
    private final TextViewModel batteryPercentText;
    private final ViewModel remoteControllerSignalContainer;
    private final ImageViewModel remoteControllerSignalView;
    private final TextViewModel remoteControllerUnknownSignalTextView;
    private final ViewModel gpsContainer;
    private final ImageViewModel gpsImageView;
    private final TextViewModel statusTextView;

    private final Activity activity;

    private final TextView distanceFromHome;
    private final TextViewModel droneAGL;
    private final TextViewModel droneATL;
    private final TextView droneHorizontalVelocity;

    private final TextViewModel pitchDegree;
    private final TextViewModel headingDegree;
    private final TextViewModel tvGpsNum;
    private DroneTabModel currentTab = null;

    private final ViewModel includeHeading;
    private final ViewModel includeVelocity;
    private final ViewModel includeGimbalPitch;
    private final ViewModel includeHomeDistance;
    private final ViewModel includeAltitudeDistance;
    private final ViewModel includeATL;
    private final ViewModel includeVirtualRCConnected;
    private final ViewModel includeDTMAltitude;
    private final ViewModel includeASC;
    private final TextViewModel crosshairDTM;
    private final TextViewModel ascText;

    private final ViewModel pingContainer;
    private final TextViewModel pingText;

    private final ViewModel satelliteKnownContainer;
    private final TextViewModel satelliteUnknownText;

    private final ImageViewModel rcBatteryImageView;
    private final TextViewModel rcBatteryTextView;
    private final ViewModel rcBatteryContainer;

    private final TextViewModel virtualRCConnected;

    private final DtmProvider dtmProvider;

    private final ObservableValue<Location> crosshairLocation;

    private final ImageViewModel mcMenuButton;

    private final Context context;

    public void hideStatusObject(DroneStatusObjectsType objectsType, boolean isAlwaysGone) {
        switch (objectsType) {

            case VIRTUAL_RC:
                includeVirtualRCConnected.setAlwaysGone(isAlwaysGone);
                break;
            case ATL:
                includeATL.setAlwaysGone(isAlwaysGone);
                break;
            case HEADNG:
                includeHeading.setAlwaysGone(isAlwaysGone);
                break;
            case GIMBAL_PITCH:
                includeGimbalPitch.setAlwaysGone(isAlwaysGone);
                break;
            case VELOCITY:
                includeVelocity.setAlwaysGone(isAlwaysGone);
                break;
            case AGL:
                includeAltitudeDistance.setAlwaysGone(isAlwaysGone);
                break;
            case DISTANCE_FROM_HOME:
                includeHomeDistance.setAlwaysGone(isAlwaysGone);
                break;
            case GPS:
                gpsContainer.setAlwaysGone(isAlwaysGone);
                break;
            case RC_SIGNAL:
                remoteControllerSignalContainer.setAlwaysGone(isAlwaysGone);
                break;
            case RC_BATTERY:
                rcBatteryContainer.setAlwaysGone(isAlwaysGone);
                break;
            case ASC_DTM:
                includeASC.setAlwaysGone(isAlwaysGone);
                break;
            case CROSSHAIR_DTM:
                includeDTMAltitude.setAlwaysGone(isAlwaysGone);
                break;
            case WIFI:
                wifiImageView.setAlwaysGone(isAlwaysGone);
                break;
            case BATTERY:
                break;
        }
    }


    public DroneStatusModel(DroneUnit unit, Context context, ObservableValue<Location> crosshairLocation, final DtmProviderWrapper dtmProvider) {
        this.context = context;
        this.dtmProvider = dtmProvider;

        this.activity = (Activity) context;

        this.crosshairLocation = crosshairLocation;

        batteryPercentImageView = new ImageViewModel((ImageView) activity.findViewById(R.id.droneBatteryImageView));
        batteryPercentText = new TextViewModel((TextView) (activity.findViewById(R.id.battery_view)));
        remoteControllerSignalView = new ImageViewModel((ImageView) (activity.findViewById(R.id.remote_controller_view)));
        remoteControllerUnknownSignalTextView = new TextViewModel((TextView) activity.findViewById(R.id.controllerSignalUnknownText));
        remoteControllerSignalContainer = new ViewModel(activity.findViewById(R.id.controllerSignalContainer));

        gpsImageView = new ImageViewModel((ImageView) (activity.findViewById(R.id.satellite_view)));
        statusTextView = new TextViewModel((TextView) (activity.findViewById(R.id.status_view)));
        tvGpsNum = new TextViewModel((TextView) (activity.findViewById(R.id.tvGpsNum)));
        statusTextView.textRunning().set(true);

        distanceFromHome = (TextView) (activity.findViewById(R.id.distanceFromHomeInMeters));
        droneAGL = new TextViewModel((TextView) (activity.findViewById(R.id.droneHeightInMeter)));
        droneATL = new TextViewModel((TextView) (activity.findViewById(R.id.aboveTakeOffAltitude)));
        droneHorizontalVelocity = (TextView) (activity.findViewById(R.id.velocityInMeterForSecondHorizontal));

        pingContainer = new ViewModel(activity.findViewById(R.id.pingContainer));
        pingText = new TextViewModel((TextView) activity.findViewById(R.id.pingText));

        pitchDegree = new TextViewModel((TextView) activity.findViewById(R.id.cameraPitch));
        headingDegree = new TextViewModel((TextView) activity.findViewById(R.id.droneHeading));
        crosshairDTM = new TextViewModel((TextView) activity.findViewById(R.id.crosshairDTMText));
        ascText = new TextViewModel((TextView) activity.findViewById(R.id.ascText));

        virtualRCConnected = new TextViewModel((TextView) activity.findViewById(R.id.virtualRCConnectedText));

        mcMenuButton = new ImageViewModel((ImageView) activity.findViewById(R.id.mc_menu_button));

        appLogo = new ImageViewModel((ImageView) activity.findViewById(R.id.app_icon));

        includeVirtualRCConnected = new ViewModel(activity.findViewById(R.id.virtualRCConnectedContainer));
        includeHeading = new ViewModel(activity.findViewById(R.id.droneHeadingLayout));
        includeVelocity = new ViewModel(activity.findViewById(R.id.horizontalSpeedLayout));
        includeGimbalPitch = new ViewModel(activity.findViewById(R.id.cameraPitchLayout));
        includeHomeDistance = new ViewModel(activity.findViewById(R.id.distanceLayout));
        includeAltitudeDistance = new ViewModel(activity.findViewById(R.id.heightLayout));
        includeATL = new ViewModel(activity.findViewById(R.id.fromTakeOffAltitudeLayout));
        includeDTMAltitude = new ViewModel(activity.findViewById(R.id.crosshairDTM));
        includeASC = new ViewModel(activity.findViewById(R.id.ascLayout));

        gpsContainer = new ViewModel(activity.findViewById(R.id.gpsContainer));
        satelliteKnownContainer = new ViewModel(activity.findViewById(R.id.satelliteKnownContainer));
        satelliteUnknownText = new TextViewModel((TextView) activity.findViewById(R.id.satelliteUnknownText));
        satelliteUnknownText.visibility().set(ViewModel.Visibility.GONE);

        wifiImageView = new ImageViewModel((ImageView) activity.findViewById(R.id.ivWifi));

        rcBatteryContainer = new ViewModel(activity.findViewById(R.id.rcBatteryContainer));
        rcBatteryImageView = new ImageViewModel((ImageView) activity.findViewById(R.id.rcBatteryStatusBarImageView));
        rcBatteryTextView = new TextViewModel((TextView) activity.findViewById(R.id.rcBatteryStatusBarTextView));

        virtualRCConnected.text().bind(unit.isRCConnected().transform(new Function<Boolean, String>() {
            @Override
            public String apply(Boolean input) {

                if(input == null){
                    return "N/A";
                }

                if(input){
                    return "Yes";
                }

                return "No";
            }
        },false));

        if(dtmProvider.getSemiProvider() != null){
            ascText.text().bind(crosshairLocation.transform(new Function<Location, String>() {
                @Override
                public String apply(Location input) {

                    if (input == null) {
                        return "N/A";
                    }

                    try {
                        return DistanceUnitType.formatNumber(DistanceUnitType.METER,2, dtmProvider.getSemiProvider().terrainAltitude(input));
                    } catch (TerrainNotFoundException e) {
                        e.printStackTrace();
                        return "NULL";
                    }
                }
            },false));
        }
        else{
            ascText.text().set("N/A");
        }

        crosshairDTM.text().bind(crosshairLocation.transform(new Function<Location, String>() {
            @Override
            public String apply(Location input) {

                if (input == null) {
                    return "N/A";
                }

                try {
                    return DistanceUnitType.formatNumber(DistanceUnitType.METER,2, dtmProvider.getMainProvider().terrainAltitude(input));
                } catch (TerrainNotFoundException e) {
                    e.printStackTrace();
                    return "NULL";
                }
            }
        },false));
    }


    public Removable bindToController(final DroneController controller) {

        final ArrayList<Removable> removablesList = new ArrayList<>();

        if(controller instanceof TCPController){
            TCPController tcpController = (TCPController) controller;
            pingContainer.visibility().set(ViewModel.Visibility.VISIBLE);

            removablesList.add(
                    pingText.text().bind(tcpController.getPingGrade().transform(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer input) {

                            if(input == null){
                                return "N/A";
                            }

                            return input + "";
                        }
                    },false))
            );

            removablesList.add(
                    pingText.textColor().bind(tcpController.getPingGrade().transform(new Function<Integer, Integer>() {
                        @Override
                        public Integer apply(Integer input) {

                            if(input == null || input <= 4){
                                return ContextCompat.getColor(context, R.color.red_indicator);
                            }

                            if(input >=8){
                                return ContextCompat.getColor(context, R.color.green_indicator);
                            }

                            return ContextCompat.getColor(context, R.color.orange_indicator);
                        }
                    },false))
            );

        }
        else{
            pingContainer.visibility().set(ViewModel.Visibility.GONE);
        }

        removablesList.add(
          droneATL.text().bind(controller.telemetry().transform(new Function<Telemetry, String>() {
              @Override
              public String apply(Telemetry input) {

                  if(input == null || input.location() == null){
                      return "N/A";
                  }
                  return DistanceUnitType.formatNumber(EyesatopAppConfiguration.getInstance().getAppMeasureType().value(), 1, input.location().getAltitude());
              }
          },false))
        );

        removablesList.add(
                rcBatteryTextView.text().bind(controller.rcBattery().transform(BatteryState.REMAINING_PERCENT_STRING,false))
        );

        removablesList.add(
                rcBatteryTextView.textColor().bind(controller.rcBattery().transform(new BatteryStateToColor(context),false))
        );

        removablesList.add(
                rcBatteryImageView.tint().bind(controller.rcBattery().notNull().toggle(null,Colour.WRAP_ID.apply(R.color.gray)))
        );

        removablesList.add(
                rcBatteryImageView.imageDrawable().bind(controller.rcBattery().transform(new RCBatteryStateToDrawable(activity),false))
        );

        removablesList.add(
                batteryPercentText.text().bind(controller.droneBattery().transform(BatteryState.REMAINING_PERCENT_STRING,false))
        );

        removablesList.add(
                batteryPercentText.textColor().bind(controller.droneBattery().transform(new BatteryStateToColor(context),false))
        );

        removablesList.add(
                batteryPercentImageView.tint().bind(controller.droneBattery()
                        .transform(new BatteryStateToColor(context),false).transform(Colour.WRAP_VALUE))
        );


        removablesList.add(
                controller.rcSignalStrengthPercent().observe(new Observer<Integer>() {
                    @Override
                    public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {

                        if(newValue == null || newValue > 100 || newValue < 0){
                            remoteControllerSignalView.visibility().set(ViewModel.Visibility.GONE);
                            remoteControllerUnknownSignalTextView.visibility().set(ViewModel.Visibility.VISIBLE);
                            return;
                        }
                        else{
                            remoteControllerSignalView.visibility().set(ViewModel.Visibility.VISIBLE);
                            remoteControllerUnknownSignalTextView.visibility().set(ViewModel.Visibility.GONE);
                        }

                        if (newValue > 80) {
                            remoteControllerSignalView.imageDrawable().set(ContextCompat.getDrawable(context, R.drawable.ic_topbar_signal_level_5));
                        } else if (newValue > 60) {
                            remoteControllerSignalView.imageDrawable().set(ContextCompat.getDrawable(context, R.drawable.ic_topbar_signal_level_4));
                        } else if (newValue > 40) {
                            remoteControllerSignalView.imageDrawable().set(ContextCompat.getDrawable(context, R.drawable.ic_topbar_signal_level_3));
                        } else if (newValue > 20) {
                            remoteControllerSignalView.imageDrawable().set(ContextCompat.getDrawable(context, R.drawable.ic_topbar_signal_level_2));
                        } else if (newValue > 0) {
                            remoteControllerSignalView.imageDrawable().set(ContextCompat.getDrawable(context, R.drawable.ic_topbar_signal_level_1));
                        } else {
                            remoteControllerSignalView.imageDrawable().set(ContextCompat.getDrawable(context, ic_topbar_signal_level_0));
                        }
                    }
                }, UI_EXECUTOR).observeCurrentValue()
        );

        removablesList.add(headingDegree.text().bind(controller.telemetry().transform(new Function<Telemetry, String>() {
            @Override
            public String apply(Telemetry input) {

                if(input == null){
                    return "N/A";
                }

                return ((int)input.heading()) + DEGREE;
            }
        })));

        removablesList.add(controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                GimbalState currentGimbalState = controller.gimbal().gimbalState().value();
                if (currentGimbalState == null) {
                    pitchDegree.text().set("N/A");
//                    headingDegree.text().set("N/A");
                } else {
                    pitchDegree.text().set((int) currentGimbalState.getPitch() + DEGREE);
//                    headingDegree.text().set((int) currentGimbalState.getYaw() + DEGREE);
                }
            }
        }).observeCurrentValue());

        removablesList.add(

                controller.gps().observe(new Observer<GpsState>() {
                    @Override
                    public void observe(GpsState oldValue, GpsState newValue, Observation<GpsState> observation) {

                        if (newValue == null) {
                            gpsImageView.imageDrawable().set(ContextCompat.getDrawable(context, R.drawable.ic_topbar_signal_level_0));
                            tvGpsNum.text().set("");
                            return;
                        }

                        int textColor = newValue.getSatelliteCount() <= 6 || newValue.getGpsSignalLevel() == GpsSignalLevel.LEVEL0 ? Color.RED : Color.WHITE;
                        tvGpsNum.textColor().set(textColor);
                        tvGpsNum.text().set("" + newValue.getSatelliteCount());
                        int gpsSignalLevelDrawable = getSatIconFromSignalLevel(newValue.getGpsSignalLevel());
                        Drawable chosenDrawble = ContextCompat.getDrawable(context, gpsSignalLevelDrawable);
                        gpsImageView.imageDrawable().set(chosenDrawble);
                    }
                }, UI_EXECUTOR).observeCurrentValue()
        );

        removablesList.add(
                satelliteKnownContainer.visibility().bind(controller.gps().notNull().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE))
        );

        removablesList.add(
                satelliteUnknownText.visibility().bind(controller.gps().notNull().not().toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE))
        );

        removablesList.add(controller.aboveGroundAltitude().observe(new Observer<Double>() {
            @Override
            public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                updateAboveGroundAltitude(controller);
            }
        }).observeCurrentValue());

        removablesList.add(controller.telemetry().observe(new Observer<Telemetry>() {
            @Override
            public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                updateTelemetry(controller, newValue);
            }
        }, UI_EXECUTOR).observeCurrentValue());

        removablesList.add(
                controller.connectivity().observe(new Observer<DroneConnectivity>() {
                    @Override
                    public void observe(DroneConnectivity oldValue, DroneConnectivity newValue, Observation<DroneConnectivity> observation) {
                        calcBannerInfo(controller);
                    }
                })
        );

        removablesList.add(
                controller.flying().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        calcBannerInfo(controller);
                    }
                })
        );

        removablesList.add(
                controller.flightTasks().tasksBlockers().observe(new CollectionObserver<FlightTaskBlockerType>() {
                    @Override
                    public void added(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
                        calcBannerInfo(controller);
                    }

                    @Override
                    public void removed(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
                        calcBannerInfo(controller);
                    }
                })
        );

        removablesList.add(
                controller.flightMode().observe(new Observer<FlightMode>() {
                    @Override
                    public void observe(FlightMode oldValue, FlightMode newValue, Observation<FlightMode> observation) {
                        calcBannerInfo(controller);
                    }
                }).observeCurrentValue()
        );

        removablesList.add(EyesatopAppConfiguration.getInstance().getAppMeasureType().observe(new Observer<DistanceUnitType>() {
            @Override
            public void observe(DistanceUnitType oldValue, DistanceUnitType newValue, Observation<DistanceUnitType> observation) {
                updateTelemetry(controller, controller.telemetry().value());
                updateAboveGroundAltitude(controller);
            }
        }));

        return new RemovableCollection(removablesList);
    }

    private void calcBannerInfo(DroneController controller) {

        switch (controller.connectivity().withDefault(DroneConnectivity.NOT_CONNECTED).value()) {

            case NOT_CONNECTED:
                statusTextView.text().set("Disconnected");
                statusTextView.backgroundResource().set(R.drawable.img_stts_disconnected_design);
                return;
            case CONTROLLER_CONNECTED:

                statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_red_design);
                statusTextView.text().set("Only RC Connected");
                return;
            case REFRESHING:
                statusTextView.backgroundResource().set(R.drawable.img_stts_disconnected_design);
                statusTextView.text().set("Refreshing Info");
                return;
            case DRONE_CONNECTED:
                break;
        }

        Boolean isFlying = controller.flying().value();

        if (isFlying == null) {
            statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_red_design);
            statusTextView.text().set("Unknown Aircraft Position(Air or Ground)");
            return;
        }

        if (isFlying) {
            if (controller.flightMode().isNull()) {
                statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_red_design);
                statusTextView.text().set("Unknown Drone Controller Flight Mode");
                return;
            }

            switch (controller.flightMode().value()) {

                case AUTO_GO_HOME:
                    statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_green_design);
                    statusTextView.text().set("In-Flight(Going Home)");
                    return;
                case AUTO_TAKE_OFF:
                    statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_green_design);
                    statusTextView.text().set("In-Flight(Taking-Off)");
                    return;
                case AUTO_LAND:
                    statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_green_design);
                    statusTextView.text().set("In-Flight(Landing)");
                    return;
                case APP_CONTROL:
                    statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_green_design);
                    statusTextView.text().set("In-Flight(GPS)");
                    return;
                case ATTI:
                    statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_green_design);
                    statusTextView.text().set("In-Flight(Atti)");
                    return;
                case SPORT:
                    statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_green_design);
                    statusTextView.text().set("In-Flight(Sport)");
                    return;
                case EXTERNAL:
                    statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_green_design);
                    statusTextView.text().set("In-Flight(Unknown GPS State)");
                    return;
            }
        } else {
            if (controller.flightTasks().tasksBlockers().contains(FlightTaskBlockerType.PREHEATING)) {
                statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_orange_design);
                statusTextView.text().set("Aircraft is warming up");
                return;
            }

            ArrayList<FlightTaskBlockerType> takeOffBlockers = new ArrayList();
            try {
                for (FlightTaskBlockerType blocker : controller.flightTasks().tasksBlockers()) {
                    if (blocker.affectedTasks().contains(FlightTaskType.TAKE_OFF) && (!(blocker == FlightTaskBlockerType.BUSY || blocker == FlightTaskBlockerType.MISSION_PLANNER))) {
                        statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_red_design);
                        statusTextView.text().set("Cannot takeoff - " + blocker.getName());
                        return;
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

            statusTextView.backgroundResource().set(R.drawable.img_stts_bkgd_green_design);
            statusTextView.text().set("Ready to Fly(GPS)   ");
        }
    }

    private int getSatIconFromSignalLevel(GpsSignalLevel gpsLevel) {

        switch (gpsLevel) {

            case LEVEL0:
                return ic_topbar_signal_level_0;
            case LEVEL1:
                return R.drawable.ic_topbar_signal_level_1;
            case LEVEL2:
                return R.drawable.ic_topbar_signal_level_2;
            case LEVEL3:
                return R.drawable.ic_topbar_signal_level_3;
            case LEVEL4:
                return R.drawable.ic_topbar_signal_level_4;
            case LEVEL5:
                return R.drawable.ic_topbar_signal_level_5;
            default:
                return ic_topbar_signal_level_0;
        }
    }

    private void updateAboveGroundAltitude(DroneController controller) {
        Double newValue = controller.aboveGroundAltitude().value();
        if (newValue == null) {
            droneAGL.textColor().set(Color.WHITE);
            droneAGL.text().set("N/A");
        } else {
            droneAGL.text().set(DistanceUnitType.formatNumber(EyesatopAppConfiguration.getInstance().getAppMeasureType().value(), 1, newValue));
            droneAGL.textColor().set(newValue < 30 ? Color.RED : Color.WHITE);
        }
    }

    private void updateTelemetry(DroneController controller, Telemetry newValue) {
        if (newValue == null || newValue.location() == null) {
            distanceFromHome.setText("N/A");
            droneHorizontalVelocity.setText("N/A");
            return;
        }

        Location homeLocation = controller.droneHome().homeLocation().value();
        if (homeLocation == null) {
            distanceFromHome.setText("N/A");
        } else {
            int distnace = (int) homeLocation.distance(newValue.location());

            distanceFromHome.setText(DistanceUnitType.formatNumber(EyesatopAppConfiguration.getInstance().getAppMeasureType().value(), 1, distnace));
        }

        Velocities velocities = newValue.velocities();

        if (velocities != null) {

            double horizontalVelocity = (Math.sqrt(Math.pow(velocities.getX(), 2) + Math.pow(velocities.getY(), 2)));
//            int altitudeVelocity = (int) (newValue.velocities().getZ());

            droneHorizontalVelocity.setText(DistanceUnitType.formatNumber(EyesatopAppConfiguration.getInstance().getAppMeasureType().value(), 2, horizontalVelocity) + "/s");
        } else {
            droneHorizontalVelocity.setText("N/A");
        }
    }

    public void setAppLogo(Drawable drawable) {
        appLogo.imageDrawable().set(drawable);
    }
}
