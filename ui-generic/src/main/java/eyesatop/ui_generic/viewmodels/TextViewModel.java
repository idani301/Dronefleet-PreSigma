package eyesatop.ui_generic.viewmodels;

import android.view.KeyEvent;
import android.widget.TextView;

import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

public class TextViewModel extends AbstractViewModel<TextView> {

    private final Property<String> text = new Property<>();
    private final Property<Integer> textColor = new Property<>();
    private final Property<Float> textSize = new Property<>();
    private final Property<Integer> backgroundColor = new Property<>();
    private final Property<Integer> backgroundResource = new Property<>();
    private final Property<Integer> background = new Property<>();
    private final BooleanProperty textRunning = new BooleanProperty();


    public TextViewModel(final TextView view) {
        super(view);

        view.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });

        text.withDefault("").observe(new Observer<String>() {
            @Override
            public void observe(String oldValue, String newValue, Observation<String> observation) {
                view.setText(newValue);
            }
        },UI_EXECUTOR);

        textColor.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                view.setTextColor(newValue);
            }
        },UI_EXECUTOR);

        textSize.observe(new Observer<Float>() {
            @Override
            public void observe(Float oldValue, Float newValue, Observation<Float> observation) {
                view.setTextSize(newValue);
            }
        },UI_EXECUTOR);

        backgroundColor.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                view.setBackgroundColor(newValue);
//                GradientDrawable gradientDrawable = (GradientDrawable) view.getBackground().getCurrent();
//                gradientDrawable.setColor(newValue);
            }
        },UI_EXECUTOR);

        backgroundResource.withDefault(0).observe(new Observer<Integer>() {
                                                      @Override
                                                      public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                                                          view.setBackgroundResource(newValue);
                                                      }
                                                  },UI_EXECUTOR);

        textRunning.withDefault(false).observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                view.setSelected(newValue);
            }
        },UI_EXECUTOR);
    }

    public Property<Float> textSize() {
        return textSize;
    }

    public Property<Integer> textBackground() {
        return backgroundColor;
    }

    public Property<String> text() {
        return text;
    }

    public Property<Integer> textColor() {
        return textColor;
    }

    public Property<Integer> backgroundResource() {
        return backgroundResource;
    }

    public BooleanProperty textRunning() {
        return textRunning;
    }
}
