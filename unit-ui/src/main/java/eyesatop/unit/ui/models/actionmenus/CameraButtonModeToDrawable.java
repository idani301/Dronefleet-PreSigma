package eyesatop.unit.ui.models.actionmenus;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import eyesatop.unit.ui.R;

/**
 * Created by einav on 19/07/2017.
 */

public class CameraButtonModeToDrawable implements eyesatop.util.Function<CameraButtonModes,Drawable> {

    private final Activity activity;

    public CameraButtonModeToDrawable(Activity activity) {
        this.activity = activity;
    }

    @Override
    public Drawable apply(CameraButtonModes input) {

        switch (input){

            case START_RECORD:
                return ContextCompat.getDrawable(activity, R.drawable.start_record_new);
            case STOP_RECORD:
                return ContextCompat.getDrawable(activity, R.drawable.stop_record_new);
            case START_RECORD_SD_FULL:
                return ContextCompat.getDrawable(activity, R.drawable.start_record_no_sd);
            case STILLS:
                return ContextCompat.getDrawable(activity, R.drawable.take_photo_new);
            case STILLS_SHOOTING_PHOTOS:
                return ContextCompat.getDrawable(activity, R.drawable.taking_photo);
            case STILLS_SD_FULL_DISABLED:
                return ContextCompat.getDrawable(activity, R.drawable.take_photo_no_sed);
            case STILLS_DISABLED:
                return ContextCompat.getDrawable(activity, R.drawable.take_photo_disable);
            case UNKNOWN:
                return ContextCompat.getDrawable(activity,R.drawable.take_photo_no_sed);
            default:
                return ContextCompat.getDrawable(activity,R.drawable.take_photo_no_sed);
        }

    }
}
