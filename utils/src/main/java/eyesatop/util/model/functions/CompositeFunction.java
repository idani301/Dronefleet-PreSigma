package eyesatop.util.model.functions;

import eyesatop.util.Function;

public class CompositeFunction<I,O> implements Function<I,O> {

    private final Function<I,O> delegate;

    public CompositeFunction(Function<I, O> delegate) {
        this.delegate = delegate;
    }

    @Override
    public O apply(I input) {
        return delegate.apply(input);
    }

    public <O2> CompositeFunction<I, O2> with(final Function<O, O2> fn) {
        return new CompositeFunction<>(new Function<I, O2>() {
            @Override
            public O2 apply(I input) {
                return fn.apply(CompositeFunction.this.apply(input));
            }
        });
    }
}
