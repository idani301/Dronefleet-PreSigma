package eyesatop.unit.ui.models.generic;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import eyesatop.unit.ui.models.UiModel;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.ObservableList;
import eyesatop.util.model.Observation;
import eyesatop.util.observablelistidan.ListObserver;
import eyesatop.util.observablelistidan.ObservableListIdan;

public class ViewGroupModel extends AbstractViewModel<ViewGroup> {

    private final ObservableListIdan<UiModel> children = new ObservableListIdan<>();

    public ViewGroupModel(final ViewGroup view) {
        super(view);
        children.observe(
            new ListObserver<UiModel>() {
                @Override
                public void added(UiModel value, int index) {
                    view.addView(value.view());
                }

                @Override
                public void removed(UiModel value, int oldIndex) {
                    view.removeView(value.view());
                }

                @Override
                public void replaced(UiModel oldValue, UiModel newValue, int index) {
                    int i = view.indexOfChild(oldValue.view());
                    if (i != -1) {
                        view.removeViewAt(i);
                    } else {
                        i = view.getChildCount();
                    }
                    view.addView(newValue.view(), i);
                }

                @Override
                public void swapped(UiModel firstValue, UiModel secondValue, int firstValueOldIndex, int secondValueOldIndex) {
                    view.removeView(firstValue.view());
                    view.removeView(secondValue.view());

                    if(firstValueOldIndex < secondValueOldIndex){
                        view.addView(secondValue.view(),firstValueOldIndex);
                        view.addView(firstValue.view(),secondValueOldIndex);
                    }
                    else{
                        view.addView(firstValue.view(),secondValueOldIndex);
                        view.addView(secondValue.view(),firstValueOldIndex);
                    }
                }
            }
        , UI_EXECUTOR);
    }

    public ObservableListIdan<UiModel> children() {
        return children;
    }

    public View inflate(@LayoutRes int layoutId) {
        return findActivity(view()).getLayoutInflater().inflate(layoutId, view(), false);
    }
}
