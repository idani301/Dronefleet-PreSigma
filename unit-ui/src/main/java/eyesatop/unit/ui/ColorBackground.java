package eyesatop.unit.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import eyesatop.util.Function;

public class ColorBackground implements Background {

    public static final Function<Colour,Background> WRAP = new Function<Colour, Background>() {
        @Override
        public Background apply(Colour input) {
            return new ColorBackground(input);
        }
    };

    private final Colour color;

    public ColorBackground(Colour color) {
        this.color = color;
    }

    @Override
    public Drawable resolve(Context context) {
        return new ColorDrawable(color.resolve(context));
    }
}
