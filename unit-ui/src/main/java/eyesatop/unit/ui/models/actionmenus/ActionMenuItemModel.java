package eyesatop.unit.ui.models.actionmenus;

import android.widget.ImageView;

import eyesatop.unit.ui.ColourId;
import eyesatop.unit.ui.models.generic.ImageViewModel;
import eyesatop.unit.ui.R;
import eyesatop.util.model.BooleanProperty;

public class ActionMenuItemModel extends ImageViewModel {

//    private final BooleanProperty disabled = new BooleanProperty();

    public ActionMenuItemModel(ImageView view) {
        super(view);
        focusable().bind(clickable());
        tint().bind(clickable()
                .toggle(R.color.foreground, R.color.disabled)
                .transform(ColourId.WRAP_ID, UI_EXECUTOR));
    }

//    public BooleanProperty disabled() {
//        return disabled;
//    }
}
