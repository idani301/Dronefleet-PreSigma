package eyesatop.unit.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import eyesatop.util.Function;

public class DrawableIdBackground implements Background {

    public static final Function<Integer,Background> WRAP = new Function<Integer, Background>() {
        @Override
        public Background apply(Integer input) {
            return new DrawableIdBackground(input);
        }
    };

    private final int id;

    public DrawableIdBackground(@DrawableRes int id) {
        this.id = id;
    }

    @Override
    public Drawable resolve(Context context) {
        TypedValue value = new TypedValue();
        context.getResources().getValue(id, value, true);
        return ContextCompat.getDrawable(context, id);
    }
}
