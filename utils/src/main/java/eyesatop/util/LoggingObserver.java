package eyesatop.util;

import java.util.Collection;

import eyesatop.util.model.Observation;
import eyesatop.util.model.Observer;

public class LoggingObserver<T> implements Observer<T> {

    private final Collection<T> collection;

    public LoggingObserver(Collection<T> collection) {
        this.collection = collection;
    }

    @Override
    public void observe(T oldValue, T newValue, Observation<T> observation) {
        collection.add(newValue);
    }
}
