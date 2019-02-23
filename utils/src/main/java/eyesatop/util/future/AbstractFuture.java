package eyesatop.util.future;

import java.util.concurrent.Executor;

import eyesatop.util.Consumer;
import eyesatop.util.Function;

public abstract class AbstractFuture<T> implements Future<T> {

    @Override
    public <R> Future<R> then(final Function<T, R> fn, Executor executor) {
        final SettableFuture<R> future = new SettableFuture<>();
        then(new Consumer<T>() {
            @Override
            public void apply(T result) {
                future.set(fn.apply(result));
            }
        }, executor);
        return future;
    }
}
