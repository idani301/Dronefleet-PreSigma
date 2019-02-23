package eyesatop.unit.ui.models.specialfunctions;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import eyesatop.unit.ui.Colour;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.util.model.BooleanProperty;

/**
 * Created by einav on 29/06/2017.
 */

public class SpecialFunctionImageViewModel extends ImageViewModel {

    private final BooleanProperty selected = new BooleanProperty();

    public SpecialFunctionImageViewModel(ImageView view) {
        super(view);
        tint().bind(selected.toggle(Colour.WRAP_ID.apply(R.color.cyan),Colour.WRAP_ID.apply(R.color.foreground)));
//        imageDrawable().bind(selected.toggle(selectedDrawable,nonSelectedDrawable));
    }

    public BooleanProperty selected() {
        return selected;
    }
}
