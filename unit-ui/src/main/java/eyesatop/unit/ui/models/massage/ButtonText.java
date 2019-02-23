package eyesatop.unit.ui.models.massage;

import android.graphics.Color;
import android.view.MotionEvent;

import eyesatop.util.Function;

/**
 * Created by Einav on 05/07/2017.
 */

public class ButtonText extends Text{

    private final Function<MotionEvent, Boolean> buttonAction;

    public ButtonText(String text, float textSize, int color, int backgroundColor, Function<MotionEvent, Boolean> buttonAction) {
        super(text, textSize, color, backgroundColor);
        this.buttonAction = buttonAction;
    }

    public ButtonText(String text, float textSize, int color, Function<MotionEvent, Boolean> buttonAction) {
        super(text, textSize, color, Color.TRANSPARENT);
        this.buttonAction = buttonAction;
    }

    public Function<MotionEvent, Boolean> getButtonAction() {
        return buttonAction;
    }


}
