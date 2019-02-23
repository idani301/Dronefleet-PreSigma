package eyesatop.util.model;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import eyesatop.util.BlockingExecutor;
import eyesatop.util.Function;
import eyesatop.util.Predicate;
import eyesatop.util.Removable;
import eyesatop.util.model.functions.DefaultOnNull;
import eyesatop.util.model.predicates.NotNull;

public class ObservableList<T> extends AbstractList<T> implements ObservableCollection<T> {

    private final ObservationPublisher<T> observations;
    private final List<T> list;

    public  ObservableList() {
        this(new ArrayList<T>());
    }

    public ObservableList(List<T> list) {
        this.list = list;
        this.observations = new ObservationPublisher<>(this, new CopyOnWriteArraySet<Observation<T>>(), BlockingExecutor.INSTANCE);
    }

    public ObservableList(List<T> list, ObservationPublisher<T> observations) {
        this.list= list;
        this.observations = observations;
    }

    @Override
    public Observation<T> observe(Observer<T> observer) {
        return observe(observer, BlockingExecutor.INSTANCE);
    }

    @Override
    public Observation<T> observe(Observer<T> observer, Executor executor) {
        return observations.observe(observer, executor);
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
    public ObservableValue<T> filter(Predicate<T> predicate) {
        return filter(predicate, BlockingExecutor.INSTANCE);
    }

    @Override
    public ObservableValue<T> filter(Predicate<T> predicate, Executor executor) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Observable<T> filterNulls() {
        return filter(new NotNull<T>());
    }

    @Override
    public <K> ObservableValue<K> transform(Function<T, K> fn, Executor executor) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public <K> ObservableValue<K> transform(Function<T, K> fn, Executor executor, boolean nullOnNull) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public <K> ObservableValue<K> transform(Function<T, K> fn) {
        return transform(fn, BlockingExecutor.INSTANCE);
    }

    @Override
    public <K> ObservableValue<K> transform(Function<T, K> fn, boolean nullOnNull) {
        return transform(fn,BlockingExecutor.INSTANCE,nullOnNull);
    }

    @Override
    public ObservableValue<T> withDefault(T defaultValue) {
        return transform(new DefaultOnNull<>(defaultValue));
    }

    @Override
    public ObservableValue<String> format(String format) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void add(int index, T element) {
        list.add(index, element);
        observations.publish(null, element);
    }

    @Override
    public T set(int index, T element) {
        T before = list.set(index, element);
        observations.publish(before, element);
        return before;
    }

//    @Override
//    public void clear() {
//        removeAll(new Predicate<T>() {
//            @Override
//            public boolean test(T subject) {
//                return true;
//            }
//        });
//    }

    @Override
    public T remove(int index) {
        T removed = list.remove(index);
        observations.publish(removed, null);
        return removed;
    }

    @Override
    public boolean remove(Object o) {

        boolean removed = list.remove(o);

        if(removed){
            observations.publish((T)o, null);
        }

        return removed;
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public T lastValue() {
        if (list.size() == 0) return null;
        return list.get(list.size()-1);
    }

    @Override
    public ObservableList<T> unmodifiable() {
        return new ObservableList<>(Collections.unmodifiableList(list), observations);
    }

    @Override
    public int removeAll(Predicate<T> predicate) {
        Iterator<T> iterator = iterator();
        int c = 0;
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (predicate.test(item)) {
                iterator.remove();
                c++;
            }
        }
        return c;
    }

    @Override
    public T value() {
        return lastValue();
    }

    @Override
    public boolean is(T value) {
        return value.equals(value());
    }

    @Override
    public boolean isOfType(Class type) {
        return type.isInstance(value());
    }

    @Override
    public boolean isNull() {
        return value() == null;
    }

    @Override
    public ObservableBoolean appliesTo(Predicate<T> predicate) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ObservableBoolean equalsTo(T standard) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public ObservableBoolean notNull() {
        throw new UnsupportedOperationException("not implemented");
    }

    public Removable bindToOtherList(ObservableList<T> otherList){

        clear();

        Removable removable = otherList.observe(new CollectionObserver<T>(){
            @Override
            public void added(T value, Observation<T> observation) {
                add(value);
            }

            @Override
            public void removed(T value, Observation<T> observation) {
                remove(value);
            }
        });

        for(T tempValue : otherList){
            add(tempValue);
        }

        return removable;
    }
}
