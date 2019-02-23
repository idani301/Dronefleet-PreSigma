package eyesatop.ui_generic.viewmodels;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.concurrent.Executor;

import eyesatop.ui_generic.viewmodels.beans.Background;
import eyesatop.ui_generic.viewmodels.beans.DrawableBackground;
import eyesatop.ui_generic.viewmodels.beans.UiModel;
import eyesatop.util.Consumer;
import eyesatop.util.Function;
import eyesatop.util.Removable;
import eyesatop.util.android.HandlerExecutor;
import eyesatop.util.model.BooleanProperty;
import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;
import eyesatop.util.model.Property;

public class ViewModel implements UiModel {

    private Removable visibilityRemovable = Removable.STUB;

    public static Activity findActivity(View view) {
        return findActivity(view.getContext());
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public enum Visibility {
        VISIBLE     (View.VISIBLE),
        INVISIBLE   (View.INVISIBLE),
        GONE        (View.GONE);

        public static Visibility valueOf(int visibility) {
            switch (visibility) {
                case View.VISIBLE: return VISIBLE;
                case View.INVISIBLE: return INVISIBLE;
                case View.GONE: return GONE;
            }
            return null;
        }

        private final int value;

        Visibility(int value) {
            this.value = value;
        }
    }

    public static final Executor UI_EXECUTOR = HandlerExecutor.MAIN_LOOPER_EXECUTOR;

    private final Property<Visibility> visibility = new Property<>();
    private final Property<Float> alpha = new Property<>();
    private final Property<Background> background = new Property<>();
    private final Property<Float> layoutWeight = new Property<>();
    private final BooleanProperty focusable = new BooleanProperty();
    private final BooleanProperty clickable = new BooleanProperty();

    private final Property<Function<MotionEvent,Boolean>> singleTap = new Property<>();
    private final Property<Consumer<MotionEvent>> longPress = new Property<>();
    private final Property<Function<MotionEvent,Boolean>> doubleTap = new Property<>();

    private final View view;

    public ViewModel(final View view) {
        this.view = view;

        final GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return !singleTap.isNull() || !longPress.isNull() || !doubleTap.isNull();
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (!singleTap.isNull()) {
                    return singleTap.value().apply(e);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (!longPress.isNull()) {
                    longPress.value().apply(e);
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (!doubleTap.isNull()) {
                    return doubleTap.value().apply(e);
                }
                return super.onDoubleTap(e);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        visibilityRemovable = visibility.withDefault(Visibility.valueOf(view.getVisibility())).observe(new Observer<Visibility>() {
            @Override
            public void observe(Visibility oldValue, Visibility newValue, Observation<Visibility> observation) {
                view.setVisibility(newValue.value);
            }
        }, UI_EXECUTOR);

        alpha.withDefault(view.getAlpha()).observe(new Observer<Float>() {
            @Override
            public void observe(Float oldValue, Float newValue, Observation<Float> observation) {
                view.setAlpha(newValue);
            }
        }, UI_EXECUTOR);

        background.withDefault(new DrawableBackground(view.getBackground()))
                .transform(new Background.Resolver(view.getContext()))
                .observe(new Observer<Drawable>() {
                    @Override
                    public void observe(Drawable oldValue, Drawable newValue, Observation<Drawable> observation) {
                        view.setBackground(newValue);
                    }
                }, UI_EXECUTOR);

        layoutWeight.withDefault(0f).observe(new Observer<Float>() {
            @Override
            public void observe(Float oldValue, Float newValue, Observation<Float> observation) {
                if (view.getParent() instanceof LinearLayout) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams();
                    layoutParams.weight = newValue;
                    view.setLayoutParams(layoutParams);
                }
            }
        }, UI_EXECUTOR);

        focusable.withDefault(true).observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                view.setFocusable(newValue);
            }
        }, UI_EXECUTOR);

        clickable.withDefault(true).observe(new Observer<Boolean>() {
            @Override
            public void observe(Boolean oldValue, Boolean newValue, Observation<Boolean> observation) {
                view.setClickable(newValue);
            }
        }, UI_EXECUTOR);
    }

    public Property<Visibility> visibility() {
        return visibility;
    }

    protected void setVisibilityObserver(boolean isAlwaysGone){

        if(!isAlwaysGone){
            visibilityRemovable.remove();
            visibilityRemovable = visibility.withDefault(Visibility.valueOf(view.getVisibility())).observe(new Observer<Visibility>() {
                @Override
                public void observe(Visibility oldValue, Visibility newValue, Observation<Visibility> observation) {
                    view.setVisibility(newValue.value);
                }
            }, UI_EXECUTOR).observeCurrentValue();
        }
        else {
            visibilityRemovable.remove();
            visibilityRemovable = Removable.STUB;

            UI_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    view.setVisibility(View.GONE);
                }
            });
        }
    }

    public Property<Float> alpha() {
        return alpha;
    }

    public Property<Background> background() {
        return background;
    }

    public Property<Float> layoutWeight() {
        return layoutWeight;
    }

    public Property<Function<MotionEvent,Boolean>> singleTap() {
        return singleTap;
    }

    public BooleanProperty focusable() {
        return focusable;
    }

    public BooleanProperty clickable() {
        return clickable;
    }


    public void setAlwaysGone(boolean isAlwaysGone){
        setVisibilityObserver(isAlwaysGone);
    }

    @Override
    public View view() {
        return view;
    }
}
