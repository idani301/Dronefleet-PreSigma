package eyesatop.unit.ui.models;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.BatteryState;
import eyesatop.controller.beans.ZoomInfo;
import eyesatop.unit.ui.Colour;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.activities.EyesatopAppConfiguration;
import eyesatop.unit.ui.functions.BatteryStateToColor;
import eyesatop.unit.ui.functions.RCBatteryStateToDrawable;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.tabs.DroneTab;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.DistanceUnitType;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

/**
 * Created by Idan on 12/12/2017.
 */

public class VideoLayoutModel {

    public enum VideoButtons {
        FULL_SCREEN;
    }

    private final Activity activity;

    private final BooleanProperty showAll = new BooleanProperty(true);

    private final BooleanProperty targetDistanceFromDroneVisible = new BooleanProperty(false);

    private final TextViewModel zoomLevelText;
    private final ImageViewModel fullScreenVideoButton;
    private final ImageViewModel targetImage;

    private final RelativeLayout videoContainer;

    private final ViewModel moreDetailsContainer;

    private final ObservableValue<Location> crosshairLocation;
    private final ImageViewModel screenTapImageView;

    private final ImageViewModel targetDistanceFromDroneButton;
    private final TextViewModel rcBatteryTextView;
    private final ImageViewModel rcBatteryImageView;
    private final TextViewModel targetDistanceFromDroneDistanceText;
    private final TextViewModel targetDistanceFromDroneAzText;
    private final TextViewModel droneHeight;

    private final TextViewModel batteryPercentText;
    private final ImageViewModel batteryPercentImageView;

    public void hideButton(VideoButtons type,boolean isAlwaysGone){
        switch (type){

            case FULL_SCREEN:
                fullScreenVideoButton.setAlwaysGone(isAlwaysGone);
                break;
        }
    }

    public VideoLayoutModel(final Activity activity, final BooleanProperty fullScreenMode, ObservableValue<Location> crosshairLocation) {

        this.activity = activity;
        this.crosshairLocation = crosshairLocation;

        fullScreenVideoButton = new ImageViewModel((ImageView) activity.findViewById(R.id.ivFullScreen));
        screenTapImageView = new ImageViewModel((ImageView) activity.findViewById(R.id.ivCrosshair));
        targetImage = new ImageViewModel((ImageView) activity.findViewById(R.id.ivTarget));
        rcBatteryTextView = new TextViewModel((TextView) activity.findViewById(R.id.tvRemoteBattery));
        rcBatteryImageView = new ImageViewModel((ImageView) activity.findViewById(R.id.ivRemoteBatteryVideo));

        zoomLevelText = new TextViewModel((TextView) activity.findViewById(R.id.tvZoomScale));
        moreDetailsContainer = new ViewModel(activity.findViewById(R.id.moreDetailsContainer));

        batteryPercentText = new TextViewModel((TextView) (activity.findViewById(R.id.battery_view_video)));
        batteryPercentImageView = new ImageViewModel((ImageView) activity.findViewById(R.id.droneBatteryVideoImageView));

        videoContainer = (RelativeLayout) activity.findViewById(R.id.videoLayout);

        targetImage.visibility().bind(fullScreenMode.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
        moreDetailsContainer.visibility().bind(fullScreenMode.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));

        droneHeight = new TextViewModel((TextView) (activity.findViewById(R.id.videoDroneHeight)));

        targetDistanceFromDroneDistanceText = new TextViewModel((TextView) activity.findViewById(R.id.tvDroneDistance));
        targetDistanceFromDroneAzText = new TextViewModel((TextView) activity.findViewById(R.id.tvDroneAzimuth));
        targetDistanceFromDroneButton = new ImageViewModel((ImageView) activity.findViewById(R.id.targetDistanceFromDroneButton));

        fullScreenVideoButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                fullScreenMode.set(!fullScreenMode.value());
                return false;
            }
        });
        fullScreenVideoButton.imageDrawable().bind(fullScreenMode.transform(new Function<Boolean, Drawable>() {
            @Override
            public Drawable apply(Boolean input) {
                if (input) {
                    return ContextCompat.getDrawable(activity, R.drawable.exit_full_screen_icon);
                } else {
                    return ContextCompat.getDrawable(activity, R.drawable.full_screen_icon);
                }
            }
        }));

        targetDistanceFromDroneButton.visibility().bind(fullScreenMode.toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
        targetDistanceFromDroneDistanceText.visibility().bind(fullScreenMode.and(targetDistanceFromDroneVisible).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
        targetDistanceFromDroneAzText.visibility().bind(fullScreenMode.and(targetDistanceFromDroneVisible).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
    }

    private void updateAboveGroundAltitude(DroneController controller) {
        Double newValue = controller.aboveGroundAltitude().value();
        if (newValue == null) {
            droneHeight.text().set("N/A");
        } else {
            droneHeight.text().set(DistanceUnitType.formatNumber(EyesatopAppConfiguration.getInstance().getAppMeasureType().value(), 1, newValue));
        }
    }

    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture fadeAwayFuture;

    public void setTouchCoordinates(float x, float y) {

        if (fadeAwayFuture != null) {
            fadeAwayFuture.cancel(false);
        }

        screenTapImageView.visibility().set(ViewModel.Visibility.VISIBLE);
        screenTapImageView.xPixel().set(x);
        screenTapImageView.yPixel().set(y);

        fadeAwayFuture = executorService.schedule(new Runnable() {
            @Override
            public void run() {
                screenTapImageView.visibility().set(ViewModel.Visibility.GONE);
            }
        }, 3, TimeUnit.SECONDS);
    }

    public Removable bindToTab(final DroneTab tabModel) {
        ArrayList<Removable> removableList = new ArrayList<>();

        final DroneController controller = tabModel == null ? null : tabModel.getDroneController();

        removableList.add(
                rcBatteryTextView.text().bind(controller.rcBattery().transform(BatteryState.REMAINING_PERCENT_STRING,false))
        );

        removableList.add(
                rcBatteryTextView.textColor().bind(controller.rcBattery().transform(new BatteryStateToColor(activity),false))
        );

        removableList.add(
                rcBatteryImageView.tint().bind(controller.rcBattery().notNull().toggle(null, Colour.WRAP_ID.apply(R.color.gray)))
        );

        removableList.add(
                rcBatteryImageView.imageDrawable().bind(controller.rcBattery().transform(new RCBatteryStateToDrawable(activity),false))
        );

        removableList.add(
                batteryPercentText.text().bind(controller.droneBattery().transform(BatteryState.REMAINING_PERCENT_STRING,false))
        );

        removableList.add(
                batteryPercentText.textColor().bind(controller.droneBattery().transform(new BatteryStateToColor(activity),false))
        );

        removableList.add(
                batteryPercentImageView.tint().bind(controller.droneBattery()
                        .transform(new BatteryStateToColor(activity),false).transform(Colour.WRAP_VALUE))
        );

        removableList.add(
                controller.telemetry().observe(new Observer<Telemetry>() {
                    @Override
                    public void observe(Telemetry oldValue, Telemetry newValue, Observation<Telemetry> observation) {
                        Location lookAtLocation = controller.lookAtLocation().value();
                        Double aboveSeaLevel = controller.aboveSeaAltitude().value();

                        if (newValue == null || newValue.location() == null || lookAtLocation == null || aboveSeaLevel == null) {
                            targetDistanceFromDroneDistanceText.text().set("N/A");
                            targetDistanceFromDroneAzText.text().set("N/A");
                            return;
                        }
                        double distance = newValue.location().altitude(controller.aboveSeaAltitude().value()).distance3D(lookAtLocation);
                        String distanceString = DistanceUnitType.formatNumber(EyesatopAppConfiguration.getInstance().getAppMeasureType().value(), 1, distance);
                        String azString = ((int) newValue.location().az(lookAtLocation)) + DroneStatusModel.DEGREE;
                        targetDistanceFromDroneDistanceText.text().set(distanceString);
                        targetDistanceFromDroneAzText.text().set(azString);
                    }
                }).observeCurrentValue()
        );

        removableList.add(controller.aboveGroundAltitude().observe(new Observer<Double>() {
            @Override
            public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                updateAboveGroundAltitude(controller);
            }
        }).observeCurrentValue());

        removableList.add(
                crosshairLocation.observe(new Observer<Location>() {
                    @Override
                    public void observe(Location oldValue, Location newValue, Observation<Location> observation) {
                        controller.telemetry().value();
                    }
                })
        );

        zoomLevelText.visibility().set(ViewModel.Visibility.VISIBLE);

//        removableList.add(
//                zoomLevelText.visibility().bind(showAll.and(tabModel.getDroneController().camera().isZoomSupported()).transform(new Function<Boolean, ViewModel.Visibility>() {
//                    @Override
//                    public ViewModel.Visibility apply(Boolean input) {
//                        return input ? ViewModel.Visibility.VISIBLE : ViewModel.Visibility.GONE;
//                    }
//                }))
//        );


        removableList.add(
                zoomLevelText.textColor().bind(tabModel.getDroneController().camera().zoomInfo().transform(new Function<ZoomInfo, Integer>() {
                    @Override
                    public Integer apply(ZoomInfo input) {

                        if(input == null || input.getDigitalZoomFactor() == 1){
                            return ContextCompat.getColor(activity, R.color.white_indicator);
                        }

                        return ContextCompat.getColor(activity, R.color.red_indicator);

                    }
                },false))
        );

        removableList.add(
                zoomLevelText.text().bind(tabModel.getDroneController().camera().zoomInfo().transform(new Function<ZoomInfo, String>() {
                    @Override
                    public String apply(ZoomInfo input) {

                        if(input == null){
                            return "N/A";
                        }

                        return "x" +  (input.getOpticalZoomFactor() * input.getDigitalZoomFactor());
                    }
                },false))
        );

//        removableList.add(
//                zoomLevelText.text().bind(
//                        tabModel.getDroneController().camera().zoomLevel().transform(new Function<Double, String>() {
//                            @Override
//                            public String apply(Double input) {
//
//                                if (input == null) {
//                                    return "N/A";
//                                }
//
//                                int intValue = input.intValue();
//
//                                if (input == intValue) {
//                                    return "x" + intValue;
//                                }
//
//                                return "x" + input.toString();
//                            }
//                        }).withDefault("N/A")
//                )
//        );

        targetDistanceFromDroneButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                targetDistanceFromDroneVisible.set(!targetDistanceFromDroneVisible.value());
                return false;
            }
        });

        zoomLevelText.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                Double currentZoomLevel = tabModel.getDroneController().camera().zoomLevel().value();
                if (currentZoomLevel == null) {
                    return false;
                }

                if (currentZoomLevel < 2) {
                    tabModel.getDroneTasks().setZoomLevel(2);
                } else {
                    tabModel.getDroneTasks().setZoomLevel(1);
                }

//
                return false;
            }
        });

        return new RemovableCollection(removableList);
    }

    public Removable bindToNULL() {
        showAll.set(true);
        zoomLevelText.visibility().set(ViewModel.Visibility.GONE);
        return Removable.STUB;
    }
}
