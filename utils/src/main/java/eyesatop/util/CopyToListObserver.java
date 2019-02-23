package eyesatop.util;

import java.util.List;

import eyesatop.util.model.CollectionObserver;
import eyesatop.util.model.Observation;

public class CopyToListObserver<T> extends CollectionObserver<T> {
    private final List<T> target;

    public CopyToListObserver(List<T> target) {
        this.target = target;
    }

    @Override
    public void added(T value, Observation<T> observation) {
        target.add(value);
    }

    @Override
    public void removed(T value, Observation<T> observation) {
        target.remove(value);
    }

    @Override
    public void replaced(T oldValue, T newValue, Observation<T> observation) {
        target.set(target.indexOf(oldValue), newValue);
    }
}
