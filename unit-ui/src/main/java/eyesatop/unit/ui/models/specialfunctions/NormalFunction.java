package eyesatop.unit.ui.models.specialfunctions;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.tabs.SpecialFunctionType;
import eyesatop.util.model.BooleanProperty;

/**
 * Created by einav on 27/06/2017.
 */

public class NormalFunction extends SpecialFunction {

    private final BooleanProperty isFunctionScreenOpened;

    public NormalFunction(Activity activity,BooleanProperty isFunctionScreenOpened) {
        super(activity);
        this.isFunctionScreenOpened = isFunctionScreenOpened;
    }

    @Override
    public Drawable getFunctionDrawable() {
        return ContextCompat.getDrawable(activity,R.drawable.btn_fmode);
    }

    @Override
    public void actionMenuButtonPressed() {
        isFunctionScreenOpened.set(!isFunctionScreenOpened.value());
    }

    @Override
    public SpecialFunctionType functionType() {
        return SpecialFunctionType.NORMAL;
    }
}
