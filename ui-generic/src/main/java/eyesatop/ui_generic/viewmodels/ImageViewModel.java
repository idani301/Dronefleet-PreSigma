package eyesatop.ui_generic.viewmodels;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import eyesatop.ui_generic.viewmodels.beans.Colour;
import eyesatop.ui_generic.viewmodels.beans.RotateImage3D;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

public class ImageViewModel extends AbstractViewModel<ImageView> {

    private final Property<Colour> tint = new Property<>();
    private final Property<Drawable> imageDrawable = new Property<>();
    private boolean isAlwaysGone = false;
    private final Property<Visibility> visibilityStub = new Property<>();

    private final Property<Float> xPixel = new Property<>();
    private final Property<Float> yPixel = new Property<>();

    private final Property<Integer> size = new Property<>();

    public ImageViewModel(final ImageView view) {
        super(view);
        imageDrawable.withDefault(view.getDrawable()).observe(new Observer<Drawable>() {
            @Override
            public void observe(Drawable oldValue, Drawable newValue, Observation<Drawable> observation) {
                view.setImageDrawable(newValue);
            }
        }, UI_EXECUTOR).observeCurrentValue();

        xPixel.set(view.getX());
        yPixel.set(view.getY());

        tint.observe(new Observer<Colour>() {
            @Override
            public void observe(Colour oldValue, Colour newValue, Observation<Colour> observation) {
                if (newValue == null) {
                    view.clearColorFilter();
                } else {
                    view.setColorFilter(newValue.resolve(view.getContext()));
                }
            }
        }, UI_EXECUTOR).observeCurrentValue();

        xPixel.observe(new Observer<Float>() {
            @Override
            public void observe(Float oldValue, Float newValue, Observation<Float> observation) {
                if(newValue != null) {
                    view.setX(newValue);
                }
            }
        },UI_EXECUTOR);

        yPixel.observe(new Observer<Float>() {
            @Override
            public void observe(Float oldValue, Float newValue, Observation<Float> observation) {
                if(newValue != null) {
                    view.setY(newValue);
                }
            }
        },UI_EXECUTOR);

        size.observe(new Observer<Integer>() {
            @Override
            public void observe(Integer oldValue, Integer newValue, Observation<Integer> observation) {
                if(newValue != null) {
                    view.getLayoutParams().height = newValue;
                    view.getLayoutParams().width = newValue;
                }
                else{
                    view.getLayoutParams().height = 0;
                    view.getLayoutParams().width = 0;
                }
            }
        },UI_EXECUTOR);
    }

    public Property<Float> xPixel() {
        return xPixel;
    }

    public Property<Float> yPixel() {
        return yPixel;
    }

    public Property<Drawable> imageDrawable() {
        return imageDrawable;
    }

    public Property<Colour> tint() {
        return tint;
    }
}
