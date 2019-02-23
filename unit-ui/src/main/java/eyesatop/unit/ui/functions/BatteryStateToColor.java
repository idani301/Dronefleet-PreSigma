package eyesatop.unit.ui.functions;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import eyesatop.controller.beans.BatteryState;
import eyesatop.unit.ui.R;
import eyesatop.util.Function;

import static eyesatop.controller.beans.BatteryState.BATTERY_HIGH_PIVOT;
import static eyesatop.controller.beans.BatteryState.BATTERY_MED_PIVOT;

/**
 * Created by Idan on 01/01/2018.
 */

public class BatteryStateToColor implements Function<BatteryState,Integer> {

    private final Context context;

    public BatteryStateToColor(Context context) {
        this.context = context;
    }

    @Override
    public Integer apply(BatteryState input) {

        Double percent = BatteryState.REMAINING_PERCENT.apply(input);

        if(percent == null){
            return ContextCompat.getColor(context, R.color.white_indicator);
        }

        if(percent >= BATTERY_HIGH_PIVOT){
            return ContextCompat.getColor(context, R.color.green_indicator);
        } else if(percent >= BATTERY_MED_PIVOT){
            return ContextCompat.getColor(context, R.color.orange_indicator);
        }
        else {
            return ContextCompat.getColor(context, R.color.red_indicator);
        }
    }
}
