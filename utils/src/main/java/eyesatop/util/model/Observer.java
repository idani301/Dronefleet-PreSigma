package eyesatop.util.model;

import eyesatop.util.Removable;

public interface Observer<T> {
    void observe(T oldValue, T newValue, Observation<T> observation);
}
