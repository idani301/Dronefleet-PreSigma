package eyesatop.util.model;

import java.util.concurrent.TimeUnit;

public interface ObservableBoolean extends ObservableValue<Boolean> {
    ObservableBoolean or(ObservableValue<Boolean> other);
    ObservableBoolean and(ObservableValue<Boolean> other);
    ObservableBoolean not();
    <T> ObservableValue<T> toggle(T enabled, T disabled);
    void awaitTrue() throws InterruptedException;
    boolean awaitTrue(int timeout, TimeUnit timeUnit) throws InterruptedException;
    void awaitFalse() throws InterruptedException;
    boolean awaitFalse(int timeout, TimeUnit timeUnit) throws InterruptedException;
}
