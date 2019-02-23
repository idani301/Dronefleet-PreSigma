package eyesatop.unit.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ActionMenuItem extends FrameLayout {
    public ActionMenuItem(Context context) {
        super(context);
    }

    public ActionMenuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionMenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ActionMenuItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
