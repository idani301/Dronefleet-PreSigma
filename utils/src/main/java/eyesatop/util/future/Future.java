package eyesatop.util.future;

import java.util.concurrent.Executor;

import eyesatop.util.Consumer;
import eyesatop.util.Function;

public interface Future<T> {
    Future<T> then(Consumer<T> consumer, Executor executor);
    <R> Future<R> then(Function<T,R> fn, Executor executor);

    T await() throws InterruptedException;
}
