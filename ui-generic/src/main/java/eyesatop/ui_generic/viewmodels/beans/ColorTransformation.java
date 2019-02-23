package eyesatop.ui_generic.viewmodels.beans;

import android.animation.ArgbEvaluator;
import android.view.animation.Interpolator;

import eyesatop.util.Function;

public class ColorTransformation implements Function<Float, Integer> {

    private static final ArgbEvaluator evaluator = new ArgbEvaluator();

    private final int startColor;
    private final int endColor;
    private final Interpolator interpolator;

    public ColorTransformation(int startColor, int endColor, Interpolator interpolator) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.interpolator = interpolator;
    }

    @Override
    public Integer apply(Float input) {
        return (Integer) evaluator.evaluate(interpolator.getInterpolation(input), startColor, endColor);
    }
}
