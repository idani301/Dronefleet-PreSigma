package eyesatop.util.model;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import eyesatop.util.Function;
import eyesatop.util.Predicate;

public interface ObservableValue<T> extends Observable<T>, Valued<T> {
    ObservableValue NULL = new Property() {
        @Override
        public void set(Object value) {
            throw new IllegalStateException("May not set value for ObservableValue.NULL");
        }
    };

    ObservableValue<T> filter(Predicate<T> predicate);
    ObservableValue<T> filter(Predicate<T> predicate, Executor executor);
    <K> ObservableValue<K> transform(Function<T,K> fn);
    <K> ObservableValue<K> transform(Function<T,K> fn,boolean nullOnNull);
    <K> ObservableValue<K> transform(Function<T,K> fn, Executor executor);
    <K> ObservableValue<K> transform(Function<T,K> fn, Executor executor, boolean nullOnNull);
    ObservableValue<T> withDefault(T defaultValue);
    ObservableValue<String> format(String format);
    ObservableBoolean appliesTo(Predicate<T> predicate);
    ObservableBoolean equalsTo(T standard);
    ObservableBoolean notNull();
}
