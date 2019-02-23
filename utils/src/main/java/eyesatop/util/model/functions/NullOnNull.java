package eyesatop.util.model.functions;

import eyesatop.util.Function;

public class NullOnNull<I,O> implements Function<I,O> {
    private final Function<I,O> delegate;

    public NullOnNull(Function<I, O> delegate) {
        this.delegate = delegate;
    }

    @Override
    public O apply(I input) {
        return input != null ? delegate.apply(input) : null;
    }
}
