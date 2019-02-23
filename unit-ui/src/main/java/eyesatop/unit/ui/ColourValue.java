package eyesatop.unit.ui;

import android.content.Context;
import android.support.annotation.ColorInt;

public class ColourValue implements Colour {

    private final int value;

    public ColourValue(@ColorInt int value) {
        this.value = value;
    }

    @Override
    public int resolve(Context context) {
        return value;
    }
}
