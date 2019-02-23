package eyesatop.unit.ui.functions;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import eyesatop.controller.beans.BatteryState;
import eyesatop.unit.ui.R;
import eyesatop.util.Function;

/**
 * Created by Idan on 01/01/2018.
 */

public class RCBatteryStateToDrawable implements Function<BatteryState,Drawable> {

    private final Activity activity;

    public RCBatteryStateToDrawable(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Drawable apply(BatteryState input) {

        Double percent = BatteryState.REMAINING_PERCENT.apply(input);

        if(percent == null){
            return ContextCompat.getDrawable(activity, R.drawable.rc_power_icon_red);
        }

        if(percent >= BatteryState.BATTERY_HIGH_PIVOT){
            return ContextCompat.getDrawable(activity,R.drawable.rc_power_icon_green);
        } else if(percent >= BatteryState.BATTERY_MED_PIVOT){
            return ContextCompat.getDrawable(activity,R.drawable.rc_power_icon_orange);
        }
        else{
            return ContextCompat.getDrawable(activity,R.drawable.rc_power_icon_red);
        }
    }
}
