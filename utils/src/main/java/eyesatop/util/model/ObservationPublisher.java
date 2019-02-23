package eyesatop.util.model;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import eyesatop.util.BlockingExecutor;
import eyesatop.util.Function;
import eyesatop.util.Predicate;
import eyesatop.util.model.predicates.NotNull;

public class ObservationPublisher<T> extends AbstractCollection<Observation<T>> implements Publisher<T>, Observable<T> {

    private final ObservableValue<T> observable;
    private final Collection<Observation<T>> observations;
    private final Publisher<T> publisher;
    private final Executor defaultExecutor;

    public ObservationPublisher(ObservableValue<T> observable, Collection<Observation<T>> observations, Executor defaultExecutor) {
        this.observable = observable;
        this.observations = observations;
        publisher = new CollectionPublisher<>(observable, observations);
        this.defaultExecutor = defaultExecutor;
    }

    @Override
    public boolean add(Observation<T> observation) {
        return observations.add(observation);
    }

    @Override
    public boolean remove(Object o) {
        return observations.remove(o);
    }

    @Override
    public int publish(T oldValue, T newValue) {
        return publisher.publish(oldValue, newValue);
    }

    @Override
    public Iterator<Observation<T>> iterator() {
        return observations.iterator();
    }

    @Override
    public int size() {
        return observations.size();
    }

    @Override
    public Observation<T> observe(Observer<T> observer) {
        return observe(observer, defaultExecutor);
    }

    @Override
    public Observation<T> observe(Observer<T> observer, Executor executor) {
        observer = new ExecutorObserver<>(observer, executor);
        Observation<T> observation = new SimpleObservation<>(observable, observer, observations);
        observations.add(observation);
        return observation;
    }

    @Override
    public void await(Predicate<T> predicate) throws InterruptedException {
        // FIXME: 23/01/2017 implement
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void await(Predicate<T> predicate, int timeout, TimeUnit timeUnit) throws InterruptedException {
        // FIXME: 23/01/2017 implement
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Observable<T> filter(Predicate<T> predicate) {
        return filter(predicate, BlockingExecutor.INSTANCE);
    }

    @Override
    public Observable<T> filter(Predicate<T> predicate, Executor executor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Observable<T> filterNulls() {
        return filter(new NotNull<T>());
    }

    @Override
    public <K> Observable<K> transform(Function<T, K> fn) {
        return transform(fn, BlockingExecutor.INSTANCE);
    }

    @Override
    public <K> Observable<K> transform(Function<T, K> fn, Executor executor) {
        throw new UnsupportedOperationException("not implemented");
    }
}
