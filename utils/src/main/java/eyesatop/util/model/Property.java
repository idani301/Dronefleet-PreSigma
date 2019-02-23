package eyesatop.util.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;

import eyesatop.util.BlockingExecutor;
import eyesatop.util.Function;
import eyesatop.util.Predicate;
import eyesatop.util.PropertyMaybe;
import eyesatop.util.model.functions.DefaultOnNull;
import eyesatop.util.model.functions.FormatString;
import eyesatop.util.model.predicates.EqualsPredicate;
import eyesatop.util.model.predicates.NotNull;

public class Property<T> extends Observable.Abstract<T> implements ObservableValue<T>, Settable<T>, Bindable<T> {

    private static class PredicateObserver<T> implements Observer<T> {
        private final PropertyMaybe<Boolean> propertyMaybe;
        private final Predicate<T> predicate;

        private PredicateObserver(Property<Boolean> settable, Predicate<T> predicate) {
            this.propertyMaybe = new PropertyMaybe<>(settable);
            this.predicate = predicate;
        }

        @Override
        public void observe(T oldValue, T newValue, Observation<T> observation) {
            Settable<Boolean> settable = propertyMaybe.getProperty();
            if (settable == null) {
                observation.remove();
            } else {
                settable.set(predicate.test(newValue));
            }
        }
    }

    private final AtomicReference<T> valueRef;
    private final ObservationPublisher<T> observations;

    public Property() {
        this(null);
    }

    public Property(T value) {
        valueRef = new AtomicReference<>(value);
        observations = new ObservationPublisher<>(this, new CopyOnWriteArraySet<Observation<T>>(), BlockingExecutor.INSTANCE);
    }

    @Override
    public T value() {
        return valueRef.get();
    }

    @Override
    public boolean is(T value) {
        return value == valueRef.get() || (value != null && value.equals(valueRef.get()));
    }

    @Override
    public boolean isOfType(Class type) {
        return type.isInstance(valueRef.get());
    }

    @Override
    public boolean isNull() {
        return is(null);
    }

    @Override
    public Observation<T> observe(Observer<T> observer, Executor executor) {
        return observations.observe(observer, executor);
    }

    @Override
    public void set(T value) {
        T oldValue = valueRef.get();
        valueRef.set(value);
        observations.publish(oldValue, value);
    }

    public void setIfNew(T value){
        T oldValue = valueRef.get();
        if(!areEquals(oldValue,value)){
            valueRef.set(value);
            observations.publish(oldValue, value);
        }
    }

    public boolean areEquals(T oldValue,T newValue){
        if(oldValue == null){
            return newValue == null;
        }
        else if(newValue == null){
            return false;
        }
        else{
            return newValue.equals(oldValue);
        }
    }

    @Override
    public Observation<T> bind(Observable<T> observable) {
        return observable.observe(new Observer<T>() {
            @Override
            public void observe(T oldValue, T newValue, Observation<T> observation) {
                set(newValue);
            }
        }).observeCurrentValue();
    }

    @Override
    public ObservableBoolean appliesTo(final Predicate<T> predicate) {
        BooleanProperty booleanProperty = new BooleanProperty();
        observe(new PredicateObserver<T>(booleanProperty, predicate));
        booleanProperty.set(predicate.test(value()));
        return booleanProperty;
    }

    @Override
    public ObservableBoolean equalsTo(T standard) {
        return appliesTo(new EqualsPredicate<T>(standard));
    }

    @Override
    public ObservableBoolean notNull() {
        return appliesTo(new NotNull<T>());
    }

    @Override
    public <K> Property<K> transform(Function<T, K> fn, Executor executor) {
        return transform(fn, executor, true);
    }

    @Override
    public <K> Property<K> transform(Function<T, K> fn, Executor executor, boolean nullOnNull) {
        Property<K> transformed = super.transform(fn, executor);
        if (nullOnNull) {
            transformed.set(value() == null ? null : fn.apply(value()));
        } else {
            transformed.set(fn.apply(value()));
        }
        return transformed;
    }

    @Override
    public Property<T> filter(Predicate<T> predicate, Executor executor) {
        Property<T> filtered = super.filter(predicate, executor);
        filtered.set(predicate.test(value()) ? value() : null);
        return filtered;
    }

    @Override
    public <K> ObservableValue<K> transform(Function<T, K> fn, boolean nullOnNull) {
        return transform(fn,BlockingExecutor.INSTANCE,nullOnNull);
    }

    @Override
    public ObservableValue<T> withDefault(T defaultValue) {
        Property<T> transformed = transform(new DefaultOnNull<>(defaultValue), BlockingExecutor.INSTANCE);
        if (transformed.isNull()) {
            transformed.set(defaultValue);
        }
        return transformed;
    }

    @Override
    public ObservableValue<String> format(String format) {
        return transform(new FormatString<T>(format));
    }

    public Collection<Observation<T>> observations() {
        return observations;
    }

    public String toString(){
        T value = value();
        if(value == null){
            return "Null";
        }
        else{
            return value.toString();
        }
    }
}
