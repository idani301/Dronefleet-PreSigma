package eyesatop.unit.ui.models.tabs;

import android.view.View;

import java.util.UUID;

import eyesatop.unit.ui.ComponentNameResolver;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.util.geo.Location;
import eyesatop.util.model.ObservableValue;

/**
 * Created by einav on 19/04/2017.
 */

public class LandingPadTabsModel extends AbstractViewModel<View> implements ComponentNameResolver {

    protected LandingPadTabsModel(View view) {
        super(view);
    }

    @Override
    public String resolve(UUID uuid) {
        return null;
    }

    @Override
    public ObservableValue<Location> focusRequests() {
        return null;
    }
}
