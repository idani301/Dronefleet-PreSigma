package eyesatop.ui_generic.viewmodels.beans;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.util.TypedValue;

public class ColourId implements Colour {

    private final int id;

    public ColourId(@ColorRes int id) {
        this.id = id;
    }

    @Override
    public int resolve(Context context) {
        TypedValue value = new TypedValue();
        context.getResources().getValue(id, value, true);
        return value.data;
    }
}
