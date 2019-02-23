package eyesatop.ui_generic.viewmodels;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import eyesatop.ui_generic.R;
import eyesatop.util.model.ObservableValue;
import eyesatop.util.model.Property;

/**
 * Created by Idan on 11/04/2018.
 */

public class SpinnerViewModel extends AbstractViewModel<Spinner> {

    private final Property<Object> selectedItem = new Property<>();

    private final Object[] values;

    public SpinnerViewModel(final Spinner view, final Object[] values) {
        super(view);
        this.values = values;

        final ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(view.getContext(),R.layout.support_simple_spinner_dropdown_item);

        if(values == null){
            visibility().set(Visibility.GONE);
            return;
        }

        if(values.length > 0){
            selectedItem.set(values[0]);
        }

        for(Object spinnerOption : values){
            adapter.add(spinnerOption);
        }

        view.setAdapter(adapter);

        visibility().set(values.length > 0 ? Visibility.VISIBLE : Visibility.GONE);

        view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem.set(adapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                throw new IllegalStateException("Idan Exception - onNothing Selected reached");
            }
        });
    }

    public void setSelectedItem(Object value){
        if(values == null){
            throw new IllegalStateException("Idan - didn't expect someone to use set Selected item with no items.");
        }

        if(value == null){
            throw new IllegalStateException("set Selected item with null value.");
        }

        for(int i=0; i < values.length; i++){
            if(value.equals(values[i])){
                view().setSelection(i);
                selectedItem.set(value);
                return;
            }
        }
        throw new IllegalStateException("Couldn't find the value inside the objects array");
    }

    public void clear(){

        if(values != null){
            view().setSelection(0);
            selectedItem.set(values[0]);
        }
    }

    public ObservableValue<Object> selectedItem(){
        return selectedItem;
    }

}
