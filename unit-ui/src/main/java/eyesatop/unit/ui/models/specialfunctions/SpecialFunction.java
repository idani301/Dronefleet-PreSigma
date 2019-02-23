package eyesatop.unit.ui.models.specialfunctions;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import eyesatop.unit.ui.models.tabs.SpecialFunctionType;
import eyesatop.util.model.ObservableBoolean;

/**
 * Created by einav on 27/06/2017.
 */
public abstract class SpecialFunction {

    protected final Activity activity;

    protected SpecialFunction(Activity activity) {
        this.activity = activity;
    }

    public abstract Drawable getFunctionDrawable();
    public abstract void actionMenuButtonPressed();

    public abstract SpecialFunctionType functionType();
}
