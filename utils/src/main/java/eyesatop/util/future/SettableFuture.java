package eyesatop.util.future;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eyesatop.util.Consumer;
import eyesatop.util.ExecutorConsumer;
import eyesatop.util.model.Settable;

public class SettableFuture<T> extends AbstractFuture<T> implements Settable<T> {

    private final Lock lock;
    private final Condition valueSet;
    private final Collection<Consumer<T>> consumers;
    private ImmutableFuture<T> delegate;

    public SettableFuture() {
        lock = new ReentrantLock();
        valueSet = lock.newCondition();
        consumers = new LinkedList<>();
    }

    @Override
    public Future<T> then(Consumer<T> consumer, Executor executor) {
        lock.lock();
        try {
            if (delegate != null) {
                delegate.then(consumer, executor);
                return delegate;
            } else {
                consumers.add(new ExecutorConsumer<T>(consumer, executor));
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    @Override
    public T await() throws InterruptedException {
        lock.lock();
        try {
            while (delegate == null) {
                valueSet.await();
            }
        } finally {
            lock.unlock();
        }
        return delegate.await();
    }

    @Override
    public void set(T value) {
        if (delegate == null) {
            lock.lock();
            try {
                delegate = new ImmutableFuture<>(value);
                for (Consumer<T> consumer : consumers) {
                    consumer.apply(value);
                }
                consumers.clear();
                valueSet.signalAll();
            } finally {
                lock.unlock();
            }
        } else {
            throw new IllegalStateException("future was already set.");
        }
    }
}
