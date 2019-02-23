package eyesatop.pipeapp;

import android.app.Activity;
import android.widget.TextView;

import eyesatop.controller.DroneController;
import eyesatop.controller.beans.BatteryState;
import eyesatop.ui_generic.viewmodels.TextViewModel;
import eyesatop.util.Function;
import eyesatop.util.geo.DistanceUnitType;
import eyesatop.util.geo.Location;
import eyesatop.util.geo.Telemetry;
import eyesatop.util.model.ObservableValue;

public class PipeStatusModel {

    private final Activity activity;
    private final DroneController controller;
    private final TextViewModel batteryTextView;
    private final TextViewModel aglTextView;
    private final TextViewModel atlTextView;
    private final TextViewModel remoteIPTextView;
    private final TextViewModel droneIDTextView;

    public PipeStatusModel(Activity activity, DroneController controller, ObservableValue<String> remoteIP,final int droneID) {
        this.activity = activity;
        this.controller = controller;

        batteryTextView = new TextViewModel((TextView) activity.findViewById(R.id.lightBatteryTextView));
        aglTextView = new TextViewModel((TextView) activity.findViewById(R.id.lightAGLTextView));
        atlTextView = new TextViewModel((TextView) activity.findViewById(R.id.lightATLTextView));
        remoteIPTextView = new TextViewModel((TextView) activity.findViewById(R.id.remoteIPTextView));
        droneIDTextView = new TextViewModel((TextView) activity.findViewById(R.id.droneIDText));
        droneIDTextView.text().set("" + droneID);

        batteryTextView.text().bind(controller.droneBattery().transform(new Function<BatteryState, String>() {
            @Override
            public String apply(BatteryState input) {

                if(input == null){
                    return "N/A";
                }

                return BatteryState.getPercent(input) + "%";
            }
        },false));

        aglTextView.text().bind(controller.aboveGroundAltitude().transform(new Function<Double, String>() {
            @Override
            public String apply(Double input) {

                if(input == null){
                    return "N/A";
                }

                return DistanceUnitType.formatNumber(DistanceUnitType.METER, 1, input);
            }
        },false));

        atlTextView.text().bind(controller.telemetry().transform(new Function<Telemetry, String>() {
            @Override
            public String apply(Telemetry input) {

                Location location = Telemetry.telemetryToLocation(input);
                if(location == null){
                    return "N/A";
                }

                return DistanceUnitType.formatNumber(DistanceUnitType.METER, 1, location.getAltitude());
            }
        },false));

        remoteIPTextView.text().bind(remoteIP.withDefault("N/A"));
    }
}

