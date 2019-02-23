package eyesatop.util;

import eyesatop.util.model.functions.CompositeFunction;

public class Functions {
    public static <I,O> CompositeFunction<I,O> compose(Function<I,O> fn) {
        return new CompositeFunction<>(fn);
    }
}
