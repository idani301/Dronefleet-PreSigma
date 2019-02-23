package eyesatop.util.android.display;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Created by Idan on 05/10/2017.
 */

public class DisplayEyesatop {
    private static DisplayMetrics displayMetrics;

    public static void init(Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
    }

    public static DisplayMetrics getDisplayMetrics(){
        if(displayMetrics == null){
            throw new IllegalArgumentException("Must init first");
        }

        return displayMetrics;
    }

    public static double getScreenInches(){

        DisplayMetrics dm = getDisplayMetrics();

        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        return Math.sqrt(x+y);
    }

    public static boolean isTablet(){
        return getScreenInches() >= 6;
    }
}
