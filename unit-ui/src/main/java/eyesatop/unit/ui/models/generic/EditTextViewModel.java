package eyesatop.unit.ui.models.generic;

import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

/**
 * Created by einav on 20/07/2017.
 */

public class EditTextViewModel extends AbstractViewModel<EditText> {

    public interface EditTextOnDone{
        void onDoneCallback(String textWritten);
    }

    public interface EditTextTextWritten{
        void onTextWritten(String textWritten);
    }

    private final Property<Integer> inputType = new Property<>();
    private final Property<String> text = new Property<>();
    private final Property<EditTextOnDone> onDoneListener = new Property<>();
    private final Property<EditTextTextWritten> onTextWrittenListener = new Property<>();

    public EditTextViewModel(final EditText view) {

        super(view);

        inputType.withDefault(InputType.TYPE_CLASS_NUMBER).observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                view.setInputType(newValue);
            }
        },UI_EXECUTOR);

        text.withDefault("").observe(new Observer<String>() {
            @Override
            public void observe(String oldValue, String newValue, Observation<String> observation) {
                view.setText(newValue);
            }
        },UI_EXECUTOR);

        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    EditTextOnDone listener = onDoneListener.value();
                    if(listener != null){
                        listener.onDoneCallback(v.getText().toString());
                    }
                }

                EditTextTextWritten textListener = onTextWrittenListener.value();
                if(textListener != null){
                    textListener.onTextWritten(v.getText().toString());
                }
                return false;
            }
        });
    }

    public Property<Integer> inputType() {
        return inputType;
    }

    public Property<String> text() {
        return text;
    }

    public void setOnDoneListener(EditTextOnDone newListener){
        onDoneListener.set(newListener);
    }

    public void setOnTextListener(EditTextTextWritten newListener){
        onTextWrittenListener.set(newListener);
    }

}
