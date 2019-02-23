package eyesatop.util.model;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import eyesatop.util.BlockingExecutor;
import eyesatop.util.Function;
import eyesatop.util.Predicate;
import eyesatop.util.PropertyMaybe;
import eyesatop.util.model.predicates.NotNull;

public interface Observable<T> {
    abstract class Abstract<T> implements Observable<T> {
        @Override
        public Observation<T> observe(Observer<T> observer) {
            return observe(observer, BlockingExecutor.INSTANCE);
        }

        @Override
        public void await(final Predicate<T> predicate, int timeout, TimeUnit timeUnit) throws InterruptedException {
            final CountDownLatch latch = new CountDownLatch(1);
            observe(new Observer<T>() {
                @Override
                public void observe(T oldValue, T newValue, Observation<T> observation) {
                    if (predicate.test(newValue)) {
                        latch.countDown();
                        observation.remove();
                    }
                }
            }).observeCurrentValue();
            if (timeout == -1) {
                latch.await();
            } else {
                latch.await(timeout, timeUnit);
            }
        }

        @Override
        public void await(Predicate<T> predicate) throws InterruptedException {
            await(predicate, -1, null);
        }

        @Override
        public Property<T> filterNulls() {
            return filter(new NotNull<T>());
        }

        @Override
        public Property<T> filter(Predicate<T> predicate) {
            return filter(predicate, BlockingExecutor.INSTANCE);
        }

        @Override
        public Property<T> filter(final Predicate<T> predicate, Executor executor) {
            Property<T> property = new Property<>();
            final PropertyMaybe<T> propertyMaybe = new PropertyMaybe<>(property);
            try {
                observe(new Observer<T>() {
                    @Override
                    public void observe(T oldValue, T newValue, Observation<T> observation) {
                        Property<T> property = propertyMaybe.getProperty();
                        if (property == null) {
                            observation.remove();
                        } else {
                            if (predicate.test(newValue)) {
                                property.set(newValue);
                            }
                            propertyMaybe.invalidate();
                        }
                    }
                }, executor);
                return property;
            } finally {
                property = null;
            }
        }

        @Override
        public <K> Property<K> transform(Function<T, K> fn) {
            return transform(fn, BlockingExecutor.INSTANCE);
        }

        @Override
        public <K> Property<K> transform(final Function<T, K> fn, Executor executor) {
            Property<K> property = new Property<>();
            final PropertyMaybe<K> propertyMaybe = new PropertyMaybe<>(property);
            try {
                observe(new Observer<T>() {
                    @Override
                    public void observe(T oldValue, T newValue, Observation<T> observation) {
                        Property<K> property = propertyMaybe.getProperty();
                        if (property == null) {
                            observation.remove();
                        } else {
                            property.set(fn.apply(newValue));
                            propertyMaybe.invalidate();
                        }
                    }
                }, executor);
                return property;
            } finally {
                property = null;
            }
        }
    }
    Observation<T> observe(Observer<T> observer);
    Observation<T> observe(Observer<T> observer, Executor executor);
    void await(Predicate<T> predicate) throws InterruptedException;
    void await(Predicate<T> predicate, int timeout, TimeUnit timeUnit) throws InterruptedException;
    Observable<T> filter(Predicate<T> predicate);
    Observable<T> filter(Predicate<T> predicate, Executor executor);
    Observable<T> filterNulls();
    <K> Observable<K> transform(Function<T,K> fn);
    <K> Observable<K> transform(Function<T,K> fn, Executor executor);
}
