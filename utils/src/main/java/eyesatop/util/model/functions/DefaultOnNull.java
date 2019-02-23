package eyesatop.util.model.functions;

import eyesatop.util.Function;

public class DefaultOnNull<T> implements Function<T,T> {

    private final T defaultValue;

    public DefaultOnNull(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public T apply(T input) {
        return input == null ? defaultValue : input;
    }
}
