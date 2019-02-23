package eyesatop.util.model;

import java.util.Collection;

public class SimpleObservation<T> implements Observation<T> {

    private final ObservableValue<T> observable;
    private final Observer<T> observer;
    private final Collection<Observation<T>> observations;

    public SimpleObservation(ObservableValue<T> observable, Observer<T> observer, Collection<Observation<T>> observations) {
        this.observable = observable;
        this.observer = observer;
        this.observations = observations;
    }

    @Override
    public Observation observeCurrentValue() {
        observer.observe(null, observable.value(), this);
        return this;
    }

    @Override
    public Observer<T> observer() {
        return observer;
    }

    @Override
    public Observable<T> observable() {
        return observable;
    }

    @Override
    public void remove() {
        observations.remove(this);
    }
}
