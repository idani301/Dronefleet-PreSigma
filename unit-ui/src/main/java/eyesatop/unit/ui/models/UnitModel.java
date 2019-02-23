package eyesatop.unit.ui.models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import eyesatop.math.camera.CameraName;
import eyesatop.util.drone.DroneModel;
import eyesatop.controller.beans.ZoomInfo;
import eyesatop.util.android.VideoCodec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.DroneConnectivity;
import eyesatop.controller.functions.TelemetryLocation;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.flight.FlightTaskBlockerType;
import eyesatop.controller.tasks.flight.FlightTaskType;
import eyesatop.controller.tasks.flight.FlyTo;
import eyesatop.controller.tasks.flight.FlyToSafeAndFast;
import eyesatop.controller.tasks.flight.FlyToUsingDTM;
import eyesatop.controller.tasks.gimbal.GimbalTaskType;
import eyesatop.controller.tasks.gimbal.LockGimbalAtLocation;
import eyesatop.controller.tasks.gimbal.LockYawAtLocation;
import eyesatop.controller.tasks.gimbal.LookAtPoint;
//import eyesatop.imageprocess.android.ImageprocessAnyvisionAndroid;
import eyesatop.math.Geometry.Point3D;
import eyesatop.math.Geometry.RotationMatrix3D;
import eyesatop.math.MathException;
import eyesatop.math.camera.Frame;
import eyesatop.math.camera.ImageInfo;
import eyesatop.math.camera.PinHoleCameraModule;
import eyesatop.math.camera.Pixel;
import eyesatop.unit.DroneUnit;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.gesture.OnSwipeTouchListener;
import eyesatop.unit.ui.models.actionmenus.DroneActionMenu;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.map.AddComponentName;
import eyesatop.unit.ui.models.map.MapCircle;
import eyesatop.unit.ui.models.map.MapDrawable;
import eyesatop.unit.ui.models.map.MapItem;
import eyesatop.unit.ui.models.map.MapLine;
import eyesatop.unit.ui.models.map.MapViewModel;
import eyesatop.unit.ui.models.massage.LittleMessageViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.unit.ui.models.missionplans.uicomponents.FlightComponentEditViewModel;
import eyesatop.unit.ui.models.specialfunctions.MissionExecutionMenuViewModel;
import eyesatop.unit.ui.models.specialfunctions.MissionPlannerMenuViewModel;
//import eyesatop.unit.ui.models.specialfunctions.ObliMenuViewModel;
import eyesatop.unit.ui.models.specialfunctions.SpecialFunctionMenu;
import eyesatop.unit.ui.models.tabs.DroneTab;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.DroneTabsModel;
import eyesatop.unit.ui.models.video.VideoStreamModel;
import eyesatop.unit.ui.models.video.VideoSurfaceInfo;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.dtm.DtmProviderWrapper;
import eyesatop.util.videoclicks.VideoClickInfo;
import logs.LoggerTypes;
import eyesatop.util.android.logs.MainLogger;
import eyesatop.util.geo.dtm.DtmProvider;
import eyesatop.util.geo.GimbalState;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

public class UnitModel extends AbstractViewModel<View> {


    public enum MainScreenModes {
        HALF_MAP_HALF_VIDEO,
        FULL_VIDEO,
        FULL_MAP
    }

    private static Function<MainScreenModes, MainScreenModes> NEXT_SCREEN_MODE = new Function<MainScreenModes, MainScreenModes>() {
        @Override
        public MainScreenModes apply(MainScreenModes input) {
            switch (input) {

                case HALF_MAP_HALF_VIDEO:
                    return MainScreenModes.FULL_VIDEO;
                case FULL_VIDEO:
                    return MainScreenModes.FULL_MAP;
                case FULL_MAP:
                    return MainScreenModes.HALF_MAP_HALF_VIDEO;
                default:
                    return MainScreenModes.HALF_MAP_HALF_VIDEO;
            }
        }
    };

    public enum UnitModelButtonsType {
        VIDEO_MAP_SWITCH;
    }

//    public static UnitModel forUnit(final DroneUnit unit, final Activity activity, VideoCodec videoCodec,DtmProviderWrapper dtmProvider){
//        return forUnit(unit,activity,videoCodec,null,dtmProvider);
//    }

//    public static ImageprocessAnyvisionAndroid instance = null;

    public static UnitModel forUnit(final DroneUnit unit,
                                    final Activity activity,
                                    VideoCodec videoCodec,
                                    DtmProviderWrapper dtmProvider) {

//        instance = imageprocessAnyvisionAndroid;

        final HashMap<DroneController,Property<VideoClickInfo>> videoClickedLocations = new HashMap<>();

        for(DroneController controller : unit.controllers()){
            videoClickedLocations.put(controller,new Property<VideoClickInfo>());
        }

        activity.setContentView(eyesatop.unit.ui.R.layout.ranger_ui_main);

        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final View view = activity.findViewById(R.id.renger_ui);

        final Property<Location> crosshairLocation = new Property<>();

        final MapViewModel mapViewModel = new MapViewModel(view, crosshairLocation);

        final LittleMessageViewModel littleMessageViewModel = new LittleMessageViewModel(view.getContext());
        final TextureView videoSurface = (TextureView) view.findViewById(R.id.cameraVideo);
        final MessageViewModel messageViewModel = new MessageViewModel(view.getContext(), unit, videoSurface);

        WifiManager wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        WifiManager.WifiLock lock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "LockTag");
        lock.acquire();

        final DroneTab nullTab = new DroneTab.Stub(activity,mapViewModel,messageViewModel,littleMessageViewModel);
        final BooleanProperty fullScreenMode = new BooleanProperty(false);

        final DtmProviderWrapper uiDtmProvider = dtmProvider;

        final Property<Location> myLocation = new Property<>();

        final MapDrawable myLocationDrawable = new MapDrawable(1F);
        myLocationDrawable.location().bind(myLocation);
        myLocationDrawable.drawable().set(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_operator));
//        myLocationDrawable.mapListener().set(new MapItem.MapClickListener() {
//            @Override
//            public void onMapClick() {
////                System.out.println("hit");
//            }
//        });
        mapViewModel.addMapItem(myLocationDrawable);

        final MapDrawable crosshairDrawable = new MapDrawable(Float.MAX_VALUE);
        crosshairDrawable.location().bind(crosshairLocation);
        crosshairDrawable.drawable().bind(
                crosshairLocation.notNull().toggle(
                        ContextCompat.getDrawable(view.getContext(), R.drawable.crosshair),
                        ContextCompat.getDrawable(view.getContext(), R.drawable.nothing))
        );
        mapViewModel.addMapItem(crosshairDrawable);

//        if(unit.provider().corners() != null){
//            MapPolyline polyline = new MapPolyline(new ObservableList<>(unit.provider().corners()),Color.BLUE);
//            polyline.getIsClosedShape().set(true);
//            mapViewModel.addMapItem(polyline);
//        }

        final DroneTabsModel tabsModel = DroneTabsModel.forUnit(unit, mapViewModel, view.findViewById(R.id.tabs),messageViewModel,littleMessageViewModel, uiDtmProvider);
        final DroneStatusModel statusModel = new DroneStatusModel(unit,view.getContext(), crosshairLocation, uiDtmProvider);
//        final MapViewModel mapViewModel = MapViewModel.forUnit(unit,tabsModel, view, crosshairLocation, tabsModel,uiDtmProvider,myLocation);

        final MCViewModel mcViewModel = new MCViewModel((Activity) view.getContext(), tabsModel, messageViewModel);
        final VideoStreamModel videoStreamModel = new VideoStreamModel(activity, unit, tabsModel, videoSurface, videoCodec);
        final MapFocusButtonsModel mapFocusButtonsModel = new MapFocusButtonsModel(activity, tabsModel, mapViewModel.focusRequests(), littleMessageViewModel, myLocation);
        final DroneActionMenu droneMenu = new DroneActionMenu(activity, tabsModel, crosshairLocation, videoClickedLocations,unit, uiDtmProvider, messageViewModel, littleMessageViewModel, fullScreenMode);

        final VideoLayoutModel videoLayoutModel = new VideoLayoutModel(activity, fullScreenMode, crosshairLocation);

        final FlightComponentEditViewModel pathPlannerViewModel = new FlightComponentEditViewModel(activity, tabsModel,messageViewModel,littleMessageViewModel,mapViewModel, crosshairLocation);
        final MissionPlannerMenuViewModel missionPlannerMenuViewModel = new MissionPlannerMenuViewModel(tabsModel, activity,mapViewModel,crosshairLocation,messageViewModel, littleMessageViewModel);
        final MissionExecutionMenuViewModel missionExecutionMenuViewModel = new MissionExecutionMenuViewModel(tabsModel, activity,mapViewModel,crosshairLocation,messageViewModel, littleMessageViewModel);

        final ExecutorService crosshairLocationExecutor = Executors.newSingleThreadExecutor();
        final DtmProvider crosshairLocationDtmProvider = uiDtmProvider.duplicate();

        final BooleanProperty doneActionUnderScale = new BooleanProperty(false);
        final Property<Integer> zoomCounter = new Property<>(0);

        final ScaleGestureDetector detector = new ScaleGestureDetector(activity, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                float scaleFactor = detector.getScaleFactor();

                DroneTabModel currentTab = tabsModel.selected().value();

                if (currentTab != null && !doneActionUnderScale.value()) {
                    DroneController currentControler = currentTab.getDroneController();
                    if (currentControler != null) {
                        DroneTask currentCameraTask = currentControler.camera().currentTask().value();

                        if (currentCameraTask == null) {
                            if (scaleFactor > 1) {
//                                System.out.println("Zoom in");
                                zoomCounter.set(zoomCounter.value() + 1);
                                if (zoomCounter.value() >= 4) {
                                    doneActionUnderScale.set(true);
                                    currentTab.getDroneTasks().zoomIn();
                                }
                            } else {
//                                System.out.println("Zoom out");
                                zoomCounter.set(zoomCounter.value() - 1);
                                if (zoomCounter.value() <= -6) {
                                    doneActionUnderScale.set(true);
                                    currentTab.getDroneTasks().zoomOut();
                                }
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                doneActionUnderScale.set(false);
                zoomCounter.set(0);
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                doneActionUnderScale.set(false);
                zoomCounter.set(0);
            }
        });

        final OnSwipeTouchListener onSwipeTouchListener = new OnSwipeTouchListener(activity) {

            @Override
            public void onClick(double x, double y) {

                DroneController currentController = tabsModel.selected().value().getDroneController();

                videoLayoutModel.setTouchCoordinates((float) x, (float) y);

                VideoSurfaceInfo surfaceInfo = videoStreamModel.getVideoSurfaceInfo().value();

                System.out.println("Height : " + surfaceInfo.getHeight() + " , Width : " + surfaceInfo.getWidth());
                System.out.println("Y : " + y + " , X : " + x);

                final DroneTabModel selectedTabModel = tabsModel.selected().value();
                if (selectedTabModel == null) {
                    return;
                }
                final DroneController controller = selectedTabModel.getDroneController();
                if (controller == null) {
                    return;
                }

//                Boolean isFlying = controller.flying().value();
//                if (isFlying == null || !isFlying) {
//                    return;
//                }

                Location currentLocation = Telemetry.telemetryToLocation(controller.telemetry().value());
                if (currentLocation == null) {
                    currentLocation = new Location(0,0);
                }

                final GimbalState currentGimbalState = controller.gimbal().gimbalState().value();
                if (currentGimbalState == null) {
                    return;
                }

                DroneModel droneModel = controller.model().value();
                if(droneModel == null){
                    return;
                }

                PinHoleCameraModule pinHoleModel;
                switch (droneModel){
                    case MAVIC:
                        pinHoleModel = new PinHoleCameraModule(CameraName.MAVIC);
                        break;
                    case PHANTOM_4:
                        pinHoleModel = new PinHoleCameraModule(CameraName.PHANTOM_4);
                        break;
                    case MATRICE100:
                        pinHoleModel = new PinHoleCameraModule(CameraName.MATRICE_100);
                        break;
                        default:
                            pinHoleModel = new PinHoleCameraModule(CameraName.MATRICE_100);
                }

                ZoomInfo zoomInfo = currentController.camera().zoomInfo().value();
                double opticalZoomLevel;
                double digitalZoomLevel;

                if(zoomInfo == null){
                    opticalZoomLevel = 1;
                    digitalZoomLevel = 1;
                }
                else {
                    opticalZoomLevel = zoomInfo.getOpticalZoomFactor();
                    digitalZoomLevel = zoomInfo.getDigitalZoomFactor();
                }

                double k = Math.PI / 180;

                ImageInfo imageInfo = new ImageInfo(
                        pinHoleModel,
                        RotationMatrix3D.Body3DNauticalAngles(currentGimbalState.getYaw() * k, -currentGimbalState.getPitch() * k, 0),
                        new eyesatop.math.Geometry.EarthGeometry.Location(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude()),
                        null);
                try {
                    Point3D point3D = imageInfo.getLineOfSightFromPixelOpticalZoom(new Pixel(x, y, -1), new Frame(surfaceInfo.getWidth(), surfaceInfo.getHeight()), digitalZoomLevel,opticalZoomLevel);

                    final double yaw = point3D.getAzimuthDegree();
                    final double pitch = point3D.getElevationDegree();
                    final GimbalState lineDirection = new GimbalState(0, pitch, yaw);

                    Location newCrosshairLocation = DtmProvider.DtmTools.cutWithDTM(currentLocation, lineDirection, controller.droneHome().homeLocation().value(), crosshairLocationDtmProvider);

                    VideoClickInfo clickInfo = new VideoClickInfo(newCrosshairLocation,(int)x,(int)y,videoSurface.getWidth(),videoSurface.getHeight(), lineDirection, System.currentTimeMillis());
                    videoClickedLocations.get(currentController).set(clickInfo);
                    System.out.println(clickInfo.toString());

                    if (newCrosshairLocation != null) {
                        crosshairLocation.set(newCrosshairLocation);
                    }

                } catch (MathException e) {
                    return;
                }

//                System.out.println("hi");
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();

                DroneTabModel selectedTab = tabsModel.selected().value();
                DroneController controller = selectedTab == null ? null : selectedTab.getDroneController();
                GimbalState currentGimbal = controller == null ? null : controller.gimbal().gimbalState().value();

                if (currentGimbal != null) {
//                    selectedTab.getDroneTasks().rotateGimbal(new GimbalRequest(new GimbalState(0, currentGimbal.getPitch() + 5, 0), true, false, false));
                }

//                System.out.println("Swipe Down detected");
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();

                DroneTabModel selectedTab = tabsModel.selected().value();
                DroneController controller = selectedTab == null ? null : selectedTab.getDroneController();
                GimbalState currentGimbal = controller.gimbal().gimbalState().value();

                double yaw = currentGimbal.getYaw() + 10;
//                selectedTab.getDroneTasks().rotateGimbal(new GimbalRequest(new GimbalState(0, 0, yaw), false, false, true));

//                System.out.println("Swipe Left detected");
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();

                DroneTabModel selectedTab = tabsModel.selected().value();
                DroneController controller = selectedTab == null ? null : selectedTab.getDroneController();
                GimbalState currentGimbal = controller == null ? null : controller.gimbal().gimbalState().value();

                if (currentGimbal != null) {
//                    selectedTab.getDroneTasks().rotateGimbal(new GimbalRequest(new GimbalState(0, currentGimbal.getPitch() - 5, 0), true, false, false));
                }
//                System.out.println("Swipe Up detected");
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();

                DroneTabModel selectedTab = tabsModel.selected().value();
                DroneController controller = selectedTab == null ? null : selectedTab.getDroneController();

                GimbalState currentGimbal = controller.gimbal().gimbalState().value();

                double yaw = currentGimbal.getYaw() - 10;
//                selectedTab.getDroneTasks().rotateGimbal(new GimbalRequest(new GimbalState(0, 0, yaw), false, false, true));

//                System.out.println("Swipe Right detected");
            }
        };

        videoSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onSwipeTouchListener.onTouch(v, event);
                return detector.onTouchEvent(event);
            }
        });

//                new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return detector.onTouchEvent(event);
//            }
//        });


        LocationManager locationManager;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {
                    if (location != null) {
                        myLocation.set(new Location(location.getLatitude(), location.getLongitude()));
                    } else {
                        myLocation.set(null);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

        android.location.Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null) {
            myLocation.set(new Location(currentLocation.getLatitude(), currentLocation.getLongitude()));
        } else {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (currentLocation != null) {
                myLocation.set(new Location(currentLocation.getLatitude(), currentLocation.getLongitude()));
            }
        }

        Location myCurrentLocation = myLocation.value();
        if (myCurrentLocation != null) {
            mapViewModel.focusRequests().set(myCurrentLocation);
        }

        tabsModel.selected().observe(new Observer<DroneTabModel>() {

            private Removable videoLayoutRemovable = Removable.STUB;

            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {

                videoLayoutRemovable.remove();

                if (newValue != null) {
                    videoLayoutRemovable = videoLayoutModel.bindToTab(newValue);
                } else {
                    videoLayoutRemovable = videoLayoutModel.bindToTab(nullTab);
                }
            }
        }).observeCurrentValue();

        // update status bar
        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            Removable statusBinding = Removable.STUB;

            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
                statusBinding.remove();
                statusBinding = Removable.STUB;

                DroneController controller = null;
                if (newValue == null) {
                    controller = nullTab.getDroneController();
                }
                else{
                    controller = newValue.getDroneController();
                }
                statusBinding = statusModel.bindToController(controller);
            }
        }).observeCurrentValue();

        // update menu
        tabsModel.selected().observe(new Observer<DroneTabModel>() {

            Removable menuBinding = Removable.STUB;

            boolean everStarted = false;

            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {

                menuBinding.remove();
                menuBinding = Removable.STUB;

                if (newValue != null) {
                    DroneController controller = null;
                    controller = newValue.getDroneController();
                    menuBinding = droneMenu.bind(newValue, controller);
                } else {
                    droneMenu.bind(nullTab,nullTab.getDroneController());
                }
            }
        }).observeCurrentValue();

        ViewGroup specialFunctionMenuView = (ViewGroup) view.findViewById(R.id.includeMainManu);
        LayoutInflater inflater;
        View menuView;

        specialFunctionMenuView.removeAllViews();
        inflater = LayoutInflater.from(view.getContext());
        menuView = inflater.inflate(R.layout.main_menus, specialFunctionMenuView, true);
        final SpecialFunctionMenu specialFunctionMenu = new SpecialFunctionMenu(menuView);

        // update special function menu
        tabsModel.selected().observe(new Observer<DroneTabModel>() {

            Removable menuBinding = Removable.STUB;

            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {

                menuBinding.remove();
                menuBinding = Removable.STUB;

                if(newValue != null) {
                    menuBinding = specialFunctionMenu.bindToTab(newValue);
                }
                else{
                    menuBinding = specialFunctionMenu.bindToNull();
                }
            }
        }).observeCurrentValue();

//        ViewGroup obliFunctionMenuView = (ViewGroup) view.findViewById(R.id.includeMissionExecutionMenu);
//
//        LayoutInflater inflater;
//        View menuView;

//        obliFunctionMenuView.removeAllViews();
//        inflater = LayoutInflater.from(view.getContext());
//        menuView = inflater.inflate(R.layout.mission_execution_menu, obliFunctionMenuView, true);
//        final ObliMenuViewModel missionExecutionMenuViewModel = new ObliMenuViewModel(menuView, crosshairLocation, messageViewModel, littleMessageViewModel);

        // update obli menu
//        tabsModel.selected().observe(new Observer<DroneTabModel>() {
//
//            Removable menuBinding = Removable.STUB;
//
//            @Override
//            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
//
//                menuBinding.remove();
//                menuBinding = Removable.STUB;
//
//                if (newValue != null) {
//
//                    menuBinding = missionExecutionMenuViewModel.bindToTab(newValue);
//                }
//                else{
//                    menuBinding = missionExecutionMenuViewModel.bindToTab(nullTab);
//                }
//            }
//        }).observeCurrentValue();

        // visible gone for loading screen

        return new UnitModel(activity,
                view,
                unit,
                tabsModel,
                videoStreamModel,
                mcViewModel,
                statusModel,
                missionPlannerMenuViewModel,
                pathPlannerViewModel,
                droneMenu,
                missionExecutionMenuViewModel,
                fullScreenMode,
                videoSurface, mapViewModel, specialFunctionMenu, videoLayoutModel, messageViewModel, littleMessageViewModel, videoClickedLocations);
    }

    public TextureView getVideoTextureView() {
        return this.videoSurface;
    }

    private final DroneTabsModel tabsModel;
    private final DroneStatusModel statusModel;
    private final DroneActionMenu droneActionMenu;
    private final TextureView videoSurface;
    private final MissionExecutionMenuViewModel missionExecutionMenuViewModel;
    private final MCViewModel mcViewModel;

    private final Property<MainScreenModes> currentScreenMode = new Property<>(MainScreenModes.HALF_MAP_HALF_VIDEO);
    private final ViewModel mapView;
    private final ViewModel videoView;
    private final DroneUnit unit;
    private final MapViewModel mapViewModel;

    private final ImageViewModel currentScreenModeButton;

    private final ViewModel headerContainer;

    private final MissionPlannerMenuViewModel missionPlannerMenuViewModel;
    private final BooleanProperty fullScreenMode;

    private final FlightComponentEditViewModel pathPlannerViewModel;
    private final VideoStreamModel videoStreamModel;
    private final SpecialFunctionMenu specialFunctionMenu;
    private final VideoLayoutModel videoLayoutModel;
    private final MessageViewModel messageViewModel;
    private final LittleMessageViewModel littleMessageViewModel;

    private final HashMap<DroneController,Property<VideoClickInfo>> videoClickedLocations;

    public UnitModel(final Activity activity, View view,
                     DroneUnit unit,
                     final DroneTabsModel tabsModel,
                     VideoStreamModel videoStreamModel,
                     MCViewModel mcViewModel,
                     DroneStatusModel statusModel,
                     MissionPlannerMenuViewModel missionPlannerMenuViewModel,
                     FlightComponentEditViewModel pathPlannerViewModel,
                     DroneActionMenu droneActionMenu,
                     MissionExecutionMenuViewModel missionExecutionMenuViewModel,
                     BooleanProperty fullScreenMode,
                     TextureView videoSurface,
                     MapViewModel mapViewModel,
                     SpecialFunctionMenu specialFunctionMenu,
                     VideoLayoutModel videoLayoutModel,
                     MessageViewModel messageViewModel,
                     LittleMessageViewModel littleMessageViewModel,
                     HashMap<DroneController, Property<VideoClickInfo>> videoClickedLocations) {
        super(view);
        this.unit = unit;
        this.videoStreamModel = videoStreamModel;
        this.missionPlannerMenuViewModel = missionPlannerMenuViewModel;
        this.fullScreenMode = fullScreenMode;
        this.mcViewModel = mcViewModel;
        this.tabsModel = tabsModel;
        this.statusModel = statusModel;
        this.videoSurface = videoSurface;
        this.droneActionMenu = droneActionMenu;
        this.pathPlannerViewModel = pathPlannerViewModel;
        this.missionExecutionMenuViewModel = missionExecutionMenuViewModel;

        mapView = new ViewModel(activity.findViewById(R.id.includeMap));
        videoView = new ViewModel(activity.findViewById(R.id.includeCamera));

        headerContainer = new ViewModel(view.findViewById(R.id.header));
        this.specialFunctionMenu = specialFunctionMenu;
        this.videoLayoutModel = videoLayoutModel;
        this.messageViewModel = messageViewModel;
        this.littleMessageViewModel = littleMessageViewModel;
        this.videoClickedLocations = videoClickedLocations;
        headerContainer.visibility().bind(fullScreenMode.toggle(Visibility.GONE, Visibility.VISIBLE));

        currentScreenModeButton = new ImageViewModel((ImageView) view.findViewById(R.id.changeScreenModeButton));
        this.mapViewModel = mapViewModel;
        currentScreenModeButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                currentScreenMode.set(NEXT_SCREEN_MODE.apply(currentScreenMode.value()));
                return false;
            }
        });
        currentScreenModeButton.imageDrawable().bind(currentScreenMode.transform(new Function<MainScreenModes, Drawable>() {
            @Override
            public Drawable apply(MainScreenModes input) {
                MainScreenModes nextValue = NEXT_SCREEN_MODE.apply(input);

                switch (nextValue) {

                    case HALF_MAP_HALF_VIDEO:
                        return ContextCompat.getDrawable(activity, R.drawable.programming);
                    case FULL_VIDEO:
                        return ContextCompat.getDrawable(activity, R.drawable.video_camera);
                    case FULL_MAP:
                        return ContextCompat.getDrawable(activity, R.drawable.map);
                    default:
                        return ContextCompat.getDrawable(activity, R.drawable.programming);
                }
            }
        }));

        currentScreenModeButton.visibility().bind(fullScreenMode.toggle(Visibility.GONE, Visibility.VISIBLE));

        mapView.visibility().bind(fullScreenMode.not().and(currentScreenMode.equalsTo(MainScreenModes.FULL_VIDEO).not()).toggle(Visibility.VISIBLE, Visibility.GONE));
        videoView.visibility().bind(currentScreenMode.equalsTo(MainScreenModes.FULL_MAP).not().toggle(Visibility.VISIBLE, Visibility.GONE));

        ImageView closeSpecialFunctionsMenuImageView = (ImageView) view.findViewById(R.id.closeSpecialFunctionsMenu);
        ImageViewModel closeSpecialFunctionsMenu = new ImageViewModel(closeSpecialFunctionsMenuImageView);

        closeSpecialFunctionsMenu.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                DroneTabModel selectedTab = tabsModel.selected().value();

                if (selectedTab != null) {
                    selectedTab.getFunctionsModel().isFunctionScreenOpen().set(false);
                }
                return false;
            }
        });

        addUnitToMap();
    }

    public MessageViewModel getMessageViewModel() {
        return messageViewModel;
    }

    public void hideButton(UnitModelButtonsType type, boolean isAlwaysGone) {

        switch (type) {

            case VIDEO_MAP_SWITCH:
                currentScreenModeButton.setAlwaysGone(isAlwaysGone);
//                currentScreenModeButton.setAlwaysGone(isAlwaysGone);
//                fullVideoButton.setAlwaysGone(isAlwaysGone);
//                splittedScreenButton.setAlwaysGone(isAlwaysGone);
                break;
        }
    }

    public Property<MainScreenModes> getCurrentScreenMode() {
        return currentScreenMode;
    }

    public DroneActionMenu droneActionMenu() {
        return droneActionMenu;
    }

    public DroneTabsModel tabs() {
        return tabsModel;
    }

    public DroneStatusModel status() {
        return statusModel;
    }

    enum DroneIconTypes {
        NOTHING,
        GROUND,
        FLYING,
        ERROR,
        PREHEATING
    }

    private void addUnitToMap() {

        final CollectionObserver<DroneTabModel> controllersObserver = new CollectionObserver<DroneTabModel>() {

            Map<UUID, Removable> bindings = new HashMap<>();

            private Drawable fromDroneIconType(DroneIconTypes iconType) {
                switch (iconType) {

                    case NOTHING:
                        return ContextCompat.getDrawable(view().getContext(), R.drawable.nothing);
                    case GROUND:
                        return ContextCompat.getDrawable(view().getContext(), R.drawable.map_drone_lost);
                    case FLYING:
                        return ContextCompat.getDrawable(view().getContext(), R.drawable.map_drone_on);
                    case ERROR:
                        return ContextCompat.getDrawable(view().getContext(), R.drawable.map_drone_error);
                    case PREHEATING:
                        return ContextCompat.getDrawable(view().getContext(), R.drawable.map_drone_preparing);
                }

                MainLogger.logger.write_message(LoggerTypes.ERROR, "Illegal icon progressState");
                return ContextCompat.getDrawable(view().getContext(), R.drawable.nothing);
            }


            private DroneIconTypes calcDrawableFromDroneController(DroneController droneController) {

                DroneConnectivity connectivity = droneController.connectivity().value();

                if (connectivity == null || connectivity != DroneConnectivity.DRONE_CONNECTED) {
                    return DroneIconTypes.NOTHING;
                }

                Telemetry droneTelemetry = droneController.telemetry().value();
                if (droneTelemetry == null || droneTelemetry.location() == null) {
                    return DroneIconTypes.NOTHING;
                }

                for (FlightTaskBlockerType taskBlocker : droneController.flightTasks().tasksBlockers()) {
                    if (taskBlocker == FlightTaskBlockerType.PREHEATING) {
                        return DroneIconTypes.PREHEATING;
                    }
                }

                for (FlightTaskBlockerType taskBlocker : droneController.flightTasks().tasksBlockers()) {
                    if (taskBlocker != FlightTaskBlockerType.BUSY && taskBlocker != FlightTaskBlockerType.MISSION_PLANNER) {
                        return DroneIconTypes.ERROR;
                    }
                }


                Boolean isFlying = droneController.flying().value();
                if (isFlying != null && isFlying) {
                    return DroneIconTypes.FLYING;
                }

                Boolean motorsOn = droneController.motorsOn().value();
                if (motorsOn != null && motorsOn) {
                    return DroneIconTypes.FLYING;
                }

                return DroneIconTypes.GROUND;
            }

            @Override
            public void added(final DroneTabModel value, Observation<DroneTabModel> observation) {

                final ArrayList<Removable> removablesList = new ArrayList<>();

                final DroneController controller = value.getDroneController();
                final MapDrawable droneDrawable = new MapDrawable(3F);
                final MapDrawable droneLookAtTargetDrawable = new MapDrawable(1F);
                final MapLine droneViewLine = new MapLine();
                final MapDrawable homeDrawable = new MapDrawable(2F);
                final MapCircle flightLimitationCircle = new MapCircle();
                flightLimitationCircle.getColor().set(Color.RED);
                final Property<Drawable> droneStateFromDroneController = new Property<>(ContextCompat.getDrawable(view().getContext(), R.drawable.nothing));
                final Property<DroneIconTypes> droneStateCurrentIcon = new Property<>(DroneIconTypes.NOTHING);

                final MapDrawable lookAtDrawable = new MapDrawable(MapItem.Z_INDEX_LOW);
                final Property<Location> lookAtLocationMissionTarget = new Property<>();
                final MapLine lookAtMissionLine = new MapLine();
                removablesList.add(lookAtMissionLine.getStartPoint().bind(droneDrawable.location()));
                removablesList.add(lookAtMissionLine.getEndPoint().bind(lookAtLocationMissionTarget));
                lookAtMissionLine.getDash().set(20D);
                lookAtMissionLine.getGap().set(20D);
                lookAtMissionLine.getColor().set(Color.BLACK);

                removablesList.add(
                        lookAtDrawable.location().bind(lookAtLocationMissionTarget)
                );

                lookAtDrawable.drawable().set(ContextCompat.getDrawable(view().getContext(), R.drawable.rc_lookat_off));


                final MapDrawable flyToDrawable = new MapDrawable(MapItem.Z_INDEX_LOW);
                final Property<Location> flyToLocation = new Property<>();

                final MapLine flyToLine = new MapLine();
                removablesList.add(flyToLine.getStartPoint().bind(droneDrawable.location()));
                removablesList.add(flyToLine.getEndPoint().bind(flyToLocation));
                flyToLine.getDash().set(20D);
                flyToLine.getGap().set(20D);
                flyToLine.getColor().set(Color.RED);

                removablesList.add(
                        controller.gimbal().currentTask().observe(new Observer<DroneTask<GimbalTaskType>>() {
                            @Override
                            public void observe(DroneTask<GimbalTaskType> oldValue, DroneTask<GimbalTaskType> newValue, Observation<DroneTask<GimbalTaskType>> observation) {
                                if (newValue != null) {
                                    switch (newValue.taskType()) {

                                        case LOOK_AT_POINT:
                                            lookAtLocationMissionTarget.set(((LookAtPoint) newValue).location());
                                            break;
                                        case LOCK_LOOK_AT_LOCATION:
                                            lookAtLocationMissionTarget.set(((LockGimbalAtLocation) newValue).location());
                                            break;
                                        case LOCK_YAW_AT_LOCATION:
                                            lookAtLocationMissionTarget.set(((LockYawAtLocation) newValue).location());
                                            break;
                                        default:
                                            lookAtLocationMissionTarget.set(null);
                                    }
                                } else {
                                    lookAtLocationMissionTarget.set(null);
                                }
                            }
                        }).observeCurrentValue()
                );

                removablesList.add(controller.flightTasks().current().observe(new Observer<DroneTask<FlightTaskType>>() {
                    @Override
                    public void observe(DroneTask<FlightTaskType> oldValue, DroneTask<FlightTaskType> newValue, Observation<DroneTask<FlightTaskType>> observation) {

                        if (newValue != null) {
                            switch (newValue.taskType()) {
                                case FLY_SAFE_TO:
                                    flyToLocation.set(((FlyToSafeAndFast)newValue).targetLocation());
                                    break;
                                case GOTO_POINT:
                                    flyToLocation.set(((FlyTo) newValue).location());
                                    break;
                                case FLY_TO_USING_DTM:
                                    flyToLocation.set(((FlyToUsingDTM) newValue).location());
                                    break;
                                default:
                                    flyToLocation.set(null);
                            }
                        } else {
                            flyToLocation.set(null);
                        }
                    }
                }).observeCurrentValue());

                removablesList.add(
                        flyToDrawable.location().bind(flyToLocation)
                );

                flyToDrawable.drawable().set(ContextCompat.getDrawable(view().getContext(), R.drawable.btn_fly_to));
                droneLookAtTargetDrawable.drawable().set(ContextCompat.getDrawable(view().getContext(), R.drawable.ic_cam_target));

                removablesList.add(droneLookAtTargetDrawable.location().bind(controller.lookAtLocation()));

                removablesList.add(controller.connectivity().observe(new Observer<DroneConnectivity>() {
                    @Override
                    public void observe(DroneConnectivity oldValue, DroneConnectivity newValue, Observation<DroneConnectivity> observation) {

                        DroneIconTypes newIcon = calcDrawableFromDroneController(controller);

                        if (newIcon != droneStateCurrentIcon.value()) {
                            droneStateFromDroneController.set(fromDroneIconType(newIcon));
                            droneStateCurrentIcon.set(newIcon);
                        }
                    }
                }));

                removablesList.add(controller.telemetry().observe(new Observer<Telemetry>() {
                    @Override
                    public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {

                        DroneIconTypes newIcon = calcDrawableFromDroneController(controller);

                        if (newIcon != droneStateCurrentIcon.value()) {
                            droneStateFromDroneController.set(fromDroneIconType(newIcon));
                            droneStateCurrentIcon.set(newIcon);
                        }
                    }
                }));

                removablesList.add(controller.flying().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {

                        DroneIconTypes newIcon = calcDrawableFromDroneController(controller);

                        if (newIcon != droneStateCurrentIcon.value()) {
                            droneStateFromDroneController.set(fromDroneIconType(newIcon));
                            droneStateCurrentIcon.set(newIcon);
                        }
                    }
                }).observeCurrentValue());

                removablesList.add(controller.flightTasks().tasksBlockers().observe(new CollectionObserver<FlightTaskBlockerType>() {
                    @Override
                    public void added(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
                        DroneIconTypes newIcon = calcDrawableFromDroneController(controller);

                        if (newIcon != droneStateCurrentIcon.value()) {
                            droneStateFromDroneController.set(fromDroneIconType(newIcon));
                            droneStateCurrentIcon.set(newIcon);
                        }
                    }

                    @Override
                    public void removed(FlightTaskBlockerType value, Observation<FlightTaskBlockerType> observation) {
                        DroneIconTypes newIcon = calcDrawableFromDroneController(controller);

                        if (newIcon != droneStateCurrentIcon.value()) {
                            droneStateFromDroneController.set(fromDroneIconType(newIcon));
                            droneStateCurrentIcon.set(newIcon);
                        }
                    }
                }));


                removablesList.add(flightLimitationCircle.center().bind(controller.droneHome().homeLocation()));
                ObservableBoolean flightLimitationCircleVisibility =
                        controller.droneHome().maxDistanceFromHome().notNull().
                                and(controller.droneHome().homeLocation().notNull().
                                        and(controller.droneHome().limitationActive().withDefault(false)));
                removablesList.add(flightLimitationCircle.visible().bind(flightLimitationCircleVisibility));
                removablesList.add(flightLimitationCircle.radius().bind(controller.droneHome().maxDistanceFromHome()));

                removablesList.add(droneViewLine.getStartPoint().bind(droneDrawable.location()));
                removablesList.add(droneViewLine.getEndPoint().bind(droneLookAtTargetDrawable.location()));

                removablesList.add(droneDrawable.drawable().bind(
                        droneStateFromDroneController
                                .transform(new AddComponentName(view().getResources(), tabsModel.resolve(controller.uuid()), 42))
                ));

                removablesList.add(homeDrawable.drawable().bind(controller.droneHome().homeLocation().
                        equalsTo(null).
                        toggle(ContextCompat.getDrawable(view().getContext(), R.drawable.nothing),
                                ContextCompat.getDrawable(view().getContext(), R.drawable.home_point))
                        .transform(new AddComponentName(view().getResources(), tabsModel.resolve(controller.uuid()), 42))));

                removablesList.add(droneDrawable.location().bind(controller.telemetry().transform(new TelemetryLocation())));

                removablesList.add(homeDrawable.location().bind(controller.droneHome().homeLocation()));

                mapViewModel.addMapItem(lookAtDrawable);
                mapViewModel.addMapItem(lookAtMissionLine);
                mapViewModel.addMapItem(flyToDrawable);
                mapViewModel.addMapItem(flyToLine);
                mapViewModel.addMapItem(droneDrawable);
                mapViewModel.addMapItem(homeDrawable);
                mapViewModel.addMapItem(flightLimitationCircle);
                mapViewModel.addMapItem(droneViewLine);
                mapViewModel.addMapItem(droneLookAtTargetDrawable);

                removablesList.add(new Removable() {
                    @Override
                    public void remove() {


                        mapViewModel.removeMapItem(lookAtDrawable);
                        mapViewModel.removeMapItem(lookAtMissionLine);
                        mapViewModel.removeMapItem(flyToDrawable);
                        mapViewModel.removeMapItem(flyToLine);
                        mapViewModel.removeMapItem(droneDrawable);
                        mapViewModel.removeMapItem(homeDrawable);
                        mapViewModel.removeMapItem(flightLimitationCircle);
                        mapViewModel.removeMapItem(droneViewLine);
                        mapViewModel.removeMapItem(droneLookAtTargetDrawable);
                    }
                });

                bindings.put(controller.uuid(), new RemovableCollection(removablesList));
            }

            @Override
            public void removed(DroneTabModel value, Observation<DroneTabModel> observation) {
                Removable removable = bindings.remove(value.getDroneController().uuid());
                if (removable != null) {
                    removable.remove();
                }
            }
        };

        tabsModel.items().observe(new CollectionObserver<DroneTabModel>(){
            @Override
            public void added(DroneTabModel value, Observation<DroneTabModel> observation) {
                if(value != null){
                    controllersObserver.added(value,observation);
                }
            }

            @Override
            public void removed(DroneTabModel value, Observation<DroneTabModel> observation) {
                controllersObserver.removed(value,observation);
            }
        });

        for (DroneTabModel tabModel: tabsModel.items()) {
            controllersObserver.added(tabModel, null);
        }
    }

    public MissionPlannerMenuViewModel getMissionPlannerMenuViewModel() {
        return missionPlannerMenuViewModel;
    }

    public SpecialFunctionMenu getSpecialFunctionMenu() {
        return specialFunctionMenu;
    }

    public MissionExecutionMenuViewModel getMissionExecutionMenuViewModel() {
        return missionExecutionMenuViewModel;
    }

    public LittleMessageViewModel getLittleMessageViewModel() {
        return littleMessageViewModel;
    }

    public VideoStreamModel getVideoStreamModel() {
        return videoStreamModel;
    }

    public FlightComponentEditViewModel getPathPlannerViewModel() {
        return pathPlannerViewModel;
    }

    public MCViewModel getMcViewModel() {
        return mcViewModel;
    }

    public VideoLayoutModel getVideoLayoutModel() {
        return videoLayoutModel;
    }
}
