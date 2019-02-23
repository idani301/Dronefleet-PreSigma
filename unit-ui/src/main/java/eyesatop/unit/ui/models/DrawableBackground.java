package eyesatop.unit.ui.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import eyesatop.unit.ui.Background;

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
