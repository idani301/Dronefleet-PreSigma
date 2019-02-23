package eyesatop.util.model;

import java.util.concurrent.Executor;

import eyesatop.util.Removable;

public class ExecutorObserver<T> implements Observer<T> {

    private class ObserveRunnable implements Runnable {
        private final T oldValue;
        private final T newValue;
        private final Observation<T> removable;

        private ObserveRunnable(T oldValue, T newValue, Observation<T> removable) {
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.removable = removable;
        }

        @Override
        public void run() {
            delegate.observe(oldValue, newValue, removable);
        }
    }

    private final Observer<T> delegate;
    private final Executor executor;

    public ExecutorObserver(Observer<T> delegate, Executor executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    @Override
    public void observe(T oldValue, T newValue, Observation<T> observer) {
        executor.execute(new ObserveRunnable(oldValue, newValue, observer));
    }


}
