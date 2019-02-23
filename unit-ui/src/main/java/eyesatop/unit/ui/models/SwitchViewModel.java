package eyesatop.unit.ui.models;

import android.widget.CompoundButton;
import android.widget.Switch;

import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.ObservableBoolean;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

/**
 * Created by Idan on 13/12/2017.
 */

public class SwitchViewModel extends AbstractViewModel<Switch> {

    private final BooleanProperty isSwitchChecked = new BooleanProperty();
    private final ObservableValue<Boolean> isSwitchCheckedObservable;

    public SwitchViewModel(Switch view) {
        super(view);
        isSwitchCheckedObservable = isSwitchChecked.withDefault(false);
        isSwitchChecked.set(view.isChecked());

        view.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSwitchChecked.set(isChecked);
            }
        });

        isSwitchChecked.observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {

                if(newValue == null){
                    newValue = false;
                }

                boolean isCurrnetlyChecked = view().isChecked();
                if(isCurrnetlyChecked != newValue){
                    view().setChecked(newValue);
                }
            }
        },UI_EXECUTOR).observeCurrentValue();
    }

    public void setIsSwitchChecked(boolean isSwitchChecked){
        this.isSwitchChecked.set(isSwitchChecked);
    }

    public ObservableValue<Boolean> getIsSwitchChecked(){
        return isSwitchCheckedObservable;
    }
}
