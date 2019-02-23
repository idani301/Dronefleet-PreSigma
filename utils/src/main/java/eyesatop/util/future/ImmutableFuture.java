package eyesatop.util.future;

import java.util.concurrent.Executor;

import eyesatop.util.Consumer;
import eyesatop.util.ExecutorConsumer;

public class ImmutableFuture<T> extends AbstractFuture<T> {

    private final T value;

    public ImmutableFuture(T value) {
        this.value = value;
    }

    @Override
    public Future<T> then(Consumer<T> consumer, Executor executor) {
        new ExecutorConsumer<>(consumer, executor).apply(value);
        return this;
    }

    @Override
    public T await() {
        return value;
    }
}
