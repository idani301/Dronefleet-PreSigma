package eyesatop.unit.ui.models.generic;

import android.support.annotation.IdRes;
import android.view.View;

public abstract class AbstractViewModel<T extends View> extends ViewModel {
    private final T view;

    protected AbstractViewModel(T view) {
        super(view);
        this.view = view;
    }

    @Override
    public T view() {
        return view;
    }

    protected <K extends View> K find(@IdRes int id) {
        // noinspection unchecked
        return (K)view().findViewById(id);
    }
}
