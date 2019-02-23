package eyesatop.util.model;

import java.util.concurrent.Executor;

public class Trigger {

    private final Property<Void> delegate;

    public Trigger() {
        this(new Property<Void>());
    }

    public Trigger(Property<Void> delegate) {
        this.delegate = delegate;
    }

    public Observation<Void> observe(Observer<Void> observer) {
        return delegate.observe(observer);
    }

    public Observation<Void> observe(Observer<Void> observer, Executor executor) {
        return delegate.observe(observer, executor);
    }

    public void trigger() {
        delegate.set(null);
    }
}
