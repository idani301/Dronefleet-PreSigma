package eyesatop.unit.ui.models;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.text.DecimalFormat;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.MediaStorage;
import eyesatop.controller.beans.StreamState;
import eyesatop.controller.tasks.DroneTask;
import eyesatop.controller.tasks.home.HomeTaskType;
import eyesatop.unit.ui.Colour;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.activities.EyesatopAppConfiguration;
import eyesatop.unit.ui.models.actionmenus.ActionMenuItemModel;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.models.generic.SpinnerViewModel;
import eyesatop.unit.ui.models.generic.TextViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.unit.ui.models.massage.MessageViewModel;
import eyesatop.unit.ui.models.tabs.DroneTabModel;
import eyesatop.unit.ui.models.tabs.DroneTabsModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.geo.DistanceUnitType;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

import static eyesatop.unit.ui.models.generic.ViewModel.UI_EXECUTOR;

/**
 * Created by einav on 20/07/2017.
 */

public class MCViewModel {

    public enum MCStateType{
        FLIGHT_LIMITATION("Flight Limitation"),
        LIVE_STREAM("Live Stream"),
        SENSORS("Sensors"),
        BATTERY("Battery"),
        RELASE_NOTES("Release Notes"),
        SD_SETTING("SD Card Settings"),
        APP_SETTINGS("Application Settings");

        private final String name;

        MCStateType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final Activity activity;


    private final DecimalFormat formatter = new DecimalFormat("00");

    private final ImageViewModel mcMenuButton;

    private final Property<MCStateType> currentMenuState = new Property<>(MCStateType.FLIGHT_LIMITATION);
    private final EditText returnToHomeAltitudeEditText;
    private final EditText maxAltEditText;
    private final EditText maxDistanceEditText;
    private final Switch limitationActiveSwitch;

    private final MessageViewModel messageViewModel;

    private final SwitchViewModel unitMeasureTypeSwitch;

    private final ImageViewModel closeButton;
    private final ImageViewModel releaseNotesButton ,flightLimitationBtn;
    private final ViewModel settingsContainer;

    private final ViewModel appSettingContainer;
    private final EditText takeoffAltitudeEditText;

    private final ViewModel sdCardSettingsContainer;
    private TextViewModel tvReleaseNotes, mcSettingHeaderTxt;
    private final ImageViewModel appSettingButton;
    private final ImageViewModel sdCardSettingButton;
    private final ImageViewModel sensorsSettingButton;
    private final ImageViewModel batterySettingButton;

    private final TextViewModel liveStreamAvailableUrls;
    private final ImageViewModel liveStreamSettingButton;
    private final TextViewModel liveStreamIsStreaming;
    private final ActionMenuItemModel liveStreamStartButton;
    private final ActionMenuItemModel liveStreamStopButton;
    private final EditText liveStreamURL;
    private final TextViewModel liveStreamCurrentURLTextViewModel;
    private final TextViewModel liveStreamVideoBitRate;
    private final ViewModel liveStreamContainer;

    private final ImageViewModel formatSDCardButton;
    private final TextViewModel sdCardSpaceRemainingText;
    private final TextViewModel sdCardRecordTimeRemainingText;

    private final DroneTabsModel tabsModel;

    private DroneTabModel currentTab = null;

    private final View visibilityView;

    public void hideButton(MCStateType type,boolean isAlwaysGone){
        switch (type){

            case FLIGHT_LIMITATION:
                flightLimitationBtn.setAlwaysGone(isAlwaysGone);
                break;
            case SENSORS:
                sensorsSettingButton.setAlwaysGone(isAlwaysGone);
                break;
            case BATTERY:
                batterySettingButton.setAlwaysGone(isAlwaysGone);
                break;
            case RELASE_NOTES:
                releaseNotesButton.setAlwaysGone(isAlwaysGone);
                break;
            case SD_SETTING:
                sdCardSettingButton.setAlwaysGone(isAlwaysGone);
                break;
            case APP_SETTINGS:
                appSettingButton.setAlwaysGone(isAlwaysGone);
                break;
        }
    }

    public MCViewModel(Activity activity, DroneTabsModel tabsModel,MessageViewModel messageViewModel) {
        this.activity = activity;

        mcMenuButton = new ImageViewModel((ImageView) activity.findViewById(R.id.mc_menu_button));

        this.tabsModel = tabsModel;
        this.returnToHomeAltitudeEditText = (EditText) activity.findViewById(R.id.editTextReturnToHomeAltitude);
        this.maxAltEditText = (EditText) activity.findViewById(R.id.editTextMaxAlt);
        this.maxDistanceEditText = (EditText) activity.findViewById(R.id.editTextMaxDistanceFlight);
        this.limitationActiveSwitch = (Switch) activity.findViewById(R.id.switchIsDistanceLimitOn);
        this.visibilityView = activity.findViewById(R.id.includeMcSettingMenu);
        this.closeButton = new ImageViewModel((ImageView) activity.findViewById(R.id.mc_menu_close_button));
        this.flightLimitationBtn = new ImageViewModel((ImageView) activity.findViewById(R.id.flightLimitationBtn));
        this.releaseNotesButton = new ImageViewModel((ImageView) activity.findViewById(R.id.imageViewReleaseNotes));

        this.messageViewModel = messageViewModel;
        this.unitMeasureTypeSwitch = new SwitchViewModel((Switch) activity.findViewById(R.id.switchDistanceUnits));

        this.liveStreamSettingButton = new ImageViewModel((ImageView) activity.findViewById(R.id.mcSettingLiveStreamButton));
        this.liveStreamIsStreaming = new TextViewModel((TextView) activity.findViewById(R.id.liveStreamIsStreamingText));
        this.liveStreamContainer = new ViewModel(activity.findViewById(R.id.liveStreamContainer));
        this.liveStreamStartButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.liveStreamStartStreamButton));
        liveStreamStopButton = new ActionMenuItemModel((ImageView) activity.findViewById(R.id.liveStreamStopStreamButton));
        liveStreamCurrentURLTextViewModel = new TextViewModel((TextView) activity.findViewById(R.id.liveStreamCurrentURLTextView));
        liveStreamVideoBitRate = new TextViewModel((TextView) activity.findViewById(R.id.liveStreamVideoBitRateText));
        liveStreamURL = activity.findViewById(R.id.urlEditText);
        liveStreamAvailableUrls = new TextViewModel((TextView) activity.findViewById(R.id.liveStreamUrls));

        this.appSettingButton = new ImageViewModel((ImageView) activity.findViewById(R.id.imageViewSettings));
        this.appSettingContainer = new ViewModel(activity.findViewById(R.id.generalSettingsContainer));
        this.takeoffAltitudeEditText = (EditText) activity.findViewById(R.id.takeOffAltitudeEditText);

        this.settingsContainer = new ViewModel((View) activity.findViewById(R.id.settingsContainer));
        this.tvReleaseNotes = new TextViewModel((TextView) activity.findViewById(R.id.tvReleaseNotes));
        this.mcSettingHeaderTxt = new TextViewModel((TextView) activity.findViewById(R.id.mcSettingHeaderTxt));
        this.sdCardSettingsContainer = new ViewModel(activity.findViewById(R.id.sdCardContainer));

        this.sdCardSettingButton= new ImageViewModel((ImageView)activity.findViewById(R.id.sdCardSettingButton));

        this.sensorsSettingButton= new ImageViewModel((ImageView) activity.findViewById(R.id.mcSettingSensorsButton));
        this.batterySettingButton = new ImageViewModel((ImageView) activity.findViewById(R.id.mcSettingBatteryButton));

        this.formatSDCardButton = new ImageViewModel((ImageView) activity.findViewById(R.id.delete));
        this.sdCardSpaceRemainingText = new TextViewModel((TextView) activity.findViewById(R.id.tvSdCardSpaceRemaining));
        this.sdCardRecordTimeRemainingText = new TextViewModel((TextView) activity.findViewById(R.id.tvSDCardRecordTimeRemaining));

        maxAltEditText.setClickable(true);
        maxAltEditText.setFocusable(true);

        addSelectedTabListener(tabsModel);
        addSingleTapListener();

        mcSettingHeaderTxt.text().bind(currentMenuState.transform(new Function<MCStateType, String>() {
            @Override
            public String apply(MCStateType input) {
                return input.getName();
            }
        }));

        takeoffAltitudeEditText.setText(EyesatopAppConfiguration.getInstance().getTakeoffAltitude().withDefault(50).value() + "");

        takeoffAltitudeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        int takeoffAltitude = Integer.parseInt(textView.getText().toString());
                        EyesatopAppConfiguration.getInstance().getTakeoffAltitude().set(takeoffAltitude);
                    } catch (Exception e) {
                        takeoffAltitudeEditText.setText(EyesatopAppConfiguration.getInstance().getTakeoffAltitude().withDefault(50).value() + "");
                    }
                }
                return false;
            }
        });

        liveStreamAvailableUrls.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                String currentText = liveStreamAvailableUrls.text().value();

                if(currentText != null && !currentText.equals("")){
                    liveStreamURL.setText(currentText);
                }
                return false;
            }
        });

        unitMeasureTypeSwitch.setIsSwitchChecked(EyesatopAppConfiguration.getInstance().getAppMeasureType().value() == DistanceUnitType.METER);

        unitMeasureTypeSwitch.getIsSwitchChecked().observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                EyesatopAppConfiguration.getInstance().getAppMeasureType().set(newValue ? DistanceUnitType.METER : DistanceUnitType.FEET);
            }
        });

        liveStreamSettingButton.tint().bind(currentMenuState.equalsTo(MCStateType.LIVE_STREAM).toggle(Colour.WRAP_ID.apply(R.color.cyan),Colour.WRAP_ID.apply(R.color.foreground)));
        appSettingButton.tint().bind(currentMenuState.equalsTo(MCStateType.APP_SETTINGS).toggle(Colour.WRAP_ID.apply(R.color.cyan),Colour.WRAP_ID.apply(R.color.foreground)));
        releaseNotesButton.tint().bind(currentMenuState.equalsTo(MCStateType.RELASE_NOTES).toggle(Colour.WRAP_ID.apply(R.color.cyan),Colour.WRAP_ID.apply(R.color.foreground)));
        sdCardSettingButton.tint().bind(currentMenuState.equalsTo(MCStateType.SD_SETTING).toggle(Colour.WRAP_ID.apply(R.color.cyan),Colour.WRAP_ID.apply(R.color.foreground)));
        flightLimitationBtn.tint().bind(currentMenuState.equalsTo(MCStateType.FLIGHT_LIMITATION).toggle(Colour.WRAP_ID.apply(R.color.cyan),Colour.WRAP_ID.apply(R.color.foreground)));

        liveStreamContainer.visibility().bind(currentMenuState.equalsTo(MCStateType.LIVE_STREAM).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
        appSettingContainer.visibility().bind(currentMenuState.equalsTo(MCStateType.APP_SETTINGS).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
        tvReleaseNotes.visibility().bind(currentMenuState.equalsTo(MCStateType.RELASE_NOTES).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
        settingsContainer.visibility().bind(currentMenuState.equalsTo(MCStateType.FLIGHT_LIMITATION).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
        sdCardSettingsContainer.visibility().bind(currentMenuState.equalsTo(MCStateType.SD_SETTING).toggle(ViewModel.Visibility.VISIBLE, ViewModel.Visibility.GONE));
    }

    private void addSingleTapListener() {

        closeButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                currentTab.getIsMcMenuOpened().set(false);
                return false;
            }
        });

        sdCardSettingButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                currentMenuState.set(MCStateType.SD_SETTING);
                return false;
            }
        });

        liveStreamSettingButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                currentMenuState.set(MCStateType.LIVE_STREAM);
                return false;
            }
        });

        appSettingButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                currentMenuState.set(MCStateType.APP_SETTINGS);
                return false;
            }
        });

        releaseNotesButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                currentMenuState.set(MCStateType.RELASE_NOTES);
//                settingsContainer.setVisibility();
                //currentTab.getIsMcMenuOpened().set(false);
                return false;
            }
        });

        flightLimitationBtn.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {
                currentMenuState.set(MCStateType.FLIGHT_LIMITATION);
                return false;
            }
        });

        returnToHomeAltitudeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        double returnHomeAltitude = Integer.parseInt(v.getText().toString());
                        currentTab.getDroneTasks().setReturnHomeAltitude(returnHomeAltitude);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Double currentReturnHomeAltitude = currentTab.getDroneController().droneHome().returnHomeAltitude().value();
                    returnToHomeAltitudeEditText.setText(objectToString(currentReturnHomeAltitude));
                }

                return false;
            }
        });

        maxDistanceEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    try {
                        double maxLimitation = Integer.parseInt(maxDistanceEditText.getText().toString());
                        currentTab.getDroneTasks().setMaxDistance(maxLimitation);
                    } catch (Exception e) {
                    }

                    Double currentMaxDistance = currentTab.getDroneController().droneHome().maxDistanceFromHome().value();
                    maxDistanceEditText.setText(objectToString(currentMaxDistance));
                }

                return false;
            }
        });

        maxAltEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    try {
                        double maxLimitation = Integer.parseInt(maxAltEditText.getText().toString());
                        currentTab.getDroneTasks().setMaxAltitude(maxLimitation);
                    } catch (Exception e) {
                    }

                    Double currentMaxAltitude = currentTab.getDroneController().droneHome().maxAltitudeFromTakeOffLocation().value();
                    maxAltEditText.setText(objectToString(currentMaxAltitude));

                    return false;
                }
                return false;
            }
        });
        limitationActiveSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean newValue = limitationActiveSwitch.isChecked();
                currentTab.getDroneTasks().setLimitationActive(newValue);

                Boolean currentValue = currentTab.getDroneController().droneHome().limitationActive().value();
                limitationActiveSwitch.setChecked(currentValue == null ? false : currentValue);
            }
        });
    }

    private Removable bindings = Removable.STUB;

    public void addSelectedTabListener(DroneTabsModel tabsModel) {
        tabsModel.selected().observe(new Observer<DroneTabModel>() {
            @Override
            public void observe(DroneTabModel oldValue, DroneTabModel newValue, Observation<DroneTabModel> observation) {
                bindings.remove();
                if (newValue != null) {
                    bindings = bindToTab(newValue);
                } else {
                    UI_EXECUTOR.execute(new Runnable() {
                        @Override
                        public void run() {
                            visibilityView.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).observeCurrentValue();
    }

    private Removable bindToTab(final DroneTabModel tabModel) {

        currentTab = tabModel;

        mcMenuButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if (currentTab != null) {
                    currentTab.getIsMcMenuOpened().set(!currentTab.getIsMcMenuOpened().value());
                }
                return false;
            }
        });

        final DroneController controller = tabModel.getDroneController();

        liveStreamStartButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(!liveStreamStartButton.clickable().value()){
                    return false;
                }

                String url = liveStreamURL.getText().toString();
                tabsModel.getItem(controller).getDroneTasks().startLiveStream(url);
                return false;
            }
        });

        liveStreamStopButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(!liveStreamStopButton.clickable().value()){
                    return false;
                }

                tabsModel.getItem(controller).getDroneTasks().stopLiveStream();
                return false;
            }
        });

        formatSDCardButton.singleTap().set(new Function<MotionEvent, Boolean>() {
            @Override
            public Boolean apply(MotionEvent input) {

                if(controller == null){
                    return false;
                }

                String controllerName = tabsModel.resolve(controller.uuid());
                Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.format_sd_card_orange);
                String headerText = "Format SD Card";
                String bodyText = "Aircraft " + controllerName + " Will lose all data on it's SD Card. Continue ?";
                MessageViewModel.MessageViewModelListener listener = new MessageViewModel.MessageViewModelListener() {
                    @Override
                    public void onOkButtonPressed() {
                        tabsModel.getItem(controller).getDroneTasks().formatSDCard();
                    }

                    @Override
                    public void onCancelButtonPressed() {

                    }
                };

                messageViewModel.addGeneralMessage(headerText,bodyText,drawable,listener);
                return false;
            }
        });

        return new RemovableCollection(

                liveStreamStartButton.clickable().bind(controller.camera().currentTask().notNull().not()),
                liveStreamStopButton.clickable().bind(controller.camera().currentTask().notNull().not()),

                controller.camera().streamState().observe(new Observer<StreamState>() {
                    @Override
                    public void observe(StreamState oldValue, StreamState newValue, Observation<StreamState> observation) {
                        if(newValue == null){
                            liveStreamCurrentURLTextViewModel.text().set("N/A");
                            liveStreamVideoBitRate.text().set("N/A");
                            liveStreamIsStreaming.text().set("N/A");
                            return;
                        }

                        liveStreamCurrentURLTextViewModel.text().set(newValue.getStreamURL() == null ? "N/A" : newValue.getStreamURL());
                        liveStreamVideoBitRate.text().set("" + newValue.getVideoBitRate());
                        liveStreamIsStreaming.text().set(newValue.isStreaming() ? "Yes" : "No");
                    }
                }).observeCurrentValue(),

                controller.camera().mediaStorage().observe(new Observer<MediaStorage>() {
                    @Override
                    public void observe(MediaStorage oldValue, MediaStorage newValue, Observation<MediaStorage> observation) {

                        if(newValue != null){

                            int numOfHours = newValue.getRemainingTime() / 3600;
                            int numOfMinutes = (newValue.getRemainingTime() - 3600*numOfHours)/60;
                            int numOfSeconds = newValue.getRemainingTime() - 3600*numOfHours - 60 * numOfMinutes;

                            String finalRecordText = formatter.format(numOfHours) + ":" +
                                    formatter.format(numOfMinutes) + ":" +
                                    formatter.format(numOfSeconds);


                            int sdCardPercentage = (newValue.getTotalSpaceInBytes() == 0) ? 0 : Math.round(100*newValue.getRemainingSpaceInBytes()/newValue.getTotalSpaceInBytes());

                            sdCardRecordTimeRemainingText.text().set(finalRecordText);
                            sdCardSpaceRemainingText.text().set(newValue.getRemainingSpaceInBytes() +
                                    "MB" + "/" +
                                    newValue.getTotalSpaceInBytes() + "MB" + " (" + sdCardPercentage + "%)");
                        }
                        else{
                            sdCardRecordTimeRemainingText.text().set("N/A");
                            sdCardSpaceRemainingText.text().set("N/A");
                        }
                    }
                }).observeCurrentValue(),

                controller.droneHome().currentTask().observe(new Observer<DroneTask<HomeTaskType>>() {
                    @Override
                    public void observe(DroneTask<HomeTaskType> oldValue, DroneTask<HomeTaskType> newValue, Observation<DroneTask<HomeTaskType>> observation) {
                        setEnabled(newValue == null);
                    }
                }, UI_EXECUTOR).observeCurrentValue(),

//                controller.droneHome().currentTask().observe(new Observer<DroneTask<HomeTaskType>>() {
//                    @Override
//                    public void observe(DroneTask<HomeTaskType> oldValue, DroneTask<HomeTaskType> newValue, Observation<DroneTask<HomeTaskType>> observation) {
//                        if(newValue != null && !newValue.status().value().isTaskDone()){
//
//                            maxAltEditText.setClickable(false);
//                            maxAltEditText.setFocusable(false);
//                            maxDistanceEditText.setClickable(false);
//                            maxDistanceEditText.setFocusable(false);
//                            limitationActiveSwitch.setClickable(false);
//                            limitationActiveSwitch.setFocusable(false);
//                        }
//                        else{
//
//                            maxAltEditText.setClickable(true);
//                            maxAltEditText.setFocusable(true);
//                            maxDistanceEditText.setClickable(true);
//                            maxDistanceEditText.setFocusable(true);
//                            limitationActiveSwitch.setClickable(true);
//                            limitationActiveSwitch.setFocusable(true);
//                        }
//                    }
//                },UI_EXECUTOR).observeCurrentValue(),

                controller.droneHome().maxAltitudeFromTakeOffLocation().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        maxAltEditText.setText(objectToString(newValue));
                    }
                }, UI_EXECUTOR).observeCurrentValue(),
                controller.droneHome().maxDistanceFromHome().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        maxDistanceEditText.setText(objectToString(newValue));
                    }
                }, UI_EXECUTOR).observeCurrentValue(),
                controller.droneHome().limitationActive().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        limitationActiveSwitch.setChecked(newValue == null ? false : newValue);
                    }
                }, UI_EXECUTOR).observeCurrentValue(),
                controller.droneHome().returnHomeAltitude().observe(new Observer<Double>() {
                    @Override
                    public void observe(Double oldValue, Double newValue, Observation<Double> observation) {
                        returnToHomeAltitudeEditText.setText(objectToString(newValue));
                    }
                }, UI_EXECUTOR).observeCurrentValue(),
                tabModel.getIsMcMenuOpened().observe(new Observer<Boolean>() {
                    @Override
                    public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                        visibilityView.setVisibility(newValue ? View.VISIBLE : View.GONE);
                    }
                }).observeCurrentValue()
        );
    }

    private void setEnabled(boolean enabled) {

        limitationActiveSwitch.setClickable(enabled);
        limitationActiveSwitch.setFocusable(enabled);
    }

    private String objectToString(Double input) {
        if (input == null) {
            return "";
        } else {
            return ((int) input.doubleValue()) + "";
        }
    }

    public TextViewModel getLiveStreamAvailableUrls() {
        return liveStreamAvailableUrls;
    }

    public EditText getLiveStreamURL() {
        return liveStreamURL;
    }

    public void setReleaseNotes(String releaseNotes){
        tvReleaseNotes.text().set(releaseNotes);
    }
}
