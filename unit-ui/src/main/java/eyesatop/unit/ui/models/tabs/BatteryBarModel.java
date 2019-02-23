package eyesatop.unit.ui.models.tabs;

import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import eyesatop.unit.ui.ColorBackground;
import eyesatop.unit.ui.ColorTransformation;
import eyesatop.unit.ui.Colour;
import eyesatop.unit.ui.R;
import eyesatop.unit.ui.models.generic.AbstractViewModel;
import eyesatop.unit.ui.models.generic.ViewModel;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.RemovableCollection;
import eyesatop.util.model.Property;

public class BatteryBarModel extends AbstractViewModel<View> {

    private final Property<Float> percent = new Property<>();

    private final ViewModel full;
    private final ViewModel empty;

    public BatteryBarModel(View view) {
        super(view);
        full = new ViewModel(view.findViewById(R.id.batteryFull));
        empty = new ViewModel(view.findViewById(R.id.batteryEmpty));

        full.layoutWeight()
                .bind(percent.withDefault(0f));

        full.background()
                .bind(percent.withDefault(0f)
                .transform(new ColorTransformation(
                        Color.RED,
                        Color.GREEN,
                        new AccelerateDecelerateInterpolator()))
                .transform(Colour.WRAP_VALUE)
                .transform(ColorBackground.WRAP));

        empty.layoutWeight()
                .bind(percent.withDefault(0f)
                        .transform(new Function<Float, Float>() {
                                @Override
                                public Float apply(Float input) {
                                    return 1f - input;
                                }
                        }));
    }

    public Property<Float> percent() {
        return percent;
    }
}
