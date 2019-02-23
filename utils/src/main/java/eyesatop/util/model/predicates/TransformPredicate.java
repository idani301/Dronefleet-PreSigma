package eyesatop.util.model.predicates;

import eyesatop.util.Function;
import eyesatop.util.Predicate;

public class TransformPredicate<T,K> implements Predicate<T> {

    private final Function<T, K> transformFunction;
    private final Predicate<K> delegate;

    public TransformPredicate(Function<T, K> transformFunction, Predicate<K> delegate) {
        this.transformFunction = transformFunction;
        this.delegate = delegate;
    }

    @Override
    public boolean test(T subject) {
        return delegate.test(transformFunction.apply(subject));
    }
}
