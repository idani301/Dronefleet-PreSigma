package eyesatop.util;

import java.util.concurrent.Executor;

public class ExecutorConsumer<T> implements Consumer<T> {

    private final Consumer<T> delegate;
    private final Executor executor;

    public ExecutorConsumer(Consumer<T> delegate, Executor executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    @Override
    public void apply(final T result) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                delegate.apply(result);
            }
        });
    }
}
