package eyesatop.ui_generic.viewmodels.beans;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class DrawableBackground implements Background {

    private final Drawable drawable;

    public DrawableBackground(Drawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public Drawable resolve(Context context) {
        return drawable;
    }
}
