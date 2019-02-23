package eyesatop.util.model.functions;

import eyesatop.util.Function;

public class Toggle<T> implements Function<Boolean, T> {

    private final T enabled;
    private final T disabled;

    public Toggle(T enabled, T disabled) {
        this.enabled = enabled;
        this.disabled = disabled;
    }

    @Override
    public T apply(Boolean input) {
        return input ? enabled : disabled;
    }
}
